package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.checkerframework.common.aliasing.qual.Unique;

import java.util.Objects;

public class teacherDB extends AppCompatActivity {

    TextInputEditText editName, editEmail, editSId;
    Button btn, button;
    private DatabaseReference teacherRef, nameRef, emailRef, SIdRef;
    private double userIdCounter = 0001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teacher_db);

        btn = findViewById(R.id.DBButton);
        button = findViewById(R.id.back);
        editName=findViewById(R.id.Name);
        editEmail=findViewById(R.id.DBEmail);
        editSId=findViewById(R.id.Std_id);

        teacherRef = FirebaseDatabase.getInstance().getReference().child("Teacher");
        nameRef = FirebaseDatabase.getInstance().getReference().child("Teacher").child("name");
        emailRef = FirebaseDatabase.getInstance().getReference().child("Teacher").child("Email");
        SIdRef = FirebaseDatabase.getInstance().getReference().child("Teacher").child("Email");


     btn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            String name= Objects.requireNonNull(editName.getText()).toString();
            String Email= Objects.requireNonNull(editEmail.getText()).toString();
            Double SId= userIdCounter++;
            String parentID = teacherRef.push().getKey();



            DatabaseReference newParentRef = teacherRef.child(parentID);
            newParentRef.child("name").setValue(name);
            newParentRef.child("email").setValue(Email);
            newParentRef.child("SId").setValue(SId);
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