package expensetracker;

import expensetracker.models.Budget;
import expensetracker.models.Expense;
import expensetracker.models.User;
import expensetracker.datastore.DataStore;
import expensetracker.ds.Stack;
import expensetracker.ds.Queue;
import expensetracker.ds.BinarySearchTree;  // ← NEW
import expensetracker.utils.ConsoleUtils;

import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static DataStore store = new DataStore();

    private static Stack deletedBudgets   = new Stack();
    private static Stack deletedExpenses  = new Stack();
    private static Queue recentActions    = new Queue();

    private static class DeletedExpense {
        Budget  budget;
        Expense expense;
        DeletedExpense(Budget b, Expense e) { this.budget = b; this.expense = e; }
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
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1: login();    break;
                case 2: register(); break;
                case 0: System.out.println("Goodbye!"); System.exit(0);
                default:
                    System.out.println("Invalid choice. Press Enter to continue.");
                    scanner.nextLine();
            }
        }
    }

    private static void login() {
        System.out.print("User ID: ");    String uid = scanner.nextLine();
        System.out.print("Password: ");   String pw  = scanner.nextLine();
        User user = store.authenticate(uid, pw);
        if (user != null) dashboard(user);
        else {
            System.out.println("Login failed. Press Enter."); scanner.nextLine();
        }
    }

    private static void register() {
        System.out.print("Choose User ID: ");   String uid = scanner.nextLine();
        System.out.print("Choose Password: ");  String pw  = scanner.nextLine();
        boolean ok = store.register(new User(uid, pw));
        System.out.println(ok ? "Registered! Press Enter."
                : "Failed (ID exists). Press Enter.");
        scanner.nextLine();
    }

    private static void dashboard(User user) {
        while (true) {
            double totalBudget = user.getBudgetList().sum(b -> b.getLimit());
            double totalSpent  = user.getBudgetList().sum(b -> b.getSpent());
            double savings     = totalBudget - totalSpent;
            int    count       = user.getBudgetList().size();

            ConsoleUtils.clearScreen();
            ConsoleUtils.printBox(new String[]{
                    "Dashboard for " + user.getId(),
                    "Budgets: " + count,
                    String.format("Total Budget: %.2f", totalBudget),
                    String.format("  Spent: %.2f", totalSpent),
                    String.format("Savings: %.2f", savings)
            });
            ConsoleUtils.printGraph(totalSpent, totalBudget);

            ConsoleUtils.printBox(new String[]{
                    "1. Manage Budgets",
                    "2. View ALl Expenses",
                    "3. Undo Last Delete",
                    "4. Show Recent Adds",
                    "0. Logout"
            });
            System.out.print("Choose: ");
            int c = Integer.parseInt(scanner.nextLine());

            if      (c == 1) manageBudgets(user);
            else if (c == 2) viewAllExpenses(user);
            else if (c == 3) undoDeletion(user);
            else if (c == 4) showRecentAdds();
            else if (c == 0) break;
        }
    }

    private static void manageBudgets(User user) {
        while (true) {
            ConsoleUtils.clearScreen();
            ConsoleUtils.printBox(new String[]{
                    "Manage Budgets",
                    "1. List Budgets",
                    "2. Create Budget",
                    "3. Search Budget",
                    "4. Show Sorted Budgets",  // ← new
                    "0. Back"
            });
            System.out.print("Choose: ");
            int c = Integer.parseInt(scanner.nextLine());
            if      (c == 1) listBudgets(user);
            else if (c == 2) createBudget(user);
            else if (c == 3) searchBudgets(user);
            else if (c == 4) showSortedBudgets(user);  // ← new
            else if (c == 0) break;
        }
    }


    private static void listBudgets(User user) {
        ConsoleUtils.clearScreen();

        int size = user.getBudgetList().size();
        String[] lines = new String[size + 2];
        lines[0] = "Select Budget:";

        // Print using getAt so index 0 = first‐created, index size-1 = last‐created
        for (int idx = 0; idx < size; idx++) {
            Budget b = user.getBudgetList().getAt(idx);
            lines[idx + 1] = (idx + 1) + ") "
                    + b.getName()
                    + "  Limit:" + b.getLimit()
                    + "  Spent:" + b.getSpent();
        }

        lines[size + 1] = "0) Back";
        ConsoleUtils.printBox(lines);

        System.out.print("Choose: ");
        int sel = Integer.parseInt(scanner.nextLine());
        if (sel > 0 && sel <= size) {
            // sel 1 maps to getAt(0), sel 2 → getAt(1), etc.
            budgetActions(user, user.getBudgetList().getAt(sel - 1));
        }
    }

    private static void createBudget(User user) {
        System.out.print("Budget Name: ");           String name = scanner.nextLine();
        System.out.print("Limit Amount: ");          double lim  = Double.parseDouble(scanner.nextLine());
        Budget b = new Budget(name, lim);
        user.getBudgetList().add(b);
        recentActions.enqueue("Created budget: " + name);
    }

    private static void budgetActions(User user, Budget b) {
        while (true) {
            ConsoleUtils.clearScreen();
            ConsoleUtils.printBox(new String[]{
                    "Budget: " + b.getName(),
                    "1. Add Expense",
                    "2. List Expenses",
                    "3. Edit Budget",
                    "4. Delete Budget",
                    "0. Back"
            });
            System.out.print("Choose: "); int c = Integer.parseInt(scanner.nextLine());

            if (c == 1) {
                // Add Expense
                System.out.print("Expense Desc: ");   String desc = scanner.nextLine();
                System.out.print("Amount: ");          double amt  = Double.parseDouble(scanner.nextLine());
                if (b.getSpent() + amt > b.getLimit()) {
                    ConsoleUtils.printBox(new String[]{
                            "ERROR: Over Budget!",
                            "Limit: " + b.getLimit(),
                            "Current Spent: " + b.getSpent(),
                            "Attempted: " + amt
                    });
                    System.out.println("Press Enter to continue.");
                    scanner.nextLine();
                } else {
                    Expense e = new Expense(desc, amt);
                    b.addExpense(e);
                    recentActions.enqueue(
                            "Added expense \"" + desc + "\" ("+amt+") to " + b.getName()
                    );
                }
            }
            else if (c == 2) {
                // List / Edit / Delete Expenses
                listExpenses(b);
            }
            else if (c == 3) {
                // Edit Budget (rename & limit)
                System.out.print("New Name (Enter to skip): ");
                String newName = scanner.nextLine();
                if (!newName.isEmpty()) {
                    String old = b.getName();
                    b.setName(newName);
                    recentActions.enqueue("Renamed budget: " + old + " → " + newName);
                }
                System.out.print("New Limit (Enter to skip): ");
                String limStr = scanner.nextLine();
                if (!limStr.isEmpty()) {
                    double oldLim = b.getLimit();
                    double newLim = Double.parseDouble(limStr);
                    b.setLimit(newLim);
                    recentActions.enqueue(
                            "Changed limit for " + b.getName()
                                    + ": " + oldLim + " → " + newLim
                    );
                }
            }
            else if (c == 4) {
                // Delete Budget
                recentActions.enqueue("Deleted budget: " + b.getName());
                deletedBudgets.push(b);
                user.getBudgetList().remove(b);
                break;
            }
            else if (c == 0) {
                break;
            }
        }
    }

    private static void listExpenses(Budget b) {
        ConsoleUtils.clearScreen();
        int size = b.getExpenses().size();
        if (size == 0) {
            ConsoleUtils.printBox(new String[]{"No expenses in " + b.getName()});
            System.out.println("Press Enter to continue.");
            scanner.nextLine();
            return;
        }

        String[] lines = new String[size + 2];
        lines[0] = "Expenses for " + b.getName() + ":";
        int i = 1;
        for (Expense e : b.getExpenses()) {
            lines[i++] = (i-1) + ") " + e.getDesc() + ": " + e.getAmount();
        }
        lines[i] = "0) Back";
        ConsoleUtils.printBox(lines);

        System.out.print("Choose: ");
        int sel = Integer.parseInt(scanner.nextLine());
        if (sel > 0 && sel <= size) {
            expenseActions(b, b.getExpenses().getAt(sel-1));
        }
    }

    private static void expenseActions(Budget b, Expense e) {
        while (true) {
            ConsoleUtils.clearScreen();
            ConsoleUtils.printBox(new String[]{
                    "Expense: " + e.getDesc() + " (" + e.getAmount() + ")",
                    "1. Edit Expense",
                    "2. Delete Expense",
                    "0. Back"
            });
            System.out.print("Choose: "); int c = Integer.parseInt(scanner.nextLine());

            if (c == 1) {
                System.out.print("New Desc (Enter to skip): ");
                String d = scanner.nextLine();
                if (!d.isEmpty()) {
                    e.setDesc(d);
                    recentActions.enqueue("Edited expense desc to \""+d+"\" in "+b.getName());
                }
                System.out.print("New Amount (Enter to skip): ");
                String aStr = scanner.nextLine();
                if (!aStr.isEmpty()) {
                    double oldAmt = e.getAmount();
                    e.setAmount(Double.parseDouble(aStr));
                    recentActions.enqueue(
                            "Edited expense amount in "+b.getName()
                                    +": "+oldAmt+" → "+e.getAmount()
                    );
                }
            }
            else if (c == 2) {
                recentActions.enqueue(
                        "Deleted expense \""+e.getDesc()+"\" from "+b.getName()
                );
                deletedExpenses.push(new DeletedExpense(b, e));
                b.getExpenses().remove(e);
                break;
            }
            else if (c == 0) {
                break;
            }
        }
    }

    private static void viewAllExpenses(User user) {
        ConsoleUtils.clearScreen();
        for (Budget b : user.getBudgetList()) {
            System.out.println("-- " + b.getName());
            for (Expense e : b.getExpenses()) {
                System.out.println("   " + e.getDesc() + ": " + e.getAmount());
            }
        }
        System.out.println("Press Enter to continue.");
        scanner.nextLine();
    }

    private static void undoDeletion(User user) {
        ConsoleUtils.clearScreen();
        if (!deletedExpenses.isEmpty()) {
            DeletedExpense de = (DeletedExpense) deletedExpenses.pop();
            de.budget.addExpense(de.expense);
            ConsoleUtils.printBox(new String[]{
                    "Undo Delete",
                    "Restored expense: " + de.expense.getDesc()
            });
        }
        else if (!deletedBudgets.isEmpty()) {
            Budget b = (Budget) deletedBudgets.pop();
            user.getBudgetList().add(b);
            ConsoleUtils.printBox(new String[]{
                    "Undo Delete",
                    "Restored budget: " + b.getName()
            });
        }
        else {
            ConsoleUtils.printBox(new String[]{
                    "Undo Delete",
                    "Nothing to undo"
            });
        }
        System.out.println("Press Enter to continue.");
        scanner.nextLine();
    }

    private static void showRecentAdds() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printBox(new String[]{"Recent Actions"});
        Queue temp = new Queue();
        while (!recentActions.isEmpty()) {
            Object act = recentActions.dequeue();
            System.out.println(" - " + act);
            temp.enqueue(act);
        }
        // restore
        while (!temp.isEmpty()) {
            recentActions.enqueue(temp.dequeue());
        }
        System.out.println("Press Enter to continue.");
        scanner.nextLine();
    }

    private static void searchBudgets(User user) {
        ConsoleUtils.clearScreen();
        // Build BST of budget names
        BinarySearchTree tree = new BinarySearchTree();
        for (int i = 0; i < user.getBudgetList().size(); i++) {
            tree.insert(user.getBudgetList().getAt(i).getName());
        }

        System.out.print("Enter budget name to search: ");
        String query = scanner.nextLine();

        if (tree.contains(query)) {
            // Find its index in creation order via getAt
            int size = user.getBudgetList().size();
            int foundIdx = -1;
            for (int i = 0; i < size; i++) {
                if (user.getBudgetList().getAt(i).getName().equals(query)) {
                    foundIdx = i;
                    break;
                }
            }

            if (foundIdx >= 0) {
                ConsoleUtils.printBox(new String[]{
                        "Found budget #" + (foundIdx + 1) + ": " + query
                });
            } else {
                // Fallback (shouldn't happen if BST contained it)
                ConsoleUtils.printBox(new String[]{
                        "Found budget: " + query
                });
            }
        } else {
            ConsoleUtils.printBox(new String[]{
                    "No budget named \"" + query + "\""
            });
        }

        System.out.println("Press Enter to continue.");
        scanner.nextLine();
    }

    private static void showSortedBudgets(User user) {
        ConsoleUtils.clearScreen();
        int size = user.getBudgetList().size();
        if (size == 0) {
            ConsoleUtils.printBox(new String[]{"No budgets to show."});
            System.out.println("Press Enter to continue.");
            scanner.nextLine();
            return;
        }

        // 1) Extract into an array
        Budget[] arr = new Budget[size];
        for (int i = 0; i < size; i++) {
            arr[i] = user.getBudgetList().getAt(i);
        }

        // 2) Sort by limit
        quickSort(arr, 0, size - 1);

        // 3) Build lines for the box
        String[] lines = new String[size + 1];
        lines[0] = "Budgets sorted by Limit:";
        for (int i = 0; i < size; i++) {
            lines[i + 1] = (i + 1)
                    + ") " + arr[i].getName()
                    + "  Limit:" + arr[i].getLimit()
                    + "  Spent:" + arr[i].getSpent();
        }

        // 4) Display
        ConsoleUtils.printBox(lines);
        System.out.println("Press Enter to continue.");
        scanner.nextLine();
    }

    /** Quicksort on arrays of Budget, comparing getLimit() */
    private static void quickSort(Budget[] arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);
            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

    private static int partition(Budget[] arr, int low, int high) {
        double pivot = arr[high].getLimit();
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (arr[j].getLimit() <= pivot) {
                i++;
                Budget temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        // swap pivot into place
        Budget temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;
        return i + 1;
    }

}
