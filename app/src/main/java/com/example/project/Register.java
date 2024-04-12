package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Register extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword;
    Button ButtonReg;
    FirebaseAuth mAuth;
    TextView textView;

    RadioGroup roleSelect;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        editTextEmail= findViewById(R.id.Email);
        editTextPassword= findViewById(R.id.password);
        ButtonReg = findViewById(R.id.btn_register);
        textView= findViewById(R.id.goLogin);
        roleSelect = findViewById(R.id.role_select);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        ButtonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());
                int selectedRoleId = roleSelect.getCheckedRadioButtonId();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(Register.this, "Enter Email",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    Toast.makeText(Register.this, "Enter password!",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (selectedRoleId == -1) {
                    // No role selected
                    Toast.makeText(Register.this, "Please select a role.", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Registration successful, redirect to appropriate activity based on role
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    redirectToRoleActivity(user);
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(Register.this, "Authentication passed.",
                                            Toast.LENGTH_SHORT).show();

                                } else {
                                    // If sign in fails, display a message to the user.

                                    Toast.makeText(Register.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

            }
        });


       /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/
    }

    private void redirectToRoleActivity(FirebaseUser user) {
        if (user != null) {
            int selectedRoleId = roleSelect.getCheckedRadioButtonId();
            Intent intent;
            if (selectedRoleId == R.id.role_Teacher) {
                // Teacher role selected
                intent = new Intent(getApplicationContext(), teacherDB.class);
            } else {
                // Parent role selected (default if no role selected)
                intent = new Intent(getApplicationContext(), CreateDB.class);
            }
            startActivity(intent);
            finish();
        }
    }
}