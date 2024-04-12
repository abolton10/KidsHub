package com.example.project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.common.aliasing.qual.Unique;

import java.util.Objects;

public class CreateDB extends AppCompatActivity {

    TextInputEditText editFName,editLName, editPhoneNo, editEmail, editSId;
    Button btn, button;
    ImageButton imageButton;
    ImageView imageView;
    private DatabaseReference parentRef, nameRef, emailRef, SIdRef;
    private double userIdCounter = 0001;
    private FirebaseAuth mAuth;
    private Uri imageUri;
    private StorageReference imageStorageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_db);

        parentRef = FirebaseDatabase.getInstance().getReference().child("Parent");
        imageStorageRef = FirebaseStorage.getInstance().getReference().child("images");
        mAuth = FirebaseAuth.getInstance();
        nameRef = FirebaseDatabase.getInstance().getReference().child("Parent").child("name");
        emailRef = FirebaseDatabase.getInstance().getReference().child("Parent").child("Email");
        SIdRef = FirebaseDatabase.getInstance().getReference().child("Parent").child("Email");

        btn = findViewById(R.id.DBButton);
        button = findViewById(R.id.back);
        imageButton = findViewById(R.id.imageButton);

        editFName=findViewById(R.id.FName);
        editLName=findViewById(R.id.LName);
        editPhoneNo=findViewById(R.id.PhoneNo);
        editEmail=findViewById(R.id.DBEmail);
        editSId=findViewById(R.id.Std_id);



        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });



        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String Fname= Objects.requireNonNull(editFName.getText()).toString();
                String Lname= Objects.requireNonNull(editLName.getText()).toString();
                String PhoneNo= Objects.requireNonNull(editPhoneNo.getText()).toString();
                String Email= Objects.requireNonNull(editEmail.getText()).toString();
                String SId= Objects.requireNonNull(editSId.getText()).toString();
                //double x = 0;
                String Image = String.valueOf(imageUri);

                String parentID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();



                DatabaseReference newParentRef = parentRef.child(parentID);
                newParentRef.child("First name").setValue(Fname);
                newParentRef.child("Last name").setValue(Lname);
                newParentRef.child("Phone No").setValue(PhoneNo);
                newParentRef.child("email").setValue(Email);
                newParentRef.child("SId").setValue(SId);
                newParentRef.child("Position").setValue(0);


                // Save the image if available
                if (imageUri != null) {
                    saveImage(parentID);
                }

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        resultLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                    }
                }
            }
    );

    private void saveImage(String parentID) {
        StorageReference filePath = imageStorageRef.child(parentID + ".jpg");

        filePath.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            // Image uploaded successfully
            Toast.makeText(CreateDB.this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show();

            // Get the download URL of the uploaded image
            filePath.getDownloadUrl().addOnSuccessListener(uri -> {
                // Save the download URL to the Realtime Database
                String imageUrl = uri.toString();
                DatabaseReference newParentRef = parentRef.child(parentID);
                newParentRef.child("imageURL").setValue(imageUrl);
            });

        }).addOnFailureListener(e -> {
            // Error occurred while uploading image
            Toast.makeText(CreateDB.this, "Failed to upload image!", Toast.LENGTH_SHORT).show();
        });
    }





}