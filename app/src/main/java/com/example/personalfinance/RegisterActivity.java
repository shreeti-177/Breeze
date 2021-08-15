package com.example.personalfinance;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        SetRegistrationDetails();
    }

    private void SetRegistrationDetails(){
        EditText m_FirstName = findViewById(R.id.firstNameField);
        EditText m_LastName = findViewById(R.id.lastNameField);
//        String m_UserName=m_FirstName.getText().toString().trim()+m_LastName.getText().toString().trim();
        m_NewUserEmail=findViewById(R.id.emailField);
        m_NewUserPassword=findViewById(R.id.passwordField);
        m_ConfirmUserPassword=findViewById(R.id.confirmPasswordField);
        Button m_SignUpBtn = findViewById(R.id.signUpBtn);
        TextView m_SignInLink = findViewById(R.id.signInLink);
        m_ProgressBar=findViewById(R.id.progress_log);
        m_Auth= FirebaseAuth.getInstance();

        m_SignUpBtn.setOnClickListener(v -> RegistrationButtonClicked());

        m_SignInLink.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), LoginActivity.class)));
    }

    private void RegistrationButtonClicked(){
        String userEmail=GetUserEmail();
        String userPassword=GetUserPassword();
        m_ProgressBar.setVisibility(View.VISIBLE);
        RegisterWithFirebase(userEmail,userPassword);
    }

    private void RegisterWithFirebase(String a_UserEmail, String a_UserPassword){
        m_Auth.createUserWithEmailAndPassword(a_UserEmail,a_UserPassword).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()){
                Log.d(TAG, "CreateUserWithEmail: success");
                FirebaseUser currentUser = m_Auth.getCurrentUser();
                Toast.makeText(getApplicationContext(), "Registration Successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), OnboardActivity.class));
            }
            else{
                Log.w(TAG, "CreateUserWithEmail: failure", task.getException());
                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String GetUserEmail(){
        String userEmail=m_NewUserEmail.getText().toString().trim();
        CheckForNullEntry(userEmail, m_NewUserEmail);
        if(!(userEmail.contains("@"))){
            m_NewUserEmail.setError("Invalid Email Address");
            m_NewUserEmail.requestFocus();
        }
        return userEmail;
    }

    private String GetUserPassword(){
        String userPassword=m_NewUserPassword.getText().toString().trim();
        String confirmUserPassword =m_ConfirmUserPassword.getText().toString().trim();
        CheckForNullEntry(userPassword,m_NewUserPassword);
        CheckForNullEntry(confirmUserPassword,m_ConfirmUserPassword);

        if(userPassword.length()<6){
            Log.e(TAG, "Password should be at least 6 characters");
            m_NewUserPassword.setError("Password should be at least 6 characters");
            m_NewUserPassword.requestFocus();
        }
        if(!(userPassword.contentEquals(confirmUserPassword))){
            Log.e(TAG, "Passwords do not match");
            m_NewUserPassword.setError("Passwords do not match");
            m_ConfirmUserPassword.setError("Passwords do not match");
            m_NewUserPassword.requestFocus();
        }
        return userPassword;
    }

    private void CheckForNullEntry(String a_TextEntry, EditText a_TextField){
        if(a_TextEntry.isEmpty()){
            Log.e(TAG,"Empty Field");
            a_TextField.setError("Required Field");
            a_TextField.requestFocus();
        }
    }

    // Register Class member variables
    private EditText m_NewUserEmail;
    private EditText m_NewUserPassword;
    private EditText m_ConfirmUserPassword;
    private FirebaseAuth m_Auth;
    private ProgressBar m_ProgressBar;
    private static final String TAG = "RegistrationActivity";

}
