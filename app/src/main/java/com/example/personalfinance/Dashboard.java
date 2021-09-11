//
// Implementation of Dashboard class
//
package com.example.personalfinance;

import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

public class Dashboard {

    public Dashboard(LineChart a_LineChart){
        this.m_LineChart=a_LineChart;
    }

    /**/
    /*
    * NAME
        Dashboard::SetUpLineChart() - Sets up basic formatting for the line chart

    * SYNOPSIS
        void Dashboard::SetUpLineChart(String a_Description);
        * a_Description => description of the chart

    * DESCRIPTION
        This function will attempt to set up the formatting display for a new chart.
        It will enable features such as zooming into and out of the chart, showing axis and
        gridlines, and the legend for the chart.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        9:15pm, 02/12/2021
    */
    /**/

    public void SetUpLineChart(String a_Description){

        m_LineChart.getDescription().setEnabled(false);

        //Enable zooming in/out of the chart
        m_LineChart.setTouchEnabled(true);
        m_LineChart.setPinchZoom(true);

        //Set plain background for chart
        m_LineChart.setDrawGridBackground(false);

        //Disable right axis
        m_LineChart.getAxisRight().setEnabled(false);

        //Disable gridlines
        m_LineChart.getAxisLeft().setDrawGridLines(false);
        m_LineChart.getXAxis().setDrawGridLines(false);

        //Set legend disable or enable to hide {the left down corner name of graph}
        Legend legend = m_LineChart.getLegend();
        legend.setEnabled(false);
    }/* public void SetUpLineChart(String a_Description)*/


    /**/
    /*
    * NAME
        Dashboard::LoadLineChart() - Loads x-y plot values for the chart

    * SYNOPSIS
        void Dashboard::LoadLineChart(List<Summary> a_SummaryList);
        * a_SummaryList => list of summary type values (which include month/year, expense, budget)

    * DESCRIPTION
        This function will attempt to load plot values for the chart. It will not have any null
        values since the home fragment ensures the validity of the values before passing the summary
        list to this function.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        9:25pm, 02/13/2021
    */
    /**/

    public void LoadLineChart(List<Summary> a_SummaryList){

        ArrayList<Entry> entryArrayList = new ArrayList<>();

        //Add (x,y) plot values to entryArrayList
        List<String> a_XAxisLabel = new ArrayList<>();
        for (int i=0;i<a_SummaryList.size();i++){
            float y_value = a_SummaryList.get(i).getExpense().floatValue();
            a_XAxisLabel.add(a_SummaryList.get(i).getMonth());
            entryArrayList.add(new Entry(i, y_value));
        }

        XAxis a_XAxis = m_LineChart.getXAxis();
        m_LineChart.getAxisLeft().setDrawLabels(false);

        //Set string month values for X-axis
        a_XAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return a_XAxisLabel.get((int) value);
            }
        });

        a_XAxis.setLabelRotationAngle(-45f);
        a_XAxis.setGranularityEnabled(true);

        a_XAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        //LineDataSet is the line on the graph
        LineDataSet lineDataSet = new LineDataSet(entryArrayList, "These are y values");

        ArrayList<ILineDataSet> iLineDataSetArrayList = new ArrayList<>();
        iLineDataSetArrayList.add(lineDataSet);

        //LineData is the data accord
        LineData lineData = new LineData(iLineDataSetArrayList);
        lineData.setValueTextSize(8f);
        lineData.setValueTextColor(Color.BLACK);

        lineDataSet.setCircleHoleColor(Color.WHITE);
        lineDataSet.setCircleColor(Color.BLACK);
        lineDataSet.setHighLightColor(Color.YELLOW);
        lineDataSet.setDrawValues(true);
        lineDataSet.setCircleRadius(5f);

        //To make the line smooth for a smooth curve
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        //To enable the cubic density : if 1 then it will be sharp curve
        lineDataSet.setCubicIntensity(0.2f);

        lineDataSet.setDrawFilled(false);

        //Set transparency
        lineDataSet.setFillAlpha(80);

        m_LineChart.setData(lineData);
        m_LineChart.invalidate();

    }/* public void LoadLineChart(List<Summary> a_SummaryList) */

    private final LineChart m_LineChart;
}
