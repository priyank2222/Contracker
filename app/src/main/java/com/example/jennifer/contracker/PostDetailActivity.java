package com.example.jennifer.contracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PostDetailActivity extends AppCompatActivity {

    // Ui component declaration
    private Toolbar mToolbar;
    private TextView postDetailJobTitle;
    private TextView postDetailLocation;
    private TextView postDetailCategory;
    private TextView postDetailDescription;
    private TextView postDetailCloseBid;
    private EditText postDetailHourlyRate;
    private EditText postDetailEstimatedHour;
    private Button postDetailSubmitBtn;

     // Firebase methods declaration
    private FirebaseAuth mAuth;
    private DatabaseReference postDetailBidDatabaseRef;
    private DatabaseReference postsDatabaseRef;
    String currentUserID;
    String recievierID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

         // UI component initialization
        mToolbar = (Toolbar) findViewById(R.id.post_detail_toolbar);
        postDetailJobTitle =(TextView)findViewById(R.id.post_detail_job_title);
        postDetailLocation =(TextView)findViewById(R.id.post_detail_location);
        postDetailCategory =(TextView)findViewById(R.id.post_detail_category);
        postDetailDescription =(TextView)findViewById(R.id.post_detail_description);
        postDetailHourlyRate =(EditText)findViewById(R.id.post_detail_hourly_rate);
        postDetailEstimatedHour =(EditText)findViewById(R.id.post_detail_estimated_hour);
        postDetailSubmitBtn = (Button)findViewById(R.id.post_detail_submit_btn);

//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setTitle("Place a Bid");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

          // firebase initialization
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        recievierID = getIntent().getExtras().getString("receiverID");

        postDetailBidDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Bids");
        postsDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(recievierID);


        //retrieve post data from database
        retrievePostsInfo();

        //call notify bid method
        postDetailSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyBidInfo();
            }
        });

    }

    //save bid information onto database
    private void notifyBidInfo()
    {
        final String hourlyRate = postDetailHourlyRate.getText().toString();
        final String estimatedHour = postDetailEstimatedHour.getText().toString();

        //check if input is empty
        if(TextUtils.isEmpty(hourlyRate) || TextUtils.isDigitsOnly(hourlyRate) == false){
            Toast.makeText(this, "Incorrect Input", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(estimatedHour) || TextUtils.isDigitsOnly(estimatedHour) == false) {
            Toast.makeText(this, "Incorrect Input", Toast.LENGTH_SHORT).show();
        }


        else {
            //set databse data
            postDetailSubmitBtn.setEnabled(false);
            postDetailBidDatabaseRef.child(currentUserID).child(recievierID).child("hourly_rate").setValue(hourlyRate);
            postDetailBidDatabaseRef.child(currentUserID).child(recievierID).child("estimated_hour").setValue(estimatedHour);
            postDetailBidDatabaseRef.child(currentUserID).child(recievierID).child("request_type").setValue("sent")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                postDetailBidDatabaseRef.child(recievierID).child(currentUserID).child("hourly_rate").setValue(hourlyRate);
                                postDetailBidDatabaseRef.child(recievierID).child(currentUserID).child("estimated_hour").setValue(estimatedHour);
                                postDetailBidDatabaseRef.child(recievierID).child(currentUserID).child("request_type")
                                        .setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            postDetailSubmitBtn.setEnabled(true);
                                            sendUserToMainActivity();

                                        }
                                    }
                                });
                            }
                        }
                    });

        }
    }

    //send user to main page
    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mainIntent);


    }



    //retrieve data from database and convert view contents
    private void retrievePostsInfo()
    {
        postsDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //check if database has a child
                if(dataSnapshot.hasChild("job_title")){
                    postDetailJobTitle.setText("Job Title: "+dataSnapshot.child("job_title").getValue().toString());
                }
                  //check if database has a child
                if(dataSnapshot.hasChild("job_location")){
                    postDetailLocation.setText("Job Location: "+dataSnapshot.child("job_location").getValue().toString());
                }
                  //check if database has a child
                if(dataSnapshot.hasChild("job_description")){
                    postDetailDescription.setText("Job Description: "+dataSnapshot.child("job_description").getValue().toString());
                }
                  //check if database has a child
                if(dataSnapshot.hasChild("service_category")){
                    postDetailCategory.setText("Category: "+dataSnapshot.child("service_category").getValue().toString());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
