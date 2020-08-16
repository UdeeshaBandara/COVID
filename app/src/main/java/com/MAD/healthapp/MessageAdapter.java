package com.MAD.healthapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> mMessageList;
    Context mContext;

    public MessageAdapter(Context context, List<Messages> mMessageList) {
        this.mMessageList = mMessageList;
        mContext = context;

    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout, parent, false);
        return new MessageViewHolder(v);
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messageText, messageTime;
        public CircleImageView profileImage;

        public MessageViewHolder(View view) {
            super(view);
            messageText = view.findViewById(R.id.message_text_layout);
            messageTime = view.findViewById(R.id.time_text_layout);
            profileImage = view.findViewById(R.id.message_profile_layout);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {

        Messages c = mMessageList.get(position);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_user_id = currentUser.getUid();

        String from_user = c.getFrom();

        if (from_user.equals(current_user_id)) {
            holder.messageText.setBackgroundColor(Color.WHITE);
            holder.messageText.setTextColor(Color.BLACK);

        } else {
            holder.messageText.setBackgroundResource(R.drawable.message_text_background);
            holder.messageText.setTextColor(Color.WHITE);

        }

        holder.messageText.setText(c.getMessage());
        String timeAgo = GetTimeAgo.getTimeAgo(c.getTime(), mContext);
        holder.messageTime.setText(timeAgo);

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }
}
