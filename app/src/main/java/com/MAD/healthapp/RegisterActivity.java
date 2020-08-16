package com.MAD.healthapp;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText txt_name, txt_email, txt_password,txt_reEnter;
    private Button btn_register;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private DatabaseReference firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        txt_name = findViewById(R.id.txt_name);
        txt_email = findViewById(R.id.txt_email);
        txt_password = findViewById(R.id.txt_password);
        btn_register = findViewById(R.id.btn_register);
        txt_reEnter=findViewById(R.id.txt_reEnter);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(txt_name.getText()) && !TextUtils.isEmpty(txt_email.getText()) && !TextUtils.isEmpty(txt_password.getText())&&!TextUtils.isEmpty(txt_reEnter.getText())) {
                    progressDialog.setTitle("Registration Process");
                    progressDialog.setMessage("Please Wait!");
                    progressDialog.show();

                    register_user(txt_name.getText().toString(), txt_email.getText().toString(), txt_password.getText().toString());
                } else {

                        Toast.makeText(getApplicationContext(),"Enter data correctly",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void register_user(String toString, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser=FirebaseAuth.getInstance().getCurrentUser();
                            String Uid=currentUser.getUid();
                            firebaseDatabase=FirebaseDatabase.getInstance().getReference().child("Users").child(Uid);
                            HashMap<String,String>userMap=new HashMap<>();
                            userMap.put("Name",txt_name.getText().toString());
                            userMap.put("Status","Hi there! I'm using COVID-App");
                            userMap.put("Image","Default");
                            userMap.put("thumb_image","Default");
                            firebaseDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {

                                        progressDialog.dismiss();
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Intent startIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(startIntent);
                                        finish();

                                    }
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }
}
