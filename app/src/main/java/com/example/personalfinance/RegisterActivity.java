//
// Implementation of the RegisterActivity class
//
package com.example.personalfinance;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        SetRegistrationDetails();
    }

    /**/
    /*
    * NAME
        RegisterActivity::SetRegistrationDetails() - Connects register field views to layout and
        adds listeners for button clicks

    * SYNOPSIS
        void RegisterActivity::SetRegistrationDetails();

    * DESCRIPTION
        This function will attempt to setup view for registration and assign their view id

    * AUTHOR
        Shreeti Shrestha

    * DATE
        08:00am, 02/01/2021
    */
    /**/
    private void SetRegistrationDetails(){
        m_FirstName = findViewById(R.id.firstNameField);
        m_LastName = findViewById(R.id.lastNameField);
        m_NewUserEmail=findViewById(R.id.emailField);
        m_NewUserPassword=findViewById(R.id.passwordField);
        m_ConfirmUserPassword=findViewById(R.id.confirmPasswordField);
        m_SignUpBtn = findViewById(R.id.signUpBtn);
        TextView m_SignInLink = findViewById(R.id.signInLink);
        m_ProgressBar=findViewById(R.id.progress_log);
        m_Auth= FirebaseAuth.getInstance();

        m_SignUpBtn.setOnClickListener(v -> RegistrationButtonClicked());

        m_SignInLink.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), LoginActivity.class)));
    }/* private void SetRegistrationDetails() */

    /**/
    /*
    * NAME
        RegisterActivity::RegistrationButtonClicked() - Gets and validates user entered values before
        invoking Firebase to create a new user

    * SYNOPSIS
        void RegisterActivity::RegistrationButtonClicked();

    * DESCRIPTION
        Once the user clicks the Register button, this function will attempt to get the email and
        password, and then call the SignUpWithFirebase function to create a new user

    * AUTHOR
        Shreeti Shrestha

    * DATE
        09:00am, 02/01/2021
    */
    /**/
    private void RegistrationButtonClicked(){
        String userEmail=GetUserEmail();
        String userPassword=GetUserPassword();
        m_UserName=m_FirstName.getText().toString().trim()+" " + m_LastName.getText().toString().trim();
        m_ProgressBar.setVisibility(View.VISIBLE);
        RegisterWithFirebase(userEmail,userPassword);
    }/* private void RegistrationButtonClicked() */

    /**/
    /*
    * NAME
        RegisterActivity::RegisterWithFirebase() - Collects entered values for registration and
        validates them, finally invoking Firebase to create a new user

    * SYNOPSIS
        void RegisterActivity::RegisterWithFirebase(String a_UserEmail, String a_UserPassword);
        * a_UserEmail => non-empty email entered by the user
        * a_UserPassword => non-empty password entered by the user

    * DESCRIPTION
        This function will attempt to create a new user based on the email and password provided.
        It calls the Firebase Authentication function to validate the requirements for credentials
        and to check if an account already exists with those values.
        On successful authentication, it creates a new user and leads to the landing home page.
        Otherwise, it prompts the user to enter new values for registration.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        09:00am, 02/01/2021
    */
    /**/
    private void RegisterWithFirebase(String a_UserEmail, String a_UserPassword){
        m_Auth.createUserWithEmailAndPassword(a_UserEmail,a_UserPassword).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()){
                Log.d(TAG, "CreateUserWithEmail: success");
                SetUserProfile();
                Toast.makeText(getApplicationContext(), "Registration Successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), OnboardActivity.class));
            }
            else{
                Log.w(TAG, "CreateUserWithEmail: failure", task.getException());
                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }/* private void RegisterWithFirebase(String a_UserEmail, String a_UserPassword) */

    /**/
    /*
    * NAME
        RegisterActivity::SetUserProfile() - Update user profile with name

    * SYNOPSIS
        void RegisterActivity::SetUserProfile();

    * DESCRIPTION
        This function will attempt to set the name of the user, so that it can be accessible for
        display at any point in the program

    * AUTHOR
        Shreeti Shrestha

    * DATE
        08:00pm, 02/02/2021
    */
    /**/
    private void SetUserProfile(){
        FirebaseUser currentUser = m_Auth.getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(m_UserName)
//                        .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                .build();

        currentUser.updateProfile(profileUpdates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "User profile updated.");
            }
        });
    }/* private void SetUserProfile() */


    /**/
    /*
    * NAME
        RegisterActivity::GetUserEmail() - Validates user email before returning to the caller

    * SYNOPSIS
        String RegisterActivity::GetUserEmail();

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
        String userEmail=m_NewUserEmail.getText().toString().trim();
        CheckForNullEntry(userEmail, m_NewUserEmail);
        if(!(userEmail.contains("@"))){
            m_NewUserEmail.setError("Invalid Email Address");
            m_NewUserEmail.requestFocus();
            m_SignUpBtn.setClickable(false);
        }
        return userEmail;
    }/* private String GetUserEmail() */


    /**/
    /*
    * NAME
        RegisterActivity::GetUserPassword() - Validates user password before returning to the caller

    * SYNOPSIS
        String RegisterActivity::GetUserPassword();

    * DESCRIPTION
        This function will attempt to collect the user entered password and check for null entry.

    * RETURNS
        Returns validated userPassword to caller

    * AUTHOR
        Shreeti Shrestha

    * DATE
        08:00pm, 02/02/2021
    */
    /**/
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
    }/* private String GetUserPassword() */

    /**/
    /*
    * NAME
        RegisterActivity::CheckForNullEntry() - Checks if a text field has a null value

    * SYNOPSIS
        void RegisterActivity::CheckForNullEntry(a_TextEntry, a_TextField);
        * a_TextEntry: user entered value
        * a_TextField: the field where the user has supposedly entered their desired value

    * DESCRIPTION
        This function will attempt to collect get the user entries and check if they're null before
        verifying that they're authenticated. If the value is indeed null, it will display an error
        message and focus on the text field to prompt the user to enter a valid non-null value.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        08:00pm, 02/02/2021
    */
    /**/
    private void CheckForNullEntry(String a_TextEntry, EditText a_TextField){
        if(a_TextEntry.isEmpty()){
            a_TextField.setError("Required Field");
            a_TextField.requestFocus();
        }
    }/* private void CheckForNullEntry(String a_TextEntry, EditText a_TextField) */



    private EditText m_NewUserEmail;
    private EditText m_NewUserPassword;
    private EditText m_ConfirmUserPassword;
    private EditText m_FirstName;
    private EditText m_LastName;
    private FirebaseAuth m_Auth;
    private String m_UserName;
    private Button m_SignUpBtn;
    private ProgressBar m_ProgressBar;
    private static final String TAG = "RegistrationActivity";


}
