package com.example.personalfinance;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

import static java.security.AccessController.getContext;

public class BudgetActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        m_RecyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager m_LinearLayoutManager = new LinearLayoutManager(this);
        m_RecyclerView.setLayoutManager(m_LinearLayoutManager);

        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(m_BudgetRef, Data.class)
                .build();

        m_Adapter = new BudgetAdapter(options);
        m_RecyclerView.setAdapter(m_Adapter);

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

        // Put it inside a button for future use if needed
//        SharedPreferences sharedPreferences = PreferenceManager
//                .getDefaultSharedPreferences(this);
//        SharedPreferences.Editor editor = sharedPreferences.edit().clear();
//        editor.commit();

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

        Apparel.setOnClickListener(v -> {
            OpenCategoryDialog(Apparel);
        });

        Community.setOnClickListener(v -> {
            OpenCategoryDialog(Community);
        });
        Food.setOnClickListener(v -> {
            OpenCategoryDialog(Food);
        });
        Education.setOnClickListener(v -> {
            OpenCategoryDialog(Education);
        });
        Healthcare.setOnClickListener(v -> {
            OpenCategoryDialog(Healthcare);
        });
        Merchandise.setOnClickListener(v -> {
            OpenCategoryDialog(Merchandise);
        });
        Miscellaneous.setOnClickListener(v -> {
            OpenCategoryDialog(Miscellaneous);
        });
        Payments.setOnClickListener(v -> {
            OpenCategoryDialog(Payments);
        });
        Recreation.setOnClickListener(v -> {
            OpenCategoryDialog(Recreation);
        });
        Travel.setOnClickListener(v -> {
            OpenCategoryDialog(Travel);
        });


        TextView m_TotalBudget = findViewById(R.id.totalBudget);

        m_BudgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Double a_Total = 0.0;
                if (snapshot.hasChildren()) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        Data data = snap.getValue(Data.class);
                        a_Total += data.getAmount();
                        m_TotalBudget.setText("Total Budget: " + String.valueOf(a_Total));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });

    }

    private void OpenCategoryDialog(Button Category) {

        MaterialAlertDialogBuilder addDialog = new MaterialAlertDialogBuilder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        a_View = inflater.inflate(R.layout.dialog_category_budget, null);
        addDialog.setView(a_View);

        final AlertDialog dialog = addDialog.create();
        dialog.show();
        dialog.setCancelable(false);

//        Selecting a Category for setting budget
        m_CategoryField = (AutoCompleteTextView) a_View.findViewById(R.id.categoryField);
        Button a_ConfirmBtn = a_View.findViewById(R.id.confirmBudget);
        Button a_CancelBtn = a_View.findViewById(R.id.cancelAction);
        m_CategoryField.setText(Category.getText());

        a_ConfirmBtn.setOnClickListener(v -> {
            Data a_Budget = SetCategoryBudget();

            m_BudgetRef.child(a_Budget.getCategory()).setValue(a_Budget).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "SetCategoryBudget: success");
                        Toast.makeText(getApplicationContext(), "Budget for this category set successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.w(TAG, "SetCategoryBudget: failure", task.getException());
                        Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

            });
            savePreferences(Category.getText().toString(), "true");
            Category.setEnabled(false);
            dialog.dismiss();

        });

        a_CancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void savePreferences(String key,String value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.commit();
    }

    private void LoadSavedPreferences(Button Category){
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        if (sharedPreferences.getString(Category.getText().toString(),null) != null)
            Category.setEnabled(false);
        else
            Category.setEnabled(true);
    }

    private Data SetCategoryBudget() {
        EditText a_AmountField = a_View.findViewById(R.id.amountField);
        String a_Amount = a_AmountField.getText().toString().trim();

        if (a_Amount.isEmpty()) {
            Log.e(TAG, "Empty Field");
            a_AmountField.setError("Required Field");
            a_AmountField.requestFocus();
        }
        String a_BudgetId = m_BudgetRef.push().getKey();
        DateFormat a_DateFormat = new SimpleDateFormat("MM-dd-yyyy");
        Calendar a_Calendar = Calendar.getInstance();
        String a_Date = a_DateFormat.format(a_Calendar.getTime());

        MutableDateTime a_Epoch = new MutableDateTime();
        a_Epoch.setDate(0);
        DateTime a_Now = new DateTime();
        Months a_Month = Months.monthsBetween(a_Epoch, a_Now).minus(1);

        String a_Category = m_CategoryField.getText().toString();

        Data a_Budget = new Data(a_BudgetId, a_Category, Double.parseDouble(a_Amount), a_Date, a_Month.getMonths());

        return a_Budget;
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

    private View a_View;
    private FirebaseAuth m_Auth = FirebaseAuth.getInstance();
    private String a_Uid = Objects.requireNonNull(m_Auth.getCurrentUser()).getUid();
    Months currentMonth = Util.getMonth().minus(1);
    private DatabaseReference m_BudgetRef = FirebaseDatabase.getInstance().getReference().child("budget").child(a_Uid).child(String.valueOf(currentMonth));
    private RecyclerView m_RecyclerView;
    private BudgetAdapter m_Adapter;
    private final static String TAG = "BudgetActivity";
}