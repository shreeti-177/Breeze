package com.example.personalfinance;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.plaid.client.model.Transaction;
import com.plaid.client.model.TransactionsGetRequest;
import com.plaid.client.model.TransactionsGetResponse;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import retrofit2.Response;

public class BackgroundTasks extends AppCompatActivity {
    BackgroundTasks(){}


    static public void StoreExpenseSummary(Double a_TotalExpense){
        Util.GetSummaryReference().child("expense").setValue(a_TotalExpense)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "SetMonthlyExpenseSummary: success");
                    } else {
                        Log.w(TAG, "SetMonthlyExpenseSummary: failure", task.getException());
                    }
                });
    }

    static public void StoreBudgetSummary(Double a_TotalBudget){
        Util.GetSummaryReference().child("budget").setValue(a_TotalBudget)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "SetMonthlyBudgetSummary: success");
                    } else {
                        Log.w(TAG, "SetMonthlyBudgetSummary: failure", task.getException());
                    }
                });
    }

    static public void UpdateOnlineTransactions(){
        Util.m_Executor.execute(()->{
            GetAccessToken();
        });
    }

    public static void GetAccessToken(){
        m_BaseDataRef.child("access-token").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                m_AccessToken = String.valueOf(snapshot.getValue());
                Log.i("Get Token", m_AccessToken);
                Util.m_Executor.execute(()->{
                    try {
                        FetchTransactions(m_AccessToken);
                        AddTransactionsToDatabase();
                        FetchExistingSummaries();
                        UpdateSummaries();
                        Log.i("Ends","Reaches here");

                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.e("Error fetching access token",error.getMessage(),error.toException());
            }
        });
    }

    public static void FetchExistingSummaries() {
        for(int month: expenseSummary.keySet()){
            FirebaseDatabase.getInstance().getReference().child("summary").child(Util.getUid())
                    .child(String.valueOf(month)).child("expense")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Double prevVal = (Double) dataSnapshot.getValue();
                        Double newVal = expenseSummary.get(month)+prevVal;
                        expenseSummary.put(month, newVal);
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {}
                });
        }
    }

    public static void UpdateSummaries(){
        for(int month:expenseSummary.keySet()){

            DateTime d = new DateTime();

            MutableDateTime a_Epoch = new MutableDateTime();
            a_Epoch.setDate(0);
            a_Epoch.addMonths(month+1);

//            DateTime date = new DateTime();
            String currentMonth = a_Epoch.toString("MMM-yyyy");
            Log.i("HEre month", currentMonth);
            Summary summary = new Summary(currentMonth, month, expenseSummary.get(month));

            FirebaseDatabase.getInstance().getReference().child("summary").child(Util.getUid())
                    .child(String.valueOf(month)).setValue(summary)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "AddTransaction: success");
                        } else {
                            Log.w(TAG, "AddTransaction: failure", task.getException());
                        }
                    });
        }
        Log.i("Update summ","Reaches here");
    }

    public static void AddTransactionsToDatabase() throws ParseException {
        expenseSummary.clear();
        for(Transaction t: m_OnlineTransactions) {
            if(t.getAmount()<0){
                continue;
            }
            Data a_Expense = CreateExpenseObject(t);
            if (!expenseSummary.containsKey(a_Expense.getMonth())) {
                expenseSummary.put(a_Expense.getMonth(), 0.0);
            }
            Double prevVal = expenseSummary.get(a_Expense.getMonth());
            Double newVal = a_Expense.getAmount() + prevVal;
            expenseSummary.put(a_Expense.getMonth(), newVal);
            Log.i("Pre summ","Reaches here");

            Util.GetExpenseReference().child(a_Expense.getId()).setValue(a_Expense).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "AddTransaction: success");
                } else {
                    Log.w(TAG, "AddTransaction: failure", task.getException());
                }
            });
        }
    }

    public static Data CreateExpenseObject(Transaction t) throws ParseException {
        Double amount = t.getAmount();
        String merchant = t.getMerchantName();
        String note = t.getName();
        String expenseId = t.getTransactionId();
        LocalDate dateText = t.getDate();

        String categoryName = Objects.requireNonNull(t.getCategory()).get(0);

        List<String> categories = Util.GetExistingCategories();

        if(!(categories.contains(categoryName))){
            if(categoryName.equals("Food and Drink")){
                categoryName="Food";
            }
            else if(categoryName.equals("Shops")) {
                categoryName = "Merchandise";
            }
            else {
                categoryName = "Miscellaneous";
            }
        }

        SimpleDateFormat m_CurrentFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date a_ObjectDate = m_CurrentFormat.parse(dateText.toString());


        Calendar calendar = new GregorianCalendar();
        calendar.setTime(a_ObjectDate);

        DateFormat m_TargetFormat = new SimpleDateFormat("MM-dd-yyyy");
        String a_Date = m_TargetFormat.format(a_ObjectDate);

        MutableDateTime a_Epoch = new MutableDateTime();
        a_Epoch.setDate(0);
        MutableDateTime a_TransactionTime = new MutableDateTime();
        a_TransactionTime.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH));

        DateTime a_now=new DateTime(a_TransactionTime);

        Months a_Month = Months.monthsBetween(a_Epoch,a_now);
        Days a_Day = Days.daysBetween(a_Epoch,a_now);

        Data a_Expense = new Data(expenseId, categoryName, merchant, amount, a_Date, a_Month.getMonths(), a_Day.getDays(), note);
//        a_Expense.setDateTime(new DateTime(a_ObjectDate));
        a_dateTime=new DateTime(a_ObjectDate);

        return a_Expense;
    }

    public static void FetchTransactions(String a_AccessToken) throws IOException {
        LocalDate startDate = LocalDate.now().minusYears(2);
//        LocalDate startDate = LocalDate.now().minusMonths(1).withDayOfMonth(1);
//        LocalDate endDate = LocalDate.now().minusMonths(1).withDayOfMonth(31);
        LocalDate endDate=LocalDate.now();

        TransactionsGetRequest request = new TransactionsGetRequest()
                .accessToken(a_AccessToken)
                .startDate(startDate)
                .endDate(endDate);

        Log.i("Request",String.valueOf(request));
        m_OnlineTransactions = new ArrayList<>();
        Response<TransactionsGetResponse> apiResponse = null;
        for (int i = 0; i < 5; i++) {
            apiResponse=(Util.PlaidClient().transactionsGet(request).execute());
            if (apiResponse.isSuccessful()) {
                break;
            } else {
                Log.i("Transaction","Not ready");
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {
                    Log.i("Error retrieving transactions",e.getMessage());
                }
            }
        }
        TransactionsGetResponse transactionResponse = apiResponse.body();
        Log.i("Api response",String.valueOf(apiResponse.body()));

        assert apiResponse.body() != null;
        Log.i("Total Number of Transactions",String.valueOf(apiResponse.body().getTotalTransactions()));
        m_OnlineTransactions.addAll(apiResponse.body().getTransactions());
    }

    private static Map<Integer, Double> expenseSummary =  new HashMap<>();
    private static DatabaseReference m_BaseDataRef = FirebaseDatabase.getInstance().getReference()
            .child("base-data").child(Util.getUid());
    public static List<Transaction> m_OnlineTransactions=new ArrayList<>();
    public static String m_AccessToken;
    private final static String TAG = "BackgroundTask";
    private static DateTime a_dateTime=new DateTime();

}
