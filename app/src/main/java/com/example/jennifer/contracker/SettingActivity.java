package com.example.jennifer.contracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    // Ui component declaration
    private Toolbar mToolbar;
    private CircleImageView settingUserImage;
    private TextView settingUserName;
    private TextView experienceSettingContentTxt;
    private TextView expertiseContentTxt;
    private RatingBar expertiseRatingBar;

    private TextView editSettingBtn;

    // Firebase methods declaration
    private FirebaseAuth mAuth;
    private DatabaseReference databaseSettingReference;
    private String currentUserID;
    String recieveUserID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

         // UI component initialization
        settingUserImage = (CircleImageView) findViewById(R.id.setting_user_image);
        settingUserName = (TextView)findViewById(R.id.setting_user_name);
        editSettingBtn = (TextView) findViewById(R.id.edit_setting_btn);

        experienceSettingContentTxt =(TextView) findViewById(R.id.experience_setting_content_txt);
        expertiseContentTxt = (TextView) findViewById(R.id.expertise_content_txt);
        expertiseRatingBar = (RatingBar) findViewById(R.id.expertise_rating_bar);

        mToolbar = (Toolbar)findViewById(R.id.seting_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Contractor Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

         // firebase initialization
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        databaseSettingReference = FirebaseDatabase.getInstance().getReference().child("Users");

        //user can click this to edit their profile
        editSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editIntent = new Intent(getApplicationContext(),EditProfileActivity.class);
                startActivity(editIntent);
            }
        });

    }
    
    //this looks for changes to the users profile in the database and updates them accordingly 
    @Override
    protected void onStart() {
        super.onStart();
        databaseSettingReference.child(currentUserID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    
                    //change in username
                    if (dataSnapshot.hasChild("username")) {
                        String userName = dataSnapshot.child("username").getValue().toString();
                        settingUserName.setText(userName);
                    }

                    //change in image
                    if (dataSnapshot.hasChild("user_image")) {
                        String image = dataSnapshot.child("user_image").getValue().toString();
                        Picasso.with(SettingActivity.this).load(image).placeholder(R.drawable.user_default).into(settingUserImage);
                    }
                    
                    //change in experience
                    if (dataSnapshot.hasChild("experience")) {
                        String experienceContent = dataSnapshot.child("experience").getValue().toString();
                        experienceSettingContentTxt.setText(experienceContent);
                    }

                    //change in expertise
                    if (dataSnapshot.hasChild("expertise")) {
                        String expertiseContent = dataSnapshot.child("expertise").getValue().toString();
                        expertiseContentTxt.setText(expertiseContent);
                    }

                    //change in rating
                    if (dataSnapshot.hasChild("rating")) {
                        String rating = dataSnapshot.child("rating").getValue().toString();
                        expertiseRatingBar.setRating(Float.parseFloat(rating));
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }
}
