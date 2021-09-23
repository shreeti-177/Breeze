package com.example.personalfinance;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.datepicker.MaterialDatePicker;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class CashTransactionActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_transactions);

        m_CategoryField = findViewById(R.id.categoryField);
        m_MerchantField=findViewById(R.id.merchantField);
        m_AmountField = findViewById(R.id.amountField);
        m_DateField = findViewById(R.id.dateField);
        m_NoteField = findViewById(R.id.notesField);
        Button confirmBtn = findViewById(R.id.confirmButton);
        Button cancelBtn = findViewById(R.id.cancelButton);

        Toolbar toolbar = findViewById(R.id.toolBar);
        toolbar.setNavigationOnClickListener(v -> finish());

        ArrayAdapter<String> itemsAdapter = GetExistingCategoryList();
        m_CategoryField.setAdapter(itemsAdapter);

        m_DatePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();
        m_DateField.setOnClickListener(v -> m_DatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER"));
        m_DatePicker.addOnPositiveButtonClickListener(
                selection -> m_DateField.setText(m_DatePicker.getHeaderText()));


        confirmBtn.setOnClickListener(v -> {
            Data a_Category = null;
            try {
                a_Category = AddNewTransaction();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            assert a_Category != null;
            Util.GetExpenseReference().child(a_Category.getId()).setValue(a_Category)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                        Log.d(TAG, "AddTransaction: success");
                        Toast.makeText(getApplicationContext(),"Transaction added successfully",
                                Toast.LENGTH_SHORT).show();
                        finish();
                }
                else{
                    Log.w(TAG, "AddTransaction: failure", task.getException());
                    Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException())
                            .getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        cancelBtn.setOnClickListener(v -> finish());
    }

    private Data AddNewTransaction() throws ParseException {
        String categoryName = m_CategoryField.getText().toString().trim();
        Util.CheckForNullEntry(categoryName,m_CategoryField);

        String amount = m_AmountField.getText().toString().trim();
        Util.CheckForNullEntry(amount,m_AmountField);

        String merchant = m_MerchantField.getText().toString().trim();
        String note = m_NoteField.getText().toString().trim();

        String expenseId = Util.GetExpenseReference().push().getKey();
        String dateText = m_DateField.getText().toString().trim();
        Util.CheckForNullEntry(dateText,m_DateField);

        SimpleDateFormat currentFormat = new SimpleDateFormat("MMM dd, yyyy");
        Date objectDate = currentFormat.parse(dateText);

        DateFormat targetFormat = new SimpleDateFormat("MM-dd-yyyy");
        assert objectDate != null;
        String date = targetFormat.format(objectDate);

        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months month = Months.monthsBetween(epoch,now);
        Days day = Days.daysBetween(epoch,now);

        return new Data(expenseId, categoryName, merchant, Double.parseDouble(amount), date, month.getMonths(), day.getDays(), note);
    }

    private ArrayAdapter<String> GetExistingCategoryList(){
        List<String> items=Util.GetExistingCategories();
        return new ArrayAdapter<>(this, R.layout.list_item, R.id.category_items, items);
    }

    private AutoCompleteTextView m_CategoryField;
    private EditText m_MerchantField;
    private EditText m_AmountField;
    private EditText m_DateField;
    private EditText m_NoteField;

    private MaterialDatePicker m_DatePicker;
    private final String TAG = "AddTransactionActivity";

}
