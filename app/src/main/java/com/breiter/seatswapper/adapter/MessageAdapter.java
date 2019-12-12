package com.breiter.seatswapper.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.breiter.seatswapper.R;
import com.breiter.seatswapper.ResponseSubmitter;
import com.breiter.seatswapper.model.Flight;
import com.breiter.seatswapper.model.FlightPassenger;
import com.breiter.seatswapper.model.Message;
import com.breiter.seatswapper.model.User;
import com.breiter.seatswapper.tool.MessageTimeConverter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private static final int MSG_PENDING = 0;
    private static final int MSG_ACCEPTED = 1;
    private static final int MSG_REJECTED = 2;

    private FirebaseUser currentUser;
    private DatabaseReference rootRef;
    private ResponseSubmitter responseSubmitter;
    private Context context;
    private List<Message> messageList;

    public MessageAdapter(Context context, List<Message> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();
        responseSubmitter = new ResponseSubmitter();

        View view;

        if (viewType == MSG_PENDING)
            view = LayoutInflater.from(context).inflate(R.layout.item_mail_request_pending, parent, false);

        else if (viewType == MSG_ACCEPTED)
            view = LayoutInflater.from(context).inflate(R.layout.item_mail_request_accepted, parent, false);

        else
            view = LayoutInflater.from(context).inflate(R.layout.item_mail_request_rejected, parent, false);

        return new MessageAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.ViewHolder holder, final int position) {

        final Message message = messageList.get(position);
        final String flightId = message.getFlightId();
        displayRequesterName(message, holder);     //1
        displayResponderName(message, holder);     //2
        displaySenderSeat(message, holder);        //3
        displayResponderSeat(message, holder);     //4
        displayDateTimeDetails(message, holder);   //5
        displayFlightDetails(message, holder);     //6
        controlButtonsVisibility(message, holder); //7

        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                responseSubmitter.acceptRequest(context, message, flightId);

            }
        });

        holder.rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                responseSubmitter.rejectRequest(context, message, flightId);

            }
        });

    }

    //1. Retrieve requester name and set it in the relevant text views
    private void displayRequesterName(final Message message, final ViewHolder holder) {

        rootRef.child("Users")
                .child(message.getRequester())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        if (user != null) {
                            if (user.getUserId().equals(message.getRequester())) {
                                String username = user.getUsername();

                                if (message.getRequester().equals(currentUser.getUid())) {
                                    holder.requesterNameLineOneTextView.setText("You");
                                    holder.requesterNameLineTwoTextView.setText("Your");
                                    holder.requesterNameLineThreeTextView.setText("You");

                                } else {
                                    holder.requesterNameLineOneTextView.setText(username);
                                    holder.requesterNameLineTwoTextView.setText(username + "'s");
                                    holder.requesterNameLineThreeTextView.setText(username);
                                }
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    //2. Retrieve responder name and set it in the relevant text views
    private void displayResponderName(final Message message, final ViewHolder holder) {

        rootRef.child("Users")
                .child(message.getResponder())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            String username = user.getUsername();

                            if (message.getResponder().equals(currentUser.getUid())) {
                                holder.responderNameLineOneTextView.setText("you");
                                holder.responderNameLineTwoTextView.setText("you");
                                holder.responderNameLineThreeTextView.setText("You");
                                holder.responderNameLineFourTextView.setText("your");
                                holder.responderNameLineFiveTextView.setText("yours ");

                            } else {
                                holder.responderNameLineOneTextView.setText(username);
                                holder.responderNameLineTwoTextView.setText(username);
                                holder.responderNameLineThreeTextView.setText(username);
                                holder.responderNameLineFourTextView.setText(username + "'s");
                                holder.responderNameLineFiveTextView.setText(username + "'s ");

                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    //3.
    private void displaySenderSeat(final Message message, final ViewHolder holder) {
        holder.requesterSeatNumTextView.setText(message.getRequesterSeat());
        holder.responderNewSeatTextView.setText(message.getRequesterSeat());

    }

    //4.
    private void displayResponderSeat(final Message message, final ViewHolder holder) {
        holder.responderSeatNumTextView.setText(message.getResponderSeat());
        holder.requesterNewSeatTextView.setText(message.getResponderSeat());
    }

    //5.
    private void displayDateTimeDetails(Message message, ViewHolder holder) {

        //Display request time
        holder.dateRequestTextView.setText(MessageTimeConverter.getRequestTime(message));

        //Display response time
        holder.dateResponseTextView.setText(MessageTimeConverter.getResponseTime(message));

    }

    //6.
    private void displayFlightDetails(Message message, final ViewHolder holder) {

        rootRef.child("Flights").child(message.getFlightId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Flight flight = dataSnapshot.getValue(Flight.class);

                if (flight != null) {
                    holder.departureTextView.setText(flight.getDeparture());
                    holder.destinationTextView.setText(flight.getDestination());
                    holder.flightDateTextView.setText(flight.getDate() + ", ");
                    holder.flightTimeTextView.setText(flight.getTime());
                    holder.flightNumberTextView.setText(flight.getFlightNumber() + " ");
                    holder.airlinesTextView.setText(flight.getAirlines());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    /*
    7. Buttons are only visible for a responder user, who may accept or reject the request
    when it's still in the pending phase.
     */
    private void controlButtonsVisibility(Message message, ViewHolder holder) {

        if (message.getType().equals("pending request") && message.getResponder().equals(currentUser.getUid()))
            holder.responseRelativeLayout.setVisibility(View.VISIBLE);
        else
            holder.responseRelativeLayout.setVisibility(View.GONE);
    }


    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {


        if (messageList.get(position).getType().equals("pending request"))
            return MSG_PENDING;

        else if (messageList.get(position).getType().equals("accepted response"))
            return MSG_ACCEPTED;

        else
            return MSG_REJECTED;

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView requesterNameLineOneTextView;
        TextView requesterNameLineTwoTextView;
        TextView requesterNameLineThreeTextView;
        TextView responderNameLineOneTextView;
        TextView responderNameLineTwoTextView;
        TextView responderNameLineThreeTextView;
        TextView responderNameLineFourTextView;
        TextView responderNameLineFiveTextView;
        TextView requesterSeatNumTextView;
        TextView responderSeatNumTextView;
        TextView requesterNewSeatTextView;
        TextView responderNewSeatTextView;
        TextView dateRequestTextView;
        TextView dateResponseTextView;
        TextView statusResponseTextView;
        TextView departureTextView;
        TextView destinationTextView;
        TextView flightDateTextView;
        TextView flightTimeTextView;
        TextView flightNumberTextView;
        TextView airlinesTextView;
        RelativeLayout responseRelativeLayout;
        Button acceptButton;
        Button rejectButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            requesterNameLineOneTextView = itemView.findViewById(R.id.requesterNameLineOneTextView);
            requesterNameLineTwoTextView = itemView.findViewById(R.id.requesterNameLineTwoTextView);
            requesterNameLineThreeTextView = itemView.findViewById(R.id.requesterNameLineThreeTextView);
            responderNameLineOneTextView = itemView.findViewById(R.id.responderNameLineOneTextView);
            responderNameLineTwoTextView = itemView.findViewById(R.id.responderNameLineTwoTextView);
            responderNameLineThreeTextView = itemView.findViewById(R.id.responderNameLineThreeTextView);
            responderNameLineFourTextView = itemView.findViewById(R.id.responderNameLineFourTextView);
            responderNameLineFiveTextView = itemView.findViewById(R.id.responderNameLineFiveTextView);
            requesterSeatNumTextView = itemView.findViewById(R.id.requesterSeatNumTextView);
            responderSeatNumTextView = itemView.findViewById(R.id.responderSeatNumTextView);
            requesterNewSeatTextView = itemView.findViewById(R.id.requesterNewSeatTextView);
            responderNewSeatTextView = itemView.findViewById(R.id.responderNewSeatTextView);
            dateRequestTextView = itemView.findViewById(R.id.dateRequestTextView);
            dateResponseTextView = itemView.findViewById(R.id.dateResponseTextView);
            statusResponseTextView = itemView.findViewById(R.id.statusResponseTextView);
            departureTextView = itemView.findViewById(R.id.departureTextView);
            destinationTextView = itemView.findViewById(R.id.destinationTextView);
            flightDateTextView = itemView.findViewById(R.id.flightDateTextView);
            flightTimeTextView = itemView.findViewById(R.id.flightTimeTextView);
            flightNumberTextView = itemView.findViewById(R.id.flightNumberTextView);
            airlinesTextView = itemView.findViewById(R.id.airlinesTextView);
            responseRelativeLayout = itemView.findViewById(R.id.responseRelativeLayout);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);


        }
    }
}











