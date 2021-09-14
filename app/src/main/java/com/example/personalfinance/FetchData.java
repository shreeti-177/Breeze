//package com.example.personalfinance;
//
//import android.util.Log;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.common.net.InternetDomainName;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.plaid.client.model.Transaction;
//import com.plaid.client.model.TransactionsGetRequest;
//import com.plaid.client.model.TransactionsGetResponse;
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
//import java.util.List;
//import java.util.Objects;
//
//import retrofit2.Response;
//
//public class FetchData extends AppCompatActivity {
//    public List<Transaction> m_OnlineTransactions=new ArrayList<>();
//    public List<Data> m_AllTransactions=new ArrayList<>();
//    public DatabaseReference m_BaseDataRef = FirebaseDatabase.getInstance().getReference().child("base-data").child(Util.getUid());
//    private DatabaseReference m_ExpenseRef = FirebaseDatabase.getInstance().getReference().child("expenses").child(Util.getUid());
//    private String TAG="FetchDataActivity";
//
//    public FetchData(){
//        FetchAndAdd();
//    }
//
//    public List<Data> GetAllTransactions(){
//        return m_AllTransactions;
//    }
//
//    public void FetchAndAdd() {
//        m_BaseDataRef.child("accessToken").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                String m_AccessToken = String.valueOf(snapshot.getValue());
//                Log.i("Get Token", m_AccessToken);
//                Util.m_Executor.execute(() -> {
//                    try {
//                        FetchTransactions(m_AccessToken);
//                        AddTransactionsToDatabase();
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                });
////                Util.m_Executor.execute(()->{
////                    AddTotalExpenses();
////                });
//            }
//
//            @Override
//            public void onCancelled(@NonNull @NotNull DatabaseError error) {
//            }
//        });
//    }
//
//
//    private void AddTransactionsToDatabase() throws ParseException {
//        for(Transaction t: m_OnlineTransactions) {
//            Data a_Expense = CreateExpenseObject(t);
//            m_ExpenseRef.child(a_Expense.getId()).setValue(a_Expense).addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    Log.d(TAG, "AddTransaction: success");
////                    Toast.makeText(this, "Transaction added successfully", Toast.LENGTH_SHORT).show();
//                } else {
//                    Log.w(TAG, "AddTransaction: failure", task.getException());
////                    Toast.makeText(this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    }
//
//    private Data CreateExpenseObject(Transaction t) throws ParseException {
//        Double a_Amount = t.getAmount();
//        String a_Merchant = t.getMerchantName();
//        String a_Note = t.getName();
//        String a_ExpenseId = t.getTransactionId();
//        String a_DateText = t.getDate();
//
//        String a_CategoryName = t.getCategory().get(0);
//
//        List<String> a_Categories = Util.GetExistingCategories();
//
//        if(!(a_Categories.contains(a_CategoryName))){
//            if(a_CategoryName.equals("Food and Drink")){
//                a_CategoryName="Food";
//            }
//            else if(a_CategoryName.equals("Shops")) {
//                a_CategoryName = "Merchandise";
//            }
//            else {
//                a_CategoryName = "Miscellaneous";
//            }
//        }
//
//        SimpleDateFormat m_CurrentFormat = new SimpleDateFormat("yyyy-MM-dd");
//        Date a_ObjectDate = m_CurrentFormat.parse(a_DateText);
//
//
//        Calendar calendar = new GregorianCalendar();
//        calendar.setTime(a_ObjectDate);
//
//        DateFormat m_TargetFormat = new SimpleDateFormat("MM-dd-yyyy");
//        String a_Date = m_TargetFormat.format(a_ObjectDate);
//
//        MutableDateTime a_Epoch = new MutableDateTime();
//        a_Epoch.setDate(0);
//        MutableDateTime a_TransactionTime = new MutableDateTime();
//        a_TransactionTime.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH));
//
//        DateTime a_now=new DateTime(a_TransactionTime);
//
//        Months a_Month = Months.monthsBetween(a_Epoch,a_now);
//        Days a_Day = Days.daysBetween(a_Epoch,a_now);
//
//        Data a_Expense = new Data(a_ExpenseId, a_CategoryName, a_Merchant, a_Amount, a_Date, a_Month.getMonths(), a_Day.getDays(), a_Note);
////        a_Expense.setDateTime(new DateTime(a_ObjectDate));
//        a_dateTime=new DateTime(a_ObjectDate);
//
//        return a_Expense;
//    }
//
//    private void FetchTransactions(String a_AccessToken){
//        LocalDate startDate = LocalDate.now().minusYears(1);
////        LocalDate startDate = LocalDate.now().minusMonths(1).withDayOfMonth(1);
////        LocalDate endDate = LocalDate.now().minusMonths(1).withDayOfMonth(31);
//        LocalDate endDate=LocalDate.now();
//
//        TransactionsGetRequest request = new TransactionsGetRequest()
//                .accessToken(a_AccessToken)
//                .startDate(startDate)
//                .endDate(endDate);
//        Log.i("Request",String.valueOf(request));
//        Response<TransactionsGetResponse> apiResponse = null;
//
//        for (int i = 0; i < 10; i++) {
//            try {
//                PlaidApi a_Client = OnboardActivity.client();
//                apiResponse = a_Client.transactionsGet(request).execute();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            if (apiResponse.isSuccessful()) {
//                Log.i("Success","It's successful");
//                break;
//            } else {
//                try {
//                    Log.i("Transaction", "Not ready");
//                    Thread.sleep(5000);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        m_OnlineTransactions = new ArrayList<>();
//        assert apiResponse.body() != null;
//        Log.i("Total Number of Transactions",String.valueOf(apiResponse.body().getTotalTransactions()));
//        m_OnlineTransactions.addAll(apiResponse.body().getTransactions());
//    }
//
//    private DateTime a_dateTime=new DateTime();
//
//}
