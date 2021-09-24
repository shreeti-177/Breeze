//
// Implementation of the PlansActivity class
//
package com.example.personalfinance;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class PlansActivity extends AppCompatActivity {

    /**/
    /*
    * NAME
        PlansActivity::onCreate() - Overrides the default onCreate function for the class

    * SYNOPSIS
        void HomeActivity::onCreate(Bundle savedInstanceState);
        * savedInstanceState => previous state of the activity

    * DESCRIPTION
        This function will attempt to set the layout for showing user's current plans, and allow to
        set a new goal. In the event that there are no plans set, it will just show the option to
        set a new goal. When user clicks on Add New Goal, it will invoke another page to enter field
        values.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        10:27am, 06/08/2021
    */
    /**/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plans);

        RecyclerView m_RecyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager m_LinearLayoutManager = new LinearLayoutManager(this);
        m_RecyclerView.setLayoutManager(m_LinearLayoutManager);

        //Fetch plans data stored in Firebase
        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(m_PlansRef, Data.class)
                .build();


        m_Adapter = new PlansAdapter(options);
        m_RecyclerView.setAdapter(m_Adapter);

        ExtendedFloatingActionButton m_AddNewGoal = findViewById(R.id.addGoalBtn);
//        ExtendedFloatingActionButton m_AddSavings = findViewById(R.id.addSavingsBtn);
//
//        m_AddSavings.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(), SavingActivity.class));
//            }
//        });
        m_AddNewGoal.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),
                NewGoalActivity.class)));
    }/* protected void onCreate(Bundle savedInstanceState) */

    // Constantly keep listening for any new changes once the activity is started
    @Override
    public void onStart() {
        super.onStart();
        m_Adapter.startListening();
    }

    // Stop listening once user navigates to another activity
    @Override
    protected void onStop() {
        super.onStop();
        m_Adapter.stopListening();
    }

    private final FirebaseAuth m_Auth = FirebaseAuth.getInstance();
    private final String m_Uid = Objects.requireNonNull(m_Auth.getCurrentUser()).getUid();
    private final DatabaseReference m_PlansRef= FirebaseDatabase.getInstance().getReference().child("plans").child(m_Uid);
    private PlansAdapter m_Adapter;
}
