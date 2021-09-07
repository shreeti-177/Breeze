package com.example.personalfinance;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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


public class CategoryFragment extends Fragment {

    private View m_RootView;

    public CategoryFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        m_RootView = inflater.inflate(R.layout.fragment_category, container, false);

        m_CategoryView = m_RootView.findViewById(R.id.categoryExpenses);
        LinearLayoutManager m_LinearLayoutManager = new LinearLayoutManager(getContext());
        m_CategoryView.setLayoutManager(m_LinearLayoutManager);
        m_CategoryView.setItemAnimator(new DefaultItemAnimator());

        m_Adapter = new ExpenseAdapter();
        m_CategoryView.setAdapter(m_Adapter);

        m_PieChart=m_RootView.findViewById(R.id.pieChart);
        SetUpPieChart();


        Integer currentMonth = Util.getMonth().minus(2).getMonths();
        Log.i("Month", String.valueOf(currentMonth));
        m_ExpenseRef.orderByChild("month").equalTo(currentMonth).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Data data = dataSnapshot.getValue(Data.class);
                    monthlyDataList.add(data);
                    totalMonthlyExpense+=data.getAmount();
                    Log.i("Month", data.getDate());
                }
                Log.i("Total Month Spending", String.valueOf(totalMonthlyExpense));
//                m_Executor.execute(()->{
//                    StoreMonthlyExpense(totalMonthlyExpense);
//                });
                LoadPieChart();
                m_Adapter.SetExpenses(monthlyDataList);
                m_Adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {}
        });
        return m_RootView;
    }
    private void StoreMonthlyExpense(Double a_Expense){
        DatabaseReference m_SummaryRef = FirebaseDatabase.getInstance().getReference().child("summary").child(a_Uid).child(String.valueOf(currentMonth));
        m_SummaryRef.child("expense").setValue(a_Expense).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "SetMonthlyExpenseSummary: success");
                    Toast.makeText(getContext(), "Monthly expense summary set successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Log.w(TAG, "SetMonthlyExpenseSummary: failure", task.getException());
                    Toast.makeText(getContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
    private void LoadPieChart(){
        ArrayList<PieEntry> entries = new ArrayList<>();
        expenseByCategory=Util.GetCategoricalExpense(monthlyDataList);


        for (String name: expenseByCategory.keySet()){
            Float spending = expenseByCategory.get(name).floatValue();
            if(spending<0){
                continue;
            }
            entries.add(new PieEntry(Objects.requireNonNull(expenseByCategory.get(name)).floatValue(), name));
//            Log.i("Category Keys", name);
//            Log.i("Category Values", spendingCategories.get(name).toString());
        }

        List<Integer> colors = new ArrayList<Integer>(){{
                add(Color.parseColor("#FAF3DF"));
                add(Color.parseColor("#FDE3D5"));
                add(Color.parseColor("#F1D0CD"));
                add(Color.parseColor("#BDACBD"));
                add(Color.parseColor("#9F9BB0"));
                add(Color.parseColor("#BABBD1"));
                add(Color.parseColor("#C6D5B1"));
                add(Color.parseColor("#F8ABB3"));
                add(Color.parseColor("#F1E1BF"));
                add(Color.parseColor("#B5F2F3"));
            }};


        PieDataSet dataSet = new PieDataSet(entries, "Expense Category");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.removeLast();
        dataSet.setLabel("");

        PieData data = new PieData(dataSet);
        data.setDrawValues(false);

        data.setValueFormatter(new PercentFormatter(m_PieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setValueLinePart1OffsetPercentage(1f);
        dataSet.setValueLinePart1Length(0.2f);
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
        m_PieChart.getDescription().setEnabled(false);
        m_PieChart.setDrawEntryLabels(false);


        Legend legend = m_PieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);
        legend.setEnabled(true);
    }


    ExecutorService m_Executor = Executors.newSingleThreadExecutor();
    private final Handler m_Handler = new Handler(Looper.getMainLooper());

    private FirebaseAuth m_Auth = FirebaseAuth.getInstance();
    private String a_Uid = Objects.requireNonNull(m_Auth.getCurrentUser()).getUid();
    Months currentMonth = Util.getMonth();
    private DatabaseReference m_ExpenseRef = FirebaseDatabase.getInstance().getReference().child("expenses").child(a_Uid);
    private PieChart m_PieChart;
    private Map<String, Double> m_Categories;
    private RecyclerView m_CategoryView;
    private ExpenseAdapter m_Adapter;
    private List<Data> monthlyDataList=new ArrayList<>();
    private Map<String, Double> expenseByCategory=new HashMap<>();
    private static final String TAG = "ExpenseActivity";
    private Double totalMonthlyExpense = 0.0;
}