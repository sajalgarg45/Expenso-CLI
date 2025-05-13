package expensetracker.models;

import expensetracker.datastructures.LinkedList;

public class User {
    private String id, password;
    private LinkedList<Budget> budgets;

    public User(String id, String pw) {
        this.id = id;
        this.password = pw;
        this.budgets = new LinkedList<>();
    }

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String pw) {
        this.password = pw;
    }

    public boolean checkPw(String pw) {
        return password.equals(pw);
    }

    public LinkedList<Budget> getBudgetList() {
        return budgets;
    }
}