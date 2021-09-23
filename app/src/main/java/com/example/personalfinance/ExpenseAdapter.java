package com.example.personalfinance;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {
    List<Data> m_ExpenseList= Collections.emptyList();
    Map<String, List<Data>> m_Expenses=new HashMap<>();
    Map<String,Double> m_Categories = new HashMap<>();
    Map<String, Double> m_Budget = new HashMap<>();

    public void SetExpenses(List<Data> monthlyDataList){
        this.m_ExpenseList = monthlyDataList;
        this.m_Categories= Util.GetCategoricalExpense(monthlyDataList);
        if (!m_ExpenseList.isEmpty()) {
            for (Data a_Transaction : monthlyDataList) {
                String a_Category = a_Transaction.getCategory();
                m_Expenses.computeIfAbsent(a_Category, k -> new ArrayList<>()).add(a_Transaction);
            }
        }
        for (String a_Category : Util.GetExistingCategories()) {
            if (!m_Expenses.containsKey(a_Category)) {
                m_Expenses.put(a_Category, new ArrayList<>());
            }
        }
    }
    public ExpenseAdapter() {
        int currentMonth = Util.getMonth().getMonths();
        Util.GetBudgetReference().orderByChild("month").equalTo(currentMonth).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Data data = dataSnapshot.getValue(Data.class);
                    assert data != null;
                    m_Budget.put(data.getCategory(), data.getAmount());
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.e("Error fetching budget data",error.getMessage(),error.toException());
            }
        });
    }

    @NonNull
    @NotNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_category, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ExpenseViewHolder holder, int position) {
        if(m_Categories.size()==0){
            String noSpendingText = "No spending for this month so far!";
            holder.SetCategoryName(noSpendingText);
            return;
        }

        String key = String.valueOf(m_Categories.keySet().toArray()[position]);
        holder.SetCategoryName(key);
        holder.SetCategoryImage(key);

        double value = Math.round(m_Categories.get(key)*100.0)/100.0;
        if(value<=0.0){
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(holder.params);
        }

        holder.SetCategoryExpense(String.valueOf(value));
        Double budget = m_Budget.get(key);
        if(budget==null){
            budget=0.0;
        }
        holder.SetCategoryBudget(String.valueOf(budget));

        Integer numTransactions = Objects.requireNonNull(m_Expenses.get(key)).size();
        holder.SetNumTransactions(String.valueOf(numTransactions));

        if(value<= (0.5*budget)){
            holder.greenAlert.setColorFilter(Color.parseColor("#00FF00"));
        }
        else if(value>(0.5*budget) && value <= (0.75*budget)){
            holder.greenAlert.setColorFilter(Color.YELLOW);
            holder.yellowAlert.setColorFilter(Color.YELLOW);
        }
        else{
            holder.greenAlert.setColorFilter(Color.RED);
            holder.yellowAlert.setColorFilter(Color.RED);
            holder.redAlert.setColorFilter(Color.RED);
        }
    }



    @Override
    public int getItemCount() {
        return m_Categories.size();
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        ImageView redAlert;
        ImageView yellowAlert;
        ImageView greenAlert;
        public LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 0);


        public ExpenseViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            redAlert = itemView.findViewById(R.id.redAlert);
            yellowAlert = itemView.findViewById(R.id.yellowAlert);
            greenAlert = itemView.findViewById(R.id.greenAlert);
        }

        private void SetCategoryName(String a_CategoryName) {
            TextView categoryName = itemView.findViewById(R.id.categoryName);
            categoryName.setText(a_CategoryName);
        }

        private void SetCategoryImage(String a_CategoryName) {
            int imageId = Util.SetCategoryIcon(a_CategoryName);
            ImageView categoryImage = itemView.findViewById(R.id.categoryIcon);
            categoryImage.setImageResource(imageId);
        }

        private void SetCategoryBudget(String a_CategoryBudget) {
            TextView categoryBudget = itemView.findViewById(R.id.categoryBudget);
            categoryBudget.setText("Budget: " + a_CategoryBudget);
        }

        private void SetCategoryExpense(String a_CategoryExpense) {
            TextView categoryBudget = itemView.findViewById(R.id.categoryExpense);
            categoryBudget.setText("Expenses: " + a_CategoryExpense);
        }

        private void SetNumTransactions(String a_NumTransactions) {
            TextView numOfTransactions = itemView.findViewById(R.id.categoryTransactions);
            numOfTransactions.setText(a_NumTransactions + " transactions");
        }
    }
}
