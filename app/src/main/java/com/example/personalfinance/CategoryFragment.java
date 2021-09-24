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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View m_RootView = inflater.inflate(R.layout.fragment_category, container, false);

        RecyclerView m_CategoryView = m_RootView.findViewById(R.id.categoryExpenses);
        LinearLayoutManager m_LinearLayoutManager = new LinearLayoutManager(getContext());
        m_CategoryView.setLayoutManager(m_LinearLayoutManager);
        m_CategoryView.setItemAnimator(new DefaultItemAnimator());

        m_Adapter = new ExpenseAdapter();
        m_CategoryView.setAdapter(m_Adapter);

        PieChart pieChart= m_RootView.findViewById(R.id.pieChart);


        Util.GetExpenseReference().orderByChild("month").equalTo(Util.getMonth().getMonths())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                monthlyDataList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Data data = dataSnapshot.getValue(Data.class);
                    monthlyDataList.add(data);
                    assert data != null;
                    totalMonthlyExpense+=data.getAmount();
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
    }

    private ExpenseAdapter m_Adapter;
    private final List<Data> monthlyDataList=new ArrayList<>();
    private Double totalMonthlyExpense = 0.0;
}