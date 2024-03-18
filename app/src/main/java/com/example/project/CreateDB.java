package com.example.project;

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

public class CreateDB extends AppCompatActivity {

    TextInputEditText editName, editEmail, editSId;
    Button btn;
    private DatabaseReference parentRef, nameRef, emailRef, SIdRef;
    private double userIdCounter = 0001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_db);

        parentRef = FirebaseDatabase.getInstance().getReference().child("Parents").child("key");
        nameRef = FirebaseDatabase.getInstance().getReference().child("Parents").child("name");
        emailRef = FirebaseDatabase.getInstance().getReference().child("Parents").child("Email");
        SIdRef = FirebaseDatabase.getInstance().getReference().child("Parents").child("Email");

        btn = findViewById(R.id.DBButton);
        editName=findViewById(R.id.Name);
        editEmail=findViewById(R.id.DBEmail);
        editSId=findViewById(R.id.Std_id);





        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String name= Objects.requireNonNull(editName.getText()).toString();
                String Email= Objects.requireNonNull(editEmail.getText()).toString();
                Double SId= userIdCounter++;
                String parentID = parentRef.push().getKey();



                DatabaseReference newParentRef = parentRef.child(parentID);
                newParentRef.child("name").setValue(name);
                newParentRef.child("email").setValue(Email);
                newParentRef.child("SId").setValue(SId);
                //emailRef.setValue(Email);
                //nameRef.setValue(name);
                //SIdRef.setValue(SId);

            }
        });






    }




}