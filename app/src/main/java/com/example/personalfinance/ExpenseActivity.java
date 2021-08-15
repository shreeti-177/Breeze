package com.example.personalfinance;

import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
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
        m_ExpenseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Data data = dataSnapshot.getValue(Data.class);
                    monthlyDataList.add(data);
                }
                Double totalAmount = 0.0;
                for (Data m:monthlyDataList) {
                    totalAmount+=m.getAmount();
                }
                Log.i("Total Month Spending", String.valueOf(totalAmount));
                SetUpPieChart();
                LoadPieChart();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {}
        });
    }

    private void LoadPieChart(){
        Log.i("Here", "Reaches Here:Load Pie Chart Data");
        ArrayList<PieEntry> entries = new ArrayList<>();
        HashMap<String, Double> spendingCategories = new HashMap<>();
        for (Data d: monthlyDataList){
            String a_category = Objects.requireNonNull(d.getCategory());
            if(!spendingCategories.containsKey(a_category)){
                spendingCategories.put(a_category, d.getAmount());
                continue;
            }
            Double newValue = spendingCategories.get(a_category) + d.getAmount();
            spendingCategories.put(a_category, newValue);
        }

        for (String name: spendingCategories.keySet()){
            entries.add(new PieEntry(Objects.requireNonNull(spendingCategories.get(name)).floatValue(), name));
            Log.i("Category Keys", name);
            Log.i("Category Values", spendingCategories.get(name).toString());
        }

        ArrayList<Integer> colors = new ArrayList<>();
        for (int color: ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }

        for (int color: ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color);
        }

        PieDataSet dataSet = new PieDataSet(entries, "Expense Category");
        dataSet.setColors(colors);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(m_PieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.2f);
        dataSet.setValueLinePart2Length(0.4f);

        m_PieChart.setData(data);

        m_PieChart.invalidate();
//        m_PieChart.animateY(1400, Easing.EaseInOutQuad);


    }

    private void SetUpPieChart(){

        m_PieChart.setDrawHoleEnabled(true);
        m_PieChart.setUsePercentValues(true);
        m_PieChart.setEntryLabelTextSize(12);
        m_PieChart.setEntryLabelColor(Color.BLACK);
        m_PieChart.setCenterText("Spending by Category");
        m_PieChart.setCenterTextSize(24);
        m_PieChart.getDescription().setEnabled(false);
        m_PieChart.setDrawEntryLabels(false);

        Legend legend = m_PieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(true);
        legend.setEnabled(true);
        Log.i("Pie Chart","Pie Chart Set Up Successful");
    }

    ExecutorService m_Executor = Executors.newSingleThreadExecutor();
    private FirebaseAuth m_Auth = FirebaseAuth.getInstance();
    private String a_Uid = Objects.requireNonNull(m_Auth.getCurrentUser()).getUid();
    Months currentMonth = Util.getMonth();
    private DatabaseReference m_ExpenseRef = FirebaseDatabase.getInstance().getReference().child("expenses").child(a_Uid).child(String.valueOf(currentMonth));
    private PieChart m_PieChart;
    private Map<String, Double> m_Categories;
    private List<Data> monthlyDataList=new ArrayList<>();
    private static final String TAG = "ExpenseActivity";

}
