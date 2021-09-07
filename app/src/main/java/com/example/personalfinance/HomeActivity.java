package com.example.personalfinance;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import org.jetbrains.annotations.NotNull;

public class HomeActivity extends AppCompatActivity{

    BottomNavigationView m_BottomNavigationView;
    FloatingActionButton m_AddBtn;
    ExtendedFloatingActionButton m_AddBudgetBtn;
    ExtendedFloatingActionButton m_AddTransactionsBtn;
    ExtendedFloatingActionButton m_AddGoalsBtn;

    private Boolean expanded = false;

    private HomeFragment HomePageFragment = new HomeFragment();
    private CategoryFragment CategoryPageFragment = new CategoryFragment();
    private HabitFragment HabitPageFragment = new HabitFragment();
    private ResourceFragment ResourcePageFragment = new ResourceFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home_page_bottom_navigation);
        final FragmentManager fragmentManager = getSupportFragmentManager();

        m_BottomNavigationView = findViewById(R.id.bottomNavigation);
        m_AddBtn=findViewById(R.id.addButton);
        m_AddBudgetBtn=findViewById(R.id.addBudget);
        m_AddTransactionsBtn=findViewById(R.id.addTransactions);
        m_AddGoalsBtn=findViewById(R.id.addGoals);

        m_AddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expanded=!expanded;
                m_AddBtn.setImageResource(expanded?R.drawable.ic_baseline_cancel_24:R.drawable.icon_add);
                m_AddBudgetBtn.setVisibility(expanded?m_AddBudgetBtn.VISIBLE: m_AddBudgetBtn.GONE);
                m_AddTransactionsBtn.setVisibility(expanded?m_AddTransactionsBtn.VISIBLE: m_AddTransactionsBtn.GONE);
                m_AddGoalsBtn.setVisibility(expanded?m_AddGoalsBtn.VISIBLE: m_AddGoalsBtn.GONE);
            }
        });

        m_AddBudgetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),BudgetActivity.class));
            }
        });

        m_AddTransactionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),CashTransactionActivity.class));
            }
        });
        m_AddGoalsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),PlansActivity.class));
            }
        });

        // Set default selection
        m_BottomNavigationView.setSelectedItemId(R.id.home);

        fragmentManager.beginTransaction().replace(R.id.frameLayout, HomePageFragment).commit();


        // handle navigation selection
        m_BottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.home:
                        fragment = HomePageFragment;
                        break;
                    case R.id.categories:
                        fragment = CategoryPageFragment;
                        break;
                    case R.id.habits:
                        fragment = HabitPageFragment;
                        break;
                    case R.id.resources:
                        fragment = ResourcePageFragment;
                        break;
                    default:
                    fragment = HomePageFragment;
                    break;
                }
                fragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit();
                return true;
            }
        });

    }


}