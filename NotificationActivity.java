package com.example.myapplication;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NotificationActivity extends AppCompatActivity {


    TextView textView;
    Button yesButton, noButton;
    DatabaseReference parentRef;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        parentRef = FirebaseDatabase.getInstance().getReference().child("Parent");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification2);
        String parentID = "";                       //don't know work or not
        textView = findViewById(R.id.textViewData);
        yesButton = findViewById(R.id.Approve); // Replace with your actual 'Yes' button ID
        noButton = findViewById(R.id.Denied); // Replace with your actual 'No' button ID

        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        textView.setText(currentTime);

        yesButton.setOnClickListener(v -> {
            Toast.makeText(NotificationActivity.this, "Approved: You have handled the student", Toast.LENGTH_LONG).show();            finish();
        });

        noButton.setOnClickListener(v -> {
            // Retrieve parent info and show dialog within the ValueEventListener
            DatabaseReference parentInfoRef = parentRef.child("parentID"); // Replace "parentID" with the actual ID
            parentInfoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String name = dataSnapshot.child("name").getValue(String.class);
                        String email = dataSnapshot.child("email").getValue(String.class);

                        // Show confirmation dialog using the retrieved name and email
                        showConfirmationDialog(name, email);
                    } else {
                        Toast.makeText(NotificationActivity.this, "Error: Parent data not found.", Toast.LENGTH_SHORT).show();
                    finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(NotificationActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void showConfirmationDialog(String name, String email) {
        new AlertDialog.Builder(this)
                .setTitle("Denial Confirmation")
                .setMessage("Denied: Unexpected situation occur, please contact " + name + "\nEmail: " + email)
                .setPositiveButton("Confirmed", (dialog, which) -> {
                    // Close the AlertDialog and Activity
                    finish();
                })
                .create().show();
    }
}