//
// Implementation of the Home Activity class
// This class provides an interface to the user to set and update their category budgets for the current month
//
package com.example.personalfinance;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.util.Objects;

import static android.view.View.GONE;

public class BudgetActivity extends AppCompatActivity {

    /**/
    /*
    * NAME
        BudgetActivity::onCreate() - Overrides the default onCreate function for this class

    * SYNOPSIS
        void BudgetActivity::onCreate(Bundle savedInstanceState);
        * savedInstanceState => previous state of the activity

    * DESCRIPTION
        This function will attempt to set the layout for the set budget page.
        Then, it will display a list of 10 categories and fetch existing budgets for the month and
        display them in a list. It also sums the total of all the budgets and displays it at the top
        of the list. If there are no budgets set so far, it prompts the user to set their budgets.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        10:00pm, 04/14/2021
    */
    /**/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        //Display current budget month
        TextView budgetMonth=findViewById(R.id.budgetMonth);
        DateTime date = new DateTime();
        String month = date.toString("MMM-yyyy");
        budgetMonth.setText(getString(R.string.budgetMonth) + month);

        budgetSection = findViewById(R.id.budgetSection);


        RecyclerView m_RecyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager m_LinearLayoutManager = new LinearLayoutManager(this);
        m_RecyclerView.setLayoutManager(m_LinearLayoutManager);

        //Query to fetch category budgets for current month from Firebase
        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(Util.GetBudgetReference(), Data.class)
                .build();

        m_Adapter = new BudgetAdapter(options);
        m_RecyclerView.setAdapter(m_Adapter);

        //Link all categories to their layouts
        Apparel = findViewById(R.id.Apparel);
        Community = findViewById(R.id.Community);
        Food = findViewById(R.id.Food);
        Education = findViewById(R.id.Education);
        Healthcare = findViewById(R.id.Healthcare);
        Merchandise = findViewById(R.id.Merchandise);
        Miscellaneous = findViewById(R.id.Miscellaneous);
        Payments = findViewById(R.id.Payments);
        Recreation = findViewById(R.id.Recreation);
        Travel = findViewById(R.id.Travel);
        TextView totalBudget = findViewById(R.id.totalBudget);

        //Disable button for categories once their budget is set
        FlushSavedPreferences();
        LoadAllSavedPreferences();
        ButtonClickListeners();

        //Get existing budgets for current month
        Util.GetBudgetReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Double total = 0.0;
                if (snapshot.hasChildren()) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        Data data = snap.getValue(Data.class);
                        assert data != null;
                        total += data.GetAmount();
                        totalBudget.setText("Total Budget: " + total);
                    }
                }
                Double finalTotal = total;
                if(finalTotal==0){
                    budgetSection.setVisibility(GONE);
                }else{
                    budgetSection.setVisibility(View.VISIBLE);
                }
                Util.m_Executor.execute(()-> BackgroundTasks.StoreBudgetSummary(finalTotal));
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.i("Error getting budget data",error.getMessage(), error.toException());
            }
        });

    } /* protected void onCreate(Bundle savedInstanceState); */


    /**/
    /*
    * NAME
        BudgetActivity::ButtonClickListeners() - Sets up onClick Listeners for each button click

    * SYNOPSIS
        void BudgetActivity::ButtonClickListeners();

    * DESCRIPTION
        This function will attempt to set listeners for button clicks for each category. When a category
        is clicked, it will attempt to call the OpenCategroyDialog with the category name.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        07:00pm, 04/17/2021
    */
    /**/
    private void ButtonClickListeners(){
        Apparel.setOnClickListener(v -> OpenCategoryDialog(Apparel));
        Community.setOnClickListener(v -> OpenCategoryDialog(Community));
        Food.setOnClickListener(v -> OpenCategoryDialog(Food));
        Education.setOnClickListener(v -> OpenCategoryDialog(Education));
        Healthcare.setOnClickListener(v -> OpenCategoryDialog(Healthcare));
        Merchandise.setOnClickListener(v -> OpenCategoryDialog(Merchandise));
        Miscellaneous.setOnClickListener(v -> OpenCategoryDialog(Miscellaneous));
        Payments.setOnClickListener(v -> OpenCategoryDialog(Payments));
        Recreation.setOnClickListener(v -> OpenCategoryDialog(Recreation));
        Travel.setOnClickListener(v -> OpenCategoryDialog(Travel));
    } /*  private void ButtonClickListeners() */


    /**/
    /*
    * NAME
        BudgetActivity::LoadAllSavedPreferences() - Checks the categories for which budgets have been set already

    * SYNOPSIS
        void BudgetActivity::LoadAllSavePreferences();

    * DESCRIPTION
        This function will attempt to check the state of budget in each category. For each category name,
        it calls the LoadSavedPreferences() function to see if the user has already set a budget

    * AUTHOR
        Shreeti Shrestha

    * DATE
        08:00pm, 04/17/2021
    */
    /**/
    private void LoadAllSavedPreferences(){
        LoadSavedPreferences(Apparel);
        LoadSavedPreferences(Community);
        LoadSavedPreferences(Food);
        LoadSavedPreferences(Education);
        LoadSavedPreferences(Healthcare);
        LoadSavedPreferences(Merchandise);
        LoadSavedPreferences(Miscellaneous);
        LoadSavedPreferences(Payments);
        LoadSavedPreferences(Recreation);
        LoadSavedPreferences(Travel);
    } /* private void LoadAllSavedPreferences() */

    /**/
    /*
    * NAME
        BudgetActivity::LoadSavedPreferences() - Loads saved preference state for a button

    * SYNOPSIS
        void BudgetActivity::LoadSavedPreferences(a_Category);
        * a_Category => button for a category

    * DESCRIPTION
        This function will attempt to check the preference state for the button passed as an argument.
        If it finds the category name in the list of saved preferences, it disables the button. If not,
        it enables the button.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        08:40pm, 04/17/2021
    */
    /**/
    private void LoadSavedPreferences(Button a_Category){
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        a_Category.setEnabled(sharedPreferences.getString(a_Category.getText().toString(), null) == null);
    } /* private void LoadSavedPreferences(Button Category); */


    /**/
    /*
    * NAME
        BudgetActivity::SavePreferences() - Saves preference for a button category

    * SYNOPSIS
        void BudgetActivity::SavePreferences();

    * DESCRIPTION
        Once the user has set budget for a category, this function will attempt to set the preference
        as true. If not, it will set it as false.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        09:25pm, 04/17/2021
    */
    /**/
    private void SavePreferences(String key) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, "true");
        editor.apply();
    }/* private void SavePreferences(String key); */


    /**/
    /*
    * NAME
        BudgetActivity::FlushSavedPreferences() - Gets rid of all saved preferences

    * SYNOPSIS
        void BudgetActivity::FlushSavedPreferences();

    * DESCRIPTION
        This function attempts to clear all saved preferences, which makes all the buttons accessible
        to the user in the event of a new login so that they can set/update budgets.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        09:35pm, 04/17/2021
    */
    /**/
    private void FlushSavedPreferences(){
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit().clear();
        editor.apply();
    } /*  private void FlushSavedPreferences(); */

    /**/
    /*
    * NAME
        BudgetActivity::OpenCategoryDialog() - Opens a dialog interface for setting budget

    * SYNOPSIS
        void BudgetActivity::OpenCategoryDialog(a_Category);
        * a_Category => category passed to populate the category name field in the dialog box

    * DESCRIPTION
        This function attempts to open a dialog interface for the user to set budget. The category
        name is populated with the button name that is passed to the function.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        08:05pm, 04/18/2021
    */
    /**/
    private void OpenCategoryDialog(Button a_Category) {
        MaterialAlertDialogBuilder addDialog = new MaterialAlertDialogBuilder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        m_View = inflater.inflate(R.layout.dialog_category_budget, null);
        addDialog.setView(m_View);

        final AlertDialog dialog = addDialog.create();
        dialog.show();
        dialog.setCancelable(false);

        // Selecting a Category for setting budget
        m_CategoryField = m_View.findViewById(R.id.categoryField);
        Button confirmBtn = m_View.findViewById(R.id.confirmButton);
        Button cancelBtn = m_View.findViewById(R.id.cancelButton);
        m_CategoryField.setText(a_Category.getText());

        // once the user hits confirm, verify the budget is not empty
        // then, add the budget to the database
        confirmBtn.setOnClickListener(v -> {
            Data a_Budget = CreateBudgetObject();
            Util.GetBudgetReference().child(a_Budget.GetCategory()).setValue(a_Budget)
                    .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "SetCategoryBudget: success");
                    Toast.makeText(getApplicationContext(), "Budget for this category set successfully",
                            Toast.LENGTH_SHORT).show();

                    //disable the button once budget is set for a category
                    SavePreferences(a_Category.getText().toString());
                    a_Category.setEnabled(false);
                    budgetSection.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                } else {
                    Log.w(TAG, "SetCategoryBudget: failure", task.getException());
                    Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException())
                            .getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        cancelBtn.setOnClickListener(v -> dialog.dismiss());
    }



    /**/
    /*
    * NAME
        BudgetActivity::CreateBudgetObject() - Creates a new data object to populate the budget database

    * SYNOPSIS
        Data BudgetActivity::CreateBudgetObject();

    * DESCRIPTION
        This function attempts to check for any null entries in the budget field. If so, it prompts the
        user to enter a valid value. If not, it creates an instance of the Data class and returns the object

    * RETURNS
        Returns a new Data Object

    * AUTHOR
        Shreeti Shrestha

    * DATE
        09:35pm, 04/18/2021
    */
    /**/
    private Data CreateBudgetObject() {
        EditText amountField = m_View.findViewById(R.id.amountField);
        String amount = amountField.getText().toString().trim();

        Util.CheckForNullEntry(amount,amountField);
        String budgetId = Util.GetBudgetReference().push().getKey();
        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        Calendar calendar = Calendar.getInstance();
        String date = dateFormat.format(calendar.getTime());

        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months month = Months.monthsBetween(epoch, now);
        Days day = Days.daysBetween(epoch,now);
        String category = m_CategoryField.getText().toString();

        return new Data(budgetId, category, Double.parseDouble(amount), date,
                month.getMonths(), day.getDays());
    }


    // Keep listening for any changes in the database once this activity is started
    @Override
    public void onStart() {
        super.onStart();
        m_Adapter.startListening();
    }

    // Stop listening for changes once user navigates to some other page
    @Override
    protected void onStop() {
        super.onStop();
        m_Adapter.stopListening();
    }

    private Button Apparel;
    private Button Community;
    private Button Food;
    private Button Education;
    private Button Healthcare;
    private Button Merchandise;
    private Button Miscellaneous;
    private Button Payments;
    private Button Recreation;
    private Button Travel;
    private AutoCompleteTextView m_CategoryField;
    private LinearLayout budgetSection;
    private View m_View;
    private BudgetAdapter m_Adapter;
    private final static String TAG = "BudgetActivity";
}