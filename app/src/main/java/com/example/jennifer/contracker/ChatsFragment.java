package com.example.jennifer.contracker;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {


    // Ui component declaration
    private View mMainView;
    private RecyclerView chatsList;

    // Firebase methods declaration
    private FirebaseAuth mAuth;
    private DatabaseReference messagesReference;
    private DatabaseReference usersReference;

    String currentUserID;

    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_chats, container, false);

        // firebase initialization
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        messagesReference = FirebaseDatabase.getInstance().getReference().child("Messages").child(currentUserID);

        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");

        // UI component initialization
        chatsList = (RecyclerView) mMainView.findViewById(R.id.chats_list);

        chatsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        chatsList.setLayoutManager(linearLayoutManager);
        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        //firebase recycler adpeter declaration,bind data from the Firebase Realtime Database to app's UI.
        FirebaseRecyclerAdapter<Chats,ChatsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Chats, ChatsViewHolder>
                (
                        Chats.class,
                        R.layout.all_users_display_layout,
                        ChatsViewHolder.class,
                        messagesReference
                )
        {
            // bind chats object to the viewholder
            @Override
            protected void populateViewHolder(final ChatsViewHolder viewHolder, Chats model, int position) {


                final String listUserID = getRef(position).getKey();
                usersReference.child(listUserID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        final String username = dataSnapshot.child("username").getValue().toString();
                        String exp = dataSnapshot.child("expertise").getValue().toString();
                        if(dataSnapshot.hasChild("user_image")){

                            String thumbImage = dataSnapshot.child("user_image").getValue().toString();
                            viewHolder.setThumbImage(thumbImage, getContext());

                        }
                        viewHolder.setUserName(username);
                        viewHolder.setExpertise(exp);

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                Intent chatIntent = new Intent(getContext(),ChatActivity.class);
                                chatIntent.putExtra("visitUserID",listUserID);
                                //chatIntent.putExtra("userName",username);
                                startActivity(chatIntent);


                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        chatsList.setAdapter(firebaseRecyclerAdapter);


    }

    // view holder displaying each item
    public static class ChatsViewHolder extends RecyclerView.ViewHolder
    {

        View mView;

        public ChatsViewHolder(View itemView) {

            super(itemView);
            mView = itemView;
        }

        // set user name 
        public void setUserName(String userName){
            TextView userNameDisplay = (TextView) mView.findViewById(R.id.all_users_user_name);
            userNameDisplay.setText(userName);
        }
        // set user image
        public void setThumbImage(final String thumbImage, final Context ctx) {

            final CircleImageView thumb_Image= (CircleImageView) mView.findViewById(R.id.all_users_profile_image);
            Picasso.with(ctx).load(thumbImage).placeholder(R.drawable.user_default)
                    .into(thumb_Image);
        }

        //set user expertise
        public void setExpertise(String expertise) {
            TextView exp = (TextView)mView.findViewById(R.id.all_users_expertise);
            exp.setText("Role: " + expertise);
        }
    }

}
