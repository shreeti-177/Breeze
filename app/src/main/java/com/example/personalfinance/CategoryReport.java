package com.example.personalfinance;

import org.joda.time.Days;
import org.joda.time.Months;

public class CategoryReport {
    private String m_Category;
    private boolean m_Expanded;

    private Months m_CurrentMonth;
    private Months m_PreviousMonth;

    private Days m_CurrentMonthDay;
    private Days m_PreviousMonthDay;


    private Double m_PreviousAmount;
    private Double m_CurrentAmount;

    public CategoryReport(String a_Category){
        this.m_Category=a_Category;
        this.m_PreviousAmount=0.0;
        this.m_CurrentAmount=0.0;
    }

    public String GetCategory(){
        return m_Category;
    }

    public boolean isExpanded(){
        return m_Expanded;
    }

    public void SetExpanded(boolean a_Expanded){
        this.m_Expanded=a_Expanded;
    }

    public Double getPreviousAmount() {
        return m_PreviousAmount;
    }

    public void setPreviousAmount(Double m_PreviousAmount) {
        this.m_PreviousAmount = m_PreviousAmount;
    }

    public Double getCurrentAmount() {
        return m_CurrentAmount;
    }

    public void setCurrentAmount(Double m_CurrentAmount) {
        this.m_CurrentAmount = m_CurrentAmount;
    }

    public Months getCurrentMonth() {
        return m_CurrentMonth;
    }

    public void setCurrentMonth(Months m_CurrentMonth) {
        this.m_CurrentMonth = m_CurrentMonth;
    }

    public Months getPreviousMonth() {
        return m_PreviousMonth;
    }

    public void setPreviousMonth(Months m_PreviousMonth) {
        this.m_PreviousMonth = m_PreviousMonth;
    }

    public Days getCurrentMonthDay() {
        return m_CurrentMonthDay;
    }

    public void setCurrentMonthDay(Days m_CurrentMonthDay) {
        this.m_CurrentMonthDay = m_CurrentMonthDay;
    }

    public Days getPreviousMonthDay() {
        return m_PreviousMonthDay;
    }

    public void setPreviousMonthDay(Days m_PreviousMonthDay) {
        this.m_PreviousMonthDay = m_PreviousMonthDay;
    }
}
