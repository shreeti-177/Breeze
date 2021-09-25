//
// Implementation of the NewGoalActivity class
//
package com.example.personalfinance;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;

import java.util.List;
import java.util.Objects;

public class NewGoalActivity extends AppCompatActivity {

    /**/
    /*
    * NAME
        NewGoalActivity::onCreate() - Overrides the default onCreate function for the class

    * SYNOPSIS
        void NewGoalActivity::onCreate(Bundle savedInstanceState);
        * savedInstanceState => previous state of the activity

    * DESCRIPTION
        This function will attempt to set the layout for entering a new goal.
        It listens for 2 events: cancel vs. confirm
        If user confirms goal, it invokes other functions to validate user entries before adding the
        goal to the database.
        If user clicks cancel, it returns to the PlansActivity page, where user can view list of
        previous plans.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        9:27am, 06/08/2021
    */
    /**/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_new_goal);

        m_GoalName=findViewById(R.id.goalNameField);
        m_CategoryField=findViewById(R.id.goalCategoryField);
        m_BudgetField=findViewById(R.id.amountField);
        m_NotesField=findViewById(R.id.notesField);
        Button confirmBtn = findViewById(R.id.confirmButton);
        Button cancelBtn = findViewById(R.id.cancelButton);


        ArrayAdapter<String> itemsAdapter = GetExistingCategoryList();
        m_CategoryField.setAdapter(itemsAdapter);

        cancelBtn.setOnClickListener(v -> finish());

        confirmBtn.setOnClickListener(v -> {
            Data a_Goal = GetNewGoal();
            m_PlansRef.child(a_Goal.GetGoal()).setValue(a_Goal).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Log.d(TAG, "AddGoal: success");
                    Toast.makeText(getApplicationContext(),"New Goal added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else{
                    Log.w(TAG, "AddGoal: failure", task.getException());
                    Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }/* protected void onCreate(Bundle savedInstanceState) */

    /**/
    /*
    * NAME
        NewGoalActivity::GetNewGoal() - Processes user entries for a new goal to instantiate
        an object of a Data class

    * SYNOPSIS
        Data NewGoalActivity::GetNewGoal();
        * savedInstanceState => previous state of the activity

    * RETURNS
        Returns a Data object to add to plans in Firebase database

    * DESCRIPTION
        This function will collect user entries and validate them. Then, it will construct a new
        Data object and return it to the caller to add to database.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        11:27pm, 06/13/2021
    */
    /**/

    private Data GetNewGoal(){

        String goalName = GetGoal();
        Double amount = GetBudget();
        String notes=m_NotesField.getText().toString().trim();
        String goalId = m_PlansRef.push().getKey();

        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months month = Months.monthsBetween(epoch,now);
        Days day = Days.daysBetween(epoch,now);

        // create new Data object
        Data goal = new Data (goalId,goalName,amount, now.toString(), month.getMonths(),
                day.getDays(), notes);

        String category = m_CategoryField.getText().toString().trim();

        // check if user has assigned goal to a particular category
        // if yes, include it in goal details
        if(!category.isEmpty()){
            goal.SetGoalCategory(category);
        }

        return goal;
    } /* private Data GetNewGoal() */


    /**/
    /*
    * NAME
        NewGoalActivity::GetGoal() - Validates user entry before returning goal name to caller

    * SYNOPSIS
        String NewGoalActivity::GetGoal();

    * DESCRIPTION
        This function will attempt to collect the user entered goal name, and check for null entry.
        If it's null, it will prompt the user to enter a valid value. Otherwise, it will return the
        value entered.

    * RETURNS
        Returns validated a_Goal to caller

    * AUTHOR
        Shreeti Shrestha

    * DATE
        11:40pm, 06/13/2021
    */
    /**/
    private String GetGoal(){
        String a_Goal=m_GoalName.getText().toString().trim();
        Util.CheckForNullEntry(a_Goal, m_GoalName);
        return a_Goal;
    }/* String NewGoalActivity::GetGoal(); */


    /**/
    /*
    * NAME
        NewGoalActivity::GetBudget() - Validates user entry before returning goal budget to caller

    * SYNOPSIS
        String NewGoalActivity::GetBudget();

    * DESCRIPTION
        This function will attempt to collect the user entered budget for a goal, and check for null
        entry. If it's invalid(string or null), it will prompt the user to enter a valid value.
        Otherwise, it will return the value entered.

    * RETURNS
        Returns validated budgetValue to caller

    * AUTHOR
        Shreeti Shrestha

    * DATE
        11:40pm, 06/13/2021
    */
    /**/
    private Double GetBudget(){
        String amount = m_BudgetField.getText().toString().trim();
        Util.CheckForNullEntry(amount,m_BudgetField);
        return Double.parseDouble(amount);
    }/* String NewGoalActivity::GetBudget(); */

    private ArrayAdapter<String> GetExistingCategoryList(){
        List<String> items=Util.GetExistingCategories();
        return new ArrayAdapter<>(this, R.layout.list_item, R.id.category_items, items);
    }

    private EditText m_GoalName;
    private AutoCompleteTextView m_CategoryField;
    private EditText m_BudgetField;
    private EditText m_NotesField;

    private final FirebaseAuth m_Auth = FirebaseAuth.getInstance();
    private final String m_Uid = Objects.requireNonNull(m_Auth.getCurrentUser()).getUid();
    private final DatabaseReference m_PlansRef= FirebaseDatabase.getInstance().getReference().
            child("plans").child(m_Uid);
    private static final String TAG = "NewGoalActivity";


}
