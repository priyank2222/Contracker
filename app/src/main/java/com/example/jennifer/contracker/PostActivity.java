package com.example.jennifer.contracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class PostActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


     // Ui component declaration
    private Toolbar mToolbar;
    private EditText postJobTitle;
    private EditText postDescription;
    private EditText postLocation;
    private TextView txtviewCategory;
    private String[] categoryArray;
    private Button postJobButton;

     // Firebase methods declaration
    private FirebaseAuth mAuth;
    private DatabaseReference postDatabaseRef;
    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

         // firebase initialization
        mToolbar = (Toolbar) findViewById(R.id.post_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Job Posting");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        postDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Posts");

         // UI component initialization
        postJobTitle = (EditText) findViewById(R.id.post_job_tile);
        postDescription =(EditText) findViewById(R.id.post_description);
        postLocation = (EditText) findViewById(R.id.post_location);

//        postCloseBiddingIn = (EditText) findViewById(R.id.post_close_bidding_in);
        postJobButton = (Button) findViewById(R.id.post_job_button);

        categoryArray = getResources().getStringArray(R.array.serviceCategoryPosts);

        Spinner spinner = (Spinner) findViewById(R.id.post_service_category);

        txtviewCategory = (TextView) findViewById(R.id.txtviewCategory);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.serviceCategoryPosts, android.R.layout.simple_spinner_item);

// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);




        //call methods for saving data and redirect to main page
        postJobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePostOnDatabase();
                sendUserToMainActivity();
            }
        });

    }
   //send user to main acitivty
    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(mainIntent);
    }

    //save data onto firebase database
    private void savePostOnDatabase() {

        String jobTitle = postJobTitle.getText().toString();
        String jobDescription = postDescription.getText().toString();
        String jobLocation = postLocation.getText().toString();
        String serviceCategory = txtviewCategory.getText().toString();
//        String closeBid = postCloseBiddingIn.getText().toString();

        if(TextUtils.isEmpty(jobTitle)){
            Toast.makeText(this, "Job title cannot be empty.", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(jobDescription)){
            Toast.makeText(this, "Job description cannot be empty.", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(jobLocation)){
            Toast.makeText(this, "Job location cannot be empty.", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(serviceCategory)){
            Toast.makeText(this, "Service category cannot be empty.", Toast.LENGTH_SHORT).show();
        }
//
//        if(TextUtils.isEmpty(closeBid)){
//            Toast.makeText(this, "Close bidding cannot be empty.", Toast.LENGTH_SHORT).show();
//        }

        else{
            //hashmap to store data
            HashMap postDetail = new HashMap();
            postDetail.put("job_title",jobTitle);
            postDetail.put("job_description",jobDescription);
            postDetail.put("job_location",jobLocation);
            postDetail.put("service_category",serviceCategory);
            //postDetail.put("close_bidding",closeBid);

            //push data onto database
            postDatabaseRef.child(currentUserID).updateChildren(postDetail).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Toast.makeText(PostActivity.this, "Job posting successfully upload to database.", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        String error = task.getException().getMessage();
                        Toast.makeText(PostActivity.this, "Error: "+error, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
        txtviewCategory.setText(categoryArray[i]+"");
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
//
//            DatabaseReference userPostKey = postDatabaseRef.child("Post").child(currentUserID).push();
//
//            String userPushID = userPostKey.getKey();
//
//
//            HashMap postDetail = new HashMap();
//            postDetail.put("job_title",jobTitle);
//            postDetail.put("job_description",jobDescription);
//            postDetail.put("job_location",jobLocation);
//            postDetail.put("service_category",serviceCategoty);
////            postDetail.put("close_bidding",closeBid);
//
//
//            Map postBodyDetail = new HashMap();
//            postBodyDetail.put(currentUserID+ "/"+userPushID,postDetail);
//
//            postDatabaseRef.updateChildren(postBodyDetail).addOnCompleteListener(new OnCompleteListener() {
//                @Override
//                public void onComplete(@NonNull Task task) {
//                    if(task.isSuccessful()){
//                        Toast.makeText(PostActivity.this, "Job posting successfully upload to database.", Toast.LENGTH_SHORT).show();
//                    }
//                    else{
//                        String error = task.getException().getMessage();
//                        Toast.makeText(PostActivity.this, "Error: "+error, Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//
//        }
//
//    }
//}
