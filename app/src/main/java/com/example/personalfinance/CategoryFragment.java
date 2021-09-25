//
// Implementation of the CategoryFragment class
// This class provides an interface to view a category breakdown of all expenses for the month
//
package com.example.personalfinance;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class CategoryFragment extends Fragment {

    public CategoryFragment() {
        // Required empty public constructor
    }

    // default onCreate function with a previously saved instance
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**/
    /*
    * NAME
        CategoryFragment::onCreateView() - Overrides the default onCreateView function for a fragment

    * SYNOPSIS
        void CategoryFragment::onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState);
        * inflater => inflater used to instantiate fragment_category layout XML to view objects
        * container => group that contains children views
        * savedInstanceState => previous state of the activity

    * DESCRIPTION
        This function will attempt to set the default category fragment page.
        It will include the toolbar and the bottom navigation bar.
        Then, it will add a pie chart for the top half, and a list of categories in the bottom half.
        Once it sets up the base display, it sets up an adapter to listen to any data changes and update
        the chart and the list accordingly.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        12:37pm, 08/04/2021
    */
    /**/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View m_RootView = inflater.inflate(R.layout.fragment_category, container, false);

        RecyclerView m_CategoryView = m_RootView.findViewById(R.id.categoryExpenses);
        LinearLayoutManager m_LinearLayoutManager = new LinearLayoutManager(getContext());
        m_CategoryView.setLayoutManager(m_LinearLayoutManager);
        m_CategoryView.setItemAnimator(new DefaultItemAnimator());

        // Set up adapter to listen to data changes
        m_Adapter = new ExpenseAdapter();
        m_CategoryView.setAdapter(m_Adapter);

        // Set up pie chart
        PieChart pieChart= m_RootView.findViewById(R.id.pieChart);


        // Notify adapter on data change
        Util.GetExpenseReference().orderByChild("month").equalTo(Util.GetMonth().getMonths())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                monthlyDataList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Data data = dataSnapshot.getValue(Data.class);
                    monthlyDataList.add(data);
                    assert data != null;
                    totalMonthlyExpense+=data.GetAmount();
                }
                CategoryChart categoryChart = new CategoryChart(pieChart);
                categoryChart.SetUpPieChart();
                categoryChart.LoadPieChart(monthlyDataList);
                m_Adapter.SetExpenses(monthlyDataList);
                m_Adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.e("Error fetching expense data",error.getMessage(),error.toException());
            }
        });
        return m_RootView;
    } /* public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) */

    private ExpenseAdapter m_Adapter;
    private final List<Data> monthlyDataList=new ArrayList<>();
    private Double totalMonthlyExpense = 0.0;
}