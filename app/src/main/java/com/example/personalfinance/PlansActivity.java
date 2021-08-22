package com.example.personalfinance;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;

import java.util.Objects;

public class PlansActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plans);

        m_RecyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager m_LinearLayoutManager = new LinearLayoutManager(this);
        m_RecyclerView.setLayoutManager(m_LinearLayoutManager);

        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(m_PlansRef, Data.class)
                .build();

        m_Adapter = new PlansAdapter(options);
        m_RecyclerView.setAdapter(m_Adapter);

        m_AddNewGoal=findViewById(R.id.addGoalBtn);

        m_AddNewGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                OpenAddGoalDialog();
                startActivity(new Intent(getApplicationContext(),NewGoalActivity.class));
            }
        });
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

//    private void OpenAddGoalDialog(){
//        MaterialAlertDialogBuilder addDialog = new MaterialAlertDialogBuilder(this);
//        LayoutInflater inflater = LayoutInflater.from(this);
//        a_View = inflater.inflate(R.layout.dialog_new_goal, null);
//        addDialog.setView(a_View);
//
//        final AlertDialog dialog = addDialog.create();
//        dialog.show();
//        dialog.setCancelable(false);
//
//        m_GoalName=findViewById(R.id.goalName);
//        m_CategoryField=findViewById(R.id.goalCategoryField);
//        m_AmountField=findViewById(R.id.amountField);
//        m_NotesField=findViewById(R.id.notesField);
//        m_ConfirmBtn=findViewById(R.id.confirmBudget);
//        m_CancelBtn=findViewById(R.id.cancelAction);
//
//
////        m_CancelBtn.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                dialog.dismiss();
////            }
////        });
////
////        m_ConfirmBtn.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                Data a_Goal =null;
////                a_Goal = GetNewGoal();
////
////
////                m_PlansRef.child(a_Goal.getId()).setValue(a_Goal).addOnCompleteListener(new OnCompleteListener<Void>() {
////                    @Override
////                    public void onComplete(@NonNull @NotNull Task<Void> task) {
////                        if(task.isSuccessful()){
////                            Log.d(TAG, "AddGoal: success");
////                            Toast.makeText(getApplicationContext(),"New Goal added successfully", Toast.LENGTH_SHORT).show();
////                            dialog.dismiss();
////                        }
////                        else{
////                            Log.w(TAG, "AddGoal: failure", task.getException());
////                            Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
////                        }
////                    }
////                });
////
////            }
////        });
//    }
//    private Data GetNewGoal(){
//        String a_GoalName = GetGoal();
//        Double a_Amount = GetAmount();
//        String a_Notes=m_NotesField.getText().toString().trim();
//
//
//        String a_GoalId = m_PlansRef.push().getKey();
//
//        MutableDateTime a_Epoch = new MutableDateTime();
//        a_Epoch.setDate(0);
//        DateTime a_Now = new DateTime();
//        Months a_Month = Months.monthsBetween(a_Epoch,a_Now);
//        Days a_Day = Days.daysBetween(a_Epoch,a_Now);
//
//        Data a_Goal = new Data (a_GoalId,a_GoalName,a_Amount, a_Now.toString(), a_Month.getMonths(),a_Day.getDays(), a_Notes);
//        String a_Category = m_CategoryField.getText().toString().trim();
//        if(!a_Category.isEmpty()){
//            a_Goal.setGoalCategory(a_Category);
//        }
//        return a_Goal;
//    }

//    private String GetGoal(){
//        String a_Goal=m_GoalName.getText().toString().trim();
//        Util.CheckForNullEntry(a_Goal, m_GoalName);
//        return a_Goal;
//    }

//    private Double GetAmount(){
//        String amount = m_AmountField.getText().toString().trim();
//        Util.CheckForNullEntry(amount,m_AmountField);
//        Double a_Amount = Double.parseDouble(amount);
//        return a_Amount;
//    }



    private Button m_AddNewGoal;
    private View a_View;


//    private EditText m_GoalName;
//    private AutoCompleteTextView m_CategoryField;
//    private EditText m_AmountField;
//    private EditText m_NotesField;
//    private Button m_ConfirmBtn;
//    private Button m_CancelBtn;

    private FirebaseAuth m_Auth = FirebaseAuth.getInstance();
    private String a_Uid = Objects.requireNonNull(m_Auth.getCurrentUser()).getUid();
    Months currentMonth = Util.getMonth();
    private DatabaseReference m_PlansRef= FirebaseDatabase.getInstance().getReference().child("plans").child(a_Uid);
    private RecyclerView m_RecyclerView;
    private PlansAdapter m_Adapter;
    private static final String TAG = "PlansActivity";
}
