package com.example.jennifer.contracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
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

public class ProfileActivity extends AppCompatActivity {

      // Ui component declaration
    private Toolbar mToolbar;
    private CircleImageView profileUserImage;
    private TextView profileUserName;
    private RatingBar profileRatingBar;
    private TextView profileexperienceSettingContentTxt;
    private TextView profileexpertiseContentTxt;
    private RatingBar profileexpertiseRatingBar;
    private String profileexpertise;
    private Button btnMessage;

    private RecyclerView profileCommentList;
    private DatabaseReference commentRef;
    private DatabaseReference userRef;

    // Firebase methods declaration
    String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference usersReference;
    //private String currentUserID;
    String recieveUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

         // UI component initialization
        profileUserImage = (CircleImageView) findViewById(R.id.profile_user_image);
        profileUserName = (TextView)findViewById(R.id.profile_user_name);
//        profileRatingBar = (RatingBar)findViewById(R.id.profile_rating_bar);


        profileexperienceSettingContentTxt =(TextView) findViewById(R.id.profile_experience_setting_content_txt);
        profileexpertiseContentTxt = (TextView) findViewById(R.id.profile_expertise_content_txt);
        profileexpertiseRatingBar = (RatingBar) findViewById(R.id.profile_expertise_rating_bar);
        profileexpertise =  profileexpertiseContentTxt.getText().toString();
        btnMessage = (Button) findViewById(R.id.btnMessage);


        if(profileexpertise == "Customer"){
            profileexpertiseRatingBar.setVisibility(View.INVISIBLE);
        }
        mToolbar = (Toolbar)findViewById(R.id.profile_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //mAuth = FirebaseAuth.getInstance();

        //currentUserID = mAuth.getCurrentUser().getUid();

        // firebase initialization
        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        recieveUserID = getIntent().getExtras().getString("visitUserID");
        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chatIntent = new Intent(getApplicationContext(), ChatActivity.class);
                chatIntent.putExtra("visitUserID", recieveUserID);
                startActivity(chatIntent);
            }
        });
        mAuth = FirebaseAuth.getInstance();
        commentRef = FirebaseDatabase.getInstance().getReference().child("Comments").child(recieveUserID);
        profileCommentList= (RecyclerView) findViewById(R.id.profile_commentlist);

        profileCommentList.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();

        //get userid from preview activity
        recieveUserID = getIntent().getExtras().getString("visitUserID");
        usersReference.child(recieveUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //check if database has a child
                if (dataSnapshot.hasChild("username")) {

                    String userName = dataSnapshot.child("username").getValue().toString();
                    profileUserName.setText(userName);
                }


                 //check if database has a child
                if (dataSnapshot.hasChild("user_image")) {
                    String image = dataSnapshot.child("user_image").getValue().toString();
                    Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.user_default).into(profileUserImage);


                }
                 //check if database has a child
                if (dataSnapshot.hasChild("experience")) {
                    String experienceContent = dataSnapshot.child("experience").getValue().toString();
                    profileexperienceSettingContentTxt.setText(experienceContent);
                }

                 //check if database has a child
                if (dataSnapshot.hasChild("expertise")) {
                    String expertiseContent = dataSnapshot.child("expertise").getValue().toString();
                    profileexpertiseContentTxt.setText(expertiseContent);
                }

                 //check if database has a child
                if (dataSnapshot.hasChild("rating")) {
                    String rating = dataSnapshot.child("rating").getValue().toString();
                    profileexpertiseRatingBar.setRating(Float.parseFloat(rating));
                }


                 //firebase recycler adpeter declaration,bind data from the Firebase Realtime Database to app's UI.
                FirebaseRecyclerAdapter<Comments, ProfileActivity.CommentsViewHolder> firebaseRecyclerAdapter =
                        new FirebaseRecyclerAdapter<Comments, ProfileActivity.CommentsViewHolder>
                                (
                                        Comments.class,
                                        R.layout.activity_profilecomment,
                                        ProfileActivity.CommentsViewHolder.class,
                                        commentRef
                                ) {

                    // bind comments object to the viewholder
                            @Override
                            protected void populateViewHolder(final ProfileActivity.CommentsViewHolder viewHolder, Comments model, int position) {
                                viewHolder.setCommentbody(model.getCommentbody());
                                viewHolder.setSenderUsername(model.getSenderusername());
                                viewHolder.setDatestamp(model.getDatestamp());

                            }
                        };

                profileCommentList.setAdapter(firebaseRecyclerAdapter);

            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    // view holder displaying each item
    public static class CommentsViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public CommentsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        //set comment text
        public void setCommentbody(String commentBody){

            TextView comment = (TextView) mView.findViewById(R.id.txt_commentbody);
            comment.setText(commentBody);
        }
        //set user name text
        public void setSenderUsername(String senderUsername) {
            TextView commentwriter = (TextView) mView.findViewById(R.id.txt_commentsender);
            commentwriter.setText(senderUsername);

        }

        //set time text
        public void setDatestamp(String datestamp){
            TextView date = (TextView) mView.findViewById(R.id.txt_datestamp);
            date.setText(datestamp);
        }

    }
}
