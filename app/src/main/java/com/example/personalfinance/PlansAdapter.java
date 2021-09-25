//
// Implementation of the PlansAdapter class
//
package com.example.personalfinance;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import org.jetbrains.annotations.NotNull;

public class PlansAdapter extends FirebaseRecyclerAdapter<Data, PlansAdapter.PlansViewHolder> {

    /**/
    /*
    * NAME
        PlansAdapter::PlansAdapter() - Initializes the query to fetch plans data from Firebase

    * SYNOPSIS
        public PlansAdapter::PlansAdapter();

    * DESCRIPTION
        This function takes in a FirebaseRecyclerOption argument which fetches a Data Object for plans
        from the database. Then, it will attempt to initialize its member query with the argument query.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        10:27am, 08/08/2021
    */
    /**/
    public PlansAdapter(@NonNull FirebaseRecyclerOptions<Data> options)
    {
        super(options);
    }


    /**/
    /*
    * NAME
        PlansAdapter::onBindHolder() - Updates layout to show existing plans on receiving data from database

    * SYNOPSIS
        protected void onBindViewHolder(@NonNull @NotNull PlansAdapter.GoalViewHolder holder, int position, @NonNull @NotNull Data model);

    * DESCRIPTION
        This function waits for the query to return a value. Then, it iterates through the results and displays each plan

    * AUTHOR
        Shreeti Shrestha

    * DATE
        11:27am, 08/08/2021
    */
    /**/
    @Override
    protected void onBindViewHolder(@NonNull @NotNull PlansAdapter.PlansViewHolder holder, int position, @NonNull @NotNull Data model) {
        holder.SetPlanName("Goal Name: " + model.GetGoal());
        holder.SetPlanBudget("Goal Budget: $" + model.GetGoalBudget());
    }


    /**/
    /*
    * NAME
        PlansAdapter::onCreateViewHolder() - Inflates layout with the layout for displaying plans

    * SYNOPSIS
        public PlansAdapter.PlansViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType);
        * parent => view group into which the new view will be added after it is bound to an adapter position
        * viewType => type of the new view

    * DESCRIPTION
        This function will attempt to display each new plan in the formatting specified by activity_retrieve_goals layout

    * AUTHOR
        Shreeti Shrestha

    * DATE
        1:00pm, 08/10/2021
    */
    /**/
    @NonNull
    @NotNull
    @Override
    public PlansAdapter.PlansViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View a_View = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_retrieve_goals, parent, false);
        return new PlansViewHolder(a_View);
    }


    //
    // Implementation of the PlansViewHolder class
    //
    // This is inside the Adapter class so that adapters can use the views
    // from this class and update them.
    static class PlansViewHolder extends RecyclerView.ViewHolder{
        View m_View;

        public PlansViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            m_View = itemView;

        }

        // helper function to update plan name
        private void SetPlanName(String a_GoalName){
            TextView goalName = m_View.findViewById(R.id.goalName);
            goalName.setText(a_GoalName);
        }

        // helper function to update plan budget
        private void SetPlanBudget(String a_CategoryBudget){
            TextView goalBudget = m_View.findViewById(R.id.goalBudget);
            goalBudget.setText(a_CategoryBudget);
        }
    }
}
