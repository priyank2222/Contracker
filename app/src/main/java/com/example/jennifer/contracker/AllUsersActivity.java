package com.example.jennifer.contracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersActivity extends AppCompatActivity {


    // UI components declaration
    private Toolbar mToolbar;
    private RecyclerView allUserslists;

    private DatabaseReference allUsersDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        //UI initialization
        mToolbar = (Toolbar)findViewById(R.id.all_users_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        allUserslists = (RecyclerView)findViewById(R.id.all_users_list);

        allUserslists.setHasFixedSize(true);
        allUserslists.setLayoutManager(new LinearLayoutManager(this));

        allUsersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

    }


    @Override
    protected void onStart() {
        super.onStart();


        // firebase recycler view adpater
        FirebaseRecyclerAdapter<AllUsers,AllUsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<AllUsers, AllUsersViewHolder>
                (AllUsers.class,
                        R.layout.all_users_display_layout,
                        AllUsersViewHolder.class,
                        allUsersDatabaseReference

                ) {

            //convert xml and data into view
            @Override
            protected void populateViewHolder(AllUsersViewHolder viewHolder, AllUsers model, int position) {


                //extract date through model class to fill into view
                viewHolder.setUsername(model.getUsername());
                viewHolder.setUser_image(getApplicationContext(),model.getUser_image());
                viewHolder.setExpertise(model.getExpertise());
                final String listUserID = getRef(position).getKey();

                //view click listener
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {

                        CharSequence options[] = new CharSequence[]
                                {
                                        "User Profile"

                                };

                        // alert dialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(AllUsersActivity.this);
                        builder.setTitle("Select Options");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int pos) {

                                if (pos == 0){
                                    Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                                    profileIntent.putExtra("visitUserID", listUserID);
                                    startActivity(profileIntent);
                                }

                            }
                        });
                        builder.show();
                    }

                });

            }


        };

        allUserslists.setAdapter(firebaseRecyclerAdapter);
    }

    //view holder class
    public static class AllUsersViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public AllUsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }


        // set username referencing xml file id
        public void setUsername(String username){
            TextView name = (TextView) mView.findViewById(R.id.all_users_user_name);
            name.setText(username);
        }
        // set user image referencing xml file id
        public void setUser_image(Context ctx, String user_image){
            CircleImageView image = (CircleImageView) mView.findViewById(R.id.all_users_profile_image);
            Picasso.with(ctx).load(user_image).placeholder(R.drawable.user_default).into(image);
        }
        // set user expertise referencing xml file id
        public void setExpertise(String expertise){
            TextView role = (TextView) mView.findViewById(R.id.all_users_expertise);
            role.setText("Role: " + expertise);

        }
    }
}
