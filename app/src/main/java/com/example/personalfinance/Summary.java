package com.example.personalfinance;

public class Summary {
    private String month;
    private int numMonth;
    private Double budget;
    private Double expense;

    public Summary(){}

    public Summary(String month, Double expense){
        this.month=month;
        this.expense=expense;
    }

    public Summary(String month, int numMonth, Double expense){
        this.month=month;
        this.numMonth=numMonth;
        this.expense=expense;
    }

    public int getNumMonth() {
        return numMonth;
    }

    public void SetNumMonth(int numMonth){
        this.numMonth=numMonth;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public Double getExpense() {
        return expense;
    }

    public void setExpense(Double expense) {
        this.expense = expense;
    }
}
