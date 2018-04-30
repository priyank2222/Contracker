package com.example.jennifer.contracker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stripe.model.Charge;

public class MainActivity extends AppCompatActivity {


    // Firebase methods declaration
    private FirebaseAuth mAuth;
    private DatabaseReference usersDatabaseReference;

    // Ui component declaration
    private Toolbar mToolBar;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    private TabsPagerAdapter mTabsPagerAdapter;

    private GoogleSignInClient mGoogleSignInClient;

    //TODO delete when we know where to put payouts
    Button btnPayout;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

          // firebase initialization
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

          // UI component initialization
        mToolBar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Contracker");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //TODO delete when we know where to put payouts button
        btnPayout = (Button) findViewById(R.id.btnPayout);
        btnPayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent payoutIntent = new Intent(getApplicationContext(),getPaidActivity.class);
                payoutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(payoutIntent);
                finish();
            }
        });
        //

        //Tabs for MainActivity
        mViewPager = (ViewPager) findViewById(R.id.main_view_pager);
        mTabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mTabsPagerAdapter);
        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setIcon(R.drawable.job_icon);
        mTabLayout.getTabAt(1).setIcon(R.drawable.chat_icon);
        mTabLayout.getTabAt(2).setIcon(R.drawable.user_icon);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


    }


    @Override
    protected void onStart() {
        super.onStart();

        //get firebase user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){

            logoutUser();
        }
        else{
            checkUserExist();
        }
    }

    //check for user existence
    private void checkUserExist() {
        final String currentUserID = mAuth.getCurrentUser().getUid();
        usersDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(currentUserID)){
                    sendUserToEditProfileActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    //send user to edit profie page
    private void sendUserToEditProfileActivity() {

        Intent editIntent = new Intent(getApplicationContext(),EditProfileActivity.class);
        editIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(editIntent);
        finish();

    }
    //send user to setting page
    private void sendUserToSettingActivity() {

        Intent settingIntent = new Intent(getApplicationContext(),SettingActivity.class);
        settingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingIntent);
        finish();

    }

    @SuppressLint("RestrictedApi")
    private void logoutUser() {

        mAuth.signOut();

        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });

        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);

        return  true;
    }

    //menu bar selection 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.main_new_posts_button){
            senderUserToPostActivity();
        }
        if(item.getItemId() == R.id.main_logout_button){
            logoutUser();
        }
        if(item.getItemId() == R.id.main_all_users_button){
            Intent allUsersIntent = new Intent(getApplicationContext(),AllUsersActivity.class);
            startActivity(allUsersIntent);
        }


        if(item.getItemId() == R.id.main_setting_button){
            Intent settingIntent = new Intent(getApplicationContext(),SettingActivity.class);
            startActivity(settingIntent);
        }

        return true;
    }

    //send user to post page
    private void senderUserToPostActivity() {
        Intent postActivity = new Intent(getApplicationContext(),PostActivity.class);
        startActivity(postActivity);
    }


}


