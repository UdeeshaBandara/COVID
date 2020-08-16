package com.MAD.healthapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class FriendsFragment extends Fragment {

    private RecyclerView mFriendsList;
    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mUserRef;
    private FirebaseAuth mAuth;
    private String mCurrent_user_id;
    private View mMainView;


    public FriendsFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView=inflater.inflate(R.layout.fragment_friends,container,false);
        mFriendsList=mMainView.findViewById(R.id.friend_list);
        mAuth=FirebaseAuth.getInstance();
        mCurrent_user_id=mAuth.getCurrentUser().getUid();
        mFriendsDatabase= FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        mFriendsDatabase.keepSynced(true);
        mUsersDatabase=FirebaseDatabase.getInstance().getReference().child("Users");
        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));
        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        mUserRef.child("online").setValue("true");
        FirebaseRecyclerAdapter<Friends,FriendsViewHolder> friendsRecyclerViewAdapter=new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(
                Friends.class,
                R.layout.users_single_layout,
                FriendsViewHolder.class,
                mFriendsDatabase

        ) {
            @Override
            protected void populateViewHolder(FriendsViewHolder friendsViewHolder, Friends friends, int i) {
                friendsViewHolder.setDate(friends.getDate());
                String list_user_id=getRef(i).getKey();
                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String userName=dataSnapshot.child("Name").getValue().toString();
                        String userImage=dataSnapshot.child("Image").getValue().toString();
                        if(dataSnapshot.hasChild("online"))
                        {
                            String userOnline=dataSnapshot.child("online").getValue().toString();
                            friendsViewHolder.setUserOnline(userOnline);

                        }

                        friendsViewHolder.setName(userName);

                       friendsViewHolder.setUserImage(userImage,getContext());

                        friendsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[]=new CharSequence[]{"Open Profile","Send Message"};
                                AlertDialog.Builder al=new AlertDialog.Builder(getContext());
                                al.setTitle("Select Options");
                                al.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                            if(which==0)
                            {
                                Intent profile=new Intent(getContext(),ProfileActivity.class);
                                profile.putExtra("userid",list_user_id);

                                startActivity(profile);

                            }
                            else if(which==1)
                            {
                                Intent chat=new Intent(getContext(),ChatActivity.class);
                                chat.putExtra("userid",list_user_id);
                                chat.putExtra("user_name",userName);
                                startActivity(chat);

                            }
                                    }
                                });
                                al.show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        };
        mFriendsList.setAdapter(friendsRecyclerViewAdapter);
    }
    public static class FriendsViewHolder extends RecyclerView.ViewHolder

    {

        View mView;
        public FriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }
        public void setDate(String date)
        {

            TextView userStatusView=mView.findViewById(R.id.single_status);
            userStatusView.setText(date);
        }
        public void setName(String name)
        {

                TextView userNameView=mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }
        public void setUserImage(String image, Context context)
        {
            CircleImageView userimage=mView.findViewById(R.id.user_single_image);
            Picasso.with(context).load(image).placeholder(R.drawable.ic_account_circle_black_24dp).into(userimage);

        }
       public void setUserOnline(String online_status)
       {
           ImageView userOnline=mView.findViewById(R.id.img_online);
           if(online_status.equals("true"))
           {
               userOnline.setVisibility(View.VISIBLE);
           }
           else
           {
               userOnline.setVisibility(View.INVISIBLE);
           }


       }
    }
}
