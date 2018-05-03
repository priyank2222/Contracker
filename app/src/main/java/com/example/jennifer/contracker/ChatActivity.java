package com.example.jennifer.contracker;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    // UI component declaration
    private Toolbar mToolbar;
    private TextView chatBarUsername;
    private ImageButton sendMessageBtn;
    private ImageButton sendImageBtn;
    private EditText inputMessageTxt;
    private RecyclerView messagesList;
    private MessageAdapter messageAdapter;
    private List<Messages> userMessagesList = new ArrayList<>();

    // firebase initialization
    private DatabaseReference rootRef;
    private FirebaseAuth mAuth;
    String currentUserID;
    String receiverID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        
        //initialize firebase and UI components
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();


        //view the receiver id information
        receiverID = getIntent().getExtras().getString("visitUserID");

        rootRef = FirebaseDatabase.getInstance().getReference();

        mToolbar = (Toolbar) findViewById(R.id.chat_toolbar);

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        // call layout inflater service
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.char_bar_custom_view,null);
        actionBar.setCustomView(actionBarView);

        chatBarUsername =(TextView) findViewById(R.id.chat_bar_username);
        sendImageBtn =(ImageButton)findViewById(R.id.chat_send_image_btn);
        sendMessageBtn =(ImageButton)findViewById(R.id.chat_send_message_btn);
        inputMessageTxt = (EditText) findViewById(R.id.chat_input_text);

        messagesList =(RecyclerView) findViewById(R.id.messages_list);
        messageAdapter = new MessageAdapter(getApplicationContext(),userMessagesList);
        messagesList.setLayoutManager(new LinearLayoutManager(this));
        messagesList.setAdapter(messageAdapter);

        fetchMessages();


        // firebase database retreival
        rootRef.child("Users").child(receiverID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("username").getValue().toString();
                chatBarUsername.setText(name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        // button onclick listener
       sendMessageBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view)
           {
               sendMessage();
           }
       });

        //Toast.makeText(this, receiverID, Toast.LENGTH_SHORT).show();
    }

    // fetch data from database
    private void fetchMessages()
    {
        rootRef.child("Messages").child(currentUserID).child(receiverID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                Messages message = dataSnapshot.getValue(Messages.class);

                userMessagesList.add(message);
                messageAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //send message method to get input from textedit and save into firebase database
    private void sendMessage()
    {
        String messageText = inputMessageTxt.getText().toString();
        //check if edit text is empty
        if(TextUtils.isEmpty(messageText)){
            Toast.makeText(ChatActivity.this, "Please write your message.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            // database path
            String messageSenderRef = "Messages/"+currentUserID +"/"+ receiverID;

            String messageReceiverRef = "Messages/"+receiverID +"/"+ currentUserID;

            DatabaseReference userMessageKey = rootRef.child("Messages").child(currentUserID)
                    .child(receiverID).push();

            String userPushID = userMessageKey.getKey();

            // put data inside hasmap
            Map messageTextBody = new HashMap();
            messageTextBody.put("message",messageText);
            messageTextBody.put("seen",false);
            messageTextBody.put("type","text");
            messageTextBody.put("time", ServerValue.TIMESTAMP);
            messageTextBody.put("from", currentUserID);


            Map messageBodyDetail = new HashMap();
            messageBodyDetail.put(messageSenderRef+ "/"+userPushID,messageTextBody);
            messageBodyDetail.put(messageReceiverRef+ "/"+userPushID,messageTextBody);

            //put hashmap data into firebase database
            rootRef.updateChildren(messageBodyDetail, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
                {
                    if(databaseError != null)
                    {
                        Log.d("Chat_log",databaseError.getMessage().toString());
                    }

                    inputMessageTxt.setText("");
                }
            });
        }
    }
}
