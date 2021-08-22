package com.example.personalfinance;

public class Data {
    private String category;
    private String id;
    private Double amount;
    private String date;

    private int month;
    private int day;
    private String merchant;
    private String notes;

    private String goal;
    private String goalCategory;
    private Double goalBudget;

    public Data(){}

    public Data(String a_Id, String a_Category, Double a_Amount, String a_Date, int a_Month, int a_Day){
        this.id = a_Id;
        this.category=a_Category;
        this.amount=a_Amount;
        this.date=a_Date;
        this.month=a_Month;
        this.day=a_Day;
    }

    public Data(String a_Id, String a_Category, String a_Merchant, Double a_Amount, String a_Date, int a_Month, int a_Day, String a_Notes){
        this.id = a_Id;
        this.category=a_Category;
        this.amount=a_Amount;
        this.date=a_Date;
        this.month=a_Month;
        this.day=a_Day;
        this.notes=a_Notes;
    }

    public Data(String a_Id, String a_GoalName,Double a_GoalBudget, String a_Date, int a_Month, int a_Day, String a_Notes){
        this.id=a_Id;
        this.goal=a_GoalName;
        this.goalCategory="";
        this.goalBudget=a_GoalBudget;
        this.date=a_Date;
        this.month=a_Month;
        this.day=a_Day;
        this.notes=a_Notes;
    }


    public int getDay() { return day; }

    public void setDay(int day) { this.day = day; }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getGoalCategory() {
        return goalCategory;
    }

    public void setGoalCategory(String goalCategory) {
        this.goalCategory = goalCategory;
    }

    public Double getGoalBudget() {
        return goalBudget;
    }

    public void setGoalBudget(Double goalBudget) {
        this.goalBudget = goalBudget;
    }
}
