package com.example.jennifer.contracker;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment implements View.OnClickListener{


    private ImageView friendsFragmentProfieImage;
    private Button friendsFragmentProfile;
    private Button friendsFragmentComments;
    private TextView friendsFragmentUsername;
    private Button friendsFragmentCurrentServiceBtn;
    private Button friendsFragmentCurrentBiddingBtn;
    private Button friendsFragmentViewHistoryBtn;
    private View friendsMainView;

    private FirebaseAuth mAuth;
    private DatabaseReference imageDatabaseRef;
    String currentUserID;

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        friendsMainView = inflater.inflate(R.layout.fragment_friends, container, false);

        friendsFragmentProfieImage = (ImageView)friendsMainView.findViewById(R.id.friends_fragment_profile_image);
        friendsFragmentUsername =(TextView)friendsMainView.findViewById(R.id.friends_fragment_username);
        friendsFragmentCurrentServiceBtn = (Button)friendsMainView.findViewById(R.id.friends_fragment_current_service);
        friendsFragmentCurrentBiddingBtn =(Button)friendsMainView.findViewById(R.id.friends_fragment_current_bidding);
        friendsFragmentViewHistoryBtn =(Button)friendsMainView.findViewById(R.id.friends_fragment_view_history);
        friendsFragmentProfile = (Button)friendsMainView.findViewById(R.id.friends_fragment_user_profile);
        friendsFragmentComments =(Button)friendsMainView.findViewById(R.id.friends_fragment_comments);

        //
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        imageDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);


        getUserImage();
        friendsFragmentViewHistoryBtn.setOnClickListener(this);
        friendsFragmentComments.setOnClickListener(this);
        friendsFragmentCurrentBiddingBtn.setOnClickListener(this);
        friendsFragmentCurrentServiceBtn.setOnClickListener(this);
        friendsFragmentProfile.setOnClickListener(this);


        return friendsMainView;
    }

    private void getUserImage()
    {
        imageDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild("user_image")){
                    String image = dataSnapshot.child("user_image").getValue().toString();
                    Picasso.with(getContext()).load(image).placeholder(R.drawable.user_default).into(friendsFragmentProfieImage);
                }
                if(dataSnapshot.hasChild("username")){
                    String name = dataSnapshot.child("username").getValue().toString();
                    friendsFragmentUsername.setText(name);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.friends_fragment_user_profile:
                sendUserToSettingActivity();
                break;

            case R.id.friends_fragment_current_service:
                sendUserToCurrentServiceActivity();
                break;

            case R.id.friends_fragment_current_bidding:
                sendUserToCurrentBiddingActivity();
                break;

            case R.id.friends_fragment_view_history:
                sendUserToHistoryActivity();
                break;

            case R.id.friends_fragment_comments:
                sendUserToCommentsActivity();
                break;
        }
    }

    private void sendUserToCommentsActivity()
    {

        Intent commentIntent = new Intent(getContext(),CommentActivity.class);
        startActivity(commentIntent);
    }

    private void sendUserToHistoryActivity()
    {
        Intent historyIntent = new Intent(getContext(),HistoryActivity.class);
        startActivity(historyIntent);

    }

    private void sendUserToCurrentBiddingActivity()
    {
        Intent bidIntent = new Intent(getContext(),CurrentBidActivity.class);
        startActivity(bidIntent);

    }

    private void sendUserToCurrentServiceActivity()
    {
        Intent serviceIntent = new Intent(getContext(),CurrentServiceActivity.class);
        startActivity(serviceIntent);

    }

    private void sendUserToSettingActivity()
    {

        Intent settingIntent = new Intent(getContext(),SettingActivity.class);
        startActivity(settingIntent);

    }
}
