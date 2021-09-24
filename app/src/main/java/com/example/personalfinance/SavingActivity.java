package com.example.personalfinance;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SavingActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savings);
        m_GoalField = findViewById(R.id.goalField);
        EditText m_Amount = findViewById(R.id.amountField);

        Toolbar toolbar = findViewById(R.id.toolBar);
        toolbar.setNavigationOnClickListener(v -> finish());

        Button confirmBtn = findViewById(R.id.confirmButton);
        Button cancelBtn = findViewById(R.id.cancelButton);

        GetExistingGoalsList();

        confirmBtn.setOnClickListener(v -> {
                    String goal = m_GoalField.getText().toString();
                    String amount = m_Amount.getText().toString();

                    Util.GetPlansReference().child(goal).child("savings").setValue(amount).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "SetSaving: success");

                            finish();
                        } else {
                            Log.w(TAG, "SetSaving: failure", task.getException());
                        }
                    });
                });
        cancelBtn.setOnClickListener(v -> finish());


    }

    private void GetExistingGoalsList(){
        List<String> items = new ArrayList<>();
        Util.GetPlansReference().orderByChild("goal").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        Data data = dataSnapshot.getValue(Data.class);
                        assert data != null;
                        items.add(data.getGoal());
                    }
                }
                ArrayAdapter<String> itemsAdapter = SetItemsAdapter(items);
                m_GoalField.setAdapter(itemsAdapter);

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
    private ArrayAdapter<String> SetItemsAdapter(List<String> items){
        return new ArrayAdapter<>(this, R.layout.list_item, R.id.category_items, items);
    }

    private AutoCompleteTextView m_GoalField;
        private final static String TAG = "SavingActivity";
}
