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

public class PlansAdapter extends FirebaseRecyclerAdapter<Data, PlansAdapter.GoalViewHolder> {
    public PlansAdapter(@NonNull FirebaseRecyclerOptions<Data> options)
    {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull @NotNull PlansAdapter.GoalViewHolder holder, int position, @NonNull @NotNull Data model) {
//        Log.i("Model Data",String.valueOf(model.getGoal()));
//        Log.i("Model Data Budget",String.valueOf(model.getGoalBudget()));

        holder.SetGoalName("Goal Name: " + model.getGoal());
        holder.SetGoalBudget("Goal Budget: $" + model.getGoalBudget());
//                holder.ShowOptions();

    }

    @NonNull
    @NotNull
    @Override
    public PlansAdapter.GoalViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View a_View = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_retrieve_goals, parent, false);
        return new GoalViewHolder(a_View);
    }

    static class GoalViewHolder extends RecyclerView.ViewHolder{
        View m_View;

        public GoalViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            m_View = itemView;

        }
        private void SetGoalName(String a_GoalName){
            TextView goalName = m_View.findViewById(R.id.goalName);
            goalName.setText(a_GoalName);
        }

        private void SetGoalBudget(String a_CategoryBudget){
            TextView goalBudget = m_View.findViewById(R.id.goalBudget);
            goalBudget.setText(a_CategoryBudget);
        }

    }
}
