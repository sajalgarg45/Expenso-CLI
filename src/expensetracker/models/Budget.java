package expensetracker.models;

import expensetracker.ds.LinkedList;

public class Budget {
    private String name;
    private double limit;
    private LinkedList<Expense> expenses;

    public Budget(String name, double lim) {
        this.name = name;
        this.limit = lim;
        this.expenses = new LinkedList<>();
    }
    public String getName() { return name; }
    public void setName(String n) { this.name = n; }
    public double getLimit() { return limit; }
    public void setLimit(double lim) { this.limit = lim; }
    public void addExpense(Expense e) { expenses.add(e); }
    public double getSpent() { return expenses.sum(x -> x.getAmount()); }
    public LinkedList<Expense> getExpenses() { return expenses; }
}
