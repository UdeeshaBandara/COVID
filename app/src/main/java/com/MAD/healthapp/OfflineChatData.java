package com.MAD.healthapp;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

public class OfflineChatData extends Application {
private DatabaseReference mUserDatabase;
private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseApp.initializeApp(this);
        mAuth=FirebaseAuth.getInstance();;
        currentUser= mAuth.getCurrentUser();

        if (currentUser != null) {

              mUserDatabase=FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
       mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              if(dataSnapshot!=null)  { mUserDatabase.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);


              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        }
}}
