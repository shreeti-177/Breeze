//
// Implementation of the Util class
// This class provides static helper methods used throughout the program
//
package com.example.personalfinance;

import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.plaid.client.ApiClient;
import com.plaid.client.request.PlaidApi;

import org.joda.time.DateTime;
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


    /**/
    /*
    * NAME
        Util::CheckForNullEntry() - Checks if a text field has a null value

    * SYNOPSIS
        void Util::CheckForNullEntry(a_TextEntry, a_TextField);
        * a_TextEntry: user entered value
        * a_TextField: the field where the user has supposedly entered their desired value

    * DESCRIPTION
        This function will attempt to collect get the user entries and check if they're null before
        verifying that they're authenticated. If the value is indeed null, it will display an error
        message and focus on the text field to prompt the user to enter a valid non-null value.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        08:00pm, 02/15/2021
    */
    /**/
    public static void CheckForNullEntry(String a_TextEntry, EditText a_TextField){
        if(a_TextEntry.isEmpty()){
            a_TextField.setError("Required Field");
            a_TextField.requestFocus();
        }
    }/*  public static void CheckForNullEntry(String a_TextEntry, EditText a_TextField) */

    /**/
    /*
    * NAME
        Util::GetMonth() - Get the month number object from the start of the calendar

    * SYNOPSIS
        Months Util::GetMonth();

    * DESCRIPTION
        This function will attempt to get the number of months elapsed between now and January 1969.

    * RETURNS
        Returns a month object with the number of months between now and January 1969

    * AUTHOR
        Shreeti Shrestha

    * DATE
        08:00pm, 02/15/2021
    */
    /**/
    public static Months GetMonth() {
        MutableDateTime a_Epoch = new MutableDateTime();
        a_Epoch.setDate(0);
        DateTime a_Now = new DateTime();
        return Months.monthsBetween(a_Epoch, a_Now);
    } /*  public static Months GetMonth() */

    /**/
    /*
    * NAME
        Util::PlaidClient() - Get existing Plaid Client

    * SYNOPSIS
        PlaidApi Util::PlaidClient();

    * DESCRIPTION
        This function will attempt to get the existing plaid client through a static reference

    * RETURNS
        Returns a Plaid API access

    * AUTHOR
        Shreeti Shrestha

    * DATE
        08:00pm, 03/01/2021
    */
    /**/
    public static PlaidApi PlaidClient(){
        HashMap<String, String> apiKeys = new HashMap<>();
        apiKeys.put("clientId", BuildConfig.PLAID_CLIENT_KEY);
        apiKeys.put("secret", BuildConfig.PLAID_CLIENT_SECRET);
        apiKeys.put("plaidVersion", "2020-09-14");
        ApiClient apiClient = new ApiClient(apiKeys);
        apiClient.setPlaidAdapter(ApiClient.Sandbox);
        return apiClient.createService(PlaidApi.class);
    } /*  public static PlaidApi PlaidClient() */


    /**/
    /*
    * NAME
        Util::GetExistingCategories() - Get all the existing categories

    * SYNOPSIS
        List<String> Util::GetExistingCategories();

    * DESCRIPTION
        This function will attempt to get the a list of all the available category names. These are
        the categories under which future transactions will be grouped into.

    * RETURNS
        Returns a list with 10 category names

    * AUTHOR
        Shreeti Shrestha

    * DATE
        08:00pm, 03/01/2021
    */
    /**/
    public static List<String> GetExistingCategories() {

        return new ArrayList<String>(){{
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
    } /*  public static List<String> GetExistingCategories() */

    /**/
    /*
    * NAME
        Util::SetCategoryIcon() - Set a unique icon for each category

    * SYNOPSIS
        int Util::SetCategoryIcon(String a_Category);
        * a_Category => the category name

    * DESCRIPTION
        Based on the category name that is being passed, this function will attempt to set a unique
        icon from the list of drawables available.

    * RETURNS
        Returns an integer that specifies the icon id

    * AUTHOR
        Shreeti Shrestha

    * DATE
        10:00pm, 05/12/2021
    */
    /**/
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
    } /* public static int SetCategoryIcon(String a_Category) */


    /**/
    /*
    * NAME
        Util::GetCategoricalExpense() - Get total sum of expense for a category

    * SYNOPSIS
        Map<String, Double> Util::GetCategoricalExpense(List<Data> a_MonthlyDataList);
        * a_MonthlyDataList => List of all expenses for the month

    * DESCRIPTION
        This function attempts to iterate through the monthly data list and group expenses based on
        common categories. Then, it adds up the expense amounts for each category and stores the category
        name and corresponding amount in a map key-value pair.

    * RETURNS
        Returns a map that has category name as the key and the expense for the month as its value

    * AUTHOR
        Shreeti Shrestha

    * DATE
        11:20pm, 05/12/2021
    */
    /**/
    public static Map<String, Double> GetCategoricalExpense(List<Data> a_MonthlyDataList) {
        Map<String, Double> categoryExpense = new HashMap<>();
        for (Data d : a_MonthlyDataList) {
            if (d.GetAmount() < 0) {
                continue;
            }
            String a_category = Objects.requireNonNull(d.GetCategory());
            if (!categoryExpense.containsKey(a_category)) {
                categoryExpense.put(a_category, d.GetAmount());
                continue;
            }
            Double newValue = categoryExpense.get(a_category) + d.GetAmount();
            categoryExpense.put(a_category, newValue);
        }
        for (String category: GetExistingCategories()){
            if (!categoryExpense.containsKey(category)){
                categoryExpense.put(category,0.0);
            }
        }
        return categoryExpense;
    } /* public static Map<String, Double> GetCategoricalExpense(List<Data> a_MonthlyDataList) */

    // get a reference to the Firebase summary tree for the current month
    public static DatabaseReference GetSummaryReference(){
        return FirebaseDatabase.getInstance().getReference().child("summary").child(m_Uid)
                .child(String.valueOf(Util.GetMonth().getMonths()));
    }

    // get a reference to the Firebase budget tree for the current month
    public static DatabaseReference GetBudgetReference(){
        return FirebaseDatabase.getInstance().getReference().child("budget").child(m_Uid)
                .child(String.valueOf(Util.GetMonth().getMonths()));
    }

    // get a reference to the Firebase expense tree
    public static DatabaseReference GetExpenseReference(){
        return FirebaseDatabase.getInstance().getReference().child("expenses").child(m_Uid);
    }

    // get the Firebase user id
    public static String getUid() {
        return m_Uid;
    }

    public static ExecutorService m_Executor = Executors.newSingleThreadExecutor();
    public static FirebaseAuth m_Auth = FirebaseAuth.getInstance();
    private static final String m_Uid = Objects.requireNonNull(m_Auth.getCurrentUser()).getUid();

}
