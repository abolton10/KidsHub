package com.example.project;
/*
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import java.util.concurrent.atomic.AtomicBoolean;

public class testqueue extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 123;
    private DatabaseReference parentQueueRef, parentRef, studentRef, positionRef; //reference position
    private ImageView imageView; //qrcode
    private LinearLayout pickupConfirmationLayout;
    private TextView queuePositionText;
    Button back, viewqueue;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_queue);

        parentRef = FirebaseDatabase.getInstance().getReference().child("Parent");
        parentQueueRef = FirebaseDatabase.getInstance().getReference().child("queue");
        pickupConfirmationLayout = findViewById(R.id.pickup_confirmation_layout);
        queuePositionText = findViewById(R.id.queue_position_text);
        imageView = findViewById(R.id.qr_code);
        back = findViewById(R.id.back);
        viewqueue = findViewById(R.id.viewqueue);
        FirebaseUser currentUser = mAuth.getCurrentUser();

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
        showPickupConfirmation("1234");
    }

    private void generateQRCode(String currentUser) {
        // Generate QR code for the parent based on the database's SId
        parentRef = FirebaseDatabase.getInstance().getReference().child("Parent");
        parentQueueRef = FirebaseDatabase.getInstance().getReference().child("position");
        studentRef = parentRef.child(currentUser).child("SId");
        //String data = SId; // Combine parent ID and SId
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(data, BarcodeFormat.QR_CODE, 300, 300);

            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            imageView.setImageBitmap(bitmap);

            // Add parent to the queue in the database after generating QR code
            addParentToQueue(SId);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }
    private void addParentToQueue(String SId) {
        // Add parent to the queue in the database
        parentQueueRef.child(SId).setValue(true);

    }
    private void removeParentFromQueue(String SId) {
        // Remove parent from the queue in the database
        parentQueueRef.child(SId).removeValue();
    }
}*/