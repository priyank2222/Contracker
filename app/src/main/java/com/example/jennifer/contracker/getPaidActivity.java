package com.example.jennifer.contracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class getPaidActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private DatabaseReference userinforef;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String currentUserId;
    TextView txtamount;
    Button btntransfer;
    Button btnreturn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getpaid1);

//        mToolbar = (Toolbar) findViewById(R.id.getpaid_toolbar);
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setTitle("Your Balance");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        txtamount = (TextView) findViewById(R.id.txtAmount);
        currentUserId = mAuth.getCurrentUser().getUid();
        userinforef = FirebaseDatabase.getInstance().getReference().child("Users");

        //populate textview based on database value
        userinforef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("balance")){
                    float balance = Integer.parseInt(dataSnapshot.child("balance").getValue().toString()) / 100;

                    txtamount.setText("Your balance is $"+ String.format("%.2f", balance));
                }
                else{
                    //balance has never been set for this contractor
                    txtamount.setText("Your balance is $0.00");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnreturn = (Button) findViewById(R.id.btnreturn);
        btnreturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent homeIntent = new Intent(getApplicationContext(), MainActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(homeIntent);
                finish();
            }
        });

        //transfer money to bank account
        btntransfer = (Button) findViewById(R.id.btnPayout);

        btntransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userinforef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("balance")) {
                            if (Integer.parseInt(dataSnapshot.child("balance").getValue().toString()) == 0) {
                                Toast.makeText(getApplicationContext(), "You do not have a balance.", Toast.LENGTH_SHORT).show();
                            } else {
                                //if there is a balance, set balance to 0 and send user to Charge Paid page
                                int newbalance = 0;
                                userinforef.child(currentUserId).child("balance").setValue(newbalance);
                                Intent chargepaid = new Intent(getApplicationContext(), ChargePaid.class);
                                chargepaid.putExtra("submittedinfo", "Payment Transferred");
                                startActivity(chargepaid);
                                finish();
                            }
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "You do not have a balance.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });





    }
}

