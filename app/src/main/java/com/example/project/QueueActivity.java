package com.example.project;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QueueActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 123;
    private DatabaseReference parentQueueRef, parentRef, studentRef, positionRef; //reference position
    private ImageView imageView; //qrcode
    private LinearLayout pickupConfirmationLayout;
    private TextView queuePositionText;
    private Button back, viewqueue;
    private FirebaseAuth mAuth;
    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_queue);

        parentRef = FirebaseDatabase.getInstance().getReference().child("Parent");
        parentQueueRef = FirebaseDatabase.getInstance().getReference().child("queue");
        pickupConfirmationLayout = findViewById(R.id.pickup_confirmation_layout);
        imageView = findViewById(R.id.qr_code);
        back = findViewById(R.id.back);
        viewqueue = findViewById(R.id.viewqueue);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        viewqueue.setOnClickListener(v -> {
            Intent intent = new Intent(QueueActivity.this, seeQueue.class);
            startActivity(intent);
        });

        back.setOnClickListener(v -> {
            Intent intent = new Intent(QueueActivity.this, parentMain.class);
            startActivity(intent);
        });

        requestLocationPermissions();
    }

    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, perform location-related tasks
                checkProximityToSchool();
            } else {
                // Permission denied, handle accordingly
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkProximityToSchool() {
        // Your existing code for checking proximity to school goes here
        double schoolLatitude = 33.75;
        double schoolLongitude = -84.39; // GSU location
        float radius = 3200; // within half a mile of the school

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location userLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (userLocation != null) {
                double userLatitude = userLocation.getLatitude();
                double userLongitude = userLocation.getLongitude();

                float[] results = new float[1];
                Location.distanceBetween(userLatitude, userLongitude, schoolLatitude, schoolLongitude, results);
                float distanceInMeters = results[0];

                if (distanceInMeters <= radius) {
                    // If near school, generate QR code
                    generateQRCode();
                } else {
                    // Display message to user indicating they are not near school
                    Toast.makeText(this, "You are not in proximity to the school. Check in once you are closer!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        generateQRCode();
    }

    private void generateQRCode() {
        // Generate QR code for the parent based on the database's SId
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            generateQRCodeWithQueue(userId);
        } else {
            // Handle if the user is not authenticated
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    private void generateQRCodeWithQueue(String userId) {
        // Increment the queue in the database
        parentQueueRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int currentQueue = dataSnapshot.getValue(Integer.class);
                    int newQueue = currentQueue + 1;

                    // Update the queue in the database
                    parentQueueRef.setValue(newQueue);

                    // Update the position of the current user in the database
                    parentRef.child(userId).child("position").setValue(newQueue);

                    // Generate QR code using the new queue number
                    generateQRCodeWithQueueNumber(String.valueOf(newQueue));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void generateQRCodeWithQueueNumber(String queueNumber) {
        String data = queueNumber; // Use queue number as data for QR code
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(data, BarcodeFormat.QR_CODE, 300, 300);

            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }

    private void confirmPickupDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Pickup");
        builder.setMessage("Was your child picked up safely?");

        // Add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked Yes button
                leaveQueueAndUpdatePosition(true);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked No button
                leaveQueueAndUpdatePosition(false);
            }
        });

        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void leaveQueueAndUpdatePosition(boolean pickedUpSafely) {
        // Remove the parent from the queue
        parentRef.child(currentUser).child("position").removeValue();

        // If the child was picked up safely, decrement the queue
        if (pickedUpSafely) {
            queueDecrement();
        }
    }

    private void queueDecrement() {
        DatabaseReference parentRef = FirebaseDatabase.getInstance().getReference().child("Parent");

        parentQueueRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int currentQueue = dataSnapshot.getValue(Integer.class);
                    int newQueue = currentQueue - 1;
                    if (newQueue < 0) {
                        newQueue = 0; // Ensure queue doesn't go negative
                    }
                    parentQueueRef.setValue(newQueue);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }
}