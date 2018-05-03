package com.example.jennifer.contracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;


    private String TAG = "";
    private EditText confirmPasswordTxt;
    private EditText emailRegisterTxt;
    private EditText passwordRegisterTxt;
    private Button createAccountRegisterBtn;

    private ProgressDialog mProgressDialog;
    private Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        emailRegisterTxt = (EditText) findViewById(R.id.emailRegisterTxt);
        passwordRegisterTxt =(EditText) findViewById(R.id.passwordRegisterTxt);
        confirmPasswordTxt =(EditText) findViewById(R.id.confirmPasswordTxt);
        createAccountRegisterBtn = (Button) findViewById(R.id.createAccountRegisterBtn);

        //toolbar
//        mToolBar = (Toolbar) findViewById(R.id.regiser_toolbar);
//        setSupportActionBar(mToolBar);
//        getSupportActionBar().setTitle("Sign Up");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //progress dialog
        mProgressDialog = new ProgressDialog(this);

        createAccountRegisterBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String email = emailRegisterTxt.getText().toString();
                String password = passwordRegisterTxt.getText().toString();
                String confirmPassword = confirmPasswordTxt.getText().toString();

                registerUserAccount(email,password,confirmPassword);

            }
        });
    }
    
    //Check if the user has filled out all registration requirements 
    private void registerUserAccount(String email, String password, String confirmPassword)
    {

        if(TextUtils.isEmpty(email)){
            Toast.makeText(getApplicationContext(),"Email cannot be empty.",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(getApplicationContext(),"Password cannot be empty.",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(confirmPassword)){
            Toast.makeText(getApplicationContext(),"Confirm Password cannot be empty.",Toast.LENGTH_SHORT).show();
        }
        if(!password.equals(confirmPassword)){
            Toast.makeText(getApplicationContext(),"Password should match.",Toast.LENGTH_SHORT).show();
        }
        
        //if all requirements are completed successfully
        else{
            mProgressDialog.setTitle("Creating New Account");
            mProgressDialog.setMessage("Please wait, while we creating account for you.");
            mProgressDialog.show();
            
            //create the user account
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){

                        Toast.makeText(RegisterActivity.this, "Your account registered successfully.", Toast.LENGTH_SHORT).show();

                    }
                    else{
                        String error = task.getException().getMessage();
                        Toast.makeText(getApplicationContext(),"Error: "+ error,Toast.LENGTH_SHORT).show();
                    }
                    mProgressDialog.dismiss();
                    //send the email verification
                    sendVerification();
                    
                    mAuth.signOut();
                    //send the user to the login page
                    Intent i = new Intent(getApplicationContext() , LoginActivity.class);
                    i.putExtra("register" , "fromregisteractivity");
                    startActivity(i);

                    finish();
                }
            });
        }

    }
    //email verification process
    private void sendVerification()
    {
        final FirebaseUser user = mAuth.getCurrentUser();
        
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(getApplicationContext(),
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
    
    //after a user logs in, send them to the settings page
    private void sendUserToSettingActivity() {

        Intent settingIntent = new Intent(getApplicationContext(),SettingActivity.class);
        settingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingIntent);
        finish();
    }
    
    //send user to edit their profile
    private void sendUserToEditProfileActivity() {

        Intent editIntent = new Intent(getApplicationContext(),EditProfileActivity.class);
        editIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(editIntent);
        finish();

    }
    //check if user is currently logged in, if they are, send them to the main activity.
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            sendUserToMainActivity();
        }
    }

    //send user to the main activity if they are logged in
    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }
}
