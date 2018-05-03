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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.ServerValue;

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

public class CommentActivity extends AppCompatActivity {

    // Ui component declaration
    private RecyclerView commentList;
    private Toolbar mToolbar;
    // Firebase methods declaration
    private DatabaseReference commentRef;
    private DatabaseReference userRef;
    private DatabaseReference historyRef;
    private DatabaseReference serviceDeleteRef;
    private DatabaseReference bidRef;
    private DatabaseReference postsRef;
    private FirebaseAuth mAuth;
    String currentUserID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

          // firebase initialization
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        commentRef = FirebaseDatabase.getInstance().getReference().child("Comments").child(currentUserID);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        bidRef = FirebaseDatabase.getInstance().getReference().child("Bids");
        historyRef = FirebaseDatabase.getInstance().getReference().child("History");
        serviceDeleteRef =  FirebaseDatabase.getInstance().getReference().child("Services");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        // UI component initialization
        mToolbar = (Toolbar) findViewById(R.id.comment_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        commentList = (RecyclerView) findViewById(R.id.comment_list);

        commentList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
     //firebase recycler adpeter declaration,bind data from the Firebase Realtime Database to app's UI.
        FirebaseRecyclerAdapter<Comments, CommentsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Comments, CommentsViewHolder>
                        (
                                Comments.class,
                                R.layout.all_comment_display_layout,
                                CommentsViewHolder.class,
                                commentRef
                        ) {

             // bind comments object to the viewholder
                    @Override
                    protected void populateViewHolder(final CommentsViewHolder viewHolder, Comments model, int position) {
                        viewHolder.setCommentbody(model.getCommentbody());
                        viewHolder.setSenderUsername(model.getSenderusername());
                        viewHolder.setDatestamp(model.getDatestamp());

                    }
                };

        commentList.setAdapter(firebaseRecyclerAdapter);

    }


 // view holder displaying each item
    public static class CommentsViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public CommentsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        // set comment content
        public void setCommentbody(String commentBody){

            TextView comment = (TextView) mView.findViewById(R.id.all_comment_comments);
            comment.setText(commentBody);
        }
        //set username
        public void setSenderUsername(String senderUsername) {
            TextView commentwriter = (TextView) mView.findViewById(R.id.all_comment_username);
            commentwriter.setText(senderUsername);

        }

        //set time stamp
        public void setDatestamp(String datestamp){
            TextView date = (TextView) mView.findViewById(R.id.all_comment_date);
            date.setText(datestamp);
        }

    }
}
