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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {
    List<Data> m_ExpenseList= Collections.emptyList();
    Map<String, List<Data>> m_Expenses=new HashMap<>();
    Map<String,Double> m_Categories = new HashMap<>();
    Map<String, Double> m_Budget = new HashMap<>();

    public void SetExpenses(List<Data> monthlyDataList){
        Double tAmount=0.0;
        this.m_ExpenseList = monthlyDataList;
        this.m_Categories= Util.GetCategoricalExpense(monthlyDataList);
        if (!m_ExpenseList.isEmpty()) {
            for (Data a_Transaction : monthlyDataList) {
                String a_Category = a_Transaction.getCategory();
                Log.i("Category", a_Category);
                Log.i("Transaction", String.valueOf(a_Transaction));
                m_Expenses.computeIfAbsent(a_Category, k -> new ArrayList<>()).add(a_Transaction);
            }
        }
        for (String a_Category : Util.GetExistingCategories()) {
            if (!m_Expenses.containsKey(a_Category)) {
                m_Expenses.put(a_Category, new ArrayList<>());
            }
        }
        Log.i(TAG, "Category Breakdown: Success");


    }
    public ExpenseAdapter() {
        FirebaseAuth m_Auth = FirebaseAuth.getInstance();
        String a_Uid = Objects.requireNonNull(m_Auth.getCurrentUser()).getUid();
        Integer currentMonth = Util.getMonth().getMonths();
        DatabaseReference m_BudgetRef = FirebaseDatabase.getInstance().getReference().child("budget").child(a_Uid);

        m_BudgetRef.orderByChild("month").equalTo(currentMonth).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Data data = dataSnapshot.getValue(Data.class);
                    m_Budget.put(data.getCategory(), data.getAmount());
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {}
        });
    }

    @NonNull
    @NotNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_category, parent, false);
        ExpenseViewHolder myViewHolder = new ExpenseViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ExpenseViewHolder holder, int position) {
        if(m_Categories.size()==0){
            holder.a_CategoryName.setText("No spending for this month so far!");
            return;
        }
        String key = String.valueOf(m_Categories.keySet().toArray()[position]);

        Double value = Math.round(m_Categories.get(key)*100.0)/100.0;
        if(value<=0.0){
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(holder.params);
        }

        String category = String.valueOf(key);
        Double expense=value;
        Double budget = m_Budget.get(key);
        Integer numTransactions = m_Expenses.get(key).size();

        if(budget==null){
            budget=0.0;
        }



        ImageView a_CategoryIcon = holder.a_CategoryIcon;
        TextView a_CategoryName = holder.a_CategoryName;
        TextView a_CategoryExpense = holder.a_CategoryExpense;
        TextView a_CategoryBudget = holder.a_CategoryBudget;
        TextView a_CategoryTransactions=holder.a_NumOfTransactions;
        ImageView redAlert = holder.redAlert;
        ImageView yellowAlert = holder.yellowAlert;
        ImageView greenAlert = holder.greenAlert;

        a_CategoryIcon.setImageResource(Util.SetCategoryIcon(category));

        a_CategoryName.setText(category);
        a_CategoryExpense.setText(String.valueOf(expense));
        a_CategoryBudget.setText(String.valueOf(budget));
        a_CategoryTransactions.setText(String.valueOf(numTransactions)+ " transactions");

        if(expense<= (0.5*budget)){
            greenAlert.setColorFilter(Color.parseColor("#00FF00"));
        }
        else if(expense>(0.5*budget) && expense <= (0.75*budget)){
            yellowAlert.setColorFilter(Color.YELLOW);
        }
        else{
            redAlert.setColorFilter(Color.RED);
        }
    }



    @Override
    public int getItemCount() {
        return m_Categories.size();
    }

    public class ExpenseViewHolder extends RecyclerView.ViewHolder {
        ImageView a_CategoryIcon;
        TextView a_CategoryName;
        TextView a_CategoryExpense;
        TextView a_CategoryBudget;
        TextView a_NumOfTransactions;
        ImageView redAlert;
        ImageView yellowAlert;
        ImageView greenAlert;
        public LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 0);


        public ExpenseViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            a_CategoryIcon= itemView.findViewById(R.id.categoryIcon);
            a_CategoryName=itemView.findViewById(R.id.categoryName);
            a_CategoryExpense=itemView.findViewById(R.id.categoryExpense);
            a_CategoryBudget=itemView.findViewById(R.id.categoryBudget);
            a_NumOfTransactions=itemView.findViewById(R.id.categoryTransactions);
            redAlert=itemView.findViewById(R.id.redAlert);
            yellowAlert=itemView.findViewById(R.id.yellowAlert);
            greenAlert=itemView.findViewById(R.id.greenAlert);
        }


    }
}
