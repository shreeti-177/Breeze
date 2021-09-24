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
                        total += data.getAmount();
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

    }

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
    }

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
    }

    private void FlushSavedPreferences(){
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit().clear();
        editor.apply();
    }

    private void OpenCategoryDialog(Button a_Category) {
        MaterialAlertDialogBuilder addDialog = new MaterialAlertDialogBuilder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        m_View = inflater.inflate(R.layout.dialog_category_budget, null);
        addDialog.setView(m_View);

        final AlertDialog dialog = addDialog.create();
        dialog.show();
        dialog.setCancelable(false);

//        Selecting a Category for setting budget
        m_CategoryField = m_View.findViewById(R.id.categoryField);
        Button confirmBtn = m_View.findViewById(R.id.confirmButton);
        Button cancelBtn = m_View.findViewById(R.id.cancelButton);
        m_CategoryField.setText(a_Category.getText());

        confirmBtn.setOnClickListener(v -> {
            Data a_Budget = SetCategoryBudget();
            Util.GetBudgetReference().child(a_Budget.getCategory()).setValue(a_Budget)
                    .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "SetCategoryBudget: success");
                    Toast.makeText(getApplicationContext(), "Budget for this category set successfully",
                            Toast.LENGTH_SHORT).show();
                    //disable the button once budget is set for a category
                    savePreferences(a_Category.getText().toString());
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

    private void savePreferences(String key) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, "true");
        editor.apply();
    }

    private void LoadSavedPreferences(Button Category){
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        Category.setEnabled(sharedPreferences.getString(Category.getText().toString(), null) == null);
    }

    private Data SetCategoryBudget() {
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


    @Override
    public void onStart() {
        super.onStart();
        m_Adapter.startListening();
    }

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