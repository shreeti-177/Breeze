package com.example.personalfinance;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class UserProfile extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        
        m_NewUser=new User();
        m_FirstName = findViewById(R.id.firstNameField);
        m_LastName = findViewById(R.id.lastNameField);
        m_Email = findViewById(R.id.emailField);
        m_Image = findViewById(R.id.userImage);
        Button m_Save = findViewById(R.id.saveButton);
        Button m_Cancel = findViewById(R.id.cancelButton);

        currentUser = m_Auth.getCurrentUser();
        uId = currentUser.getUid();

        DocumentReference documentReference = m_Firestore.collection("users").document(uId);
        m_StorageReference=FirebaseStorage.getInstance().getReference();

        databaseReference = database.getReference("users");

        m_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadData();
            }
        });

        StorageReference profileReference = m_StorageReference.child("users/" + m_Auth.getCurrentUser().getUid()+ "profile.jpg");

        profileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(m_Image);
            }
        });

        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            Uri imageUri = data.getData();
                            UploadImagetoFirebase(imageUri);

                        }
                    }
                });


        m_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleyIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                someActivityResultLauncher.launch(openGalleyIntent);
            }
        });



        m_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Log.i("Here",m_Auth.getCurrentUser().getDisplayName());
        m_FirstName.setText(m_Auth.getCurrentUser().getDisplayName().split(" ")[0]);
        m_LastName.setText(m_Auth.getCurrentUser().getDisplayName().split(" ")[1]);
        m_Email.setText((m_Auth.getCurrentUser().getEmail()));

    }

    private void UploadImagetoFirebase(Uri a_ImageUri){

        StorageReference fileReference = m_StorageReference.child("users/" + m_Auth.getCurrentUser().getUid()+ "profile.jpg");
        fileReference.putFile(a_ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(m_Image);
                    }
                });
//                Toast.makeText(getApplicationContext(), "Image Uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(getApplicationContext(), "Image Upload Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void UploadData(){
        String name = m_Auth.getCurrentUser().getDisplayName();
        String firstName = m_FirstName.getText().toString();
        String lastName = m_LastName.getText().toString();
        String userEmail = m_Email.getText().toString();

        if(!TextUtils.isEmpty(firstName) ||!TextUtils.isEmpty(lastName)||!TextUtils.isEmpty(userEmail) ) {
            Map<String, String> profile = new HashMap<>();

            DocumentReference documentReference =m_Firestore.collection("users")
                    .document(currentUser.getUid());
            Map<String, Object> user = new HashMap<>();
            user.put("firstName",firstName);
            user.put("lastName",lastName);
            user.put("Email", userEmail);

            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d(TAG, "User profile updated for "+currentUser.getUid());
                }
            });
        }
        else{
            Log.e("Exception","Empty Field");
        }
    }



    private static final int PICK_IMAGE =1;
    private User m_NewUser;
    private FirebaseAuth m_Auth = FirebaseAuth.getInstance();
    FirebaseUser currentUser;
    private String uId;

    private Uri m_ImageUri;
    private  ImageView m_Image;
    private EditText m_FirstName;
    private EditText m_LastName;
    private EditText m_Email;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;
    private FirebaseFirestore m_Firestore = FirebaseFirestore.getInstance();
    private StorageReference m_StorageReference;
    private DocumentReference m_DocumentReference;
    private static final String TAG = "UserProfile";
}
