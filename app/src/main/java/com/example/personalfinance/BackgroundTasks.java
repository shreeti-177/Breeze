//
// Implementation of the BackgroundTasks class
// This class has static methods to fetch and update any new transactions from the Plaid API
// Upon finding any new transactions, it implements methods to update the Firebase database in real time
//
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

import retrofit2.Response;

public class BackgroundTasks extends AppCompatActivity {

    //default empty constructor
    BackgroundTasks(){}

    /**/
    /*
    * NAME
        BackgroundTasks::StoreBudgetSummary() - Adds the total set budget to the summary tree

    * SYNOPSIS
        static public void StoreBudgetSummary(Double a_TotalBudget);
        * a_TotalBudget => the total updated budget set for the month

    * DESCRIPTION
        Based on a recent total budget passed, this function will attempt to access the summary tree
        in the Firebase database. Then, it updates the current budget value for the current month with
        the new value passed into the function

    * AUTHOR
        Shreeti Shrestha

    * DATE
        10:00pm, 08/20/2021
    */
    /**/
    static public void StoreBudgetSummary(Double a_TotalBudget){
        Util.GetSummaryReference().child("budget").setValue(a_TotalBudget)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "SetMonthlyBudgetSummary: success");
                    } else {
                        Log.w(TAG, "SetMonthlyBudgetSummary: failure", task.getException());
                    }
                });
    } /* static public void StoreBudgetSummary(Double a_TotalBudget); */

    /**/
    /*
    * NAME
        BackgroundTasks::UpdateOnlineTransactions() - Asynchronously calls the update function to
        fetch and add new data in the background

    * SYNOPSIS
        static public void UpdateOnlineTransactions();

    * DESCRIPTION
        This function attempts to execute an asynchronous function call to the Update function.
        Once it makes the function call, it returns immediately to its original caller so as to avoid
        unresponsive UI when the update function is executing

    * AUTHOR
        Shreeti Shrestha

    * DATE
        11:00pm, 08/20/2021
    */
    /**/
    static public void UpdateOnlineTransactions(){
        Util.m_Executor.execute(BackgroundTasks::Update);
    }/* static public void UpdateOnlineTransactions(); */


    /**/
    /*
    * NAME
        BackgroundTasks::Update() - Fetches access token and calls another function with the received token

    * SYNOPSIS
        static public void Update();

    * DESCRIPTION
        This function attempts to reference the base-data tree in the database, and retrieves the permanent
        access token for the user. If there is an error in getting the token, it throws an exception.
        Upon receiving the token, it passes the token to the try catch block, which
        calls a series of functions to fetch transactions, add them to the database and update summaries

    * AUTHOR
        Shreeti Shrestha

    * DATE
        08:00pm, 08/22/2021
    */
    /**/
    public static void Update(){

        //Get access token from the base-data tree
        m_BaseDataRef.child("access-token").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                m_AccessToken = String.valueOf(snapshot.getValue());
                Util.m_Executor.execute(()->{

                    //Execute all updates in a try/catch block to catch any errors
                    try {
                        FetchTransactions(m_AccessToken);
                        AddTransactionsToDatabase();
                        GetNewSummaries();

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
    } /* static public void Update(); */


    /**/
    /*
    * NAME
        BackgroundTasks::GetNewSummaries() - Creates new summary with the updated transactions

    * SYNOPSIS
        static public void GetNewSummaries();

    * DESCRIPTION
        This function attempts to reference the express tree in the database, and sets up a listener
        to detect any changes for current month within the tree. If change is detected, it fetches
        all the transactions from the database and iterates through each to add the new monthly total to a list.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        11:00pm, 08/23/2021
    */
    /**/
    public static void GetNewSummaries() {
        expenseSummary.clear();
        Util.GetExpenseReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Data data = dataSnapshot.getValue(Data.class);
                    assert data != null;

                    // if transaction is a credit, disclude it from the expense summary
                    if(data.GetAmount()<0) {
                        continue;
                    }
                    if (!expenseSummary.containsKey(data.GetMonth())) {
                    expenseSummary.put(data.GetMonth(), 0.0);
                    }
                    Double prevVal = expenseSummary.get(data.GetMonth());
                    Double newVal = data.GetAmount() + prevVal;
                    expenseSummary.put(data.GetMonth(), newVal);
                }
                UpdateSummaries();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.e("Error getting new summaries",error.getMessage(),error.toException());
            }
        });
    }


    /**/
    /*
    * NAME
        BackgroundTasks::UpdateSummaries() - Updates the summary tree with the new summary object

    * SYNOPSIS
        static public void UpdateSummaries();

    * DESCRIPTION
        This function replaces the summary object for the current month with the new summary object.
        The summary object has fields such as month number, date, expense amount.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        11:00pm, 08/23/2021
    */
    /**/

    public static void UpdateSummaries(){
        for(int month:expenseSummary.keySet()){

            MutableDateTime a_Epoch = new MutableDateTime();
            a_Epoch.setDate(0);
            a_Epoch.addMonths(month+1);

            String currentMonth = a_Epoch.toString("MMM-yyyy");
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
    }


    /**/
    /*
    * NAME
        BackgroundTasks::AddTransactionsToDatabase() - Updates the expense tree with the new list of transactions

    * SYNOPSIS
        static public void AddTransactionsToDatabase();

    * DESCRIPTION
        This function attempts to iterate through each transaction in the enw transaction list and
        add it to the database. For each transaction, it filters down to only a set of fields and creates
        a new instance of the Data class to create an expense object. Then, it adds the expense object to
        the database.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        11:00pm, 08/24/2021
    */
    /**/

    public static void AddTransactionsToDatabase() throws ParseException {
        for(Transaction t: m_OnlineTransactions) {
            Data a_Expense = CreateExpenseObject(t);
            Util.GetExpenseReference().child(a_Expense.GetId()).setValue(a_Expense).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "AddTransaction: success");
                } else {
                    Log.w(TAG, "AddTransaction: failure", task.getException());
                }
            });
        }
    }/* static public void AddTransactionsToDatabase(); */


    /**/
    /*
    * NAME
        BackgroundTasks::CreateDataExpenseObject() - Updates the expense tree with the new list of transactions

    * SYNOPSIS
        Data BackgroundTasks::CreateExpenseObject(Transaction t)

    * DESCRIPTION
        This function attempts to construct a Data object for the transaction passed as an argument.
        It parses the transaction object to include only the amount, merchant, expense Id, date, note
        and category fields.

    * RETURNS
        Returns a Data object created by parsing the transaction response object

    * AUTHOR
        Shreeti Shrestha

    * DATE
        11:00pm, 08/24/2021
    */
    /**/
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

        DateFormat m_TargetFormat = new SimpleDateFormat("MM-dd-yyyy");
        String a_Date = m_TargetFormat.format(a_ObjectDate);

        MutableDateTime a_Epoch = new MutableDateTime();
        a_Epoch.setDate(0);
        DateTime a_now = ParseDate(a_ObjectDate);

        Months a_Month = Months.monthsBetween(a_Epoch,a_now);
        Days a_Day = Days.daysBetween(a_Epoch,a_now);

        return new Data(expenseId, categoryName, merchant, amount, a_Date, a_Month.getMonths(), a_Day.getDays(), note);
    } /*  public static Data CreateExpenseObject(Transaction t) */

    /**/
    /*
    * NAME
        BackgroundTasks::ParseDate() - Parses the date into a Joda Date time format

    * SYNOPSIS
        DateTime BackgroundTasks::ParseDate(Date a_ObjectDate)
        * a_ObjectDate => date from the transaction object

    * DESCRIPTION
        This function attempts to parse the date field from the transaction object into a Joda date
        time format that can be mutated to get the number of months and days since 1969.

    * RETURNS
        Returns the newly formatted date

    * AUTHOR
        Shreeti Shrestha

    * DATE
        11:40pm, 08/24/2021
    */
    /**/
    private static DateTime ParseDate(Date a_ObjectDate){
        Calendar calendar = new GregorianCalendar();
        assert a_ObjectDate != null;
        calendar.setTime(a_ObjectDate);

        MutableDateTime a_Epoch = new MutableDateTime();
        a_Epoch.setDate(0);
        MutableDateTime a_TransactionTime = new MutableDateTime();
        a_TransactionTime.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH));

        DateTime a_now=new DateTime(a_TransactionTime);

        return a_now;
    } /* private static DateTime ParseDate(Date a_ObjectDate)*/

    /**/
    /*
    * NAME
        BackgroundTasks::FetchTransactions() - Fetch Transactions from user's bank account

    * SYNOPSIS
        void BackgroundTasks::FetchTransactions(String a_AccessToken);
        * a_AccessToken => access token fetched from Firebase for accessing the bank information

    * DESCRIPTION
        This function sends a request to Plaid to fetch the user transactions from the authenticated
        bank. It requests data dating back to 24 months. Upon fetching the response body, it iterates
        through the response and adds each incoming transaction to a list of transactions called
        m_OnlineTransactions

    * AUTHOR
        Shreeti Shrestha

    * DATE
        11:40pm, 04/24/2021
    */
    /**/
    public static void FetchTransactions(String a_AccessToken) throws IOException {
        LocalDate startDate = LocalDate.now().minusYears(2);
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
        Log.d("Api response",String.valueOf(apiResponse.body()));

        assert apiResponse.body() != null;
        m_OnlineTransactions.addAll(apiResponse.body().getTransactions());
    }

    private static final Map<Integer, Double> expenseSummary =  new HashMap<>();
    private static final DatabaseReference m_BaseDataRef = FirebaseDatabase.getInstance().getReference()
            .child("base-data").child(Util.getUid());
    public static List<Transaction> m_OnlineTransactions=new ArrayList<>();
    public static String m_AccessToken;
    private final static String TAG = "BackgroundTask";

}
