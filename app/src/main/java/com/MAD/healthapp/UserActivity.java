package com.MAD.healthapp;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DatabaseReference mUsersDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        mAuth = FirebaseAuth.getInstance();
        recyclerView=findViewById(R.id.users_list);

        recyclerView.setHasFixedSize(true);
        mUserRef= FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUsersDatabase= FirebaseDatabase.getInstance().getReference().child("Users");


    } @Override
    protected void onStart() {
        super.onStart();
        mUserRef.child("online").setValue(true);
        FirebaseRecyclerAdapter<Users, UserViewHolder>firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Users, UserViewHolder>(Users.class,R.layout.users_single_layout,UserViewHolder.class,mUsersDatabase) {
            @Override
            protected void populateViewHolder(UserViewHolder userViewHolder, Users users, int i) {
            userViewHolder.setDisplayName(users.getName());
            userViewHolder.setUserStatus(users.getStatus());
            userViewHolder.setImage(users.getImage(),getApplicationContext());
            String user_id=getRef(i).getKey();
            userViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent profile=new Intent(UserActivity.this,ProfileActivity.class);
                    profile.putExtra("userid",user_id);
                    startActivity(profile);

                }
            });

            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }


    public static class UserViewHolder extends RecyclerView.ViewHolder
    {
         View mView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
       mView=itemView;
        }
        public void setDisplayName(String name)
        {
            TextView userName=mView.findViewById(R.id.user_single_name);
            userName.setText(name);

        }
        public void setImage(String image, Context c) {
            CircleImageView usersImage=mView.findViewById(R.id.user_single_image);
            Picasso.with(c).load(image).placeholder(R.drawable.ic_account_circle_black_24dp).into(usersImage);

        }

        public void setUserStatus(String status) {
            TextView userstatus=mView.findViewById(R.id.single_status);
            userstatus.setText(status);
        }
    }
}
