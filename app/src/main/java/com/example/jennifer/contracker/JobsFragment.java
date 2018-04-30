package com.example.jennifer.contracker;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class JobsFragment extends Fragment {

    // Ui component declaration
    private RecyclerView mPostsList;
     // Firebase methods declaration
    private DatabaseReference postsDatabaseRef;
    private DatabaseReference rootDatabaseRef;
    private DatabaseReference postsDeleteRef;
    private FirebaseAuth mAuth;
    String currentUserID;

    private View jobMainView;

    public JobsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        jobMainView = inflater.inflate(R.layout.fragment_jobs, container, false);
         // UI component initialization
        mPostsList = (RecyclerView)jobMainView.findViewById(R.id.posts_list);

         // firebase initialization
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        rootDatabaseRef = FirebaseDatabase.getInstance().getReference();
        postsDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        postsDeleteRef = FirebaseDatabase.getInstance().getReference().child("Posts");


        mPostsList.setLayoutManager(new LinearLayoutManager(getContext()));
        return jobMainView;
    }

    @Override
    public void onStart() {
        super.onStart();


         //firebase recycler adpeter declaration,bind data from the Firebase Realtime Database to app's UI.
        FirebaseRecyclerAdapter<Posts,PostsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Posts, PostsViewHolder>
                (Posts.class,R.layout.all_posts_display_layout,PostsViewHolder.class,postsDatabaseRef) {
            
              // bind posts object to the viewholder
            @Override
            protected void populateViewHolder(final PostsViewHolder viewHolder, Posts model, final int position) {

                viewHolder.setJob_title(model.getJob_title());
                viewHolder.setJob_location(model.getJob_location());
                viewHolder.setJob_description(model.getJob_description());
                viewHolder.setService_category(model.getService_category());
//                viewHolder.setClose_bidding(model.getClose_bidding());


                final String listUserID = getRef(position).getKey();
                viewHolder.mView.findViewById(R.id.all_posts_delete_btn).setVisibility(View.INVISIBLE);

                //check if item position id equals to current user id
                if(listUserID.equals(currentUserID)){
                    viewHolder.mView.findViewById(R.id.all_posts_delete_btn).setVisibility(View.VISIBLE);
                    viewHolder.mView.findViewById(R.id.all_posts_delete_btn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            CharSequence options_confirm_delete[] = new CharSequence[]{"DELETE","CANCEL"};
                            final AlertDialog.Builder builder_delete = new AlertDialog.Builder(getContext());
                            builder_delete.setTitle("Confirm Delete");

                            builder_delete.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    postsDeleteRef.child(currentUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                viewHolder.mView.findViewById(R.id.all_posts_delete_btn).setEnabled(false);
                                                Toast.makeText(getContext(), "Your job post has been deleted.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                }
                            });

                            builder_delete.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });

                            builder_delete.show();
                        }
                    });
                }

                rootDatabaseRef.child("Users").child(listUserID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //check if data exists
                        if(dataSnapshot.hasChild("user_image")){

                            String userImage = dataSnapshot.child("user_image").getValue().toString();

                            //set view content
                            viewHolder.setUser_Image(userImage,getContext());
                        }
                        String userName = dataSnapshot.child("username").getValue().toString();
                        String userExperience = dataSnapshot.child("experience").getValue().toString();
                        viewHolder.setUsername(userName);
                        viewHolder.setExperience(userExperience);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(!listUserID.equals(currentUserID))
                            {
                                CharSequence options[] = new CharSequence[]
                                        {
                                                "Job Details",
                                                "Poster's Profile",
                                                "Chat"

                                        };

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Select Options");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int pos) {

                                        if (pos == 0) {

                                            Intent postDetailIntent = new Intent(getContext(), PostDetailActivity.class);
                                            postDetailIntent.putExtra("receiverID", listUserID);
                                            startActivity(postDetailIntent);
                                        }
                                        if (pos == 1) {

                                            Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                            profileIntent.putExtra("visitUserID", listUserID);
                                            startActivity(profileIntent);
                                        }
                                        if (pos == 2 ){

                                            Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                            chatIntent.putExtra("visitUserID", listUserID);
                                            startActivity(chatIntent);
                                        }

                                    }
                                });
                                builder.show();
                            }
                            else
                            {
                                Toast.makeText(getContext(), "You cannot bid your own job.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });




            }
        };

        mPostsList.setAdapter(firebaseRecyclerAdapter);
    }

    // view holder displaying each item
    public static class PostsViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public PostsViewHolder(View itemView) {

            super(itemView);
            mView = itemView;
        }

        //set job title text
        public void setJob_title(String job_title) {
            TextView jobTitle = (TextView) mView.findViewById(R.id.all_posts_job_title);
            jobTitle.setText(job_title);
        }
        //set job description text
        public void setJob_description(String job_description) {
            TextView description =(TextView)mView.findViewById(R.id.all_posts_description);
            description.setText("Description: \n" +job_description);
        }

        //set job location text
        public void setJob_location(String job_location) {
           TextView location =(TextView) mView.findViewById(R.id.all_posts_location);
           location.setText("Location: "+job_location);
        }

        //set category text
        public void setService_category(String service_category) {
           TextView category = (TextView) mView.findViewById(R.id.all_posts_category);
           category.setText("Category: "+service_category);
        }

//        public void setClose_bidding(String close_bidding) {
//          TextView bid = (TextView)mView.findViewById(R.id.all_posts_close_bid);
//          bid.setText("Close Bidding In: "+close_bidding);
//        }

        //set username text
        public void setUsername(String userName) {
            TextView name = (TextView)mView.findViewById(R.id.all_posts_username);
            name.setText(userName);
        }

        //set user image
        public void setUser_Image(String userImage, Context ctx) {
            final CircleImageView thumb_Image= (CircleImageView) mView.findViewById(R.id.all_posts_profile_image);
            Picasso.with(ctx).load(userImage).placeholder(R.drawable.user_default)
                    .into(thumb_Image);
        }

        //set user experience
        public void setExperience(String userExperience) {
            TextView experience = (TextView)mView.findViewById(R.id.all_posts_experience);
            experience.setText(userExperience);
        }
    }
}
