package com.example.personalfinance;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.security.AccessController.getContext;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.ChartViewHolder> {
    private DatabaseReference m_SummaryRef = FirebaseDatabase.getInstance().getReference().child("summary").child(Util.getUid());
    private static final String TAG = "DashboardAdapter";
    private List<Summary> m_SummaryList=new ArrayList<>();
    private Summary m_Summary = new Summary();
    private List<Double> m_Expenses=new ArrayList<>();
    ArrayList<Entry> entryArrayList = new ArrayList<>();

    DateTime dt = DateTime.now();
    String month = dt.toString("MMM-YYYY");

    public DashboardAdapter(){}

    public void SetMonthlyExpense(Summary a_Summary){
        this.m_Summary=a_Summary;
        m_SummaryList.add(m_Summary);

//        this.m_Expenses.addAll(a_Summary.values());
//        Summary a_Object=new Summary(month,a_Summary.get(month));
        m_SummaryRef.child(String.valueOf(Util.getMonth())).setValue(a_Summary).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "SetMonthlyExpenseSummary: success");
//                    Toast.makeText(getApplicationContext(), "Monthly expense summary set successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Log.w(TAG, "SetMonthlyExpenseSummary: failure", task.getException());
//                    Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

//    public void SetMonthlyExpense(Map<String, Double> a_Summary){
//        this.m_Summary=a_Summary;
//        this.m_Expenses.addAll(a_Summary.values());
//        Summary a_Object=new Summary(month,a_Summary.get(month));
//        m_SummaryRef.child(String.valueOf(Util.getMonth())).setValue(a_Object).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull @NotNull Task<Void> task) {
//                if (task.isSuccessful()) {
//                    Log.d(TAG, "SetMonthlyExpenseSummary: success");
////                    Toast.makeText(getApplicationContext(), "Monthly expense summary set successfully", Toast.LENGTH_SHORT).show();
//                } else {
//                    Log.w(TAG, "SetMonthlyExpenseSummary: failure", task.getException());
////                    Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }

//    public void SetMonthlyExpense(Double a_MonthlyExpense){
//        m_SummaryRef.child(String.valueOf(Util.getMonth())).child("monthly-expense").setValue(a_MonthlyExpense).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull @NotNull Task<Void> task) {
//                if (task.isSuccessful()) {
//                    Log.d(TAG, "SetMonthlyExpenseSummary: success");
////                    Toast.makeText(getApplicationContext(), "Monthly expense summary set successfully", Toast.LENGTH_SHORT).show();
//                } else {
//                    Log.w(TAG, "SetMonthlyExpenseSummary: failure", task.getException());
////                    Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }

    @NonNull
    @NotNull
    @Override
    public DashboardAdapter.ChartViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieve_dashboard, parent, false);
        return new DashboardAdapter.ChartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull DashboardAdapter.ChartViewHolder holder, int position) {
        Summary a_Summary = m_SummaryList.get(position);
        Float y_value=(m_SummaryList.get(position).getExpense().floatValue());
        entryArrayList.add(new Entry(position, y_value));
        holder.SetUpLineChart();
        holder.LoadLineChart();

    }

    @Override
    public int getItemCount() {
        return m_SummaryList.size();
    }

    public class ChartViewHolder extends RecyclerView.ViewHolder{
        private LineChart m_LineChart;


        public ChartViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            m_LineChart=itemView.findViewById(R.id.dashboardChart);
        }
        private void SetUpLineChart(){
            m_LineChart.setTouchEnabled(false);
            m_LineChart.setDragEnabled(true);
            m_LineChart.setScaleEnabled(true);
            m_LineChart.setPinchZoom(false);
            m_LineChart.setDrawGridBackground(false);
            m_LineChart.setMaxHighlightDistance(200);
            m_LineChart.setViewPortOffsets(0, 0, 0, 0);
        }

        private void LoadLineChart(){
            Description description = new Description();
            description.setText("Days Data");

            m_LineChart.setDescription(description);


//
//            for (int i=0;i<m_SummaryList.size();i++){
//                Float y_value=(m_SummaryList.get(i).getExpense().floatValue());
//                Log.i("Here data",String.valueOf(y_value));
//                entryArrayList.add(new Entry(i, y_value));
//            }

//            List<String> xLabels = new ArrayList<>();
//            xLabels.addAll(m_Summary.keySet());

//            entryArrayList.add(new Entry(0, 60f, "1"));
//            entryArrayList.add(new Entry(1, 55f, "2"));
//            entryArrayList.add(new Entry(2, 60f, "3"));
//            entryArrayList.add(new Entry(3, 40f, "4"));
//            entryArrayList.add(new Entry(4, 45f, "5"));
//            entryArrayList.add(new Entry(5, 36f, "6"));
//            entryArrayList.add(new Entry(6, 30f, "7"));
//            entryArrayList.add(new Entry(7, 40f, "8"));
//            entryArrayList.add(new Entry(8, 45f, "9"));
//            entryArrayList.add(new Entry(9, 60f, "10"));
//            entryArrayList.add(new Entry(10, 45f, "10"));
//            entryArrayList.add(new Entry(11, 20f, "10"));


//            XAxis xAxis = m_LineChart.getXAxis();
//            xAxis.setValueFormatter(new ValueFormatter() {
//                @Override
//                public String getFormattedValue(float value, AxisBase axis) {
//                    return xLabels.get((int) value);
//
//                }
//            });
            //LineDataSet is the line on the graph
            LineDataSet lineDataSet = new LineDataSet(entryArrayList, "This is y bill");

            lineDataSet.setLineWidth(5f);
            lineDataSet.setColor(Color.GRAY);
            lineDataSet.setCircleHoleColor(Color.GREEN);
            lineDataSet.setCircleColor(R.color.white);
            lineDataSet.setHighLightColor(Color.RED);
            lineDataSet.setDrawValues(false);
            lineDataSet.setCircleRadius(10f);
            lineDataSet.setCircleColor(Color.YELLOW);

            //to make the smooth line as the graph is adrapt change so smooth curve
            lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            //to enable the cubic density : if 1 then it will be sharp curve
            lineDataSet.setCubicIntensity(0.2f);

            //to fill the below of smooth line in graph
            lineDataSet.setDrawFilled(true);
            lineDataSet.setFillColor(Color.BLACK);
            //set the transparency
            lineDataSet.setFillAlpha(80);

            //set the gradiant then the above draw fill color will be replace
//            Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.gradiant);
//            lineDataSet.setFillDrawable(drawable);

            //set legend disable or enable to hide {the left down corner name of graph}
            Legend legend = m_LineChart.getLegend();
            legend.setEnabled(false);

            //to remove the cricle from the graph
            lineDataSet.setDrawCircles(false);

//            lineDataSet.setColor(ColorTemplate.COLORFUL_COLORS);


            ArrayList<ILineDataSet> iLineDataSetArrayList = new ArrayList<>();
            iLineDataSetArrayList.add(lineDataSet);

            //LineData is the data accord
            LineData lineData = new LineData(iLineDataSetArrayList);
            lineData.setValueTextSize(13f);
            lineData.setValueTextColor(Color.BLACK);


            m_LineChart.setData(lineData);
            m_LineChart.invalidate();

        }
    }
}
