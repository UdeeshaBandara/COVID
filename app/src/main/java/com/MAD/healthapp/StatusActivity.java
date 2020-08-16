package com.MAD.healthapp;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private EditText txt_newStatus;
    private Button btn_updateStatus;
    private DatabaseReference statusRef;
    private FirebaseUser firebaseUser;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();


        txt_newStatus=findViewById(R.id.txt_newStatus);
        txt_newStatus.setText(getIntent().getStringExtra("oldStat"));
        btn_updateStatus=findViewById(R.id.btn_updateStatus);
        statusRef= FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());




        btn_updateStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog=new ProgressDialog(StatusActivity.this);
                progressDialog.setTitle("Saving Changes");
                progressDialog.setMessage("Please wait Updating Status");
                progressDialog.show();
                statusRef.child("Status").setValue(txt_newStatus.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            progressDialog.dismiss();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"There is a error!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }
}
