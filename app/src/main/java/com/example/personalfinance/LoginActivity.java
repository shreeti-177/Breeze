//
// Implementation of the LoginActivity class
//
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

    /**/
    /*
    * NAME
        LoginActivity::SetLoginDetails() - Connects login field views to layout and adds listeners
        for button clicks

    * SYNOPSIS
        void LoginActivity::SetLoginDetails();

    * DESCRIPTION
        This function will attempt to setup view for login and assign their view id

    * AUTHOR
        Shreeti Shrestha

    * DATE
        9:00am, 02/02/2021
    */
    /**/
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
    }/* private void SetLoginDetails(); */

    private void ForgotPassword(){}

    /**/
    /*
    * NAME
        LoginActivity::LoginButtonClicked() - Collects entered values for login after validation

    * SYNOPSIS
        void LoginActivity::LoginButtonClicked();

    * DESCRIPTION
        This function will attempt to collect all login details (username, password) and check their
        validity before initiating Firebase authentication

    * AUTHOR
        Shreeti Shrestha

    * DATE
        9:00am, 02/02/2021
    */
    /**/
    private void LoginButtonClicked(){
        String userEmail=GetUserEmail();
        String userPassword=GetUserPassword();
        m_ProgressBar.setVisibility(View.VISIBLE);
        LoginWithFirebase(userEmail,userPassword);
    } /* private void LoginButtonClicked() */

    /**/
    /*
    * NAME
        LoginActivity::LoginWithFirebase() - Collects entered values for login and validates them

    * SYNOPSIS
        void LoginActivity::LoginWithFirebase(String a_UserEmail, String a_UserPassword);
        * a_UserEmail => non-empty email entered by the user
        * a_UserPassword => non-empty password entered by the user

    * DESCRIPTION
        This function will attempt to authenticate the user based on the email and password provided.
        It calls the Firebase Authentication function to validate the entered credentials and to
        check if an account exists with those values. On successful authentication, it leads to the
        default home page. If login fails, it shows an error and prompts to enter credentials again.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        10:30am, 02/02/2021
    */
    /**/
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
    }/* private void LoginWithFirebase(String a_UserEmail, String a_UserPassword) */

    /**/
    /*
    * NAME
        LoginActivity::GetUserEmail() - Validates user email before returning to the caller

    * SYNOPSIS
        String LoginActivity::GetUserEmail();

    * DESCRIPTION
        This function will attempt to collect get the user entered email and check for null entry.
        It will also check the basic requirements of an email (like having an @). If the entered
        email doesn't meet the basic requirements, it will prompt the user to enter again.

    * RETURNS
        Returns validated userEmail to caller

    * AUTHOR
        Shreeti Shrestha

    * DATE
        08:00pm, 02/02/2021
    */
    /**/
    private String GetUserEmail(){
        String userEmail=m_UserEmail.getText().toString().trim();
        Util.CheckForNullEntry(userEmail, m_UserEmail);
        if(!(userEmail.contains("@"))){
            m_UserEmail.setError("Invalid Email Address");
            m_UserEmail.requestFocus();
        }
        return userEmail;
    }/* private String GetUserEmail() */

    /**/
    /*
    * NAME
        LoginActivity::GetUserPassword() - Validates user password before returning to the caller

    * SYNOPSIS
        String LoginActivity::GetUserPassword();

    * DESCRIPTION
        This function will attempt to collect get the user entered password and check for null entry.

    * RETURNS
        Returns validated userPassword to caller

    * AUTHOR
        Shreeti Shrestha

    * DATE
        08:00pm, 02/02/2021
    */
    /**/
    private String GetUserPassword(){
        String userPassword=m_UserPassword.getText().toString().trim();
        Util.CheckForNullEntry(userPassword,m_UserPassword);
        return userPassword;
    } /* private String GetUserPassword() */

    private EditText m_UserEmail;
    private EditText m_UserPassword;
    private FirebaseAuth m_Auth;
    private ProgressBar m_ProgressBar;
    private static final String TAG = "LoginActivity";
}
