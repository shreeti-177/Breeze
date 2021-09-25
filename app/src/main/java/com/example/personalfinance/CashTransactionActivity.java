//
// Implementation of the CashTransactionActivity class
// This class provides an interface to the user to manually add new transactions
//
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

    /**/
    /*
    * NAME
        CashTransactionActivity::onCreate() - Overrides the default onCreate function for this class

    * SYNOPSIS
        void CashTransactionActivity::onCreate(Bundle savedInstanceState);
        * savedInstanceState => previous state of the activity

    * DESCRIPTION
        This function will attempt to set the layout for the add transaction page.
        Then, it set on click listeners for the cancel and confirm button.
        Upon confirming to add a transaction, it calls the CreateTransactionObject() to validate user
        entered values and create a Data object, and then finally adds the object to the Firebase database.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        08:00am, 04/30/2021
    */
    /**/
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
                a_Category = CreateTransactionObject();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            assert a_Category != null;
            Util.GetExpenseReference().child(a_Category.GetId()).setValue(a_Category)
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
    } /*  public void onCreate(@Nullable Bundle savedInstanceState)  */


    /**/
    /*
    * NAME
        CashTransactionActivity::CreateTransactionObject() - Creates a new data object to add to
        the expense database

    * SYNOPSIS
        Data CashTransactionActivity::CreateTransactionObject();

    * DESCRIPTION
        This function attempts to check for any null entries in the transaction amount field. If so,
        it prompts the user to enter a valid value. If not, it creates an instance of the Data class
        for the user entered values and returns the object

    * RETURNS
        Returns a new Data Object

    * AUTHOR
        Shreeti Shrestha

    * DATE
        09:00pm, 04/30/2021
    */
    /**/

    private Data CreateTransactionObject() throws ParseException {
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

        MutableDateTime dateTime = new MutableDateTime(objectDate);
        DateFormat targetFormat = new SimpleDateFormat("MM-dd-yyyy");
        assert objectDate != null;
        String date = targetFormat.format(objectDate);

        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months month = Months.monthsBetween(epoch,now);
        Days day = Days.daysBetween(epoch,dateTime);

        return new Data(expenseId, categoryName, merchant, Double.parseDouble(amount), date, month.getMonths(), day.getDays()+1, note);
    }/* private Data CreateTransactionObject() */


    /**/
    /*
    * NAME
        CashTransactionActivity::GetExistingCategoryList() - Fetches all 10 categories as an Array adapter

    * SYNOPSIS
        ArrayAdapter<String> CashTransactionActivity::GetExistingCategoryList();

    * DESCRIPTION
        This function attempts to get a list of all the 10 categories. Then, it creates a new array adapter
        with the list so that it can be used in the drop down menu when user is prompted to select a
        category name for the transaction.

    * RETURNS
        Returns a new ArrayAdapter of strings

    * AUTHOR
        Shreeti Shrestha

    * DATE
        09:30pm, 04/30/2021
    */
    /**/

    private ArrayAdapter<String> GetExistingCategoryList(){
        List<String> items=Util.GetExistingCategories();
        return new ArrayAdapter<>(this, R.layout.list_item, R.id.category_items, items);
    }/* private ArrayAdapter<String> GetExistingCategoryList() */

    private AutoCompleteTextView m_CategoryField;
    private EditText m_MerchantField;
    private EditText m_AmountField;
    private EditText m_DateField;
    private EditText m_NoteField;
    private MaterialDatePicker m_DatePicker;
    private final String TAG = "AddTransactionActivity";

}
