//
// Implementation of the ExpenseAdapter class
// This class provides an interface to show category breakdown in real time as new transactions get added
//
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

    /**/
    /*
    * NAME
        ExpenseAdapter::ExpenseAdapter() - Constructor fetches the total budget for each category

    * SYNOPSIS
        public ExpenseAdapter()

    * DESCRIPTION
        The constructor attempts to make a reference to the budget tree in the database. If setting up
        a reference to the database fails, then it throws an error message. Then, it pulls the
        budgets for each category for the current month into the application as a data object

    * AUTHOR
        Shreeti Shrestha

    * DATE
        08:30pm, 05/21/2021
    */
    /**/
    public ExpenseAdapter() {
        int currentMonth = Util.GetMonth().getMonths();
        Util.GetBudgetReference().orderByChild("month").equalTo(currentMonth).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Data data = dataSnapshot.getValue(Data.class);
                    assert data != null;
                    m_Budget.put(data.GetCategory(), data.GetAmount());
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.e("Error fetching budget data",error.getMessage(),error.toException());
            }
        });
    } /* public ExpenseAdapter() */

    /**/
    /*
    * NAME
        ExpenseAdapter::SetExpenses() - Sets the total expense for each category

    * SYNOPSIS
        public void SetExpenses(List<Data> monthlyDataList);
        * monthlyDataList => list of all transactions for the month

    * DESCRIPTION
        This function iterates through all transactions for the month in the monthlyDataList. Then,
        it adds the amount for each transaction to the category that it belongs to.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        08:30pm, 05/23/2021
    */
    /**/
    public void SetExpenses(List<Data> monthlyDataList){
        this.m_ExpenseList = monthlyDataList;
        this.m_Categories= Util.GetCategoricalExpense(monthlyDataList);
        if (!m_ExpenseList.isEmpty()) {
            for (Data a_Transaction : monthlyDataList) {
                String a_Category = a_Transaction.GetCategory();
                m_Expenses.computeIfAbsent(a_Category, k -> new ArrayList<>()).add(a_Transaction);
            }
        }
        for (String a_Category : Util.GetExistingCategories()) {
            if (!m_Expenses.containsKey(a_Category)) {
                m_Expenses.put(a_Category, new ArrayList<>());
            }
        }
    }


    /**/
    /*
    * NAME
        ExpenseAdapter::onCreateViewHolder() - Inflates layout with the layout for displaying breakdowns

    * SYNOPSIS
        ExpenseViewHolder ExpenseAdapter::onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType)
        * parent => view group into which the new view will be added after it is bound to an adapter position
        * viewType => type of the new view

    * DESCRIPTION
        This function will attempt to display each category in the formatting specified by activity_category layout

    * RETURNS
        Returns a new ViewHolder that holds a View of the category view type.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        10:30pm, 05/23/2021
    */
    /**/
    @NonNull
    @NotNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_category, parent, false);
        return new ExpenseViewHolder(view);
    } /* public ExpenseViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) */



    /**/
    /*
    * NAME
        ExpenseAdapter::onBindHolder() - Updates layout to show category breakdowns for the month

    * SYNOPSIS
        void ExpenseAdapter::onBindViewHolder(@NonNull @NotNull ExpenseViewHolder holder, int position);
        *  holder => the view for the page that contains metadata for a budget (category name,
           category icon, budget, expenses, number of transactions)
        * position => latest item in the adapter

    * DESCRIPTION
        This function populates values for each model from the values in the latest item

    * AUTHOR
        Shreeti Shrestha

    * DATE
        11:40pm, 05/25/2021
    */
    /**/
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

        // Set red, yellow and green indicators for each category
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


    /**/
    /*
    * NAME
        ExpenseAdapter::getItemCount() - Overrides the getItemCount() to return the value of the category list

    * SYNOPSIS
        int ExpenseAdapter::getItemCount();

    * DESCRIPTION
        This function counts the number of categories which have an expense for the current month. Then,
        it returns that value as the itemCount.

    * RETURNS
        Returns the size of m_Categories, which has a list of all categories that have an expense > 0.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        11:50pm, 05/25/2021
    */
    /**/
    @Override
    public int getItemCount() {
        return m_Categories.size();
    }/*  public int getItemCount() */



    //
    // Implementation of the ExpenseViewHolder class
    //
    // This is inside the Adapter class so that adapters can use the views
    // from this class and update them.
    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        ImageView redAlert;
        ImageView yellowAlert;
        ImageView greenAlert;
        public LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 0);

        // default constructor to set the itemView as the rootview
        public ExpenseViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            redAlert = itemView.findViewById(R.id.redAlert);
            yellowAlert = itemView.findViewById(R.id.yellowAlert);
            greenAlert = itemView.findViewById(R.id.greenAlert);
        }

        /**/
        /*
        * NAME
            ExpenseAdapter::SetCategoryName() - Sets category name

        * SYNOPSIS
            void ExpenseAdapter::SetCategoryName(a_CategoryName);
            * a_CategoryName => the category name to be assigned to the model

        * DESCRIPTION
            This function will attempt to set the passed category name to the model.
            Since null entry would have already been checked for before the function call,
            this simply extracts the value and assigns it to the model

        * AUTHOR
            Shreeti Shrestha

        * DATE
            9:00pm, 05/26/2021
        */
        /**/
        private void SetCategoryName(String a_CategoryName) {
            TextView categoryName = itemView.findViewById(R.id.categoryName);
            categoryName.setText(a_CategoryName);
        } /* private void SetCategoryName(String a_CategoryName); */


        /**/
        /*
        * NAME
            ExpenseAdapter::SetCategoryImage() - Sets category image

        * SYNOPSIS
            void ExpenseAdapter::SetCategoryImage(a_CategoryName);
            * a_CategoryName => the category name to identify the image to be assigned to the model

        * DESCRIPTION
            This function will use the passed category name to set up a category icon for the view.
            A call to SetCategoryIcon() in the Utils class returns an image id, which is then used by
            this function to set the image for the model

        * AUTHOR
            Shreeti Shrestha

        * DATE
            9:20pm, 05/26/2021
        */
        /**/
        private void SetCategoryImage(String a_CategoryName) {
            int imageId = Util.SetCategoryIcon(a_CategoryName);
            ImageView categoryImage = itemView.findViewById(R.id.categoryIcon);
            categoryImage.setImageResource(imageId);
        }/*  private void SetCategoryImage(String a_CategoryName); */

        /**/
        /*
        * NAME
            ExpenseAdapter::SetCategoryBudget() - Sets category budget amount

        * SYNOPSIS
            void ExpenseAdapter::SetCategoryBudget(a_CategoryBudget);
            * a_CategoryBudget => the category budget amount to be assigned to the model

        * DESCRIPTION
            This function will attempt to set the passed category budget value to the model.
            Since null entry would have already been checked for before the function call,
            this simply extracts the value and assigns it to the model

        * AUTHOR
            Shreeti Shrestha

        * DATE
            10:00pm, 05/26/2021
        */
        /**/
        private void SetCategoryBudget(String a_CategoryBudget) {
            TextView categoryBudget = itemView.findViewById(R.id.categoryBudget);
            categoryBudget.setText("Budget: " + a_CategoryBudget);
        }

        /**/
        /*
        * NAME
            ExpenseAdapter::SetCategoryExpense() - Sets category expense amount

        * SYNOPSIS
            void ExpenseAdapter::SetCategoryExpense(a_CategoryExpense);
            * a_CategoryExpense => the category expense amount to be assigned to the model

        * DESCRIPTION
            This function will attempt to set the passed category expense value to the model.
            Since null entry would have already been checked for before the function call,
            this simply extracts the value and assigns it to the model

        * AUTHOR
            Shreeti Shrestha

        * DATE
            10:05pm, 05/26/2021
        */
        /**/
        private void SetCategoryExpense(String a_CategoryExpense) {
            TextView categoryBudget = itemView.findViewById(R.id.categoryExpense);
            categoryBudget.setText("Expenses: " + a_CategoryExpense);
        }


        /**/
        /*
        * NAME
            ExpenseAdapter::SetNumTransactions() - Sets category expense amount

        * SYNOPSIS
            void ExpenseAdapter::SetNumTransactions(a_NumTransactions);
            * a_NumTransactions => the number of transactions in the category so far

        * DESCRIPTION
            This function will attempt to set the number of transactions to the model.
            Since null entry would have already been checked for before the function call,
            this simply extracts the value and assigns it to the model

        * AUTHOR
            Shreeti Shrestha

        * DATE
            10:10pm, 05/26/2021
        */
        /**/
        private void SetNumTransactions(String a_NumTransactions) {
            TextView numOfTransactions = itemView.findViewById(R.id.categoryTransactions);
            numOfTransactions.setText(a_NumTransactions + " transactions");
        }
    }

    private List<Data> m_ExpenseList= Collections.emptyList();
    private Map<String, List<Data>> m_Expenses=new HashMap<>();
    private Map<String,Double> m_Categories = new HashMap<>();
    private Map<String, Double> m_Budget = new HashMap<>();

}
