package com.example.jennifer.contracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class CurrentBidActivity extends AppCompatActivity {

     // Ui component declaration
    private RecyclerView bidsList;
    private Toolbar mToolbar;
    // Firebase methods declaration
    private DatabaseReference rootRef;
    private DatabaseReference userRef;
    private DatabaseReference bidsRef;
    private DatabaseReference serviceRef;
    private FirebaseAuth mAuth;
    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_bid);
         // firebase initialization
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference().child("Bids").child(currentUserID);
        bidsRef = FirebaseDatabase.getInstance().getReference().child("Bids");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        serviceRef = FirebaseDatabase.getInstance().getReference().child("Services");
         // UI component initialization
        mToolbar = (Toolbar) findViewById(R.id.bids_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Current Bids");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bidsList = (RecyclerView) findViewById(R.id.bids_list);

        bidsList.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();
        //firebase recycler adpeter declaration,bind data from the Firebase Realtime Database to app's UI.
        FirebaseRecyclerAdapter<Bids,BidsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Bids, BidsViewHolder>
                        (
                                Bids.class,
                                R.layout.all_current_bid_display_layout,
                                BidsViewHolder.class,
                                rootRef
                        ) {
            // bind bids object to the viewholder
            @Override
            protected void populateViewHolder(final BidsViewHolder viewHolder, Bids model, int position)
            {

                viewHolder.setEstimated_hour(model.getEstimated_hour());
                viewHolder.setHourly_rate(model.getHourly_rate());

                final String listUserID = getRef(position).getKey();
                DatabaseReference getTypeRef = getRef(position).child("request_type").getRef();

                getTypeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {

                        if (dataSnapshot.exists()) {
                            String requestType = dataSnapshot.getValue().toString();
                            if (requestType.equals("received"))
                            {
                                userRef.child(listUserID).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if(dataSnapshot.hasChild("user_image")){

                                            String userImage = dataSnapshot.child("user_image").getValue().toString();

                                            viewHolder.setUser_Image(userImage,getApplicationContext());
                                        }
                                        String name = dataSnapshot.child("username").getValue().toString();
//                                        String image = dataSnapshot.child("user_image").getValue().toString();

                                        viewHolder.setUsername(name);
//                                        viewHolder.setUser_Image(image, getApplicationContext());




                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view)
                                    {

                                        CharSequence options[] = new CharSequence[]
                                                {
                                                        "User Profile",
                                                        "Accept Bid",
                                                        "Decline Bid"

                                                };
                                        //alert dialog displaying when user clicks on the item list
                                        AlertDialog.Builder builder = new AlertDialog.Builder(CurrentBidActivity.this);
                                        builder.setTitle("Select Options");

                                        builder.setItems(options, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int pos) {

                                                if (pos == 0){
                                                    Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                                                    profileIntent.putExtra("visitUserID", listUserID);
                                                    startActivity(profileIntent);
                                                }
                                                if (pos == 1) {
                                                    acceptBid(listUserID);
                                                }
                                                if (pos == 2) {
                                                    declineBid(listUserID);
                                                }


                                            }
                                        });
                                        builder.show();
                                    }

                                });

                            }
                            else if(requestType.equals("sent"))
                            {
                                userRef.child(listUserID).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String name = dataSnapshot.child("username").getValue().toString();

                                        viewHolder.setUsername(name);
                                        viewHolder.mView.findViewById(R.id.current_bid_profile_image)
                                                .setVisibility(View.INVISIBLE);

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });



                                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view)
                                    {

                                        CharSequence options[] = new CharSequence[]
                                                {
                                                        "Cancel Bid"

                                                };

                                        AlertDialog.Builder builder = new AlertDialog.Builder(CurrentBidActivity.this);
                                        builder.setTitle("Select Options");

                                        builder.setItems(options, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int pos) {



                                                if (pos == 0) {


                                                    cancelBid(listUserID);
                                                }


                                            }
                                        });
                                        builder.show();
                                    }

                                });


                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });




            }
        };

        bidsList.setAdapter(firebaseRecyclerAdapter);
    }

    private void cancelBid(final String listUserID){
        declineBid(listUserID);
    }

    // decline bid method to cancel the bid request 
    private void declineBid(final String listUserID)
    {
        // remove the data from firebase database
        bidsRef.child(currentUserID).child(listUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //when success, deleting another users' data
                    bidsRef.child(listUserID).child(currentUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Bid Deleted", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }

            }
        });

    }

    //accept bid method when user clicks accept bid
    private void acceptBid(final String listUserID)
    {
        //get date and time 
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM-dd-yyyy");
        final String saveCurrentDate = currentDate.format(calendar.getTime());

        //add data into firebase database
        bidsRef.child(currentUserID).child(listUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //check if data exists
                if(dataSnapshot.exists())
                {

                    final String estimatedHour = dataSnapshot.child("estimated_hour").getValue().toString();
                    final String hourlyRate= dataSnapshot.child("hourly_rate").getValue().toString();

                    //bind data to UI
                    serviceRef.child(currentUserID).child(listUserID).child("estimated_hour").setValue(estimatedHour);
                    serviceRef.child(currentUserID).child(listUserID).child("hourly_rate").setValue(hourlyRate);
                    serviceRef.child(currentUserID).child(listUserID).child("request_type").setValue("sent");
                    serviceRef.child(currentUserID).child(listUserID).child("date").setValue(saveCurrentDate)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //bind data to UI
                                    serviceRef.child(listUserID).child(currentUserID).child("hourly_rate").setValue(hourlyRate);
                                    serviceRef.child(listUserID).child(currentUserID).child("estimated_hour").setValue(estimatedHour);
                                    serviceRef.child(listUserID).child(currentUserID).child("request_type").setValue("received");
                                    serviceRef.child(listUserID).child(currentUserID).child("date").setValue(saveCurrentDate)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    bidsRef.child(currentUserID).child(listUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                bidsRef.child(listUserID).child(currentUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if(task.isSuccessful()){

                                                                            Toast.makeText(getApplicationContext(), "Bid Accepted.", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                            }

                                                        }
                                                    });
                                                }
                                            });
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

     // view holder displaying each item
    public static class BidsViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public BidsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
 
        //set estimated hours text
        public void setEstimated_hour(String estimated_hour){

            TextView hour = (TextView) mView.findViewById(R.id.current_bid_hour);
            hour.setText(estimated_hour +"hr");
        }
        //set hourly rate text
        public void setHourly_rate(String hourly_rate) {
            TextView rate = (TextView) mView.findViewById(R.id.current_bid_price);
            rate.setText("$"+hourly_rate +"per/hr");

        }

        //set user name text
        public void setUsername(String name) {
            TextView username = (TextView) mView.findViewById(R.id.current_bid_username);
            username.setText(name);
        }

        //set user image
        public void setUser_Image(String image, Context ctx) {
            CircleImageView profileImage = (CircleImageView) mView.findViewById(R.id.current_bid_profile_image);
            Picasso.with(ctx).load(image).into(profileImage);
        }
    }
}
