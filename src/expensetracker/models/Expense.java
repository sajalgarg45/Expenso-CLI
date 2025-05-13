package expensetracker.models;

public class Expense {
    private String desc;
    private double amount;

    public Expense(String d, double a) {
        this.desc = d;
        this.amount = a;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String d) {
        this.desc = d;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double a) {
        this.amount = a;
    }
}