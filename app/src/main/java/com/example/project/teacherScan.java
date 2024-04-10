package com.example.project;

import android.os.Bundle;
import android.content.Intent;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class teacherScan extends CaptureAct {

    private DatabaseReference parentRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_scan);

        // Initialize Firebase reference
        parentRef = FirebaseDatabase.getInstance().getReference().child("Parent");

        // Retrieve the scanned ID from the Intent extras
        Intent intent = getIntent();
        if (intent != null) {
            String scannedId = intent.getStringExtra("scannedData");
            if (scannedId != null) {
                // Use the scanned ID to query the Firebase Realtime Database
                handleScannedData(scannedId);
            }
        }
    }

    // Method to query the Firebase Realtime Database using the scanned ID
    private void queryDatabase(String scannedId) {
        parentRef.child(scannedId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve the relevant data based on the scanned ID
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    // Display the retrieved data in TextViews
                    TextView nameTextView = findViewById(R.id.nameTextView);
                    TextView emailTextView = findViewById(R.id.emailTextView);
                    nameTextView.setText("Name: " + name);
                    emailTextView.setText("Email: " + email);
                } else {
                    // Handle the case where data for the scanned ID doesn't exist
                    Toast.makeText(teacherScan.this, "No data found for scanned ID", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur during the database query
                Toast.makeText(teacherScan.this, "Database query canceled: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}