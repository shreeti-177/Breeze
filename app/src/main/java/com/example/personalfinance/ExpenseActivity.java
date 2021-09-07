package com.example.personalfinance;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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

public class ExpenseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        m_PieChart=findViewById(R.id.pieChart);
        m_RecyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager m_LinearLayoutManager = new LinearLayoutManager(this);
        m_RecyclerView.setLayoutManager(m_LinearLayoutManager);
        m_RecyclerView.setItemAnimator(new DefaultItemAnimator());

        m_Adapter = new ExpenseAdapter();
        m_RecyclerView.setAdapter(m_Adapter);

        m_ExpenseRef.orderByChild("month").equalTo(String.valueOf(Util.getMonth())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Data data = dataSnapshot.getValue(Data.class);
                    monthlyDataList.add(data);
                }

                for (Data d:monthlyDataList) {
                    if(d.getAmount()>=0) {
                        totalMonthlyExpense += d.getAmount();
                    }
                }
                Log.i("Total Month Spending", String.valueOf(totalMonthlyExpense));


                m_Executor.execute(()->{
                    StoreMonthlyExpense(totalMonthlyExpense);
                });
                SetUpPieChart();
                LoadPieChart();
                m_Adapter.SetExpenses(monthlyDataList);
                m_Adapter.notifyDataSetChanged();

            }


            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {}
        });
    }

    private void StoreMonthlyExpense(Double a_Expense){
        DatabaseReference m_SummaryRef = FirebaseDatabase.getInstance().getReference().child("summary").child(a_Uid).child(String.valueOf(currentMonth));
        m_SummaryRef.child("monthly-expense").setValue(a_Expense).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "SetMonthlyExpenseSummary: success");
                    Toast.makeText(getApplicationContext(), "Monthly expense summary set successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Log.w(TAG, "SetMonthlyExpenseSummary: failure", task.getException());
                    Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
    private void LoadPieChart(){
        ArrayList<PieEntry> entries = new ArrayList<>();
        spendingCategories=Util.GetCategoricalExpense(monthlyDataList);


        for (String name: spendingCategories.keySet()){
            entries.add(new PieEntry(Objects.requireNonNull(spendingCategories.get(name)).floatValue(), name));
//            Log.i("Category Keys", name);
//            Log.i("Category Values", spendingCategories.get(name).toString());
        }


        PieDataSet dataSet = new PieDataSet(entries, "Expense Category");
        dataSet.setColors(ColorTemplate.PASTEL_COLORS);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.removeLast();

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(m_PieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.5f);
        dataSet.setValueLinePart2Length(0.05f);

        m_PieChart.setData(data);
//        m_PieChart.setScaleX(0.8f);
//        m_PieChart.setScaleY(0.8f);
        m_PieChart.invalidate();
        m_PieChart.animateY(1400, Easing.EaseInOutQuad);


    }

    private void SetUpPieChart(){

        m_PieChart.setDrawHoleEnabled(true);
        m_PieChart.setUsePercentValues(true);
        m_PieChart.setEntryLabelTextSize(12);
        m_PieChart.setEntryLabelColor(Color.BLACK);
        m_PieChart.setCenterText("Spending by Category");
        m_PieChart.setCenterTextSize(18);
        m_PieChart.getDescription().setEnabled(false);
        m_PieChart.setDrawEntryLabels(false);


        Legend legend = m_PieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(true);
        legend.setEnabled(true);
        Log.i("Pie Chart","Pie Chart Set Up Successful");
    }


    ExecutorService m_Executor = Executors.newSingleThreadExecutor();
    private final Handler m_Handler = new Handler(Looper.getMainLooper());

    private FirebaseAuth m_Auth = FirebaseAuth.getInstance();
    private String a_Uid = Objects.requireNonNull(m_Auth.getCurrentUser()).getUid();
    Months currentMonth = Util.getMonth();
    private DatabaseReference m_ExpenseRef = FirebaseDatabase.getInstance().getReference().child("expenses").child(a_Uid);
    private PieChart m_PieChart;
    private Map<String, Double> m_Categories;
    private RecyclerView m_RecyclerView;
    private ExpenseAdapter m_Adapter;
    private List<Data> monthlyDataList=new ArrayList<>();
    private Map<String, Double> spendingCategories=new HashMap<>();
    private static final String TAG = "ExpenseActivity";
    private Double totalMonthlyExpense = 0.0;

}
