//
// Implementation of the Home Activity class
//
package com.example.personalfinance;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeActivity extends AppCompatActivity{

    /**/
    /*
    * NAME
        HomeActivity::onCreate() - Overrides the default onCreate function for a class

    * SYNOPSIS
        void HomeActivity::onCreate(Bundle savedInstanceState);
        * savedInstanceState => previous state of the activity

    * DESCRIPTION
        This function will attempt to set the layout for the homepage.
        It will include the toolbar and the bottom navigation bar for all fragments.
        Then, it will add listeners for each button click on the screen and open the
        activities associated with each button. If there's no new activity to be opened
        for a button click, it will default to the home page fragment

    * AUTHOR
        Shreeti Shrestha

    * DATE
        10:27am, 02/04/2021
    */
    /**/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //On creating activity, set base layout
        setContentView(R.layout.activity_base_home);

        final FragmentManager fragmentManager = getSupportFragmentManager();
        BottomNavigationView m_BottomNavigationView = findViewById(R.id.bottomNavigation);
        FloatingActionButton m_AddBtn = findViewById(R.id.addButton);

        //Set default selection or home page
        m_BottomNavigationView.setSelectedItemId(R.id.home);
        fragmentManager.beginTransaction().replace(R.id.frameLayout, HomePageFragment).commit();

        //Open new page to manually add transactions
        m_AddBtn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),
                CashTransactionActivity.class)));

        // Handle navigation selection
        m_BottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment;
            switch (item.getItemId()) {
                //Navigate to expenses by category page
                case R.id.categories:
                    fragment = CategoryPageFragment;
                    break;
                //Navigate to expense habits page
                case R.id.habits:
                    fragment = HabitPageFragment;
                    break;
                //Navigate to the resources page
                case R.id.resources:
                    fragment = ResourcePageFragment;
                    break;
                //Default to viewing home page
                default:
                fragment = HomePageFragment;
                break;
            }
            fragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit();
            return true;
        });

    } /*protected void onCreate(Bundle savedInstanceState)*/


    //Create new fragment instance for each navigation button
    private final HomeFragment HomePageFragment = new HomeFragment();
    private final CategoryFragment CategoryPageFragment = new CategoryFragment();
    private final HabitFragment HabitPageFragment = new HabitFragment();
    private final ResourceFragment ResourcePageFragment = new ResourceFragment();

}