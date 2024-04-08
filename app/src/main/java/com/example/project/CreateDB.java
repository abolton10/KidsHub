package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.checkerframework.common.aliasing.qual.Unique;

import java.util.Objects;

public class CreateDB extends AppCompatActivity {

    TextInputEditText editName, editEmail, editSId;
    Button btn, button;
    ImageButton imageButton;
    ImageView imageView;
    private DatabaseReference parentRef, nameRef, emailRef, SIdRef;
    private double userIdCounter = 0001;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_db);

        parentRef = FirebaseDatabase.getInstance().getReference().child("Parent");
        mAuth = FirebaseAuth.getInstance();
        nameRef = FirebaseDatabase.getInstance().getReference().child("Parent").child("name");
        emailRef = FirebaseDatabase.getInstance().getReference().child("Parent").child("Email");
        SIdRef = FirebaseDatabase.getInstance().getReference().child("Parent").child("Email");

        btn = findViewById(R.id.DBButton);
        button = findViewById(R.id.back);
        editName=findViewById(R.id.Name);
        editEmail=findViewById(R.id.DBEmail);
        editSId=findViewById(R.id.Std_id);
        imageView = findViewById(R.id.imageView);
        imageButton = findViewById(R.id.imageButton);





        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String name= Objects.requireNonNull(editName.getText()).toString();
                String Email= Objects.requireNonNull(editEmail.getText()).toString();
                Double SId= userIdCounter++;

                String parentID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();



                DatabaseReference newParentRef = parentRef.child(parentID);
                newParentRef.child("name").setValue(name);
                //DatabaseReference newNameRef = newParentRef.child(name);
                //newNameRef.child("email").setValue(Email);
                //newNameRef.child("SId").setValue(SId);

                newParentRef.child("email").setValue(Email);
                //newParentRef.child("SId").setValue(SId);
                //emailRef.setValue(Email);
                //nameRef.setValue(name);
                //SIdRef.setValue(SId);

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




}