package com.example.personalfinance;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

        RecyclerView m_RecyclerView = m_RootView.findViewById(R.id.habitsRecycler);

        LinearLayoutManager m_LinearLayoutManager = new LinearLayoutManager(getContext());
        m_RecyclerView.setLayoutManager(m_LinearLayoutManager);
        m_RecyclerView.setItemAnimator(new DefaultItemAnimator());

        HabitsAdapter m_Adapter = new HabitsAdapter();
        m_RecyclerView.setAdapter(m_Adapter);
        return m_RootView;
    }

}