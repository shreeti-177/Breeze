//
// Implementation of the RegisterActivity class
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    /**/
    /*
    * NAME
        RegisterActivity::onCreate() - Overrides the default onCreate function for RegisterActivity class

    * SYNOPSIS
        void MainActivity::onCreate(Bundle savedInstanceState);
        * savedInstanceState => previous state of the activity

    * DESCRIPTION
        This function will be the first one to be called once the app is launched.
        It will then attempt to call the login page, which is eventually what the user sees once the
        app is launched.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        10:27am, 02/04/2021
    */
    /**/

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
        Button m_SignUpBtn = findViewById(R.id.signUpBtn);
        TextView m_SignInLink = findViewById(R.id.signInLink);
        m_ProgressBar=findViewById(R.id.progress_log);
        m_Auth= FirebaseAuth.getInstance();
        m_Firestore=FirebaseFirestore.getInstance();
//        m_Database=FirebaseDatabase.getInstance().getReference();

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
        firstName = GetString(m_FirstName);
        lastName = GetString(m_LastName);

        userEmail=GetString(m_NewUserEmail);
        String userPassword=GetString(m_NewUserPassword);
        String userConfirmPassword=GetString(m_ConfirmUserPassword);
        if(ValidateInputs(userEmail,userPassword,userConfirmPassword)){
            m_UserName=m_FirstName.getText().toString().trim()+" " + m_LastName.getText().toString().trim();
            m_ProgressBar.setVisibility(View.VISIBLE);
            RegisterWithFirebase(userEmail,userPassword);
        }

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
                Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException())
                        .getMessage(), Toast.LENGTH_SHORT).show();
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
        assert currentUser != null;
        DocumentReference documentReference =m_Firestore.collection("users")
                .document(currentUser.getUid());
        Map<String, Object> user = new HashMap<>();
        user.put("firstName",firstName);
        user.put("lastName",lastName);
        user.put("Email", userEmail);

        documentReference.set(user).addOnSuccessListener(unused ->
                Log.d(TAG, "User profile created for "+currentUser.getUid()));


        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(m_UserName)
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

    private String GetString(EditText a_TextField){
        String value = a_TextField.getText().toString().trim();
        CheckForNullEntry(value, a_TextField);
        return value;
    }

    private boolean ValidateInputs(String a_Email, String a_Password, String a_ConfirmPassword){
        if(!(a_Email.contains("@"))){
            m_NewUserEmail.setError("Invalid Email Address");
            m_NewUserEmail.requestFocus();
            return false;
        }
        if (a_Password.length() < 6) {
            Log.e(TAG, "Password should be at least 6 characters");
            m_NewUserPassword.setError("Password should be at least 6 characters");
            m_NewUserPassword.requestFocus();
            return false;
        }
        if (!(a_Password.contentEquals(a_ConfirmPassword))) {
            Log.e(TAG, "Passwords do not match");
            m_NewUserPassword.setError("Passwords do not match");
            m_ConfirmUserPassword.setError("Passwords do not match");

            m_NewUserPassword.requestFocus();
            m_ConfirmUserPassword.requestFocus();
            return false;
        }
        return true;

    }


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
    private FirebaseFirestore m_Firestore;
    private String userEmail;
    private String firstName;
    private String lastName;
    private String m_UserName;
    private ProgressBar m_ProgressBar;
    private static final String TAG = "RegistrationActivity";


}
