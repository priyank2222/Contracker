package com.example.jennifer.contracker;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by kezhao on 4/17/18.
 */

public class MessageAdapter extends RecyclerView.Adapter
{
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private Context ctx;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    private List<Messages> userMessagesList;

    public MessageAdapter(Context ctx, List<Messages> userMessagesList) {
        this.ctx = ctx;
        this.userMessagesList = userMessagesList;
    }

    //Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position)
    {
        mAuth = FirebaseAuth.getInstance();
        String sendUserId = mAuth.getCurrentUser().getUid();
        Messages messages = (Messages) userMessagesList.get(position);
        if(messages.getFrom().equals(sendUserId)){
            return VIEW_TYPE_MESSAGE_SENT;
        }
        else
        {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }

    }
    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view;
        if(viewType == VIEW_TYPE_MESSAGE_SENT){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent,parent,false);
            return new SenderMessageHolder(view);
        }
        else if(viewType == VIEW_TYPE_MESSAGE_RECEIVED)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received,parent,false);
            return new ReceivedMessageHolder(view);
        }

        return null;

    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        Messages message = (Messages)userMessagesList.get(position);

        switch (holder.getItemViewType())
        {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SenderMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
                break;

        }

    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }


    private class ReceivedMessageHolder extends RecyclerView.ViewHolder
    {

        TextView messageTxt, timeTxt,nameTxt;
        CircleImageView profileImage;

        //constructor of message text and time text, name text and profile image
        public ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageTxt = (TextView) itemView.findViewById(R.id.text_message_body);
            timeTxt = (TextView) itemView.findViewById(R.id.text_message_time);
            nameTxt = (TextView) itemView.findViewById(R.id.text_message_name);
            profileImage = (CircleImageView) itemView.findViewById(R.id.image_message_profile);
        }

        //bind data to specific xml ui components
        void bind(Messages message)
        {
            userRef = FirebaseDatabase.getInstance().getReference().child("Users");

            messageTxt.setText(message.getMessage());

            // convert firebase timestamp to real date format
            DateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm:ss", Locale.US);
            Date myDate = new Date(message.getTime());
            final String date = df.format(myDate);
            timeTxt.setText(date);

            String fromUserID = message.getFrom();
            userRef.child(fromUserID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String name = dataSnapshot.child("username").getValue().toString();

                    if(dataSnapshot.hasChild("user_image")){

                        String image = dataSnapshot.child("user_image").getValue().toString();
                        Picasso.with(ctx).load(image).placeholder(R.drawable.user_default).into(profileImage);

                    }

                    nameTxt.setText(name);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }
    //sender message view
    private class SenderMessageHolder extends RecyclerView.ViewHolder
    {

        TextView messageTxt, timeTxt;


        //constructor of message text and time text
        public SenderMessageHolder(View itemView) {
            super(itemView);

            messageTxt = (TextView) itemView.findViewById(R.id.text_message_body);
            timeTxt = (TextView) itemView.findViewById(R.id.text_message_time);


        }

        //bind data to specific xml ui components
        void bind(Messages message)
        {
            messageTxt.setText(message.getMessage());

            // convert firebase timestamp to real date format
            DateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
            df.setTimeZone(TimeZone.getTimeZone("EDT"));
            Date myDate = new Date(message.getTime());
            String date = df.format(myDate);
            timeTxt.setText(date);
        }
    }
}


