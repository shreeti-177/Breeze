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

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;
import org.joda.time.Months;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HabitFragment extends Fragment {
    View m_RootView;


    public HabitFragment() {
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
        m_RootView = inflater.inflate(R.layout.fragment_habit, container, false);

        m_RecyclerView = m_RootView.findViewById(R.id.habitsRecycler);

        LinearLayoutManager m_LinearLayoutManager = new LinearLayoutManager(getContext());
        m_RecyclerView.setLayoutManager(m_LinearLayoutManager);
        m_RecyclerView.setItemAnimator(new DefaultItemAnimator());

        m_Adapter = new HabitsAdapter();
        m_RecyclerView.setAdapter(m_Adapter);
        return m_RootView;
    }

    private RecyclerView m_RecyclerView;
    private HabitsAdapter m_Adapter;
}