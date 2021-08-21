package com.example.personalfinance;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;
import org.joda.time.Months;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HabitsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_habits);
//        m_BarChart=findViewById(R.id.barChartTotal);
        m_RecyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager m_LinearLayoutManager = new LinearLayoutManager(this);
        m_RecyclerView.setLayoutManager(m_LinearLayoutManager);
        m_RecyclerView.setItemAnimator(new DefaultItemAnimator());

        m_Adapter = new HabitsAdapter();
        m_RecyclerView.setAdapter(m_Adapter);
//        Log.i("Adapter",String.valueOf(m_Adapter));

        setTitle("BarChartActivity");

        m_SummaryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    String a_Month = String.valueOf(dataSnapshot.child("month").getValue());
                    Double a_Expenses = (Double) (dataSnapshot.child("monthly-expense").getValue());
                    Float a_Expense = a_Expenses.floatValue();
                    m_MonthlyTotals.put(a_Month,a_Expenses);
                    m_Months.add(a_Month);
                    m_Expenses.add(a_Expense);
                }
                m_Adapter.notifyDataSetChanged();
//                SetUpBarChart();
//                LoadBarChart();
            }


            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

//    private void LoadBarChart(){
//        String a_Title= "Budget Report";
//        int size = m_MonthlyTotals.size();
////                Log.i("Monthly",String.valueOf(m_MonthlyTotals));
//        for (int i=0;i<size;i++){
//            BarEntry barEntry=new BarEntry(i,m_Expenses.get(i));
//            m_Entries.add(barEntry);
//        }
//        Log.i("M_Entries",String.valueOf(m_Entries));
//
//        BarDataSet m_BarDataSet = new BarDataSet(m_Entries,a_Title);
//        BarData a_Data = new BarData(m_BarDataSet);
//        m_BarDataSet.setColor(Color.parseColor("#304567"));
//        //Setting the size of the form in the legend
//        m_BarDataSet.setFormSize(15f);
//        //showing the value of the bar, default true if not set
//        m_BarDataSet.setDrawValues(false);
//        //setting the text size of the value of the bar
//        m_BarDataSet.setValueTextSize(12f);
//        m_BarChart.setData(a_Data);
//        m_BarChart.invalidate();
//    }

//    private void SetUpBarChart(){
//        //hiding the grey background of the chart, default false if not set
//        m_BarChart.setDrawGridBackground(false);
//        //remove the bar shadow, default false if not set
//        m_BarChart.setDrawBarShadow(false);
//        //remove border of the chart, default false if not set
//        m_BarChart.setDrawBorders(false);
//
//        //remove the description label text located at the lower right corner
//        Description description = new Description();
//        description.setEnabled(false);
//        m_BarChart.setDescription(description);
//
//        //setting animation for y-axis, the bar will pop up from 0 to its value within the time we set
//        m_BarChart.animateY(1000);
//        //setting animation for x-axis, the bar will pop up separately within the time we set
//        m_BarChart.animateX(1000);
//
//        XAxis xAxis = m_BarChart.getXAxis();
//        //change the position of x-axis to the bottom
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        //set the horizontal distance of the grid line
//        xAxis.setGranularity(1f);
//        //hiding the x-axis line, default true if not set
//        xAxis.setDrawAxisLine(false);
//        //hiding the vertical grid lines, default true if not set
//        xAxis.setDrawGridLines(false);
//
//        YAxis leftAxis = m_BarChart.getAxisLeft();
//        //hiding the left y-axis line, default true if not set
//        leftAxis.setDrawAxisLine(false);
//
//        YAxis rightAxis = m_BarChart.getAxisRight();
//        //hiding the right y-axis line, default true if not set
//        rightAxis.setDrawAxisLine(false);
//
//        Legend legend = m_BarChart.getLegend();
//        //setting the shape of the legend form to line, default square shape
//        legend.setForm(Legend.LegendForm.LINE);
//        //setting the text size of the legend
//        legend.setTextSize(11f);
//        //setting the alignment of legend toward the chart
//        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
//        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
//        //setting the stacking direction of legend
//        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
//        //setting the location of legend outside the chart, default false if not set
//        legend.setDrawInside(false);
//
//    }

    ExecutorService m_Executor = Executors.newSingleThreadExecutor();
    private final Handler m_Handler = new Handler(Looper.getMainLooper());


    private FirebaseAuth m_Auth = FirebaseAuth.getInstance();
    private String a_Uid = Objects.requireNonNull(m_Auth.getCurrentUser()).getUid();
    Months currentMonth = Util.getMonth();
    private DatabaseReference m_SummaryRef = FirebaseDatabase.getInstance().getReference().child("summary").child(a_Uid);
    private BarChart m_BarChart;
    private Map<String, Double> m_MonthlyTotals=new HashMap<>();
    private RecyclerView m_RecyclerView;
    private HabitsAdapter m_Adapter;
    private List<String> m_Months=new ArrayList<>();
    private List<Float> m_Expenses=new ArrayList<>();
    private List<BarEntry> m_Entries = new ArrayList<>();
//    ExpenseAdapter m_Adapter;
//    private List<Data> monthlyDataList=new ArrayList<>();
//    private Map<String, Double> spendingCategories=new HashMap<>();
    private static final String TAG = "HabitsActivity";

}
