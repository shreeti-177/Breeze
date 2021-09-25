//
// Implementation of the Summary class
// This class implements a basic summary of expenses
//
package com.example.personalfinance;

public class Summary {
    private String month;
    private int numMonth;
    private Double budget;
    private Double expense;

    //default constructor
    public Summary(){}

    /**/
    /*
    * NAME
        Summary::Summary() - Constructor to initialize month and expense

    * SYNOPSIS
        Summary::Summary(String month, Double expense);
        * month => current month in "MMM-YYYY" format
        * expense => total expense for current month

    * DESCRIPTION
        This function will create a new summary object and initialize the month and expense member
        variables

    * AUTHOR
        Shreeti Shrestha

    * DATE
        9:00am, 05/12/2021
    */
    /**/
    public Summary(String month, Double expense){
        this.month=month;
        this.expense=expense;
    } /* public Summary(String month, Double expense) */


    /**/
    /*
    * NAME
        Summary::Summary() - Constructor to initialize month, numMonth and expense

    * SYNOPSIS
        Summary::Summary(String month, int numMonth, Double expense);
        * month => current month in "MMM-YYYY" format
        * numMonth => number of months elapsed since 1969
        * expense => total expense for current month

    * DESCRIPTION
        This function will create a new summary object and initialize the month, number of months elapsed
        and expense member variables

    * AUTHOR
        Shreeti Shrestha

    * DATE
        9:00am, 05/12/2021
    */
    /**/
    public Summary(String month, int numMonth, Double expense){
        this.month=month;
        this.numMonth=numMonth;
        this.expense=expense;
    } /* public Summary(String month, int numMonth, Double expense) */

    //
    // Getters and Setters for all member variables
    //
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
