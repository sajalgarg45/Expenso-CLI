package expensetracker.datastore;

import expensetracker.models.User;
import expensetracker.models.Budget;
import expensetracker.models.Expense;
import expensetracker.datastructures.LinkedList;

import java.io.*;

public class DataStore {
    private static final String DATA_FILE = "data.json";
    private LinkedList<User> users = new LinkedList<>();

    public DataStore() {
        load();
    }

    /** Register and persist */
    public boolean register(User u) {
        for (User ex : users)
            if (ex.getId().equals(u.getId()))
                return false;
        users.add(u);
        save();
        return true;
    }

    /** Authenticate only */
    public User authenticate(String id, String pw) {
        for (User u : users)
            if (u.getId().equals(id) && u.checkPw(pw))
                return u;
        return null;
    }

    /** Delete a user and persist */
    public boolean deleteUser(User u) {
        int before = users.size();
        users.remove(u);
        if (users.size() < before) {
            save();
            return true;
        }
        return false;
    }

    /** Serialize all users→budgets→expenses to data.json */
    public void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(DATA_FILE))) {
            bw.write("[\n");
            int ui = 0, ucount = users.size();
            for (User u : users) {
                bw.write("  {\n");
                bw.write("    \"id\":\"" + escape(u.getId()) + "\",\n");
                bw.write("    \"password\":\"" + escape(u.getPassword()) + "\",\n");
                bw.write("    \"budgets\":[\n");
                int bi = 0, bcount = u.getBudgetList().size();
                for (Budget b : u.getBudgetList()) {
                    bw.write("      {\n");
                    bw.write("        \"name\":\"" + escape(b.getName()) + "\",\n");
                    bw.write("        \"limit\":" + b.getLimit() + ",\n");
                    bw.write("        \"expenses\":[\n");
                    int ei = 0, ecount = b.getExpenses().size();
                    for (Expense e : b.getExpenses()) {
                        bw.write("          {\n");
                        bw.write("            \"desc\":\"" + escape(e.getDesc()) + "\",\n");
                        bw.write("            \"amount\":" + e.getAmount() + "\n");
                        bw.write("          }" + (++ei < ecount ? "," : "") + "\n");
                    }
                    bw.write("        ]\n");
                    bw.write("      }" + (++bi < bcount ? "," : "") + "\n");
                }
                bw.write("    ]\n");
                bw.write("  }" + (++ui < ucount ? "," : "") + "\n");
            }
            bw.write("]\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /** Load from data.json into memory */
    private void load() {
        File f = new File(DATA_FILE);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            User currentUser = null;
            Budget currentBudget = null;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("\"id\"")) {
                    String id = between(line, "\"id\":\"", "\"");
                    currentUser = new User(id, "");
                    users.add(currentUser);
                }
                else if (line.startsWith("\"password\"")) {
                    String pw = between(line, "\"password\":\"", "\"");
                    currentUser.setPassword(pw);
                }
                else if (line.startsWith("\"name\"") && currentUser != null) {
                    String name = between(line, "\"name\":\"", "\"");
                    currentBudget = new Budget(name, 0);
                    currentUser.getBudgetList().add(currentBudget);
                }
                else if (line.startsWith("\"limit\"") && currentBudget != null) {
                    String lim = between(line, "\"limit\":", line.endsWith(",")?"," : "");
                    currentBudget.setLimit(Double.parseDouble(lim));
                }
                else if (line.startsWith("\"desc\"") && currentBudget != null) {
                    String desc = between(line, "\"desc\":\"", "\"");
                    currentBudget.getExpenses().add(new Expense(desc, 0));
                }
                else if (line.startsWith("\"amount\"") && currentBudget != null) {
                    String amt = between(line, "\"amount\":", line.endsWith(",")?"," : "");
                    double a = Double.parseDouble(amt);
                    Expense last = currentBudget.getExpenses().getAt(0);
                    last.setAmount(a);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /** Helpers to parse/write JSON by hand */
    private String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
    private String between(String line, String start, String end) {
        int i = line.indexOf(start) + start.length();
        int j = end.isEmpty() ? line.length() : line.indexOf(end, i);
        return line.substring(i, j);
    }
}