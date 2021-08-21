package com.example.personalfinance;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.plaid.client.model.Transaction;
import com.plaid.client.model.TransactionsGetRequest;
import com.plaid.client.model.TransactionsGetResponse;
import com.plaid.client.request.PlaidApi;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.joda.time.Years;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Response;

public class HomePageActivity extends AppCompatActivity {
    DateTime dt = DateTime.now();
    String month = dt.toString("MMM-YYYY");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        m_Executor.execute(()->{
            DatabaseReference m_BaseDataRef = FirebaseDatabase.getInstance().getReference().child("base-data").child(a_Uid);
            m_BaseDataRef.child("accessToken").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    String m_AccessToken = String.valueOf(snapshot.getValue());
                    Log.i("Get Token",m_AccessToken);
                    Log.i("Here","Before Calling Fetch");
                    m_Executor.execute(()->{
                        FetchTransactions(m_AccessToken);
                        m_Executor.execute(()->{
                            try {
                                AddTransactionsToDatabase();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        });
                        });
                }
                @Override
                public void onCancelled (@NonNull @NotNull DatabaseError error){
                    Log.i(TAG, "Error retrieving access token");
                }
            });

        });

        m_Executor.execute(()->{
            DatabaseReference m_SummaryRef = FirebaseDatabase.getInstance().getReference().child("summary").child(a_Uid).child(String.valueOf(currentMonth));
            m_SummaryRef.child("month").setValue(month).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "SetMonthlySummary: success");
                    Toast.makeText(getApplicationContext(), "Monthly budget summary set successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Log.w(TAG, "SetMonthlySummary: failure", task.getException());
                    Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
        setContentView(R.layout.activity_home_page);

        CardView m_MyBudget = findViewById(R.id.myBudget);
        FloatingActionButton m_AddCashTransactions = findViewById(R.id.addCashTransactions);

        m_AddCashTransactions.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), CashTransactionActivity.class)));
        m_MyBudget.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), BudgetActivity.class)));

        CardView m_MyCategories = findViewById(R.id.myCategories);
        m_MyCategories.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ExpenseActivity.class)));

        CardView m_MyHabits = findViewById(R.id.myHabits);
        m_MyHabits.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), HabitsActivity.class)));

        CardView m_MyPlans = findViewById(R.id.myPlans);
        m_MyPlans.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), PlansActivity.class)));


    }

    private void AddTransactionsToDatabase() throws ParseException {
        for(Transaction t: m_Transactions) {
            Data a_Expense = CreateExpenseObject(t);
            m_ExpenseRef.child(a_Expense.getId()).setValue(a_Expense).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "AddTransaction: success");
                    Toast.makeText(getApplicationContext(), "Transaction added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Log.w(TAG, "AddTransaction: failure", task.getException());
                    Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private Data CreateExpenseObject(Transaction t) throws ParseException {
        Double a_Amount = t.getAmount();
        String a_Merchant = t.getMerchantName();
        String a_Note = t.getName();
        String a_ExpenseId = t.getTransactionId();
        String a_DateText = t.getDate();

        String a_CategoryName = t.getCategory().get(0);

        List<String> a_Categories = Util.GetExistingCategories();

        if(!(a_Categories.contains(a_CategoryName))){
            if(a_CategoryName.equals("Food and Drink")){
                a_CategoryName="Food";
            }
            else if(a_CategoryName.equals("Shops")) {
                a_CategoryName = "Merchandise";
            }
            else {
                a_CategoryName = "Miscellaneous";
            }
        }

        SimpleDateFormat m_CurrentFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date a_ObjectDate = m_CurrentFormat.parse(a_DateText);

//        Log.i("Date Object",String.valueOf(a_ObjectDate));


        Calendar calendar = new GregorianCalendar();
        calendar.setTime(a_ObjectDate);

        DateFormat m_TargetFormat = new SimpleDateFormat("MM-dd-yyyy");
        String a_Date = m_TargetFormat.format(a_ObjectDate);

        MutableDateTime a_Epoch = new MutableDateTime();
        a_Epoch.setDate(0);
        MutableDateTime a_TransactionTime = new MutableDateTime();
        a_TransactionTime.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH));
//        Log.i("DDate transaction time", String.valueOf(a_TransactionTime));

        DateTime a_now=new DateTime(a_TransactionTime);

        Months a_Month = Months.monthsBetween(a_Epoch,a_now);
        Days a_Day = Days.daysBetween(a_Epoch,a_now);
//        Log.i("DDate get month", String.valueOf(a_Month.getMonths()));

        Data a_Expense = new Data(a_ExpenseId, a_CategoryName, a_Merchant, a_Amount, a_Date, a_Month.getMonths(), a_Day.getDays(), a_Note);

        return a_Expense;

    }

    private void FetchTransactions(String a_AccessToken){
        LocalDate startDate = LocalDate.now().withDayOfMonth(1);
//        LocalDate startDate = LocalDate.now().minusMonths(1).withDayOfMonth(1);
//        LocalDate endDate = LocalDate.now().minusMonths(1).withDayOfMonth(31);
        LocalDate endDate=LocalDate.now();

        TransactionsGetRequest request = new TransactionsGetRequest()
                .accessToken(a_AccessToken)
                .startDate(startDate)
                .endDate(endDate);
        Log.i("Request",String.valueOf(request));
        Response<TransactionsGetResponse> apiResponse = null;

        for (int i = 0; i < 10; i++) {
            try {
                PlaidApi a_Client = Util.PlaidClient();
                Log.i("Here plaid client",String.valueOf(a_Client));
                apiResponse = a_Client.transactionsGet(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (apiResponse.isSuccessful()) {
                Log.i("Success","It's successful");
                break;
            } else {
                try {
                    Log.i("Transaction", "Not ready");
                    Thread.sleep(5000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        m_Transactions = new ArrayList<>();
        assert apiResponse.body() != null;
        Log.i("Total Number of Transactions",String.valueOf(apiResponse.body().getTotalTransactions()));
        m_Transactions.addAll(apiResponse.body().getTransactions());

        Log.i("All transactions",String.valueOf(m_Transactions));
//        Log.i("Here", "Returning from the function");
    }


    ExecutorService m_Executor = Executors.newSingleThreadExecutor();
    private FirebaseAuth m_Auth = FirebaseAuth.getInstance();
    private String a_Uid = Objects.requireNonNull(m_Auth.getCurrentUser()).getUid();
    Months currentMonth = Util.getMonth();
    private DatabaseReference m_ExpenseRef = FirebaseDatabase.getInstance().getReference().child("expenses").child(a_Uid).child(String.valueOf(currentMonth));
    private List<Transaction> m_Transactions;
    private String TAG="HomePageActivity";

}