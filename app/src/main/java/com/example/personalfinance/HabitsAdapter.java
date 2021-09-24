package com.example.personalfinance;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.MutableDateTime;

import java.util.ArrayList;
import java.util.List;

public class HabitsAdapter extends RecyclerView.Adapter<HabitsAdapter.ReportViewHolder>  {
    private final List<CategoryReport> m_Categories = new ArrayList<>();
    public HabitsAdapter(){
        for (String a_Category: Util.GetExistingCategories()){
            m_Categories.add(new CategoryReport(a_Category));
        }
    }

    @NonNull
    @NotNull
    @Override
    public HabitsAdapter.ReportViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_retrieve_habits, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ReportViewHolder holder, int position) {
        CategoryReport Category = m_Categories.get(position);
        String category=Category.GetCategory();
        holder.SetCategoryName(category);
        holder.SetCategoryImage(category);
        holder.bind(Category);

        holder.itemView.setOnClickListener(v -> {
            Log.i("Click","clicked");
            boolean expanded = Category.isExpanded();
            Category.SetExpanded(!expanded);
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return m_Categories.size();
    }

    public static class ReportViewHolder extends RecyclerView.ViewHolder {

        View m_SubItem;
        ImageView m_Arrow;

        public ReportViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            m_SubItem=itemView.findViewById(R.id.categoryReport);
            m_Arrow=itemView.findViewById(R.id.forwardArrow);
        }
        private void SetCategoryName(String a_CategoryName){
            TextView categoryName = itemView.findViewById(R.id.categoryName);
            categoryName.setText("Spending in: " + a_CategoryName);
        }

        private void SetCategoryImage(String a_CategoryName){
            int imageId = Util.SetCategoryIcon(a_CategoryName);
            ImageView categoryImage = itemView.findViewById(R.id.categoryIcon);
            categoryImage.setImageResource(imageId);
        }

        public void bind(CategoryReport a_Category) {
            boolean expanded = a_Category.isExpanded();
            m_SubItem.setVisibility(expanded?View.VISIBLE:View.GONE);
            m_Arrow.setImageResource(expanded?R.drawable.ic_baseline_keyboard_arrow_down_24:R.drawable.ic_baseline_keyboard_arrow_right_24);
            FetchData(a_Category);

        }

        private void FetchData(CategoryReport a_Category) {

            MutableDateTime epoch = new MutableDateTime();
            epoch.setDate(0);

            DateTime now = new DateTime();
            int today = now.getDayOfMonth();

            DateTime prevMonthDay = new DateTime().minusMonths(1).withDayOfMonth(today);
            DateTime prevMonthFirstDay = new DateTime().minusMonths(1).withDayOfMonth(1);
            Days prevMonthFirstCount = Days.daysBetween(epoch,prevMonthFirstDay);
            Days prevMonthDayCount = Days.daysBetween(epoch, prevMonthDay);

            DateTime currentMonthDay = new DateTime().withDayOfMonth(today);
            DateTime currentMonthFirstDay = new DateTime().withDayOfMonth(1);
            Days currentMonthFirstCount = Days.daysBetween(epoch,currentMonthFirstDay);
            Days currentMonthDayCount = Days.daysBetween(epoch, currentMonthDay);

            Util.GetExpenseReference().orderByChild("day").startAt(currentMonthFirstCount.getDays())
                    .endAt(currentMonthDayCount.getDays())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            Double a_CurrentTotal = 0.0;
                            for (DataSnapshot data : snapshot.getChildren()) {
                                Data d = data.getValue(Data.class);
                                assert d != null;
                                if (!(d.getCategory().equals(a_Category.GetCategory()))) {
                                    continue;
                                }
                                if (d.getAmount() > 0) {
                                    a_CurrentTotal += d.getAmount();
                                }
                            }
                            a_Category.setCurrentAmount(a_CurrentTotal);

                    Util.GetExpenseReference().orderByChild("day")
                            .startAt(prevMonthFirstCount.getDays())
                            .endAt(prevMonthDayCount.getDays())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            Double a_PrevTotal = 0.0;
                            for (DataSnapshot data : snapshot.getChildren()) {
                                Data d = data.getValue(Data.class);
                                assert d != null;
                                Log.i("Previous month",String.valueOf(d.getMonth()));
                                if(!(d.getCategory().equals(a_Category.GetCategory()))){
                                    continue;
                                }
                                    if (d.getAmount() > 0) {
                                        a_PrevTotal += d.getAmount();
                                }
                            }
                            a_Category.setPreviousAmount(a_PrevTotal);
                            BarChart barChart=itemView.findViewById(R.id.categoryBarChart);
                            HabitChart habitChart = new HabitChart(barChart);
                            if(a_Category.getPreviousAmount()==0 && a_Category.getCurrentAmount()==0){
                                barChart.setNoDataText("No data available for comparison");
                                return;
                            }
                            habitChart.SetUpBarChart();
                            habitChart.LoadBarChart(a_Category);
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {
                            Log.e("Error fetching previous month's data",
                                    error.getMessage(),error.toException());
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    Log.e("Error fetching this month's data",error.getMessage(),
                            error.toException());
                }
            });
        }
    }
}
