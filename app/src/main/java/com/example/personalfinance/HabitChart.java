package com.example.personalfinance;

import android.graphics.Color;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class HabitChart {
    public HabitChart(BarChart a_BarChart){ this.m_BarChart = a_BarChart;}

    /**/
    /*
    * NAME
        CategoryChart::SetUpPieChart() - Sets up basic formatting for the pie chart

    * SYNOPSIS
        void CategoryChart::SetUpPieChart();

    * DESCRIPTION
        This function will attempt to set up the formatting display for a new chart.
        It will enable features such as zooming into and out of the chart, labels and colors.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        9:15pm, 05/12/2021
    */
    /**/
    public void SetUpBarChart(){
        //hiding the grey background of the chart, default false if not set
        m_BarChart.setDrawGridBackground(false);

        //remove the bar shadow, default false if not set
        m_BarChart.setDrawBarShadow(false);

        //remove border of the chart, default false if not set
        m_BarChart.setDrawBorders(false);

        //remove the description label text located at the lower right corner
        Description description = new Description();
        m_BarChart.setDescription(description);
        description.setEnabled(false);

        //setting animation for y-axis, the bar will pop up from 0 to its value within the time we set
        m_BarChart.animateY(1000);

        //setting animation for x-axis, the bar will pop up separately within the time we set
        m_BarChart.animateX(1000);

        XAxis xAxis = m_BarChart.getXAxis();
        //change the position of x-axis to the bottom
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //set the horizontal distance of the grid line
        xAxis.setGranularity(1f);
        //hiding the vertical grid lines, default true if not set
        xAxis.setDrawGridLines(false);

        m_BarChart.getAxisLeft().setDrawGridLines(false);
        m_BarChart.getXAxis().setDrawGridLines(false);

        YAxis leftAxis = m_BarChart.getAxisLeft();
        //hiding the left y-axis line, default true if not set
        leftAxis.setDrawAxisLine(false);

        YAxis rightAxis = m_BarChart.getAxisRight();
        //hiding the right y-axis line, default true if not set
        rightAxis.setDrawAxisLine(false);

        Legend legend = m_BarChart.getLegend();
        legend.setEnabled(false);
    }


    /**/
    /*
    * NAME
        CategoryChart::LoadPieChart() - Loads values for the chart

    * SYNOPSIS
        void CategoryChart::LoadPieChart(List<Data> a_MonthlyDataList);
        * a_SummaryList => list of all expense data objects for the month

    * DESCRIPTION
        This function will attempt to load plot values for the chart. It will not have any null
        values since the Category fragment ensures the validity of the values before passing the
        list to this function.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        9:25pm, 05/13/2021
    */
    /**/
    public void LoadBarChart(CategoryReport a_Category) {
        String a_Title = "Budget Report";
        List<BarEntry> m_Entries = new ArrayList<>();

        List<Float> a_Expenses = new ArrayList<>();
        a_Expenses.add(a_Category.getPreviousAmount().floatValue());
        a_Expenses.add(a_Category.getCurrentAmount().floatValue());


        List<String> a_XAxisLabel = new ArrayList<>();
        a_XAxisLabel.add(new DateTime().minusMonths(1).toString("MMM-yyyy"));
        a_XAxisLabel.add(new DateTime().toString("MMM-yyyy"));

        for (int i =0;i<2;i++){
            BarEntry barEntry=new BarEntry(i,a_Expenses.get(i));
            m_Entries.add(barEntry);
        }


        XAxis a_XAxis = m_BarChart.getXAxis();
        m_BarChart.getAxisLeft().setDrawLabels(true);

        a_XAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return a_XAxisLabel.get((int) value);
            }
        });


        BarDataSet m_BarDataSet = new BarDataSet(m_Entries,a_Title);
        BarData a_Data = new BarData(m_BarDataSet);
        m_BarDataSet.setColor(Color.parseColor("#ADD8E6"));

        a_Data.setBarWidth(0.5f);

        //Setting the size of the form in the legend
        m_BarDataSet.setFormSize(10f);

        //showing the value of the bar, default true if not set
        m_BarDataSet.setDrawValues(true);

        //setting the text size of the value of the bar
        m_BarDataSet.setValueTextSize(12f);
        m_BarChart.setData(a_Data);
        m_BarChart.invalidate();
    }
    private final BarChart m_BarChart;
}








