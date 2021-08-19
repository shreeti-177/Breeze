package com.example.personalfinance;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class CashTransactionActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_transactions);

        m_CategoryField = (AutoCompleteTextView) findViewById(R.id.categoryField);
        m_MerchantField=findViewById(R.id.merchantField);
        m_AmountField = findViewById(R.id.amountField);
        m_DateField = findViewById(R.id.dateField);
        m_NoteField = findViewById(R.id.notesField);
        m_ConfirmBtn = findViewById(R.id.confirmButton);
        m_CancelBtn = findViewById(R.id.cancelButton);

        ArrayAdapter<String> itemsAdapter = GetExistingCategoryList();
        m_CategoryField.setAdapter(itemsAdapter);

        m_DatePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();
        m_DateField.setOnClickListener(v -> m_DatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER"));
        m_DatePicker.addOnPositiveButtonClickListener(
                selection -> m_DateField.setText(m_DatePicker.getHeaderText()));


        m_ConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Data a_Category = null;
                try {
                    a_Category = AddNewTransaction();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                m_ExpenseRef.child(a_Category.getId()).setValue(a_Category).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "AddTransaction: success");
                            Toast.makeText(getApplicationContext(),"Transaction added successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else{
                            Log.w(TAG, "AddTransaction: failure", task.getException());
                            Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        m_CancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private Data AddNewTransaction() throws ParseException {
        String a_CategoryName = m_CategoryField.getText().toString().trim();
        if (a_CategoryName.isEmpty()){
            Log.e(TAG,"Empty Category Field");
            m_CategoryField.setError("Required Field");
            m_CategoryField.requestFocus();
        }

        String a_Amount = m_AmountField.getText().toString().trim();
        if (a_Amount.isEmpty()){
            Log.e(TAG,"Empty Amount Field");
            m_AmountField.setError("Required Field");
            m_AmountField.requestFocus();
        }

        String a_Merchant = m_MerchantField.getText().toString().trim();
        String a_Note = m_NoteField.getText().toString().trim();

        String a_ExpenseId = m_ExpenseRef.push().getKey();
        Log.i("Date", "Shows Date");
        String a_DateText = m_DateField.getText().toString().trim();
        Log.i("Date",a_DateText);

        SimpleDateFormat m_CurrentFormat = new SimpleDateFormat("MMM dd, yyyy");
        Date a_ObjectDate = m_CurrentFormat.parse(a_DateText);

        DateFormat m_TargetFormat = new SimpleDateFormat("MM-dd-yyyy");
        String a_Date = m_TargetFormat.format(a_ObjectDate);

        MutableDateTime a_Epoch = new MutableDateTime();
        a_Epoch.setDate(0);
        DateTime a_Now = new DateTime();
        Months a_Month = Months.monthsBetween(a_Epoch,a_Now);

        Data a_Expense = new Data(a_ExpenseId, a_CategoryName, a_Merchant, Double.parseDouble(a_Amount), a_Date, a_Month.getMonths(), a_Note);

        return a_Expense;
    }

    private ArrayAdapter<String> GetExistingCategoryList(){
        List<String> items=Util.GetExistingCategories();
        return new ArrayAdapter<String>(this, R.layout.list_item, R.id.category_items, items);
    }

    private AutoCompleteTextView m_CategoryField;
    private EditText m_MerchantField;
    private EditText m_AmountField;
    private EditText m_DateField;
    private EditText m_NoteField;
    private Button m_ConfirmBtn;
    private Button m_CancelBtn;

    private MaterialDatePicker m_DatePicker;
    private FirebaseAuth m_Auth = FirebaseAuth.getInstance();
    private String a_Uid = Objects.requireNonNull(m_Auth.getCurrentUser()).getUid();
    Months currentMonth = Util.getMonth().minus(1);
    private DatabaseReference m_ExpenseRef = FirebaseDatabase.getInstance().getReference().child("expenses").child(a_Uid).child(String.valueOf(currentMonth));
    private final String TAG = "AddTransactionActivity";

}
