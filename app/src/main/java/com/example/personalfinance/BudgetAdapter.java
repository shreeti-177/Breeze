package com.example.personalfinance;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import org.jetbrains.annotations.NotNull;

public class BudgetAdapter extends FirebaseRecyclerAdapter<Data, BudgetAdapter.CategoryViewHolder> {
    public BudgetAdapter(@NonNull FirebaseRecyclerOptions<Data> options)
    {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull @NotNull BudgetAdapter.CategoryViewHolder holder, int position, @NonNull @NotNull Data model) {
        holder.SetCategoryName(model.getCategory());
        holder.SetCategoryBudget(String.valueOf(model.getAmount()));
        holder.SetCategoryImage(model.getCategory());
        holder.SetBudgetDate(model.getDate()); }

    @NonNull
    @NotNull
    @Override
    public BudgetAdapter.CategoryViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieve_category_budget, parent, false);
        return new CategoryViewHolder(view);
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder{
        View m_View;

        public CategoryViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            m_View = itemView;

        }
        private void SetCategoryName(String a_CategoryName){
            TextView categoryName = m_View.findViewById(R.id.categoryField);
            categoryName.setText(a_CategoryName);
        }

        private void SetCategoryImage(String a_CategoryName){
            int imageId = Util.SetCategoryIcon(a_CategoryName);
            ImageView categoryImage = m_View.findViewById(R.id.categoryTag);
            categoryImage.setImageResource(imageId);
        }

        private void SetBudgetDate(String a_SetDate){
            TextView date = m_View.findViewById(R.id.dateField);
            date.setText(a_SetDate);
        }
        private void SetCategoryBudget(String a_CategoryBudget){
            TextView categoryBudget = m_View.findViewById(R.id.categoryBudget);
            categoryBudget.setText(a_CategoryBudget);
        }

    }

}
