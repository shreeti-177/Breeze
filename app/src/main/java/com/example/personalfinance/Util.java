package com.example.personalfinance;


import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.plaid.client.ApiClient;
import com.plaid.client.request.PlaidApi;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Util {


    public static void CheckForNullEntry(String a_TextEntry, EditText a_TextField){
        if(a_TextEntry.isEmpty()){
            a_TextField.setError("Required Field");
            a_TextField.requestFocus();
        }
    }
    public static Months getMonth() {
        MutableDateTime a_Epoch = new MutableDateTime();
        a_Epoch.setDate(0);
        DateTime a_Now = new DateTime();
        Months a_Month = Months.monthsBetween(a_Epoch, a_Now);
        return a_Month;
    }

    public static int getFirstDay(){
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime().minusMonths(1).withDayOfMonth(1);
        Days day = Days.daysBetween(epoch,now);
        return day.getDays();
    }

    public static int getCurrentDay(){
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Days day = Days.daysBetween(epoch,now);
        Log.i("Days",String.valueOf(day.getDays()));
        return day.getDays();
    }

    public static Months getTransactionMonth(DateTime a_date) {
        MutableDateTime a_Epoch = new MutableDateTime();
        a_Epoch.setDate(0);
        Months a_Month = Months.monthsBetween(a_Epoch, a_date);
        return a_Month;
    }

    public static PlaidApi PlaidClient(){
        HashMap<String, String> apiKeys = new HashMap<>();
        apiKeys.put("clientId", BuildConfig.PLAID_CLIENT_KEY);
        apiKeys.put("secret", BuildConfig.PLAID_CLIENT_SECRET);
        apiKeys.put("plaidVersion", "2020-09-14");
        ApiClient apiClient = new ApiClient(apiKeys);
        apiClient.setPlaidAdapter(ApiClient.Sandbox);
        return apiClient.createService(PlaidApi.class);
    }

    public static List<String> GetExistingCategories() {
        List<String> basicCategories = new ArrayList<String>(){{
            add("Apparel");
            add("Community");
            add("Food");
            add("Education");
            add("Healthcare");
            add("Merchandise");
            add("Miscellaneous");
            add("Payments");
            add("Recreation");
            add("Travel");
        }};

        return basicCategories;
    }

    public static int SetCategoryIcon(String a_Category){
        switch(a_Category){
            case("Apparel"):
                return R.drawable.ic_apparel;
            case("Community"):
                return R.drawable.ic_community;
            case("Education"):
                return R.drawable.ic_education;
            case("Recreation"):
                return R.drawable.ic_recreation;
            case("Food"):
                return R.drawable.ic_food;
            case("Healthcare"):
                return R.drawable.ic_healthcare;
            case("Merchandise"):
                return R.drawable.ic_merchandise;
            case("Payments"):
                return R.drawable.ic_payment;
            case("Travel"):
                return R.drawable.ic_travel;
            default:
                return R.drawable.ic_miscellaneous;
        }
    }


    public static Map<String, Double> GetCategoricalExpense(List<Data> monthlyDataList) {
        Map<String, Double> categoryExpense = new HashMap<>();
        for (Data d : monthlyDataList) {
            if (d.getAmount() < 0) {
                continue;
            }
            String a_category = Objects.requireNonNull(d.getCategory());
            if (!categoryExpense.containsKey(a_category)) {
                categoryExpense.put(a_category, d.getAmount());
                continue;
            }
            Double newValue = categoryExpense.get(a_category) + d.getAmount();
            categoryExpense.put(a_category, newValue);
        }
        for (String category: GetExistingCategories()){
            if (!categoryExpense.containsKey(category)){
                categoryExpense.put(category,0.0);
            }
        }
        return categoryExpense;
    }

    public static DatabaseReference GetSummaryReference(){
        return FirebaseDatabase.getInstance().getReference().child("summary").child(m_Uid)
                .child(String.valueOf(Util.getMonth().getMonths()));
    }

    public static DatabaseReference GetBudgetReference(){
        return FirebaseDatabase.getInstance().getReference().child("budget").child(m_Uid)
                .child(String.valueOf(Util.getMonth().getMonths()));
    }

    public static DatabaseReference GetExpenseReference(){
        return FirebaseDatabase.getInstance().getReference().child("expenses").child(m_Uid);
    }

    public static DatabaseReference GetPlansReference(){
        return FirebaseDatabase.getInstance().getReference().child("plans").child(m_Uid);
    }
    public static String getUid() {
        return m_Uid;
    }

    public static void setUid(String a_Uid) {
        m_Uid = a_Uid;
    }

    public static ExecutorService m_Executor = Executors.newSingleThreadExecutor();
    public static FirebaseAuth m_Auth = FirebaseAuth.getInstance();
    private static String m_Uid = Objects.requireNonNull(m_Auth.getCurrentUser()).getUid();

}
