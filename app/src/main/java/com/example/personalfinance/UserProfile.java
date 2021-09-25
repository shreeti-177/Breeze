//
// Implementation of the UserProfile class
// This class provides an interface to edit user profile
// including name, email and profile picture
//
package com.example.personalfinance;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserProfile extends AppCompatActivity {

    /**/
    /*
    * NAME
        UserProfile::onCreate() - Overrides the default onCreate function for UserProfile class

    * SYNOPSIS
        void UserProfile::onCreate(Bundle savedInstanceState);
        * savedInstanceState => previous state of the activity

    * DESCRIPTION
        This functions attempts to fetch user data from FireStore and populate the fields in the layout.
        Then, it allows the user to change any values in those editable fields. It also sets on click
        listener for changing a profile picture. Once the user confirms to save their changes,
        it updates the user data in the FireStore database.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        10:40pm, 02/30/2021
    */
    /**/
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        m_FirstName = findViewById(R.id.firstNameField);
        m_LastName = findViewById(R.id.lastNameField);
        m_Email = findViewById(R.id.emailField);
        m_Image = findViewById(R.id.userImage);
        Button m_Save = findViewById(R.id.saveButton);
        Button m_Cancel = findViewById(R.id.cancelButton);

        m_CurrentUser = m_Auth.getCurrentUser();
        assert m_CurrentUser != null;
        String uId = m_CurrentUser.getUid();

        DocumentReference documentReference = m_Firestore.collection("users").document(uId);
        m_StorageReference=FirebaseStorage.getInstance().getReference();

        DatabaseReference databaseReference = m_Database.getReference("users");

        m_Save.setOnClickListener(v -> {
            UpdateUserData();
            finish();
        });

        StorageReference profileReference = m_StorageReference.child("users/" + m_Auth.getCurrentUser().getUid()+ "profile.jpg");

        profileReference.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(m_Image));

        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        assert data != null;
                        Uri imageUri = data.getData();
                        UploadImagetoFirebase(imageUri);

                    }
                });


        m_Image.setOnClickListener(v -> {
            Intent openGalleyIntent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            someActivityResultLauncher.launch(openGalleyIntent);
        });

        m_Cancel.setOnClickListener(v -> finish());

        m_FirstName.setText(Objects.requireNonNull(m_Auth.getCurrentUser()
                .getDisplayName()).split(" ")[0]);
        m_LastName.setText(m_Auth.getCurrentUser().getDisplayName().split(" ")[1]);
        m_Email.setText((m_Auth.getCurrentUser().getEmail()));

    } /*  public void onCreate(@Nullable Bundle savedInstanceState) */


    /**/
    /*
    * NAME
        UserProfile::UploadImagetoFirebase() - Uploads the selected image from gallery to Firebase

    * SYNOPSIS
        void UserProfile::UploadImagetoFirebase(Uri a_ImageUri);
        * a_ImageUri => external content uri to access the image in the future

    * DESCRIPTION
        This functions attempts to use store the image in the Firebase Storage. It parses the passed Uri
        and loads the image in the storage. Then, it creates a download uri for the image that can
        be used for displaying the image in future logins.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        10:40pm, 02/30/2021
    */
    /**/
    private void UploadImagetoFirebase(Uri a_ImageUri){

        StorageReference fileReference = m_StorageReference.child("users/" +
                Objects.requireNonNull(m_Auth.getCurrentUser()).getUid()+ "profile.jpg");
        fileReference.putFile(a_ImageUri).addOnSuccessListener(taskSnapshot ->
                fileReference.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri)
                        .into(m_Image))).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Image Upload Failed", Toast.LENGTH_SHORT).show());
    }/* private void UploadImagetoFirebase(Uri a_ImageUri) */


    /**/
    /*
    * NAME
        UserProfile::UpdateUserData() - Captures the text data fields to include any new changes for the user
        *
    * SYNOPSIS
        void UserProfile::UpdateUserData();

    * DESCRIPTION
        This functions attempts to parse all the text fields to capture new changes from the user.
        Once it validates the user input, it then uploads that data to the FireStore as a replacement for the
        old user object. If the task is successful, it returns to the homepage; throws an error otherwise.

    * AUTHOR
        Shreeti Shrestha

    * DATE
        10:40pm, 02/30/2021
    */
    /**/
    private void UpdateUserData(){
        String name = Objects.requireNonNull(m_Auth.getCurrentUser()).getDisplayName();
        String firstName = m_FirstName.getText().toString();
        String lastName = m_LastName.getText().toString();
        String userEmail = m_Email.getText().toString();

        if(!TextUtils.isEmpty(firstName) ||!TextUtils.isEmpty(lastName)||!TextUtils.isEmpty(userEmail) ) {

            DocumentReference documentReference =m_Firestore.collection("users")
                    .document(m_CurrentUser.getUid());
            Map<String, Object> user = new HashMap<>();
            user.put("firstName",firstName);
            user.put("lastName",lastName);
            user.put("Email", userEmail);

            documentReference.set(user).addOnSuccessListener(unused -> Log.d(TAG, "User profile updated for "+ m_CurrentUser.getUid()));
        }
        else{
            Log.e("Exception","Empty Field");
        }
    } /* private void UpdateUserData() */


    private final FirebaseAuth m_Auth = FirebaseAuth.getInstance();
    private FirebaseUser m_CurrentUser;
    private  ImageView m_Image;
    private EditText m_FirstName;
    private EditText m_LastName;
    private EditText m_Email;
    private final FirebaseDatabase m_Database = FirebaseDatabase.getInstance();
    private final FirebaseFirestore m_Firestore = FirebaseFirestore.getInstance();
    private StorageReference m_StorageReference;
    private static final String TAG = "UserProfile";
}
