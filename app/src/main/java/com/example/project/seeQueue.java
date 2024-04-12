package com.example.project;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class seeQueue extends AppCompatActivity {
    private DatabaseReference parentRef;
    private TextView queuePositionText;
    private FirebaseAuth mAuth;
    private Button back;
    private DatabaseReference parentQueueRef; // Initialize parentQueueRef

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_queue);

        parentRef = FirebaseDatabase.getInstance().getReference().child("Parent");
        queuePositionText = findViewById(R.id.queue_position_text);
        mAuth = FirebaseAuth.getInstance();
        back = findViewById(R.id.back);
        parentQueueRef = FirebaseDatabase.getInstance().getReference().child("queue"); // Initialize parentQueueRef

        Button confirmPickupButton = findViewById(R.id.confirm_pickup_button);
        confirmPickupButton.setOnClickListener(v -> showConfirmationDialog());

        back.setOnClickListener(v -> {
            Intent intent = new Intent(seeQueue.this, QueueActivity.class);
            startActivity(intent);
        });
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Pickup");
        builder.setMessage("Was your child picked up safely?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked Yes button
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    parentRef.child(userId).child("position").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                int position = dataSnapshot.getValue(Integer.class);
                                queueDecrement(position);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle error
                        }
                    });
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked No button
                // Implement any action needed for No
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void queueDecrement(int queue) {
        // Query to find positions greater than or equal to the queue
        Query query = parentRef.orderByChild("position").startAt(queue);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Iterate over the results
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Get the position of the user
                    int position = snapshot.child("position").getValue(Integer.class);

                    // Check if position is greater than or equal to the queue
                    if (position >= queue) {
                        // Decrement both the queue and the position
                        snapshot.getRef().child("position").setValue(position - 1);
                    }
                }

                // Decrement the queue by 1
                parentQueueRef.setValue(queue - 1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            parentRef.child(userId).child("position").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        int position = dataSnapshot.getValue(Integer.class);
                        queuePositionText.setText("Your position in queue: " + position);
                    } else {
                        queuePositionText.setText("You are not in the queue.");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle error
                }
            });
        }
    }
}