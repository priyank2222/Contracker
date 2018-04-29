package com.example.jennifer.contracker;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;

public class CommentDetailActivity extends AppCompatActivity {

     // Ui component declaration
    private EditText commentInput;
    private Button commentSubmitBtn;
    
    // Firebase methods declaration
    private DatabaseReference commentRef;
    private DatabaseReference usernameRef;
    private FirebaseAuth mAuth;
    String currentUserID;
    String receiverUserID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_detail);
         // firebase initialization
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        commentRef = FirebaseDatabase.getInstance().getReference().child("Comments");
        usernameRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        // UI component initialization
        commentInput =(EditText) findViewById(R.id.comment_input);
        commentSubmitBtn = (Button) findViewById(R.id.comment_submit_btn);

       receiverUserID = getIntent().getExtras().getString("visitUserID");

        commentSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                commentSubmitBtn.setEnabled(false);
                
                //add comment data into firebase database
                usernameRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String commentbody = commentInput.getText().toString();
                        String senderusername = dataSnapshot.child("username").getValue().toString();

                        //generating a unique key for each comment
                        DatabaseReference userCommentKey = commentRef.child("Comments").child(receiverUserID).push();
                        String userPushID = userCommentKey.getKey();
                        Date currentDate = new Date(System.currentTimeMillis());
                        String time = currentDate.toString();

                        //put comment data into a hashmap
                        HashMap commentDetail = new HashMap();
                        commentDetail.put("commentbody",commentbody);
                        commentDetail.put("senderusername", senderusername);
                        commentDetail.put("datestamp", time);
                        Map commentBodyDetail = new HashMap();
                        commentBodyDetail.put(receiverUserID+ "/"+userPushID,commentDetail);
                        
                        //add comment data into database
                        commentRef.updateChildren(commentBodyDetail).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(CommentDetailActivity.this, "Comment added.", Toast.LENGTH_SHORT).show();
                                    // after successfully adding comments, send user to history activity
                                    Intent backActivity = new Intent(getApplicationContext(),HistoryActivity.class);
                                    startActivity(backActivity);
                                }
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


//                commentDetail.put("username",jobDescription);
//                commentDetail.put("date",jobLocation);
            }
        });

    }
}
