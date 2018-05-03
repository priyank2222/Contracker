package com.example.jennifer.contracker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Token;
import com.stripe.android.model.Card;
import com.stripe.android.view.CardInputWidget;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//References: Stripe documentation at https://stripe.com/docs/mobile/android; Github: https://github.com/raeesaa/sample-stripe-android/blob/master/Stripe-API-Android.md
//http://www.zoftino.com/android-server-side-functionality-using-firebase-cloud-functions
//https://medium.com/@gordonnl/headless-stripe-payments-with-firebase-9b12639ea118

public class ChargePayment extends AppCompatActivity{

    Button btnSubmit;
    CardInputWidget mCardInputWidget;

    //firebase url links to firebase cloud function containing the back-end code for charge
    //see index.js for the javascript code uploaded to cloud function
    private static final String FirebaseUrl = "https://us-central1-contracker-789c6.cloudfunctions.net/charge";
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    TextView txtbalance;
    private Toolbar mToolbar;
    private DatabaseReference userinforef;
    private DatabaseReference jobinforef;

    String currentUserId;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_payment);

        //instantiate the built-in CardInputWidget from Stripe (has input fields for credit card number, exp date, and cvc
        final CardInputWidget mCardInputWidget = (CardInputWidget) findViewById(R.id.card_input_widget);

        final Button btnSubmit = (Button) findViewById(R.id.btnSubmit);
        txtbalance = (TextView) findViewById(R.id.txtBalance);
        final float balancef = getIntent().getExtras().getFloat("balance");
        final int balance = Math.round(balancef*100);
        final String listUserID = getIntent().getExtras().getString("contractor");
        txtbalance.setText("Amount Due is $"+ String.format("%.2f", balancef));
        final String currentUserID = getIntent().getExtras().getString("customer");


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userinforef = FirebaseDatabase.getInstance().getReference().child("Users");
        currentUserId = mAuth.getCurrentUser().getUid();
        jobinforef = FirebaseDatabase.getInstance().getReference().child("Services").child(currentUserId).child(listUserID);


        mToolbar = (Toolbar) findViewById(R.id.comment_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Payment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    // Permission is not granted
//                }
//                else {
                    //when submit button is clicked, get the information in the filled out mCardInputWidget
                    btnSubmit.setEnabled(false);
                    Card cardToSave = mCardInputWidget.getCard();
                    if (cardToSave == null) {
                        //if the user input is invalid, show toast
                        Toast.makeText(getApplicationContext(), "Invalid Input", Toast.LENGTH_SHORT).show();
                    } else {
                        //if card if valid, tokenize the card
                        Stripe stripe = new Stripe(getApplicationContext(), "pk_test_9aPYEMEURqmvhLkTMCOX4eeK");
                        stripe.createToken(
                                cardToSave,
                                new TokenCallback() {
                                    @SuppressLint("StaticFieldLeak")
                                    public void onSuccess(Token token) {
                                        // Send token to Firebase cloud function
                                        //create a hashmap to contain the parameters of your charge operation

                                        final Map<String, Object> chargeParams = new HashMap<String, Object>();
                                        //Charge takes amount in lowest monetary unit (in this case, cents)
                                        chargeParams.put("amount", balance);
                                        chargeParams.put("currency", "usd");
                                        chargeParams.put("source", token.getId());
//                                    Toast.makeText(getApplicationContext(), "Token created: " + token.getId(), Toast.LENGTH_LONG).show();

                                        OkHttpClient httpClient = new OkHttpClient();

                                        //build http request to send to firebase cloud function
                                        HttpUrl.Builder httpBuilder =
                                                HttpUrl.parse(FirebaseUrl).newBuilder();
                                        httpBuilder.addQueryParameter("amount", "" + chargeParams.get("amount"));
                                        httpBuilder.addQueryParameter("source", "" + token.getId());
                                        httpBuilder.addQueryParameter("currency", "" + chargeParams.get("currency"));


                                        Request request = new Request.Builder().
                                                url(httpBuilder.build()).build();
                                        httpClient.newCall(request).enqueue(new Callback() {

                                            @Override
                                            public void onFailure(Request request, IOException e) {
                                                Log.e("Error", "Error in getting response from Firebase cloud function");

                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(getApplicationContext(),
                                                                "Couldn't get response from cloud function",
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onResponse(Response response) throws IOException {

                                                ResponseBody responseBody = response.body();
                                                String resp = "";
                                                if (!response.isSuccessful()) {
                                                    btnSubmit.setEnabled(true);
                                                    Log.e("FAILED", "Failed to get response from firebase cloud function");

                                                } else {
                                                    //response is successful
                                                    try {
                                                        resp = responseBody.string();
                                                        Log.e("resp", resp);

                                                    } catch (IOException e) {
                                                        btnSubmit.setEnabled(true);
                                                        resp = "Problem in charging card";
                                                        Log.e("Error", "Problem in reading response " + e);
                                                    }
                                                }
                                                runOnUiThread(responseRunnable(resp));
                                            }
                                        });

                                    }

                                    private Runnable responseRunnable(final String responseStr) {
                                        Runnable resRunnable = new Runnable() {
                                            public void run() {
                                                Log.e("results", responseStr);
                                                if (responseStr.equals("succeeded")) {
                                                    //on success, modify contractor balance and mark the job as paid in database

                                                    userinforef.child(listUserID).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            if (dataSnapshot.hasChild("balance")) {
                                                                int newbalance = Integer.parseInt(dataSnapshot.child("balance").getValue().toString()) + balance;
                                                                userinforef.child(listUserID).child("balance").setValue(newbalance);
                                                                jobinforef.child("paid").setValue(true);
                                                            } else {
                                                                userinforef.child(listUserID).child("balance").setValue(balance);
                                                                jobinforef.child("paid").setValue(true);
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });
                                                    //then send user to Charge Paid page
                                                    Intent chargepaid = new Intent(getApplicationContext(), ChargePaid.class);
                                                    chargepaid.putExtra("submittedinfo", "Payment Submitted");
                                                    startActivity(chargepaid);
                                                    finish();
                                                } else {
                                                    //toasts response; if card is denied, appropriate message will be displayed
                                                    Toast.makeText(getApplicationContext()
                                                            , responseStr,
                                                            Toast.LENGTH_LONG).show();
                                                    //failure to charge card reactivates submit button
                                                    btnSubmit.setEnabled(true);
                                                    Log.e("FAILURE", "failed to charge card");
                                                }
                                            }
                                        };
                                        return resRunnable;
                                    }

                                    public void onError(Exception error) {
                                        // Show error message if token was not successfully sent to the server
                                        Toast.makeText(getApplicationContext(),
                                                "Error: Token was not successfully sent.",
                                                Toast.LENGTH_LONG
                                        ).show();
                                    }
                                }
                        );

                   // }
                }
            }
        });

    }


}
