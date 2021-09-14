package com.example.personalfinance;

import android.graphics.Color;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CategoryChart {
    public CategoryChart(PieChart a_PieChart){
        this.m_PieChart = a_PieChart;
    }

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
    public void SetUpPieChart(){

        m_PieChart.setDrawHoleEnabled(true);
        m_PieChart.setUsePercentValues(true);
        m_PieChart.setEntryLabelTextSize(12);
        m_PieChart.setEntryLabelColor(Color.BLACK);
        m_PieChart.getDescription().setEnabled(false);
        m_PieChart.setDrawEntryLabels(false);


        Legend legend = m_PieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);
        legend.setEnabled(true);
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
    public void LoadPieChart(List<Data> a_MonthlyDataList){
        ArrayList<PieEntry> entries = new ArrayList<>();
        Map<String, Double> expenseByCategory = Util.GetCategoricalExpense(a_MonthlyDataList);

        for (String name: expenseByCategory.keySet()){
            float spending = Objects.requireNonNull(expenseByCategory.get(name)).floatValue();
            if(spending<0){
                continue;
            }
            entries.add(new PieEntry(Objects.requireNonNull(expenseByCategory.get(name))
                    .floatValue(), name));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Expense Category");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.removeLast();
        dataSet.setLabel("");

        PieData data = new PieData(dataSet);
        data.setDrawValues(false);

        data.setValueFormatter(new PercentFormatter(m_PieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setValueLinePart1OffsetPercentage(1f);
        dataSet.setValueLinePart1Length(0.2f);
        dataSet.setValueLinePart2Length(0.05f);

        m_PieChart.setData(data);
        m_PieChart.invalidate();
        m_PieChart.animateY(1400, Easing.EaseInOutQuad);
    }

    private final PieChart m_PieChart;
}



