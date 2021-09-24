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

        currentUser = m_Auth.getCurrentUser();
        assert currentUser != null;
        String uId = currentUser.getUid();

        DocumentReference documentReference = m_Firestore.collection("users").document(uId);
        m_StorageReference=FirebaseStorage.getInstance().getReference();

        DatabaseReference databaseReference = database.getReference("users");

        m_Save.setOnClickListener(v -> {
            UploadData();
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

    }

    private void UploadImagetoFirebase(Uri a_ImageUri){

        StorageReference fileReference = m_StorageReference.child("users/" +
                Objects.requireNonNull(m_Auth.getCurrentUser()).getUid()+ "profile.jpg");
        fileReference.putFile(a_ImageUri).addOnSuccessListener(taskSnapshot ->
                fileReference.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri)
                        .into(m_Image))).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Image Upload Failed", Toast.LENGTH_SHORT).show());
    }

    private void UploadData(){
        String name = Objects.requireNonNull(m_Auth.getCurrentUser()).getDisplayName();
        String firstName = m_FirstName.getText().toString();
        String lastName = m_LastName.getText().toString();
        String userEmail = m_Email.getText().toString();

        if(!TextUtils.isEmpty(firstName) ||!TextUtils.isEmpty(lastName)||!TextUtils.isEmpty(userEmail) ) {

            DocumentReference documentReference =m_Firestore.collection("users")
                    .document(currentUser.getUid());
            Map<String, Object> user = new HashMap<>();
            user.put("firstName",firstName);
            user.put("lastName",lastName);
            user.put("Email", userEmail);

            documentReference.set(user).addOnSuccessListener(unused -> Log.d(TAG, "User profile updated for "+currentUser.getUid()));
        }
        else{
            Log.e("Exception","Empty Field");
        }
    }



    private final FirebaseAuth m_Auth = FirebaseAuth.getInstance();
    FirebaseUser currentUser;

    private  ImageView m_Image;
    private EditText m_FirstName;
    private EditText m_LastName;
    private EditText m_Email;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final FirebaseFirestore m_Firestore = FirebaseFirestore.getInstance();
    private StorageReference m_StorageReference;
    private DocumentReference m_DocumentReference;
    private static final String TAG = "UserProfile";
}
