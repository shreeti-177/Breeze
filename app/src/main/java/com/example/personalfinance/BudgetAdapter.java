//
// Implementation of the Budget Adapter class
// This class provides an interface to show budgets in real time as they're set by the user
//
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

    // Default constructor
    public BudgetAdapter(@NonNull FirebaseRecyclerOptions<Data> options)
    {
        super(options);
    }

    /**/
    /*
    * NAME
        BudgetAdapter::onBindHolder() - Updates layout to show existing budgets for the month

    * SYNOPSIS
        protected void onBindViewHolder(@NonNull @NotNull BudgetAdapter.CategoryViewHolder holder,
        int position, @NonNull @NotNull Data model);
        *  holder => the view for the page that contains metadata for a budget (category name,
           category icon, date, amount)
        * position => latest item in the adapter

    * DESCRIPTION
        This function waits for the query to return a value. Then, it iterates through the results and displays each budget
        that has been set so far for the month

    * AUTHOR
        Shreeti Shrestha

    * DATE
        11:30pm, 04/20/2021
    */
    /**/

    @Override
    protected void onBindViewHolder(@NonNull @NotNull BudgetAdapter.CategoryViewHolder holder, int position, @NonNull @NotNull Data model) {
        holder.SetCategoryName(model.GetCategory());
        holder.SetCategoryAmount(String.valueOf(model.GetAmount()));
        holder.SetCategoryImage(model.GetCategory());
        holder.SetBudgetDate(model.GetDate());
    } /*  protected void onBindViewHolder(@NonNull @NotNull BudgetAdapter.CategoryViewHolder holder, int position, @NonNull @NotNull Data model); */


    /**/
    /*
    * NAME
        BudgetAdapter::onCreateViewHolder() - Inflates layout with the layout for displaying budgets

    * SYNOPSIS
        BudgetAdapter.CategoryViewHolder BudgetAdapter::onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType);
        * parent => view group into which the new view will be added after it is bound to an adapter position
        * viewType => type of the new view

    * DESCRIPTION
        This function will attempt to display each new budget in the formatting specified by retrieve_category_budget layout

    * RETURNS
        Returns a new ViewHolder that holds a View of the category view type.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        1:00pm, 08/22/2021
    */
    /**/
    @NonNull
    @NotNull
    @Override
    public BudgetAdapter.CategoryViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieve_category_budget, parent, false);
        return new CategoryViewHolder(view);
    } /* public BudgetAdapter.CategoryViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) */

    //
    // Implementation of the CategoryViewHolder class
    //
    // This is inside the Adapter class so that adapters can use the views
    // from this class and update them.

    static class CategoryViewHolder extends RecyclerView.ViewHolder{
        View m_View;

        // default constructor to set the itemView as the rootview
        public CategoryViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            m_View = itemView;

        }


        /**/
        /*
        * NAME
            BudgetAdapter::SetCategoryName() - Sets category name

        * SYNOPSIS
            void BudgetAdapter::SetCategoryName(a_CategoryName);
            * a_CategoryName => the category name to be assigned to the model

        * DESCRIPTION
            This function will attempt to set the passed category name to the model.
            Since null entry would have already been checked for before the function call,
            this simply extracts the value and assigns it to the model

        * AUTHOR
            Shreeti Shrestha

        * DATE
            9:00pm, 04/22/2021
        */
        /**/
        private void SetCategoryName(String a_CategoryName){
            TextView categoryName = m_View.findViewById(R.id.categoryField);
            categoryName.setText(a_CategoryName);
        } /* private void SetCategoryName(String a_CategoryName); */

        /**/
        /*
        * NAME
            BudgetAdapter::SetCategoryImage() - Sets category image

        * SYNOPSIS
            void BudgetAdapter::SetCategoryImage(a_CategoryName);
            * a_CategoryName => the category name to identify the image to be assigned to the model

        * DESCRIPTION
            This function will use the passed category name to set up a category icon for the view.
            A call to SetCategoryIcon() in the Utils class returns an image id, which is then used by
            this function to set the image for the model

        * AUTHOR
            Shreeti Shrestha

        * DATE
            9:20pm, 04/22/2021
        */
        /**/
        private void SetCategoryImage(String a_CategoryName){
            int imageId = Util.SetCategoryIcon(a_CategoryName);
            ImageView categoryImage = m_View.findViewById(R.id.categoryTag);
            categoryImage.setImageResource(imageId);
        } /*  private void SetCategoryImage(String a_CategoryName); */

        /**/
        /*
        * NAME
            BudgetAdapter::SetBudgetDate() - Sets budget date

        * SYNOPSIS
            void BudgetAdapter::SetBudgetDate(a_Date);
            * a_Date => the set date to be assigned to the model

        * DESCRIPTION
            This function will attempt to set the passed date to the model as the date the budget was set.
            Since null entry would have already been checked for before the function call,
            this simply extracts the value and assigns it to the model

        * AUTHOR
            Shreeti Shrestha

        * DATE
            9:30pm, 04/22/2021
        */
        /**/
        private void SetBudgetDate(String a_SetDate){
            TextView date = m_View.findViewById(R.id.dateField);
            date.setText(a_SetDate);
        }/* private void SetBudgetDate(String a_SetDate); */

        /**/
        /*
        * NAME
            BudgetAdapter::SetCategoryAmount() - Sets category budget amount

        * SYNOPSIS
            void BudgetAdapter::SetCategoryAmount(a_CategoryBudget);
            * a_CategoryBudget => the category amount to be assigned to the model

        * DESCRIPTION
            This function will attempt to set the passed category amount value to the model.
            Since null entry would have already been checked for before the function call,
            this simply extracts the value and assigns it to the model

        * AUTHOR
            Shreeti Shrestha

        * DATE
            10:00pm, 04/22/2021
        */
        /**/
        private void SetCategoryAmount(String a_CategoryBudget){
            TextView categoryBudget = m_View.findViewById(R.id.categoryBudget);
            categoryBudget.setText(a_CategoryBudget);
        } /* private void SetCategoryAmount(String a_CategoryBudget); */

    }

}
