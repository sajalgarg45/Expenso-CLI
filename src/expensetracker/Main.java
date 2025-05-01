package expensetracker;

import expensetracker.models.*;
import expensetracker.datastore.DataStore;
import expensetracker.datastructures.*;
import expensetracker.utils.ConsoleUtils;

import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static DataStore store  = new DataStore();

    private static Stack deletedBudgets  = new Stack();
    private static Stack deletedExpenses = new Stack();
    private static Queue recentActions   = new Queue();

    private static class DeletedExpense {
        Budget budget; Expense expense;
        DeletedExpense(Budget b, Expense e){ this.budget=b; this.expense=e; }
    }

    public static void main(String[] args) {
        while (true) {
            ConsoleUtils.clearScreen();
            ConsoleUtils.printBox(new String[]{
                    "Welcome to ExpenseTrackerCLI",
                    "1. Login",
                    "2. Register",
                    "0. Exit"
            });
            System.out.print("Select option: ");
            int ch = Integer.parseInt(scanner.nextLine());
            if      (ch == 1) login();
            else if (ch == 2) register();
            else if (ch == 0) break;
        }
    }

    private static void login() {
        System.out.print("User ID: ");    String uid = scanner.nextLine();
        System.out.print("Password: ");   String pw  = scanner.nextLine();
        User user = store.authenticate(uid, pw);
        if (user != null) {
            dashboard(user);
        }
    }

    private static void register() {
        System.out.print("Choose User ID: ");   String uid = scanner.nextLine();
        System.out.print("Choose Password: ");  String pw  = scanner.nextLine();
        if (store.register(new User(uid, pw))) {
            System.out.println("Registered! Press Enter."); scanner.nextLine();
        }
    }

    private static void dashboard(User user) {
        while (true) {
            double totalB = user.getBudgetList().sum(b -> b.getLimit());
            double totalS = user.getBudgetList().sum(b -> b.getSpent());
            ConsoleUtils.clearScreen();
            ConsoleUtils.printBox(new String[]{
                    "Dashboard: " + user.getId(),
                    "Budgets: " + user.getBudgetList().size(),
                    String.format("Total: %.2f", totalB),
                    String.format("Spent: %.2f", totalS),
                    String.format("Save : %.2f", totalB - totalS)
            });
            ConsoleUtils.printGraph(totalS, totalB);
            ConsoleUtils.printBox(new String[]{
                    "1.Manage Budgets",
                    "2.View All Expenses",
                    "3.Undo Delete",
                    "4.Recent Actions",
                    "5.Delete Account  ",
                    "0.Logout"
            });

            int c = Integer.parseInt(scanner.nextLine());
            if      (c == 1) manageBudgets(user);
            else if (c == 2) viewAllExpenses(user);
            else if (c == 3) undoDeletion(user);
            else if (c == 4) showRecentAdds();
            else if (c == 5) {
                ConsoleUtils.clearScreen();
                ConsoleUtils.printBox(new String[]{
                        "Delete Account", "Are you sure? (y/N)"
                });
                String ans = scanner.nextLine();
                if (ans.equalsIgnoreCase("y")) {
                    if (store.deleteUser(user)) {
                        ConsoleUtils.printBox(new String[]{"Account deleted"});
                        System.out.println("Press Enter to continue.");
                        scanner.nextLine();
                        break; // back to welcome
                    }
                }
            }
            else if (c == 0) {
                break; // logout
            }
        }
    }

    private static void manageBudgets(User u) {
        while (true) {
            ConsoleUtils.clearScreen();
            ConsoleUtils.printBox(new String[]{
                    "Manage Budgets",
                    "1.List Budgets",
                    "2.Create Budget",
                    "3.Search Budget",
                    "4.Sorted Budgets",
                    "0.Back"
            });
            int c = Integer.parseInt(scanner.nextLine());
            if      (c == 1) listBudgets(u);
            else if (c == 2) createBudget(u);
            else if (c == 3) searchBudgets(u);
            else if (c == 4) showSortedBudgets(u);
            else if (c == 0) {
                store.save();
                break;
            }
        }
    }

    private static void listBudgets(User u) {
        ConsoleUtils.clearScreen();
        int sz = u.getBudgetList().size();
        String[] lines = new String[sz + 2];
        lines[0] = "Select Budget:";
        for (int i = 0; i < sz; i++) {
            Budget b = u.getBudgetList().getAt(i);
            lines[i + 1] = (i + 1) + ") " + b.getName()
                    + " L:" + b.getLimit()
                    + " S:" + b.getSpent();
        }
        lines[sz + 1] = "0) Back";
        ConsoleUtils.printBox(lines);
        int sel = Integer.parseInt(scanner.nextLine());
        if (sel > 0 && sel <= sz) {
            budgetActions(u, u.getBudgetList().getAt(sel - 1));
        }
    }

    private static void createBudget(User u) {
        System.out.print("Name: "); String n = scanner.nextLine();
        System.out.print("Limit: "); double l = Double.parseDouble(scanner.nextLine());
        Budget b = new Budget(n, l);
        u.getBudgetList().add(b);
        recentActions.enqueue("Created budget:" + n);
        store.save();
    }

    private static void budgetActions(User u, Budget b) {
        while (true) {
            ConsoleUtils.clearScreen();
            ConsoleUtils.printBox(new String[]{
                    "Budget: " + b.getName(),
                    "1.Add Exp",
                    "2.List Exp",
                    "3.Edit",
                    "4.Delete",
                    "0.Back"
            });
            int c = Integer.parseInt(scanner.nextLine());
            if (c == 1) {
                System.out.print("Desc: "); String d = scanner.nextLine();
                System.out.print("Amt : "); double a = Double.parseDouble(scanner.nextLine());
                if (b.getSpent() + a > b.getLimit()) {
                    ConsoleUtils.printBox(new String[]{"Over Budget!"});
                    scanner.nextLine();
                } else {
                    Expense e = new Expense(d, a);
                    b.addExpense(e);
                    recentActions.enqueue("Added exp " + d);
                    store.save();
                }
            }
            else if (c == 2) listExpenses(b);
            else if (c == 3) {
                System.out.print("New name (Enter skip): "); String nn = scanner.nextLine();
                if (!nn.isEmpty()) {
                    String old = b.getName();
                    b.setName(nn);
                    recentActions.enqueue("Renamed " + old + "→" + nn);
                }
                System.out.print("New lim  (Enter skip): "); String ls = scanner.nextLine();
                if (!ls.isEmpty()) {
                    double old = b.getLimit(), nv = Double.parseDouble(ls);
                    b.setLimit(nv);
                    recentActions.enqueue("Limit " + old + "→" + nv);
                }
                store.save();
            }
            else if (c == 4) {
                u.getBudgetList().remove(b);
                deletedBudgets.push(b);
                recentActions.enqueue("Deleted budget:" + b.getName());
                store.save();
                break;
            }
            else if (c == 0) break;
        }
    }

    private static void listExpenses(Budget b) {
        ConsoleUtils.clearScreen();
        int sz = b.getExpenses().size();
        if (sz == 0) {
            ConsoleUtils.printBox(new String[]{"No expenses"});
            scanner.nextLine();
            return;
        }
        String[] lines = new String[sz + 2];
        lines[0] = "Select Expense:";
        for (int i = 0; i < sz; i++) {
            Expense e = b.getExpenses().getAt(i);
            lines[i + 1] = (i + 1) + ") " + e.getDesc() + ":" + e.getAmount();
        }
        lines[sz + 1] = "0) Back";
        ConsoleUtils.printBox(lines);
        int sel = Integer.parseInt(scanner.nextLine());
        if (sel > 0 && sel <= sz) {
            expenseActions(b, b.getExpenses().getAt(sel - 1));
        }
    }

    private static void expenseActions(Budget b, Expense e) {
        while (true) {
            ConsoleUtils.clearScreen();
            ConsoleUtils.printBox(new String[]{
                    "Expense:" + e.getDesc() + "(" + e.getAmount() + ")",
                    "1.Edit",
                    "2.Delete",
                    "0.Back"
            });
            int c = Integer.parseInt(scanner.nextLine());
            if (c == 1) {
                System.out.print("New desc (Enter skip): "); String nd = scanner.nextLine();
                if (!nd.isEmpty()) { e.setDesc(nd); recentActions.enqueue("Exp desc→" + nd); }
                System.out.print("New amt  (Enter skip): "); String na = scanner.nextLine();
                if (!na.isEmpty()) {
                    double old = e.getAmount();
                    e.setAmount(Double.parseDouble(na));
                    recentActions.enqueue("Exp amt " + old + "→" + e.getAmount());
                }
                store.save();
            }
            else if (c == 2) {
                b.getExpenses().remove(e);
                deletedExpenses.push(new DeletedExpense(b, e));
                recentActions.enqueue("Deleted exp:" + e.getDesc());
                store.save();
                break;
            }
            else if (c == 0) break;
        }
    }

    private static void viewAllExpenses(User u) {
        ConsoleUtils.clearScreen();
        for (Budget b : u.getBudgetList()) {
            System.out.println("-- " + b.getName());
            for (Expense e : b.getExpenses()) {
                System.out.println("   " + e.getDesc() + ": " + e.getAmount());
            }
        }
        scanner.nextLine();
    }

    private static void undoDeletion(User u) {
        ConsoleUtils.clearScreen();
        if (!deletedExpenses.isEmpty()) {
            DeletedExpense de = (DeletedExpense) deletedExpenses.pop();
            de.budget.addExpense(de.expense);
            ConsoleUtils.printBox(new String[]{"Restored exp:" + de.expense.getDesc()});
        } else if (!deletedBudgets.isEmpty()) {
            Budget b = (Budget) deletedBudgets.pop();
            u.getBudgetList().add(b);
            ConsoleUtils.printBox(new String[]{"Restored budget:" + b.getName()});
        } else {
            ConsoleUtils.printBox(new String[]{"Nothing to undo"});
        }
        scanner.nextLine();
        store.save();
    }

    private static void showRecentAdds() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printBox(new String[]{"Recent Actions"});
        Queue tmp = new Queue();
        while (!recentActions.isEmpty()) {
            Object a = recentActions.dequeue();
            System.out.println(" - " + a);
            tmp.enqueue(a);
        }
        while (!tmp.isEmpty()) recentActions.enqueue(tmp.dequeue());
        scanner.nextLine();
    }

    private static void searchBudgets(User u) {
        ConsoleUtils.clearScreen();
        BinarySearchTree bst = new BinarySearchTree();
        for (int i = 0; i < u.getBudgetList().size(); i++) {
            bst.insert(u.getBudgetList().getAt(i).getName());
        }
        System.out.print("Name to search: ");
        String q = scanner.nextLine();
        if (bst.contains(q)) {
            int idx = -1;
            for (int i = 0; i < u.getBudgetList().size(); i++) {
                if (u.getBudgetList().getAt(i).getName().equals(q)) {
                    idx = i; break;
                }
            }
            ConsoleUtils.printBox(new String[]{"Found #" + (idx + 1) + ": " + q});
        } else {
            ConsoleUtils.printBox(new String[]{"No budget named \"" + q + "\""});
        }
        scanner.nextLine();
    }

    private static void showSortedBudgets(User u) {
        ConsoleUtils.clearScreen();
        int sz = u.getBudgetList().size();
        if (sz == 0) {
            ConsoleUtils.printBox(new String[]{"No budgets"});
            scanner.nextLine();
            return;
        }
        Budget[] arr = new Budget[sz];
        for (int i = 0; i < sz; i++) arr[i] = u.getBudgetList().getAt(i);
        quickSort(arr, 0, sz - 1);
        String[] lines = new String[sz + 1];
        lines[0] = "Sorted by limit:";
        for (int i = 0; i < sz; i++) {
            lines[i + 1] = (i + 1) + ") " + arr[i].getName()
                    + " L:" + arr[i].getLimit()
                    + " S:" + arr[i].getSpent();
        }
        ConsoleUtils.printBox(lines);
        scanner.nextLine();
    }

    private static void quickSort(Budget[] a, int lo, int hi) {
        if (lo < hi) {
            int p = partition(a, lo, hi);
            quickSort(a, lo, p - 1);
            quickSort(a, p + 1, hi);
        }
    }

    private static int partition(Budget[] a, int lo, int hi) {
        double pivot = a[hi].getLimit();
        int i = lo - 1;
        for (int j = lo; j < hi; j++) {
            if (a[j].getLimit() <= pivot) {
                i++;
                Budget t = a[i]; a[i] = a[j]; a[j] = t;
            }
        }
        Budget t = a[i + 1]; a[i + 1] = a[hi]; a[hi] = t;
        return i + 1;
    }
}