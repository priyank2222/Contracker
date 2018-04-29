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

public class CurrentServiceActivity extends AppCompatActivity {

    private RecyclerView serviceList;
    private Toolbar mToolbar;
    private DatabaseReference serviceRef;
    private DatabaseReference userRef;
    private DatabaseReference historyRef;
    private DatabaseReference serviceDeleteRef;
    private DatabaseReference bidRef;
    private DatabaseReference postsRef;
    private FirebaseAuth mAuth;
    String currentUserID;
    private DatabaseReference jobinforef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_service);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        serviceRef = FirebaseDatabase.getInstance().getReference().child("Services").child(currentUserID);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        bidRef = FirebaseDatabase.getInstance().getReference().child("Bids");
        historyRef = FirebaseDatabase.getInstance().getReference().child("History");
        serviceDeleteRef =  FirebaseDatabase.getInstance().getReference().child("Services");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        jobinforef = FirebaseDatabase.getInstance().getReference().child("Services");

        mToolbar = (Toolbar) findViewById(R.id.toolbar_service);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Current Service");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        serviceList = (RecyclerView) findViewById(R.id.service_list);

        serviceList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Services,ServicesViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Services, ServicesViewHolder>
                        (
                                Services.class,
                                R.layout.all_current_service_display_layout,
                                ServicesViewHolder.class,
                                serviceRef
                        ) {


                    @Override
                    protected void populateViewHolder(final ServicesViewHolder viewHolder, Services model, int position)
                    {

                        viewHolder.setDate(model.getDate());
                        viewHolder.setEstimated_hour(model.getEstimated_hour());
                        viewHolder.setHourly_rate(model.getHourly_rate());

                        final float balance = Float.parseFloat(model.getEstimated_hour()) * Float.parseFloat(model.getHourly_rate());
                        final String listUserID = getRef(position).getKey();
                        final DatabaseReference getTypeRef = getRef(position).child("request_type").getRef();

                        getTypeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot)
                            {

                                if (dataSnapshot.exists()) {
                                    String requestType = dataSnapshot.getValue().toString();
                                    if (requestType.equals("sent"))
                                    {


                                        userRef.child(listUserID).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                String name = dataSnapshot.child("username").getValue().toString();

                                                if(dataSnapshot.hasChild("user_image")){

                                                    String userImage = dataSnapshot.child("user_image").getValue().toString();

                                                    viewHolder.setUser_Image(userImage,getApplicationContext());
                                                }
//                                                String image = dataSnapshot.child("user_image").getValue().toString();

                                                viewHolder.setUsername(name + " is completing your task.");
//                                                viewHolder.setUser_Image(image,getApplicationContext());

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                        //message button listener
                                        viewHolder.mView.findViewById(R.id.current_service_message_btn)
                                                .setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        sendToChat(listUserID);
                                                    }
                                                });

                                        //sets
                                        viewHolder.mView.findViewById(R.id.current_service_pay_btn).setVisibility(View.INVISIBLE);

                                        jobinforef.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.child(listUserID).child(currentUserID).hasChild("completed")
                                                        && !(dataSnapshot.child(currentUserID).child(listUserID).hasChild("paid"))){
                                                    viewHolder.mView.findViewById(R.id.current_service_pay_btn).setVisibility(View.VISIBLE);
                                                }
                                                else{
                                                    viewHolder.mView.findViewById(R.id.current_service_pay_btn).setVisibility(View.INVISIBLE);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                       //pay button listener
                                        viewHolder.mView.findViewById(R.id.current_service_pay_btn)
                                                .setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        sendToPayment(balance, listUserID, currentUserID);
                                                    }
                                                });

                                        viewHolder.mView.findViewById(R.id.current_service_completed_btn).setVisibility(View.INVISIBLE);

                                        jobinforef.child(currentUserID).child(listUserID).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.hasChild("paid")){
                                                    viewHolder.mView.findViewById(R.id.current_service_pay_btn).setVisibility(View.INVISIBLE);
                                                    viewHolder.mView.findViewById(R.id.current_service_completed_btn).setVisibility(View.VISIBLE);

                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                        //complete button listener
                                        viewHolder.mView.findViewById(R.id.current_service_completed_btn)
                                                .setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        viewHolder.mView.findViewById(R.id.current_service_completed_btn).setEnabled(true);
                                                        completeTask(listUserID);
                                                        //Toast.makeText(CurrentServiceActivity.this, "Task is done.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });



                                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view)
                                            {

                                                CharSequence options[] = new CharSequence[]
                                                        {
                                                                "Cancel Task"

                                                        };

                                                AlertDialog.Builder builder = new AlertDialog.Builder(CurrentServiceActivity.this);
                                                builder.setTitle("Select Options");

                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int pos) {


                                                        if (pos == 0) {


                                                        }
                                                    }
                                                });
                                                builder.show();
                                            }

                                        });

                                    }
                                    else if(requestType.equals("received"))
                                    {
                                        userRef.child(listUserID).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                String name = dataSnapshot.child("username").getValue().toString();



                                                viewHolder.setUsername("You are working for: "+name);
                                                viewHolder.mView.findViewById(R.id.current_service_profile_image)
                                                        .setVisibility(View.INVISIBLE);

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });



                                        //message button listener
                                        viewHolder.mView.findViewById(R.id.current_service_message_btn)
                                                .setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        sendToChat(listUserID);
                                                    }
                                                });

                                        //complete button listener
                                        viewHolder.mView.findViewById(R.id.current_service_completed_btn)
                                                .setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        viewHolder.mView.findViewById(R.id.current_service_completed_btn).setEnabled(false);
                                                        Toast.makeText(CurrentServiceActivity.this, "Task is done. Please await payment.", Toast.LENGTH_SHORT).show();
                                                        jobComplete(currentUserID,listUserID);
                                                    }
                                                });

                                        //hide pay button
                                        viewHolder.mView.findViewById(R.id.current_service_pay_btn).setVisibility(View.INVISIBLE);

                                        //alert dialog for cancelling a service
                                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view)
                                            {

                                                CharSequence options[] = new CharSequence[]
                                                        {
                                                                "Cancel Task"
                                                        };

                                                AlertDialog.Builder builder = new AlertDialog.Builder(CurrentServiceActivity.this);
                                                builder.setTitle("Select Options");

                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int pos) {

                                                        if (pos == 0) {
                                                            cancelTask(listUserID);
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

        serviceList.setAdapter(firebaseRecyclerAdapter);
    }

    private void sendToPayment(float balance, String listUserID, String currentUserID) {
        Intent paymentIntent = new Intent(getApplicationContext(), ChargePayment.class);
        paymentIntent.putExtra("balance", balance);
        paymentIntent.putExtra("contractor", listUserID);
        paymentIntent.putExtra("customer", currentUserID);
        startActivity(paymentIntent);
    }

    private void sendToChat(String listUserID)
    {
        Intent chatIntent = new Intent(getApplicationContext(), ChatActivity.class);
        chatIntent.putExtra("visitUserID", listUserID);
        startActivity(chatIntent);
    }

    private void finishTask(String listUserID) {
    }

    private void cancelTask(String listUserID) {


    }

    private void jobComplete(String currentUserID, String listUserID){
        jobinforef.child(currentUserID).child(listUserID).child("completed").setValue(true);
    }

    private void completeTask(final String listUserID)
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM-dd-yyyy");
        final String saveCurrentDate = currentDate.format(calendar.getTime());

        postsRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists())
                {

                    final String jobTitle = dataSnapshot.child("job_title").getValue().toString();
                    final String jobDescription = dataSnapshot.child("job_description").getValue().toString();
                    final String jobLocation = dataSnapshot.child("job_location").getValue().toString();
                    final String category = dataSnapshot.child("service_category").getValue().toString();

                    historyRef.child(currentUserID).child(listUserID).child("job_title").setValue(jobTitle);
                    historyRef.child(currentUserID).child(listUserID).child("job_description").setValue(jobDescription);
                    historyRef.child(currentUserID).child(listUserID).child("job_location").setValue(jobLocation);
                    historyRef.child(currentUserID).child(listUserID).child("service_category").setValue(category);
                    historyRef.child(currentUserID).child(listUserID).child("request_type").setValue("sent");
                    historyRef.child(currentUserID).child(listUserID).child("date").setValue(saveCurrentDate)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    historyRef.child(listUserID).child(currentUserID).child("job_title").setValue(jobTitle);
                                    historyRef.child(listUserID).child(currentUserID).child("job_description").setValue(jobDescription);
                                    historyRef.child(listUserID).child(currentUserID).child("job_location").setValue(jobLocation);
                                    historyRef.child(listUserID).child(currentUserID).child("service_category").setValue(category);
                                    historyRef.child(listUserID).child(currentUserID).child("request_type").setValue("received");
                                    historyRef.child(listUserID).child(currentUserID).child("date").setValue(saveCurrentDate)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    serviceDeleteRef.child(currentUserID).child(listUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                serviceDeleteRef.child(listUserID).child(currentUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            postsRef.child(currentUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        Toast.makeText(CurrentServiceActivity.this, "Job finished.", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                }
                                                                            });
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



    public static class ServicesViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public ServicesViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setEstimated_hour(String estimated_hour){

            TextView hour = (TextView) mView.findViewById(R.id.current_service_hours);
            hour.setText(estimated_hour +"hr");
        }
        public void setHourly_rate(String hourly_rate) {
            TextView rate = (TextView) mView.findViewById(R.id.current_service_price);
            rate.setText("$"+hourly_rate +"per/hr");

        }

        public void setUsername(String name) {
            TextView username = (TextView) mView.findViewById(R.id.current_service_username);
            username.setText(name);
        }

        public void setUser_Image(String image, Context ctx) {
            CircleImageView profileImage = (CircleImageView) mView.findViewById(R.id.current_service_profile_image);
            Picasso.with(ctx).load(image).into(profileImage);
        }

        public void setDate(String date) {
            TextView serviceDate = (TextView) mView.findViewById(R.id.current_service_date);
            serviceDate.setText(date);
        }

    }
}
