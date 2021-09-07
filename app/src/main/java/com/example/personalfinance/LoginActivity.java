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

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SetLoginDetails();
    }

        private void SetLoginDetails(){
            m_UserEmail=findViewById(R.id.emailField);
            m_UserPassword=findViewById(R.id.passwordField);
            Button m_SignInBtn = findViewById(R.id.signInBtn);
            TextView m_ForgotPasswordLink = findViewById(R.id.forgotPasswordLink);
            TextView m_SignUpLink = findViewById(R.id.signUpLink);
            m_ProgressBar=findViewById(R.id.progressLog);
            m_Auth= FirebaseAuth.getInstance();

            m_SignInBtn.setOnClickListener(v -> LoginButtonClicked());
            m_ForgotPasswordLink.setOnClickListener(v -> ForgotPassword());
            m_SignUpLink.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), RegisterActivity.class)));
        }

        private void ForgotPassword(){}

        private void LoginButtonClicked(){
            String userEmail=GetUserEmail();
            String userPassword=GetUserPassword();
            m_ProgressBar.setVisibility(View.VISIBLE);
            LoginWithFirebase(userEmail,userPassword);
        }

        private void LoginWithFirebase(String a_UserEmail, String a_UserPassword){
            m_Auth.signInWithEmailAndPassword(a_UserEmail, a_UserPassword).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "LoginWithEmail: success");
                    FirebaseUser currentUser = m_Auth.getCurrentUser();
                    Toast.makeText(getApplicationContext(), "Login Successful!", Toast.LENGTH_SHORT).show();
                    FetchData x = new FetchData();
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                }
                else{
                    Log.w(TAG, "LoginWithEmail: failure", task.getException());
                    Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        private String GetUserEmail(){
            String userEmail=m_UserEmail.getText().toString().trim();
            CheckForNullEntry(userEmail, m_UserEmail);
            if(!(userEmail.contains("@"))){
                m_UserEmail.setError("Invalid Email Address");
                m_UserEmail.requestFocus();
            }
            return userEmail;
        }

        private String GetUserPassword(){
            String userPassword=m_UserPassword.getText().toString().trim();
            CheckForNullEntry(userPassword,m_UserPassword);
            return userPassword;
        }

        private void CheckForNullEntry(String a_TextEntry, EditText a_TextField){
            if(a_TextEntry.isEmpty()){
                Log.e(TAG,"Empty Field");
                a_TextField.setError("Required Field");
                a_TextField.requestFocus();
            }
        }

        // Login Class member variables
        private EditText m_UserEmail;
        private EditText m_UserPassword;
        private FirebaseAuth m_Auth;
        private ProgressBar m_ProgressBar;
        private static final String TAG = "LoginActivity";

    }
