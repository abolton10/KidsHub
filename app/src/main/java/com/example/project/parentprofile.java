package com.example.project;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.bumptech.glide.Glide;

import java.util.Objects;

//import kotlinx.coroutines.scheduling.Task;

public class parentprofile extends AppCompatActivity {
    ImageView profileImageView;
    TextView FnameTextView, LnameTextView, PhoneTextView, emailTextView;
    DatabaseReference parentRef;
    FirebaseAuth mAuth;

    Button b, back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parentprofile);

        b = findViewById(R.id.EditProfile);
        back = findViewById(R.id.back);
        profileImageView = findViewById(R.id.profile_image_view);
        FnameTextView = findViewById(R.id.fname);
        LnameTextView = findViewById(R.id.lname);
        PhoneTextView = findViewById(R.id.PhoneNo);
        emailTextView = findViewById(R.id.Email);
        parentRef = FirebaseDatabase.getInstance().getReference().child("Parent");
        mAuth = FirebaseAuth.getInstance();

        String parentID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        DatabaseReference parentInfoRef = parentRef.child(parentID);

        parentInfoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String fname = snapshot.child("First name").getValue(String.class);
                    String lname = snapshot.child("Last name").getValue(String.class);
                    String phoneNo = snapshot.child("Phone No").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String imageUrl = snapshot.child("imageURL").getValue(String.class);
                    FnameTextView.setText("First Name: " + fname);
                    LnameTextView.setText(" " + lname);
                    PhoneTextView.setText("Phone:" + phoneNo);
                    emailTextView.setText("Email: " + email);

                    // Load profile image using Glide
                    if (imageUrl != null) {
                        Glide.with(parentprofile.this)
                                .load(imageUrl).into(profileImageView);

                    } else {
                        // Handle case where parent data doesn't exist
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateDB.class);
                startActivity(intent);
                finish();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), parentMain.class);
                startActivity(intent);
                finish();
            }
        });
    }


}
