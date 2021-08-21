package com.example.personalfinance;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.joda.time.Months;

import java.util.Objects;

public class PlansActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Text", "Plans");
        setContentView(R.layout.activity_plans);
    }

    private AutoCompleteTextView m_CategoryField;
    private EditText m_MerchantField;
    private EditText m_AmountField;
    private EditText m_DateField;
    private EditText m_NoteField;
    private Button m_ConfirmBtn;
    private Button m_CancelBtn;

    private FirebaseAuth m_Auth = FirebaseAuth.getInstance();
    private String a_Uid = Objects.requireNonNull(m_Auth.getCurrentUser()).getUid();
    Months currentMonth = Util.getMonth();
    private DatabaseReference m_PlansRef=FirebaseDatabase.getInstance().getReference().child("plans").child(a_Uid);




}
