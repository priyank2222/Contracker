package com.example.jennifer.contracker;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // Ui component declaration
    private Toolbar mToolbar;
    private CircleImageView editProfileImage;
    private EditText editProfileUsername;
    private EditText experienceInputTxt;
    private EditText expertiseInputTxt;
    private RatingBar editProfileRatingBar;
    private Button saveEditBtn;
    private TextView txtExplvl;

    // Firebase methods declaration
    private DatabaseReference saveEditProfileDatabseReference;
    private StorageReference editProfileStorageRef;
    private FirebaseAuth mAuth;

    private String[] categoryArray;
    private final static int Gallery_Pick = 1;
    String currentUserId;
    private String profileImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // UI component initialization
        mToolbar = (Toolbar) findViewById(R.id.edit_profile_toolbar);
        experienceInputTxt = (EditText) findViewById(R.id.experience_input_txt);
        expertiseInputTxt = (EditText) findViewById(R.id.expertise_input_txt);
        editProfileRatingBar = (RatingBar) findViewById(R.id.edit_profile_rating_bar);
        saveEditBtn = (Button) findViewById(R.id.save_edit_btn);
        editProfileImage = (CircleImageView) findViewById(R.id.edit_profile_image);
        editProfileUsername = (EditText) findViewById(R.id.edit_profile_username);
        txtExplvl = (TextView) findViewById(R.id.txtExplvl);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Edit Profile");

          // firebase initialization
        mAuth = FirebaseAuth.getInstance();

        currentUserId = mAuth.getCurrentUser().getUid();
        saveEditProfileDatabseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        editProfileStorageRef = FirebaseStorage.getInstance().getReference().child("ProfileImage");

        // initialize category array 
        categoryArray = getResources().getStringArray(R.array.serviceCategory);
        // initialze spinner
        Spinner spinner = (Spinner) findViewById(R.id.spinner_edit_profile);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.serviceCategory, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);

        // photoes permission requests
        editProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(EditProfileActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);


            }
        });


        //save information button
        saveEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserProfileInfo();

            }
        });

        // retrive data from database
        retriveUserProfileInfo();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

            // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    sendToGalleryPick();
                 // permission was granted, yay! Do the
                 // contacts-related task you need to do.
                } else {

// permission denied, boo! Disable the
// functionality that depends on this permission.
                    Toast.makeText(EditProfileActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }


        }
    }

    //retrive data from database
    private void retriveUserProfileInfo() {

          saveEditProfileDatabseReference.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                //check if database has child 
                if(dataSnapshot.hasChild("username")) {

                    String userName = dataSnapshot.child("username").getValue().toString();
                    editProfileUsername.setText(userName);
                }


                 //check if database has child 
                if(dataSnapshot.hasChild("user_image")) {
                    String image = dataSnapshot.child("user_image").getValue().toString();
                    Picasso.with(EditProfileActivity.this).load(image).placeholder(R.drawable.user_default).into(editProfileImage);


                }
                 //check if database has child 
                if(dataSnapshot.hasChild("experience")){
                    String experienceContent = dataSnapshot.child("experience").getValue().toString();
                    experienceInputTxt.setText(experienceContent);
                }

                 //check if database has child 
                if(dataSnapshot.hasChild("expertise")){
                    String expertiseContent = dataSnapshot.child("expertise").getValue().toString();
                    expertiseInputTxt.setText(expertiseContent);
                }

                 //check if database has child 
                if(dataSnapshot.hasChild("rating")){
                    String rating = dataSnapshot.child("rating").getValue().toString();
                    editProfileRatingBar.setRating(Float.parseFloat(rating));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
         });

//

    }




    //gallery intent,send user to device gallery
    private void sendToGalleryPick() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,Gallery_Pick);
    }

    //save data onto firebase database
    private void saveUserProfileInfo() {

        String username = editProfileUsername.getText().toString();
        String experience = experienceInputTxt.getText().toString();
        String expertise = expertiseInputTxt.getText().toString();
        String rating = String.valueOf(editProfileRatingBar.getRating());

        //check if input is empty
        if(TextUtils.isEmpty(username)){
            Toast.makeText(this, "Username Input cannot be empty", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(experience)){
            Toast.makeText(this, "Experience Input cannot be empty", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(expertise)){
            Toast.makeText(this, "Expertise Input cannot be empty", Toast.LENGTH_SHORT).show();
        }
        else{

            //hashmap storing data 
            HashMap userMap = new HashMap();
            userMap.put("username",username);
            userMap.put("experience",experience);
            userMap.put("expertise",expertise);
            userMap.put("rating",rating);
           // userMap.put("user_image","user_default");

            // save data onto database
            saveEditProfileDatabseReference.child(currentUserId).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if(task.isSuccessful()){
                        //task success, send user to main acitivity
                        sendUserToMainActivity();
                        Toast.makeText(EditProfileActivity.this, "Info saved successfully", Toast.LENGTH_SHORT).show();

                    }
                    else{
                        String error = task.getException().getMessage();
                        Toast.makeText(EditProfileActivity.this, "Error: "+error, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    }

    //send user to main activity intent
    private void sendUserToMainActivity() {

        Intent mainIntent = new Intent(getApplicationContext(),MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }

    //spinner selection 
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        //set text according to the index of category array
        expertiseInputTxt.setText(categoryArray[i]+"");
        editProfileRatingBar.setVisibility(View.VISIBLE);
        txtExplvl.setVisibility(View.VISIBLE);

        if(categoryArray[i].equals("Customer")){
            editProfileRatingBar.setVisibility(View.INVISIBLE);
            txtExplvl.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    //expilicitly intent for gallery intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null){
            Uri ImageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                // firebase storage reference, store users images onto 
                StorageReference filePath = editProfileStorageRef.child(currentUserId+".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(EditProfileActivity.this, "Upload to Firebase storage Successfully", Toast.LENGTH_SHORT).show();

                            //get the image uri from firebase storage
                            final String downloadUri = task.getResult().getDownloadUrl().toString();
                            //save image uir to firebase database
                            saveEditProfileDatabseReference.child(currentUserId).child("user_image").setValue(downloadUri).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    //update imageview with firebase uri
                                    Toast.makeText(EditProfileActivity.this, "Image store to database", Toast.LENGTH_SHORT).show();
                                    Picasso.with(EditProfileActivity.this).load(downloadUri).into(editProfileImage);
                                  //  profileImage = downloadUri;
                                }
                            });
                        }
                        else {
                            Toast.makeText(EditProfileActivity.this, "Error Upload to Firebase ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


                //handle gallery intent error
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        //get firebase user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //check user existence
        if (currentUser == null) {

            logoutUser();
        }
    }

    //logout user method
        private void logoutUser() {

            //signout firebase user and go to login activity
            mAuth.signOut();
            Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();

        }
}
