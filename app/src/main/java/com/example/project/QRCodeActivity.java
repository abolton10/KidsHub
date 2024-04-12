package com.example.project;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.Query;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class QRCodeActivity extends AppCompatActivity {
    private DatabaseReference parentRef, userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase database reference
        parentRef = FirebaseDatabase.getInstance().getReference().child("Parent");
        userRef = FirebaseDatabase.getInstance().getReference();
        // Call method to initiate QR code scanning
        startQRCodeScanning();
    }

    private ActivityResultLauncher<Intent> scanLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        String scannedData = data.getStringExtra("SCAN_RESULT");
                        // Handle the scanned data
                        handleScannedData(scannedData);
                    }
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    // Handle case where user canceled the scan
                    Toast.makeText(this, "Scan canceled", Toast.LENGTH_SHORT).show();
                }
            });

    private void startQRCodeScanning() {
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
        scanLauncher.launch(intent);
    }

    private void handleScannedData(String scannedData) {
        // Create a query to search for the scanned student ID in the parent database
        Query query = parentRef.orderByChild("SId").equalTo(scannedData);
        // Add a ValueEventListener to listen for the query result
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String parentUid = childSnapshot.getKey();
                    String studentId = childSnapshot.child("SId").getValue(String.class);

                    if (scannedData.equals(studentId)) {
                        Toast.makeText(QRCodeActivity.this, "Student ID found for Parent with UID: " + parentUid, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(QRCodeActivity.this, tParentProfile.class);
                        intent.putExtra("parentID", parentUid);
                        startActivity(intent);
                        return;
                    } //else {
                    // Student ID not found in the database

                    //}
                }
                Toast.makeText(QRCodeActivity.this, "Student ID not found", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the database query cancellation or failure
                Toast.makeText(QRCodeActivity.this, "Database query canceled or failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

}