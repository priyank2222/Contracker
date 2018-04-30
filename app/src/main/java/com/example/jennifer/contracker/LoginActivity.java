package com.example.jennifer.contracker;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {


     // Firebase methods declaration
    private FirebaseAuth mAuth;
    // Ui component declaration
    
    private Button signInLoginBtn;
    private TextView signUpLoginBtn;
    private TextView usernameLoginTxt;
    private TextView passwordLoginTxt;

    private SignInButton btnGoogle;
    private GoogleSignInClient mGoogleSignInClient;
    private String TAG = "";

    private Toolbar mToolBar;

    private Bundle extras;

    private ProgressDialog mProgressDialog;

    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 1;  // Request Code



    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // firebase initialization
        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_login);

        extras = getIntent().getExtras();

         // UI component initialization
        signUpLoginBtn = (TextView) findViewById(R.id.signUpLoginBtn);
        signInLoginBtn = (Button) findViewById(R.id.signInLoginBtn);
        usernameLoginTxt =(TextView) findViewById(R.id.usernameLoginTxt);
        passwordLoginTxt = (TextView) findViewById(R.id.passwordLoginTxt);

        btnGoogle = (SignInButton) findViewById(R.id.btnGoogle);

        mProgressDialog = new ProgressDialog(this);

//        mToolBar = (Toolbar) findViewById(R.id.login_toolbar);
//        setSupportActionBar(mToolBar);
//        getSupportActionBar().setTitle("Log In");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //button onclick listener
        signInLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = usernameLoginTxt.getText().toString();
                String password = passwordLoginTxt.getText().toString();

                //login user method
                loginUser(email,password);


            }
        });

        //button onclick listener
        signUpLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //send user to register
                Intent registerIntent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(registerIntent);

            }
        });

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loginWithGoogle();
            }
        });


    }

    // Method to start LogIn with Google Account
    private void loginWithGoogle() {

        @SuppressLint("RestrictedApi") Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            @SuppressLint("RestrictedApi") Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    // [END onActivityResult]

    // [START handleSignInResult]
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            if(completedTask.isSuccessful()) {
                firebaseAuthWithGoogle(account);
            }

            // Signed in successfully, show authenticated UI.
            // updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            // updateUI(null);
        }
    }
    // [END handleSignInResult]

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            sendUserToMainActivity();
                            //  updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //  Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            // updateUI(null);
                        }
                    }
                });
    }

    private void loginUser(String email, String password) {


        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Email Can not be empty", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Password can not be empty", Toast.LENGTH_SHORT).show();
        }
        else {

            mProgressDialog.setTitle("Login Your Account");
            mProgressDialog.setMessage("Please wait, while signing in.");
            mProgressDialog.show();
            mProgressDialog.setCanceledOnTouchOutside(true);

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String extra = "";
                        if(getIntent().hasExtra("register")) checkIfEmailVerified();
                        else if(getIntent().hasExtra("logout")) sendUserToMainActivity();

                        else sendUserToMainActivity();

                    } else {
                        Toast.makeText(getApplicationContext(), "Error login", Toast.LENGTH_SHORT).show();
                    }
                    mProgressDialog.dismiss();
                }
            });
        }
    }

    private void checkIfEmailVerified()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user.isEmailVerified())
        {
// user is verified, so you can finish this activity or send user to activity which you want.
            sendUserToEditProfileActivity();
            Toast.makeText(LoginActivity.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
        }
        else
        {
// email is not verified, so just prompt the message to the user and restart this activity.
// NOTE: don't forget to log out the user.
            Toast.makeText(LoginActivity.this, "Verify your Email", Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();

//restart this activity

        }
    }

    //send user to edit profile page
    private void sendUserToEditProfileActivity() {

        Intent editIntent = new Intent(getApplicationContext(),EditProfileActivity.class);
        editIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(editIntent);
        finish();

    }

    //send user to main page
    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }

    @Override
    protected void onStart() {
        super.onStart();

        //get firebase user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //check user existence
        if(currentUser != null){
            sendUserToMainActivity();
        }
    }
}
