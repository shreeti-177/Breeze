package com.example.personalfinance;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class NewGoalActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_new_goal);

        m_GoalName=findViewById(R.id.goalName);
        m_CategoryField=findViewById(R.id.goalCategoryField);
        m_BudgetField=findViewById(R.id.amountField);
        m_NotesField=findViewById(R.id.notesField);
        m_ConfirmBtn=findViewById(R.id.confirmBudget);
        m_CancelBtn=findViewById(R.id.cancelAction);


        m_CancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        m_ConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Data a_Goal =null;
                a_Goal = GetNewGoal();


                m_PlansRef.child(a_Goal.getId()).setValue(a_Goal).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "AddGoal: success");
                            Toast.makeText(getApplicationContext(),"New Goal added successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else{
                            Log.w(TAG, "AddGoal: failure", task.getException());
                            Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private Data GetNewGoal(){
        String a_GoalName = GetGoal();
        Double a_Amount = GetBudget();
        String a_Notes=m_NotesField.getText().toString().trim();


        String a_GoalId = m_PlansRef.push().getKey();

        MutableDateTime a_Epoch = new MutableDateTime();
        a_Epoch.setDate(0);
        DateTime a_Now = new DateTime();
        Months a_Month = Months.monthsBetween(a_Epoch,a_Now);
        Days a_Day = Days.daysBetween(a_Epoch,a_Now);

        Log.i("Goal Budget",String.valueOf(a_Amount));
        Data a_Goal = new Data (a_GoalId,a_GoalName,a_Amount, a_Now.toString(), a_Month.getMonths(),a_Day.getDays(), a_Notes);
        String a_Category = m_CategoryField.getText().toString().trim();
        if(!a_Category.isEmpty()){
            a_Goal.setGoalCategory(a_Category);
        }
        return a_Goal;
    }

    private String GetGoal(){
        String a_Goal=m_GoalName.getText().toString().trim();
        Util.CheckForNullEntry(a_Goal, m_GoalName);
        return a_Goal;
    }

    private Double GetBudget(){
        String amount = m_BudgetField.getText().toString().trim();
        Util.CheckForNullEntry(amount,m_BudgetField);
        Double a_Budget = Double.parseDouble(amount);
        return a_Budget;
    }

    private EditText m_GoalName;
    private AutoCompleteTextView m_CategoryField;
    private EditText m_BudgetField;
    private EditText m_NotesField;
    private Button m_ConfirmBtn;
    private Button m_CancelBtn;

    private FirebaseAuth m_Auth = FirebaseAuth.getInstance();
    private String a_Uid = Objects.requireNonNull(m_Auth.getCurrentUser()).getUid();
    Months currentMonth = Util.getMonth();
    private DatabaseReference m_PlansRef= FirebaseDatabase.getInstance().getReference().child("plans").child(a_Uid);
    private static final String TAG = "NewGoalActivity";


}
