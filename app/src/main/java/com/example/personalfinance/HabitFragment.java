//
// Implementation of the HabitFragment class
//
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

    // default onCreate function with a previously saved instance
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**/
    /*
    * NAME
        HabitFragment::onCreateView() - Overrides the default onCreateView function for a fragment

    * SYNOPSIS
        void HomeFragment::onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState);
        * inflater => inflater used to instantiate fragment_habit layout XML to view objects
        * container => group that contains children views
        * savedInstanceState => previous state of the activity

    * DESCRIPTION
        This function will attempt to set the default habits page fragment.
        It will include the toolbar and the bottom navigation bar.
        Then, it will add an expansive section for each of the 10 categories to show a comparison
        of the current and previous month spending

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
        m_RootView = inflater.inflate(R.layout.fragment_habit, container, false);

        RecyclerView m_RecyclerView = m_RootView.findViewById(R.id.habitsRecycler);

        // set layout to display the charts
        LinearLayoutManager m_LinearLayoutManager = new LinearLayoutManager(getContext());
        m_RecyclerView.setLayoutManager(m_LinearLayoutManager);
        m_RecyclerView.setItemAnimator(new DefaultItemAnimator());

        // Set the Habits Adapter to fetch spending habits
        HabitsAdapter m_Adapter = new HabitsAdapter();
        m_RecyclerView.setAdapter(m_Adapter);
        return m_RootView;
    }

}