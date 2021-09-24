package com.example.personalfinance;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
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

        FirebaseUser user = m_Auth.getCurrentUser();
        uId = user.getUid();

        DocumentReference documentReference = firestore.collection("users").document(uId);

        databaseReference = database.getReference("users");

        m_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadData();
            }
        });

//        m_Image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivity(intent);
//            }
//        });

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private String getFileExt(Uri a_Uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(a_Uri));
    }

    private void UploadData(){
        String name = m_Auth.getCurrentUser().getDisplayName();
        String firstName = m_FirstName.getText().toString();
        String lastName = m_LastName.getText().toString();
        String email = m_Email.getText().toString();

        if(!TextUtils.isEmpty(name) || !TextUtils.isEmpty(firstName) ||!TextUtils.isEmpty(lastName)||!TextUtils.isEmpty(email) ) {
            Map<String, String> profile = new HashMap<>();
            profile.put("name", name);
            profile.put("email", email);
            profile.put("privacy", "Public");

            m_NewUser.SetFullName(name);
            m_NewUser.SetEmail(email);
            m_NewUser.SetUid(uId);

            databaseReference.child(uId).setValue(m_NewUser);
            m_DocumentReference.set(profile).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                                Toast.makeText(getApplicationContext(), "Profile Saved!", Toast.LENGTH_SHORT).show();
                                finish();
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

    private String uId;
    private Uri m_ImageUri;
    private  ImageView m_Image;
    private EditText m_FirstName;
    private EditText m_LastName;
    private EditText m_Email;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private DocumentReference m_DocumentReference;
}
