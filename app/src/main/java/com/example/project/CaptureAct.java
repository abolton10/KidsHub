package com.example.project;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;
public class CaptureAct extends CaptureActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startBarcodeScanning();
        // Set up barcode scanning configurations if needed
        // ...
    }

    private void startBarcodeScanning() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        // Check if the result comes from barcode scanning activity
        if (requestCode == IntentIntegrator.REQUEST_CODE && resultCode == RESULT_OK) {
            // Get the scanned data
            String scannedData = data.getStringExtra("scannedData");
            // Handle the scanned data
            handleScannedData(scannedData);
        }
        Intent intent = new Intent(getApplicationContext(), teacherScan.class);
        startActivity(intent);
        finish();
    }

    // Method to handle barcode scanning result
    void handleScannedData(String scannedData) {
        Intent scanresult = new Intent();
        scanresult.putExtra("scannedData", scannedData);
        setResult(RESULT_OK, scanresult);

        finish();
    }

    // Add any additional methods or configurations for barcode scanning
    // ...
}
