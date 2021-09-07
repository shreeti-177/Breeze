//package com.example.personalfinance;
//
//import android.content.Intent;
//import android.graphics.Color;
//import android.graphics.drawable.Drawable;
//import android.icu.text.Transliterator;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.Adapter;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.firebase.ui.database.FirebaseRecyclerOptions;
//import com.github.mikephil.charting.charts.LineChart;
//import com.github.mikephil.charting.charts.PieChart;
//import com.github.mikephil.charting.components.Description;
//import com.github.mikephil.charting.components.Legend;
//import com.github.mikephil.charting.components.LimitLine;
//import com.github.mikephil.charting.components.XAxis;
//import com.github.mikephil.charting.components.YAxis;
//import com.github.mikephil.charting.data.Entry;
//import com.github.mikephil.charting.data.LineData;
//import com.github.mikephil.charting.data.LineDataSet;
//import com.github.mikephil.charting.formatter.ValueFormatter;
//import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
//import com.github.mikephil.charting.utils.ColorTemplate;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.android.material.bottomnavigation.BottomNavigationView;
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.google.android.material.navigation.NavigationBarView;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.plaid.client.model.Transaction;
//import com.plaid.client.model.TransactionsGetRequest;
//import com.plaid.client.model.TransactionsGetResponse;
//import com.plaid.client.model.TransactionsRefreshRequest;
//import com.plaid.client.model.TransactionsRefreshResponse;
//import com.plaid.client.request.PlaidApi;
//
//import org.jetbrains.annotations.NotNull;
//import org.joda.time.DateTime;
//import org.joda.time.Days;
//import org.joda.time.Months;
//import org.joda.time.MutableDateTime;
//
//import java.io.IOException;
//import java.text.DateFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.GregorianCalendar;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//import java.util.concurrent.Executor;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import retrofit2.Response;
//
//public class HomeActivity extends AppCompatActivity {
//
//    private TextView m_UserName;
//    private RecyclerView m_HomePageView;
//    private HomeAdapter m_HomeAdapter;
//    private List<Summary> m_SummaryList=new ArrayList<>();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.activity_home);
//
//
//
//        //Set Welcome Message for User
//        m_UserName=findViewById(R.id.userNameField);
//        m_UserName.setText(Util.m_Auth.getCurrentUser().getDisplayName());
//
//        //Initialize Dashboard Chart
//        m_LineChart=findViewById(R.id.dashboardChart);
//        SetUpLineChart();
//
//        //Fetch Summary Data and Load Chart
//        m_SummaryRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
//                    Summary a_Summary = dataSnapshot.getValue(Summary.class);
//                    m_SummaryList.add(a_Summary);
//                }
//                //Set Up chart with m_SummaryList
//                LoadLineChart();
//            }
//
//            @Override
//            public void onCancelled(@NonNull @NotNull DatabaseError error) {
//
//            }
//        });
//
//        //Set Up Recycler View for Recent Transactions
//        m_HomePageView=findViewById(R.id.homePage);
//        m_HomePageView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//        m_HomeAdapter=new HomeAdapter();
//        m_HomePageView.setAdapter(m_HomeAdapter);
//
//
//        //Fetch Recent Transactions and send data to Recycler View for display
//        //Order by "day" to get transactions in ascending order
////.startAt(Util.getFirstDay()).endAt(Util.getCurrentDay())
//        m_ExpenseRef.orderByChild("day").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    Data data = dataSnapshot.getValue(Data.class);
//                    Double a_Amount = data.getAmount();
//                    if (data.getMerchant()==null||data.getDate()==null||a_Amount==null){
//                        continue;
//                    }
//                    m_AllTransactions.add(data);
//                }
//
//                //Notify Transactions Adapter that dataset has changed (due to added transactions)
//                m_HomeAdapter.notifyDataSetChanged();
//
//                //Add in the new transactions and update the recycler view
//                m_HomeAdapter.SetTransactions(m_AllTransactions);
//            }
//
//            @Override
//            public void onCancelled(@NonNull @NotNull DatabaseError error) {
//                Log.i("Error retrieving expenses",error.getDetails());
//            }
//        });
//
//        //Set Up Onclick Listeners for Bottom Navigation Options
//        SetUpOnClickListeners();
//
//        Util.m_Executor.execute(()->{
//            CreateSummary();
//        });
//
//    }
//
//
//    private void SetUpLineChart(){
//        m_LineChart.setTouchEnabled(true);
//        m_LineChart.setPinchZoom(true);
////        m_LineChart.setBackground(getDrawable(R.drawable.chart));
//
//        m_LineChart.setDrawGridBackground(false);
//        m_LineChart.getAxisRight().setEnabled(false);
//        m_LineChart.getDescription().setEnabled(false);
//
//        m_LineChart.getAxisLeft().setDrawGridLines(false);
//        m_LineChart.getXAxis().setDrawGridLines(false);
//
//        //set legend disable or enable to hide {the left down corner name of graph}
//        Legend legend = m_LineChart.getLegend();
//        legend.setEnabled(false);
//    }
//
//    private void LoadLineChart(){
////        Description description = new Description();
////        description.setText("Days Data");
////        m_LineChart.setDescription(description);
//
//        final List<String> a_XAxisLabel = new ArrayList<>();
//        for (int i=0;i<m_SummaryList.size();i++){
//            Log.i("Expenses",String.valueOf(m_SummaryList.get(i).getExpense()));
//            Float y_value = m_SummaryList.get(i).getExpense().floatValue();
//            a_XAxisLabel.add(m_SummaryList.get(i).getMonth());
//            entryArrayList.add(new Entry(i, y_value));
//        }
//
//        XAxis a_XAxis = m_LineChart.getXAxis();
//        a_XAxis.setValueFormatter(new ValueFormatter() {
//            @Override
//            public String getFormattedValue(float value) {
//                return a_XAxisLabel.get((int) value);
//            }
//        });
//
//        a_XAxis.setLabelRotationAngle(-45f);
//        a_XAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        //LineDataSet is the line on the graph
//        LineDataSet lineDataSet = new LineDataSet(entryArrayList, "This is y bill");
//
////        lineDataSet.setLineWidth(4f);
////        lineDataSet.setColor(Color.BLACK);
////        lineDataSet.setDrawCircles(true);
//        lineDataSet.setCircleHoleColor(Color.WHITE);
//        lineDataSet.setCircleColor(Color.BLACK);
//        lineDataSet.setHighLightColor(Color.YELLOW);
//        lineDataSet.setDrawValues(false);
//        lineDataSet.setCircleRadius(5f);
//
//        //to make the smooth line as the graph is adapt change so smooth curve
//        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
//
//        //to enable the cubic density : if 1 then it will be sharp curve
//        lineDataSet.setCubicIntensity(0.2f);
//
//        //to fill the below of smooth line in graph
//        lineDataSet.setDrawFilled(false);
////        lineDataSet.setFillColor(Color.BLACK);
//        //set the transparency
//        lineDataSet.setFillAlpha(80);
//
//        //set the gradiant then the above draw fill color will be replace
////            Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.gradiant);
////            lineDataSet.setFillDrawable(drawable);
//
//
////            lineDataSet.setColor(ColorTemplate.COLORFUL_COLORS.length);
//
//
//        ArrayList<ILineDataSet> iLineDataSetArrayList = new ArrayList<>();
//        iLineDataSetArrayList.add(lineDataSet);
//
//        //LineData is the data accord
//        LineData lineData = new LineData(iLineDataSetArrayList);
//        lineData.setValueTextSize(8f);
//        lineData.setValueTextColor(Color.BLACK);
//
//
//        m_LineChart.setData(lineData);
//        m_LineChart.invalidate();
//
//    }
//
//
//    private void SetUpOnClickListeners(){
//
//        //Set Up events on click for setting budget and plans
////        View optionsLayout=findViewById(R.id.include_options);
//
////        ImageView setBudgetOption = optionsLayout.findViewById(R.id.myBudget);
////        ImageView setPlansOption = optionsLayout.findViewById(R.id.myPlans);
//
////        setBudgetOption.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), BudgetActivity.class)));
////        setPlansOption.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), PlansActivity.class)));
//
//        //Set up events on click for the bottom navigation
//        View bottomNavLayout = findViewById(R.id.bottomNavigation);
//        BottomNavigationView bottomNavigation = bottomNavLayout.findViewById(R.id.bottom_navigation);
//
//        //Onclick for '+' prompts the user to manually add a transaction (helpful in cash transactions)
//        FloatingActionButton addTransactionsOptions = bottomNavLayout.findViewById(R.id.addCashTransactions);
//        addTransactionsOptions.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), CashTransactionActivity.class)));
//
//        //SetUp OnClick for the remaining menu items
//        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
//                switch (item.getItemId()){
//                    case R.id.homePage:
//                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
//                        //Open Categorical Expenses activity if category icon is selected
//                    case R.id.myCategories:
//                        startActivity(new Intent(getApplicationContext(), ExpenseActivity.class));
//                        return true;
//                    //Open Habits activity if analytics icon is selected
//                    case R.id.myHabits:
//                        startActivity(new Intent(getApplicationContext(), HabitsActivity.class));
//                        return true;
//                    default:
//                        return true;
//                }
//            }
//        });
//    }
//
//    private void CreateSummary(){
//        m_ExpenseRef.orderByChild("day").startAt(Util.getFirstDay()).endAt(Util.getCurrentDay()).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                Double monthlyExpense=0.0;
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    Data data = dataSnapshot.getValue(Data.class);
//                    Log.i("Here",String.valueOf(data.getDate()));
//                    Double expenseAmount = data.getAmount();
//                    monthlyExpense+=expenseAmount;
//                }
//
//                //Set month and total expense values for a Summary object
//                m_Summary.setMonth(String.valueOf(month));
//                m_Summary.setExpense(m_MonthlyExpense);
//                m_Summary.SetNumMonth(Util.getMonth().getMonths());
//                AddSummarytoDatabase(m_Summary);
//            }
//
//            @Override
//            public void onCancelled(@NonNull @NotNull DatabaseError error) {
//                Log.i("Error creating summary",error.getDetails());
//            }
//        });
//    }
//
//
//    private void FetchSummaries(){
//        m_SummaryRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
//                    Summary a_Summary = dataSnapshot.getValue(Summary.class);
//                    m_SummaryList.add(a_Summary);
//                }
//                SetUpLineChart();
//                LoadLineChart();
//            }
//
//            @Override
//            public void onCancelled(@NonNull @NotNull DatabaseError error) {
//                Log.i("Error retrieving summary",error.getDetails());
//            }
//        });
//    }
//
//
//
//
//    private void AddSummarytoDatabase(Summary a_Summary){
//        m_SummaryRef.child(String.valueOf(a_Summary.getNumMonth())).setValue(a_Summary).addOnCompleteListener(new OnCompleteListener<Void>() {
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
//
//    }
//
//
//
//    public List<Data> m_AllTransactions = new ArrayList<>();
//    private Summary m_Summary=new Summary();
//    ArrayList<Entry> entryArrayList = new ArrayList<>();
//
//    DateTime dt = DateTime.now();
//    String month = dt.toString("MMM-YYYY");
//
//    private RecyclerView m_TransactionView;
//    private LineChart m_LineChart;
//    private LineChart volumeReportChart;
//    private DatabaseReference m_ExpenseRef = FirebaseDatabase.getInstance().getReference().child("expenses").child(Util.getUid());
//    private DatabaseReference m_SummaryRef = FirebaseDatabase.getInstance().getReference().child("summary").child(Util.getUid());
//    public DatabaseReference m_BaseDataRef = FirebaseDatabase.getInstance().getReference().child("base-data").child(Util.getUid());
//
//    private Double m_MonthlyExpense = 0.0;
//    private List<Transaction> m_Transactions;
//    private TransactionAdapter m_TAdapter;
//    private DashboardAdapter m_DAdapter;
//    private String TAG="HomeActivity";
//}
