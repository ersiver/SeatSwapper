package com.breiter.seatswapper.adapter;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.breiter.seatswapper.R;
import com.breiter.seatswapper.activity.MessageActivity;
import com.breiter.seatswapper.model.Message;
import com.breiter.seatswapper.model.User;
import com.breiter.seatswapper.tool.MessageTimeConverter;
import com.breiter.seatswapper.tool.SwipeController;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MailAdapter extends RecyclerView.Adapter<MailAdapter.ViewHolder> {

    private FirebaseUser currentUser;
    private DatabaseReference rootRef;
    private Context context;
    private List<Message> mailsList;

    public MailAdapter(Context context, List<Message> mailsList) {
        this.context = context;
        this.mailsList = mailsList;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_mail_user, parent, false);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        rootRef = FirebaseDatabase.getInstance().getReference();

        return new MailAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Message message = mailsList.get(position);

        final String userId = getUserId(message);   //1

        displayUserDetails(holder, userId);         //2

        displayMessageDescription(holder, message); //3

        setBoldForUnreadMessage(message, holder);   //4

        setSeenIconWhenMessageSeen(message, holder);//5


        //Redirect to individual message & pass the message ID and the user ID
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, MessageActivity.class);

                intent.putExtra("messageId", message.getMessageId());

                intent.putExtra("userId", userId);

                context.startActivity(intent);

            }
        });

    }


    //1.
    private String getUserId(Message message) {

        if (!message.getRequester().equals(currentUser.getUid()))
            return message.getRequester();

        else
            return message.getResponder();

    }


    //2. Retrieve user name and photo and set to the views
    private void displayUserDetails(final ViewHolder holder, final String userId) {

        rootRef.child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                String name = user.getUsername();

                holder.usernameTextView.setText(name);

                if (user.getImageURL().equals("default"))

                    holder.profileImageView.setImageResource(R.drawable.user);

                else
                    Glide.with(context).load(user.getImageURL()).into(holder.profileImageView);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });

    }


    // 3. Retrieve time for each message and set to the the TextViews
    private void displayMessageDescription(final ViewHolder holder, final Message message) {

        String messageDescription;

        if (message.getType().equals("pending request")) { //if message is a request

            if (message.getRequester().equals(currentUser.getUid()))
                messageDescription = "You sent a request";

            else
                messageDescription = "You received a request";

            holder.dateTimeTextView.setText(MessageTimeConverter.getRequestTime(message));


        } else { //If message is a response

            if (message.getRequester().equals(currentUser.getUid()))
                messageDescription = "You received a response";

            else
                messageDescription = "You sent a response";

            holder.dateTimeTextView.setText(MessageTimeConverter.getResponseTime(message));

        }

        holder.lastMsgTextView.setText(messageDescription);

    }


    //4. Display "seen icon" for the author of the message, when the recipient read the message
    private void setSeenIconWhenMessageSeen(Message message, ViewHolder holder) {

        if (message.isIsread()) {

            if (message.getType().equals("pending request")) {
                //If message is a request & the author is a current user

                if (message.getRequester().equals(currentUser.getUid()))
                    holder.seenImageView.setVisibility(View.VISIBLE);

            } else
                //If message is a response & the author is  a current user

                if (message.getResponder().equals(currentUser.getUid()))
                    holder.seenImageView.setVisibility(View.VISIBLE);


        }

    }


    //5. Set the unread message in bold, on the recipient mail list
    private void setBoldForUnreadMessage(Message message, ViewHolder holder) {

        if (!message.isIsread()) {

            if (message.getType().equals("pending request")) {
                //If message is a request & the author is not a current user

                if (!message.getRequester().equals(currentUser.getUid()))
                    holder.lastMsgTextView.setTypeface(Typeface.DEFAULT_BOLD);


            } else
                //If message is a response & the author is not a current user

                if (!message.getResponder().equals(currentUser.getUid()))
                    holder.lastMsgTextView.setTypeface(Typeface.DEFAULT_BOLD);

        }

    }

    // Swipe to delete callback
    public void deleteMessage(int position) {

        Message messageToBeRemoved = mailsList.get(position);

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("hide", true);


        if (messageToBeRemoved.getType().equals("pending request")) {

                mailsList.remove(messageToBeRemoved);

                rootRef.child("Messages").child(currentUser.getUid()).child(messageToBeRemoved.getMessageId()).updateChildren(hashMap);


        } else {

                mailsList.remove(messageToBeRemoved);

                rootRef.child("Messages").child(currentUser.getUid()).child(messageToBeRemoved.getMessageId()).setValue(null);

            }



        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return mailsList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView usernameTextView;

        TextView lastMsgTextView;

        TextView dateTimeTextView;

        CircleImageView profileImageView;

        ImageView seenImageView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            usernameTextView = itemView.findViewById(R.id.usernameTextView);

            lastMsgTextView = itemView.findViewById(R.id.lastMsgTextView);

            dateTimeTextView = itemView.findViewById(R.id.dateTimeTextView);

            profileImageView = itemView.findViewById(R.id.profileImageView);

            seenImageView = itemView.findViewById(R.id.seenImageView);


        }
    }
}
