//
// Implementation of the Data class
// This class implements basic properties for a budget, transaction and a goal
//

package com.example.personalfinance;

public class Data {

    //default empty constructor
    public Data(){}

    /**/
    /*
    * NAME
        Data::Data() - Constructor to initialize member variables for a budget

    * SYNOPSIS
        Data::Data(String a_Id, String a_Category, Double a_Amount, String a_Date, int a_Month, int a_Day);
        * a_Id => id to be set
        * a_Category => category name passed to the function
        * a_Amount => amount passed to the function
        * a_Month => number of months elapsed
        * a_Day => number of days elapsed

    * DESCRIPTION
        This function will create a new data object for budget and initialize the member variables with the
        values passed as arguments.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        9:00am, 03/11/2021
    */
    /**/
    public Data(String a_Id, String a_Category, Double a_Amount, String a_Date, int a_Month, int a_Day){
        this.id = a_Id;
        this.category=a_Category;
        this.amount=a_Amount;
        this.date=a_Date;
        this.month=a_Month;
        this.day=a_Day;
    }/* Data(String a_Id, String a_Category, Double a_Amount, String a_Date, int a_Month, int a_Day); */


    /**/
    /*
    * NAME
        Data::Data() - Constructor to initialize member variables for a transaction

    * SYNOPSIS
        Data::Data(String a_Id, String a_Category, String a_Merchant, Double a_Amount, String a_Date, int a_Month, int a_Day, String a_Notes);
        * a_Id => id to be set
        * a_Category => category name passed to the function
        * a_Merchant => merchant for the transaction
        * a_Amount => amount passed to the function
        * a_Date => date that the transaction occurred
        * a_Month => number of months elapsed
        * a_Day => number of days elapsed
        * a_Notes => any additional notes from the user

    * DESCRIPTION
        This function will create a new data object for a transaction and initialize the member variables with the
        values passed as arguments.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        9:20am, 03/11/2021
    */
    /**/
    public Data(String a_Id, String a_Category, String a_Merchant, Double a_Amount, String a_Date, int a_Month, int a_Day, String a_Notes){
        this.id = a_Id;
        this.merchant=a_Merchant;
        this.category=a_Category;
        this.amount=a_Amount;
        this.date=a_Date;
        this.month=a_Month;
        this.day=a_Day;
        this.notes=a_Notes;
    } /* Data(String a_Id, String a_Category, String a_Merchant, Double a_Amount, String a_Date, int a_Month, int a_Day, String a_Notes) */


    /**/
    /*
    * NAME
        Data::Data() - Constructor to initialize member variables for a goal

    * SYNOPSIS
        Data::Data(String a_Id, String a_GoalName,Double a_GoalBudget, String a_Date, int a_Month, int a_Day, String a_Notes);
        * a_Id => id to be set
        * a_GoalName => goal name passed to the function
        * a_GoalBudget => budget amount for the specified goal
        * a_Date => date that the goal was set
        * a_Month => the month number at the time the goal was set
        * a_Day => the day (since 1969) at the time the goal was set
        * a_Notes => any additional notes from the user

    * DESCRIPTION
        This function will create a new data object for a new goal and initialize the member variables with the
        values passed as arguments.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        9:20am, 03/11/2021
    */
    /**/
    public Data(String a_Id, String a_GoalName,Double a_GoalBudget, String a_Date, int a_Month, int a_Day, String a_Notes){
        this.id=a_Id;
        this.goal=a_GoalName;
        this.goalCategory="";
        this.goalBudget=a_GoalBudget;
        this.date=a_Date;
        this.month=a_Month;
        this.day=a_Day;
        this.notes=a_Notes;
    } /*  public Data(String a_Id, String a_GoalName,Double a_GoalBudget, String a_Date, int a_Month, int a_Day, String a_Notes) */

    // Getters and Setters for all member variables
//    public String getCategory() {
//        return category;
//    }
//
//    public void setCategory(String category) {
//        this.category = category;
//    }
//
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public Double getAmount() {
//        return amount;
//    }
//
//    public void setAmount(Double amount) {
//        this.amount = amount;
//    }
//
//    public String getDate() {
//        return date;
//    }
//
//    public void setDate(String date) {
//        this.date = date;
//    }
//
//    public Double getSavings() {
//        return savings;
//    }
//
//    public void setSavings(Double savings) {
//        this.savings = savings;
//    }
//
//    public int getMonth() {
//        return month;
//    }
//
//    public void setMonth(int month) {
//        this.month = month;
//    }
//
//    public int getDay() {
//        return day;
//    }
//
//    public void setDay(int day) {
//        this.day = day;
//    }
//
//    public String getMerchant() {
//        return merchant;
//    }
//
//    public void setMerchant(String merchant) {
//        this.merchant = merchant;
//    }
//
//    public String getNotes() {
//        return notes;
//    }
//
//    public void setNotes(String notes) {
//        this.notes = notes;
//    }
//
//    public String getGoal() {
//        return goal;
//    }
//
//    public void setGoal(String goal) {
//        this.goal = goal;
//    }
//
//    public String getGoalCategory() {
//        return goalCategory;
//    }
//
//    public void setGoalCategory(String goalCategory) {
//        this.goalCategory = goalCategory;
//    }
//
//    public Double getGoalBudget() {
//        return goalBudget;
//    }
//
//    public void setGoalBudget(Double goalBudget) {
//        this.goalBudget = goalBudget;
//    }


    public String GetCategory() {
        return category;
    }

    public void SetCategory(String a_category) {
        this.category = a_category;
    }

    public String GetId() {
        return id;
    }

    public void SetId(String a_id) {
        this.id = a_id;
    }

    public Double GetAmount() {
        return amount;
    }

    public void SetAmount(Double a_amount) {
        this.amount = a_amount;
    }

    public String GetDate() {
        return date;
    }

    public void SetDate(String a_date) {
        this.date = a_date;
    }

    public int GetMonth() {
        return month;
    }

    public void SetMonth(int a_month) {
        this.month = a_month;
    }

    public int GetDay() { return day; }

    public void SetDay(int a_day) { this.day = a_day; }

    public String GetMerchant() {
        return merchant;
    }

    public void SetMerchant(String a_merchant) {
        this.merchant = a_merchant;
    }

    public String GetNotes() {
        return notes;
    }

    public void SetNotes(String a_notes) {
        this.notes = a_notes;
    }

    public String GetGoal() {
        return goal;
    }

    public void SetGoal(String a_goal) {
        this.goal = a_goal;
    }

    public String GetGoalCategory() {
        return goalCategory;
    }

    public void SetGoalCategory(String a_goalCategory) {
        this.goalCategory = a_goalCategory;
    }

    public Double GetGoalBudget() {
        return goalBudget;
    }

    public void SetGoalBudget(Double a_goalBudget) {
        this.goalBudget = a_goalBudget;
    }

    public void SetSavings(Double a_savings){ this.savings = a_savings; }

    public Double GetSavings(){ return savings;}

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
    private Double savings;

}
