package com.MAD.healthapp;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private TextView txt_profileName, txt_profileStatus;
    private CircleImageView pro_pic;
    private DatabaseReference databaseReference;
    private DatabaseReference mFriendRequest;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mRootRef;
    private ProgressDialog progressDialog;
    private String mCurrent_state;
    private FirebaseUser mCurrentUser;
    private Button btn_frndRequest, btn_decline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final String user_id = getIntent().getStringExtra("userid");
        mRootRef = FirebaseDatabase.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendRequest = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference("Friends");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        txt_profileName = findViewById(R.id.txt_profileName);
        txt_profileStatus = findViewById(R.id.txt_profileStatus);

        btn_frndRequest = findViewById(R.id.btn_frndRequest);
        btn_decline = findViewById(R.id.btn_decline);
        btn_decline.setVisibility(View.INVISIBLE);
        btn_decline.setEnabled(false);

        pro_pic = findViewById(R.id.pro_pic);


        txt_profileName.setText(user_id);
        mCurrent_state = "not_friends";
        progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setTitle("Loading User Data");
        progressDialog.setMessage("Please wait while we loading the data");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String displayName = dataSnapshot.child("Name").getValue().toString();
                String status = dataSnapshot.child("Status").getValue().toString();
                String image = dataSnapshot.child("Image").getValue().toString();

                txt_profileName.setText(displayName);
                txt_profileStatus.setText(status);
                Picasso.with(ProfileActivity.this).load(image).into(pro_pic);

                //update request button accordingly
                mFriendRequest.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(user_id)) {

                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();
                            if (req_type.equals("received")) {

                                mCurrent_state = "req_received";
                                btn_frndRequest.setText("Accept Friend Request");
                                btn_decline.setVisibility(View.VISIBLE);
                                btn_decline.setEnabled(true);
                            } else if (req_type.equals("sent")) {
                                mCurrent_state = "req_sent";

                                btn_frndRequest.setText("Cancel Friend Request");
                                btn_decline.setVisibility(View.INVISIBLE);
                                btn_decline.setEnabled(false);


                            }
                            progressDialog.dismiss();
                        } else {
                            mFriendDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(user_id)) {
                                        mCurrent_state = "friends";
                                        btn_frndRequest.setText("UnFriend this person");
                                        btn_decline.setVisibility(View.INVISIBLE);
                                        btn_decline.setEnabled(false);

                                    }
                                    progressDialog.dismiss();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    progressDialog.dismiss();
                                }
                            });

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        btn_frndRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_frndRequest.setEnabled(false);
                //------------------------not friends
                if (mCurrent_state.equals("not_friends")) {

                    Map requestMap = new HashMap();
                    requestMap.put("Friend_req/" + mCurrentUser.getUid() + "/" + user_id + "/request_type", "sent");
                    requestMap.put("Friend_req/" + user_id + "/" + mCurrentUser.getUid() + "/request_type", "received");
                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError != null) {

                                Toast.makeText(ProfileActivity.this, "Fail sending request", Toast.LENGTH_LONG).show();
                            }
                            btn_frndRequest.setEnabled(true);
                            mCurrent_state = "req_sent";
                            btn_frndRequest.setText("Cancel Friend Request");

                            /*   mCurrent_state = "req_sent";
                            btn_frndRequest.setText("Cancel Friend Request");
                            btn_decline.setVisibility(View.INVISIBLE);
                            btn_decline.setEnabled(false);*/
                        }
                    });


                }
//------------------------cancel rq
                if (mCurrent_state.equals("req_sent")) {
                    mFriendRequest.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendRequest.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    btn_frndRequest.setEnabled(true);
                                    mCurrent_state = "not_friends";
                                    btn_frndRequest.setText("Send Friend Request");
                                    btn_decline.setVisibility(View.INVISIBLE);
                                    btn_decline.setEnabled(false);
                                }
                            });
                        }
                    });

                }

//--------------------received state
                if (mCurrent_state.equals("req_received")) {
                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                    Map friendMap = new HashMap();
                    friendMap.put("Friends/" + mCurrentUser.getUid() + "/" + user_id + "/date", currentDate);
                    friendMap.put("Friends/" + user_id + "/" + mCurrentUser.getUid() + "/date", currentDate);
                    friendMap.put("Friend_req/" + mCurrentUser.getUid() + "/" + user_id, null);
                    friendMap.put("Friend_req/" + user_id + "/" + mCurrentUser.getUid(), null);

                    mRootRef.updateChildren(friendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                btn_frndRequest.setEnabled(true);
                                mCurrent_state = "friends";
                                btn_frndRequest.setText("Unfriend this person");
                                btn_decline.setVisibility(View.INVISIBLE);
                                btn_decline.setEnabled(false);

                            } else {

                                Toast.makeText(ProfileActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();


                            }
                        }
                    });
                }

                //------------------unfriend

                if (mCurrent_state.equals("friends")) {
                    Map unFriendMap = new HashMap();
                    unFriendMap.put("Friends/" + mCurrentUser.getUid() + "/" + user_id, null);
                    unFriendMap.put("Friends/" + user_id + "/" + mCurrentUser.getUid(), null);
                    mRootRef.updateChildren(unFriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                mCurrent_state = "not_friends";
                                btn_frndRequest.setText("Send friend request");
                                btn_decline.setVisibility(View.INVISIBLE);
                                btn_decline.setEnabled(false);

                            } else {

                                Toast.makeText(ProfileActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();


                            }

                            btn_frndRequest.setEnabled(true);
                        }
                    });


                }


            }
        });

//-----------------------------decline request
        btn_decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrent_state.equals("req_received")) {

                    Map declineFriendMap = new HashMap();
                    declineFriendMap.put("Friend_req/" + mCurrentUser.getUid() + "/" + user_id, null);
                    declineFriendMap.put("Friend_req/" + user_id + "/" + mCurrentUser.getUid(), null);
                    mRootRef.updateChildren(declineFriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                mCurrent_state = "not_friends";
                                btn_frndRequest.setText("Send friend request");
                                btn_decline.setVisibility(View.INVISIBLE);
                                btn_decline.setEnabled(false);

                            } else {

                                Toast.makeText(ProfileActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();


                            }

                            btn_frndRequest.setEnabled(true);
                        }
                    });


                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();


    }
}
