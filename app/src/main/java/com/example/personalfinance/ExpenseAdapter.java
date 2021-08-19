package com.example.personalfinance;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.plaid.client.model.Transaction;

import org.jetbrains.annotations.NotNull;
import org.joda.time.Months;
import org.w3c.dom.Text;

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
        Log.i("Reaches here", String.valueOf(monthlyDataList));
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
//        Log.i("M-Categories",String.valueOf(m_Categories));
        Log.i(TAG, "Category Breakdown: Success");


    }
    public ExpenseAdapter() {
        FirebaseAuth m_Auth = FirebaseAuth.getInstance();
        String a_Uid = Objects.requireNonNull(m_Auth.getCurrentUser()).getUid();
        Months currentMonth = Util.getMonth().minus(2);
        DatabaseReference m_BudgetRef = FirebaseDatabase.getInstance().getReference().child("budget").child(a_Uid).child(String.valueOf(currentMonth));



        m_BudgetRef.addValueEventListener(new ValueEventListener() {
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
        Log.i("Here","Reaches here");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_category, parent, false);
        ExpenseViewHolder myViewHolder = new ExpenseViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ExpenseViewHolder holder, int position) {
        Log.i("Here","Reaches here");
        String key = String.valueOf(m_Categories.keySet().toArray()[position]);

        Double value = m_Categories.get(key);
        TextView a_CategoryName = holder.a_CategoryName;
        TextView a_CategoryExpense = holder.a_CategoryExpense;
        TextView a_CategoryBudget = holder.a_CategoryBudget;
        TextView a_CategoryTransactions=holder.a_NumOfTransactions;

        a_CategoryName.setText(String.valueOf(key));
        a_CategoryExpense.setText(String.valueOf(value));
        a_CategoryBudget.setText(String.valueOf(m_Budget.get(key)));
        a_CategoryTransactions.setText(String.valueOf(m_Expenses.get(key).size())+ " transactions");
    }



    @Override
    public int getItemCount() {
        return m_Categories.size();
    }

    public class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView a_CategoryName;
        TextView a_CategoryExpense;
        TextView a_CategoryBudget;
        TextView a_NumOfTransactions;

        public ExpenseViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            a_CategoryName=itemView.findViewById(R.id.categoryName);
            a_CategoryExpense=itemView.findViewById(R.id.categoryExpense);
            a_CategoryBudget=itemView.findViewById(R.id.categoryBudget);
            a_NumOfTransactions=itemView.findViewById(R.id.categoryTransactions);
        }
    }
}
