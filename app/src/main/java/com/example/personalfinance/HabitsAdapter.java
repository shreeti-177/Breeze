package com.example.personalfinance;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Database;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class HabitsAdapter extends RecyclerView.Adapter<HabitsAdapter.ReportViewHolder>  {
//    private List<String> m_Categories=new ArrayList<>();
    private List<CategoryReport> m_Categories = new ArrayList<>();
    private boolean expanded;
    public HabitsAdapter(){
        Log.i("Reaches", "Constructor");
        for (String a_Category: Util.GetExistingCategories()){
            m_Categories.add(new CategoryReport(a_Category));
        }
    }

    @NonNull
    @NotNull
    @Override
    public HabitsAdapter.ReportViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        Log.i("Here","Reaches here");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_retrieve_habits, parent, false);
        HabitsAdapter.ReportViewHolder myViewHolder = new HabitsAdapter.ReportViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ReportViewHolder holder, int position) {
        Log.i("Here","Reaches here");
        CategoryReport Category = m_Categories.get(position);
        String a_Category=Category.GetCategory();
        TextView a_CategoryName = holder.a_CategoryName;
        a_CategoryName.setText("Spendings in " + String.valueOf(a_Category));
        holder.bind(Category);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Click","clicked");
                boolean expanded = Category.isExpanded();
                Category.SetExpanded(!expanded);
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return m_Categories.size();
    }

    public class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView a_CategoryName;
        BarChart m_BarChart;
        TextView a_SubText;
        View a_SubItem;
        private Double m_Total;
        private FirebaseAuth m_Auth = FirebaseAuth.getInstance();
        private String a_Uid = Objects.requireNonNull(m_Auth.getCurrentUser()).getUid();

        public ReportViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            a_CategoryName=itemView.findViewById(R.id.categoryName);
            m_BarChart=itemView.findViewById(R.id.categoryBarChart);
            a_SubItem=itemView.findViewById(R.id.categoryReport);
//            a_SubText=itemView.findViewById(R.id.subText);
        }

        public void bind(CategoryReport a_Category) {
            boolean expanded = a_Category.isExpanded();
            a_SubItem.setVisibility(expanded?View.VISIBLE:View.GONE);
            SetUpBarChart();
            FetchData(a_Category);
        }

        private void FetchData(CategoryReport a_Category) {

            MutableDateTime a_Epoch = new MutableDateTime();
            a_Epoch.setDate(0);

            DateTime a_Now = new DateTime();
            int a_Today = a_Now.getDayOfMonth();

            Months a_PrevMonth = Util.getMonth().minus(1);
            DateTime a_PrevMonthDay = new DateTime().minusMonths(1).withDayOfMonth(a_Today);
            Days a_PrevMonthDayCount = Days.daysBetween(a_Epoch, a_PrevMonthDay);

            Months a_CurrentMonth = Util.getMonth();
            DateTime a_CurrentMonthDay = new DateTime().withDayOfMonth(a_Today);
            Days a_CurrentMonthDayCount = Days.daysBetween(a_Epoch, a_CurrentMonthDay);

            DatabaseReference m_ExpenseRef = FirebaseDatabase.getInstance().getReference().child("expenses").child(a_Uid).child(String.valueOf(a_CurrentMonth));
            m_ExpenseRef.orderByChild("category").equalTo(a_Category.GetCategory()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    Double a_CurrentTotal = 0.0;
                    for (DataSnapshot data : snapshot.getChildren()) {
                        Data d = data.getValue(Data.class);
                        if (d.getDay() <= a_CurrentMonthDayCount.getDays()) {
//                            Log.i("Category", a_Category.GetCategory());
                            if (d.getAmount()>0) {
                                a_CurrentTotal += d.getAmount();
                            }
                        }
                    }
                    a_Category.setCurrentAmount(a_CurrentTotal);

                    DatabaseReference m_ExpenseRef = FirebaseDatabase.getInstance().getReference().child("expenses").child(a_Uid).child(String.valueOf(a_PrevMonth));
                    m_ExpenseRef.orderByChild("category").equalTo(a_Category.GetCategory()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            Double a_PrevTotal = 0.0;
                            for (DataSnapshot data : snapshot.getChildren()) {
                                Data d = data.getValue(Data.class);
                                if (d.getDay() <= a_PrevMonthDayCount.getDays()) {
//                                    Log.i("Category", a_Category.GetCategory());
                                    if (d.getAmount() > 0) {
                                        a_PrevTotal += d.getAmount();
                                    }
                                }
                            }
                            a_Category.setPreviousAmount(a_PrevTotal);
                            Log.i("Category", a_Category.GetCategory());
                            Log.i("Spending", String.valueOf(a_Category.getPreviousAmount()));
                            Log.i("Spending this month", String.valueOf(a_Category.getCurrentAmount()));
                            LoadBarChart(a_Category);
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                }
            });
        }

        private void LoadBarChart(CategoryReport a_Category) {
            String a_Title = "Budget Report";
            List<BarEntry> m_Entries = new ArrayList<>();


//            a_SubText.setText("Current Month Total = " + String.valueOf(a_Category.getCurrentAmount()) + " and Previous Month = "+String.valueOf(a_Category.getPreviousAmount()));
            List<Float> a_Expenses = new ArrayList<>();
            a_Expenses.add(a_Category.getPreviousAmount().floatValue());
            a_Expenses.add(a_Category.getCurrentAmount().floatValue());


            for (int i =0;i<2;i++){
                BarEntry barEntry=new BarEntry(i,a_Expenses.get(i));
                m_Entries.add(barEntry);
            }
            Log.i("a_Expenses",String.valueOf(a_Expenses));

            Log.i("M_Entries",String.valueOf(m_Entries));

            BarDataSet m_BarDataSet = new BarDataSet(m_Entries,a_Title);
            BarData a_Data = new BarData(m_BarDataSet);
            m_BarDataSet.setColor(Color.parseColor("#304567"));
            //Setting the size of the form in the legend
            m_BarDataSet.setFormSize(15f);
            //showing the value of the bar, default true if not set
            m_BarDataSet.setDrawValues(false);
            //setting the text size of the value of the bar
            m_BarDataSet.setValueTextSize(12f);
            m_BarChart.setData(a_Data);
            m_BarChart.invalidate();

        }


        private void SetUpBarChart(){
            //hiding the grey background of the chart, default false if not set
            m_BarChart.setDrawGridBackground(false);
            //remove the bar shadow, default false if not set
            m_BarChart.setDrawBarShadow(false);
            //remove border of the chart, default false if not set
            m_BarChart.setDrawBorders(false);

            //remove the description label text located at the lower right corner
            Description description = new Description();
            description.setEnabled(false);
            m_BarChart.setDescription(description);

            //setting animation for y-axis, the bar will pop up from 0 to its value within the time we set
            m_BarChart.animateY(1000);
            //setting animation for x-axis, the bar will pop up separately within the time we set
            m_BarChart.animateX(1000);

            XAxis xAxis = m_BarChart.getXAxis();
            //change the position of x-axis to the bottom
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            //set the horizontal distance of the grid line
            xAxis.setGranularity(1f);
            //hiding the x-axis line, default true if not set
            xAxis.setDrawAxisLine(false);
            //hiding the vertical grid lines, default true if not set
            xAxis.setDrawGridLines(false);

            YAxis leftAxis = m_BarChart.getAxisLeft();
            //hiding the left y-axis line, default true if not set
            leftAxis.setDrawAxisLine(false);

            YAxis rightAxis = m_BarChart.getAxisRight();
            //hiding the right y-axis line, default true if not set
            rightAxis.setDrawAxisLine(false);

            Legend legend = m_BarChart.getLegend();
            //setting the shape of the legend form to line, default square shape
            legend.setForm(Legend.LegendForm.LINE);
            //setting the text size of the legend
            legend.setTextSize(11f);
            //setting the alignment of legend toward the chart
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
            //setting the stacking direction of legend
            legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            //setting the location of legend outside the chart, default false if not set
            legend.setDrawInside(false);

        }
    }
}
