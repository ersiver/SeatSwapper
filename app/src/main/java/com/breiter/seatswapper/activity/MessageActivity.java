package com.breiter.seatswapper.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.breiter.seatswapper.R;
import com.breiter.seatswapper.adapter.MessageAdapter;
import com.breiter.seatswapper.model.Message;
import com.breiter.seatswapper.model.User;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    private DatabaseReference rootRef;
    private FirebaseUser currentUser;
    private String userId;
    private String messageId;


    private List<Message> messageList;
    private MessageAdapter messageAdapter;

    private RecyclerView mailRecyclerView;
    private CircleImageView profileImageView;
    private ImageView goBackImageView;
    private TextView usernameTextView;

    private ValueEventListener seenMessageListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        userId = getIntent().getStringExtra("userId");

        messageId = getIntent().getStringExtra("messageId");

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        rootRef = FirebaseDatabase.getInstance().getReference();

        messageList = new ArrayList<>();

        bindViews(); //1.

        displayMailUser(); //2.

        displayMessage(); //3.

        seenMessage(); //4.


        goBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                removeListener(); //5.

                finish(); //finish and go back to previous activity

            }
        });

    }


    //1.
    private void bindViews() {

        mailRecyclerView = findViewById(R.id.mailRecyclerView);

        mailRecyclerView.setHasFixedSize(true);

        mailRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        profileImageView = findViewById(R.id.profileImageView);

        usernameTextView = findViewById(R.id.usernameTextView);

        goBackImageView = findViewById(R.id.goBackImageView);


    }


    //2. Display user details in a toolbar
    private void displayMailUser() {

        rootRef.child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                usernameTextView.setText(user.getUsername());

                if (user.getImageURL().equals("default"))
                    profileImageView.setImageResource(R.drawable.user);
                else
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profileImageView);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    //3. Retrieve the message, attach the adapter to the RecyclerView and display
    private void displayMessage() {

        rootRef.child("Messages").child(currentUser.getUid()).child(messageId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        messageList.clear();

                        Message message = dataSnapshot.getValue(Message.class);

                        assert message != null;

                        messageList.add(message);

                        messageAdapter = new MessageAdapter(MessageActivity.this, messageList);

                        mailRecyclerView.setAdapter(messageAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }



    //4. Add seenMessageListener and update Message on Firebase, when the new message is seen by the current user
    private void seenMessage() {

        seenMessageListener = rootRef.child("Messages")
                .child(currentUser.getUid())
                .child(messageId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {

                            Message message = dataSnapshot.getValue(Message.class);

                            HashMap<String, Object> hashMap = new HashMap<>();

                            hashMap.put("isread", true);

                            if (message.getType().equals("pending request")) {

                                if (message.getResponder().equals(currentUser.getUid()) ) {

                                    dataSnapshot.getRef().updateChildren(hashMap);

                                    updateOtherUserSeenStatus(userId, messageId, hashMap); //4b

                                   // rootRef.child("Messages").child(userId).child(messageId).updateChildren(hashMap);

                                }

                            } else {

                                if (message.getRequester().equals(currentUser.getUid())) {

                                    dataSnapshot.getRef().updateChildren(hashMap);

                                    //rootRef.child("Messages").child(userId).child(messageId).updateChildren(hashMap);

                                    updateOtherUserSeenStatus(userId, messageId, hashMap); //4b


                                }

                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    //4b. Update
    private void updateOtherUserSeenStatus(String userId, String messageId, final HashMap<String, Object> hashMap) {

        rootRef.child("Messages").child(userId).child(messageId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists())
                            dataSnapshot.getRef().updateChildren(hashMap);



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }


    //5. Remove seenMessageListener when leave the chat
    private void removeListener() {

        rootRef.child("Messages")
                .child(currentUser.getUid())
                .child(messageId)
                .removeEventListener(seenMessageListener);

    }


    @Override
    protected void onPause() {
        super.onPause();

        removeListener();


    }

}


