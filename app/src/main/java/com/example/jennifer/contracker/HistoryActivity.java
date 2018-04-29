package com.example.jennifer.contracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView historyList;
    private Toolbar mToolbar;
    private DatabaseReference historyRef;
    private DatabaseReference userRef;
    private DatabaseReference bidRef;
    private FirebaseAuth mAuth;
    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        historyRef = FirebaseDatabase.getInstance().getReference().child("History").child(currentUserID);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        bidRef = FirebaseDatabase.getInstance().getReference().child("Bids");
        mToolbar = (Toolbar) findViewById(R.id.history_toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        historyList = (RecyclerView) findViewById(R.id.history_list);

        historyList.setLayoutManager(new LinearLayoutManager(this));
    }
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<History,HistoryViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<History,HistoryViewHolder>
                        (
                                History.class,
                                R.layout.all_history_display_layout,
                                HistoryViewHolder.class,
                                historyRef
                        ) {


                    @Override
                    protected void populateViewHolder(final HistoryViewHolder viewHolder, History model, int position)
                    {

                        viewHolder.setDate(model.getDate());
                        viewHolder.setJob_title(model.getJob_title());



                        final String listUserID = getRef(position).getKey();
                        DatabaseReference getTypeRef = getRef(position).child("request_type").getRef();

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

                                                viewHolder.setUsername(name);
                                                viewHolder.mView.findViewById(R.id.history_comment_btn).setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        sendUserToComment(listUserID);
                                                    }
                                                });

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
                                                                "Cancel Task"

                                                        };

                                                AlertDialog.Builder builder = new AlertDialog.Builder(HistoryActivity.this);
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
                                        ValueEventListener username = userRef.child(listUserID).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                String name = dataSnapshot.child("username").getValue().toString();

                                                viewHolder.setUsername(name);
                                                viewHolder.mView.findViewById(R.id.history_comment_btn).setVisibility(View.INVISIBLE);
//                                                viewHolder.mView.findViewById(R.id.history_rating_bar).setVisibility(View.INVISIBLE);
//                                                viewHolder.mView.findViewById(R.id.rateContractor).setVisibility(View.INVISIBLE);

                                                LinearLayout lLayout = (LinearLayout) findViewById(R.id.ll_history);
                                                ViewGroup.LayoutParams params = lLayout.getLayoutParams();

                                                params.height = 500;
                                                lLayout.setLayoutParams(params);
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

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

        historyList.setAdapter(firebaseRecyclerAdapter);
    }

    private void sendUserToComment(String listUserID)
    {
        Intent commentActivity = new Intent(getApplicationContext(),CommentDetailActivity.class);
        commentActivity.putExtra("visitUserID",listUserID);
        startActivity(commentActivity);
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public HistoryViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }


        public void setUsername(String name) {
            TextView username = (TextView) mView.findViewById(R.id.history_username);
            username.setText(name);
        }


        public void setDate(String date) {
            TextView serviceDate = (TextView) mView.findViewById(R.id.history_date);
            serviceDate.setText(date);
        }

        public void setJob_title(String job_title) {
            TextView title = (TextView) mView.findViewById(R.id.history_job_title);
            title.setText(job_title);
        }

    }
}

