package com.example.personalfinance;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private View m_RootView;
    private TextView m_UserName;
    private LineChart m_LineChart;
    private RecyclerView m_HomePageView;
    private HomeAdapter m_HomeAdapter;

    public List<Data> m_AllTransactions = new ArrayList<>();
    private Summary m_Summary=new Summary();


    DateTime dt = DateTime.now();
    String month = dt.toString("MMM-YYYY");
    private Double m_MonthlyExpense = 0.0;


    private DatabaseReference m_ExpenseRef = FirebaseDatabase.getInstance().getReference().child("expenses").child(Util.getUid());
    private DatabaseReference m_SummaryRef = FirebaseDatabase.getInstance().getReference().child("summary").child(Util.getUid());
    public DatabaseReference m_BaseDataRef = FirebaseDatabase.getInstance().getReference().child("base-data").child(Util.getUid());

    private List<Summary> m_SummaryList=new ArrayList<>();

    private final String TAG = "HomeFragment";

    public HomeFragment() {
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
        m_RootView = inflater.inflate(R.layout.fragment_home, container, false);

        //Set Welcome Message for User
        m_UserName=m_RootView.findViewById(R.id.userNameField);
        m_UserName.setText(Util.m_Auth.getCurrentUser().getDisplayName());

        //Initialize Dashboard Chart
        m_LineChart=m_RootView.findViewById(R.id.dashboardChart);
        SetUpLineChart();

        m_SummaryList.clear();
        //Fetch Summary Data and Load Chart
        m_SummaryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Summary a_Summary = dataSnapshot.getValue(Summary.class);
                    m_SummaryList.add(a_Summary);
                    Log.i("Summary",String.valueOf(a_Summary.getMonth()));
                }
                //Set Up chart with m_SummaryList
                LoadLineChart();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        //Set Up Recycler View for Recent Transactions
        m_HomePageView=m_RootView.findViewById(R.id.homePage);
        m_HomePageView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        m_HomeAdapter=new HomeAdapter();
        m_HomePageView.setAdapter(m_HomeAdapter);


        //Fetch Recent Transactions and send data to Recycler View for display
        //Order by "day" to get transactions in ascending order
//.startAt(Util.getFirstDay()).endAt(Util.getCurrentDay())
        m_ExpenseRef.orderByChild("day").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Data data = dataSnapshot.getValue(Data.class);
                    Double a_Amount = data.getAmount();
                    if (data.getMerchant()==null||data.getDate()==null||a_Amount==null){
                        continue;
                    }
                    m_AllTransactions.add(data);
                }

                //Notify Transactions Adapter that dataset has changed (due to added transactions)
                m_HomeAdapter.notifyDataSetChanged();

                //Add in the new transactions and update the recycler view
                m_HomeAdapter.SetTransactions(m_AllTransactions);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.i("Error retrieving expenses",error.getDetails());
            }
        });

        return m_RootView;
    }

    private void SetUpLineChart(){
        m_LineChart.setTouchEnabled(true);
        m_LineChart.setPinchZoom(true);
//        m_LineChart.setBackground(getDrawable(R.drawable.chart));

        m_LineChart.setDrawGridBackground(false);
        m_LineChart.getAxisRight().setEnabled(false);
        m_LineChart.getDescription().setEnabled(false);

        m_LineChart.getAxisLeft().setDrawGridLines(false);
        m_LineChart.getXAxis().setDrawGridLines(false);

        //set legend disable or enable to hide {the left down corner name of graph}
        Legend legend = m_LineChart.getLegend();
        legend.setEnabled(false);
    }

    private void LoadLineChart(){
        ArrayList<Entry> entryArrayList = new ArrayList<>();
//        Description description = new Description();
//        description.setText("Days Data");
//        m_LineChart.setDescription(description);

        List<String> a_XAxisLabel = new ArrayList<>();
        for (int i=0;i<m_SummaryList.size();i++){
            Log.i("Expenses",String.valueOf(m_SummaryList.get(i).getExpense()));
            Float y_value = m_SummaryList.get(i).getExpense().floatValue();
            a_XAxisLabel.add(m_SummaryList.get(i).getMonth());
            entryArrayList.add(new Entry(i, y_value));
        }

        XAxis a_XAxis = m_LineChart.getXAxis();
        m_LineChart.getAxisLeft().setDrawLabels(false);
//        m_LineChart.getAxisLeft().setEnabled(false);

        a_XAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return a_XAxisLabel.get((int) value);
            }
        });

        a_XAxis.setLabelRotationAngle(-45f);
        a_XAxis.setGranularityEnabled(true);

        a_XAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //LineDataSet is the line on the graph
        LineDataSet lineDataSet = new LineDataSet(entryArrayList, "This is y bill");

//        lineDataSet.setLineWidth(4f);
//        lineDataSet.setColor(Color.BLACK);
//        lineDataSet.setDrawCircles(true);
        lineDataSet.setCircleHoleColor(Color.WHITE);
        lineDataSet.setCircleColor(Color.BLACK);
        lineDataSet.setHighLightColor(Color.YELLOW);
        lineDataSet.setDrawValues(true);
        lineDataSet.setCircleRadius(5f);

        //to make the smooth line as the graph is adapt change so smooth curve
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        //to enable the cubic density : if 1 then it will be sharp curve
        lineDataSet.setCubicIntensity(0.2f);

        //to fill the below of smooth line in graph
        lineDataSet.setDrawFilled(false);
//        lineDataSet.setFillColor(Color.BLACK);
        //set the transparency
        lineDataSet.setFillAlpha(80);

        //set the gradiant then the above draw fill color will be replace
//            Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.gradiant);
//            lineDataSet.setFillDrawable(drawable);


//            lineDataSet.setColor(ColorTemplate.COLORFUL_COLORS.length);


        ArrayList<ILineDataSet> iLineDataSetArrayList = new ArrayList<>();
        iLineDataSetArrayList.add(lineDataSet);

        //LineData is the data accord
        LineData lineData = new LineData(iLineDataSetArrayList);
        lineData.setValueTextSize(8f);
        lineData.setValueTextColor(Color.BLACK);

        m_LineChart.setData(lineData);
        m_LineChart.invalidate();

    }
}