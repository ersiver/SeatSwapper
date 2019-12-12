package com.breiter.seatswapper;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.breiter.seatswapper.model.FlightPassenger;
import com.breiter.seatswapper.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class RequestSubmitter {

    private DatabaseReference rootRef;

    private Context context;
    private FlightPassenger flightPassenger;
    private String requesterId;
    private String responderId;
    private String flightId;
    private TextView recipientNameTextView;
    private TextView senderSeatTextView;
    private TextView recipientNameSecTextView;
    private TextView recipientSeatTextView;
    private Button cancelButton;
    private Button yesButton;

    public RequestSubmitter(Context context, FlightPassenger passenger, String requesterId) {

        this.context = context;
        this.flightPassenger = passenger;
        this.requesterId = requesterId;
        responderId = flightPassenger.getPassengerId();
        flightId = flightPassenger.getFlightId();
        rootRef = FirebaseDatabase.getInstance().getReference();
    }

    public void displayRequestDialog() {

        final Dialog dialog = new Dialog(context);
        setupDialog(dialog); //1

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMessagesDatabase();   //2
                updatePassengersDatabase(); //3
                Toast.makeText(context, "Your request was submitted!", Toast.LENGTH_SHORT).show();
                dialog.cancel();

            }
        });

        dialog.show();

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    //1.
    private void setupDialog(Dialog dialog) {

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_box_send_request);
        cancelButton = dialog.findViewById(R.id.cancelButton);
        yesButton = dialog.findViewById(R.id.yesButton);
        recipientNameTextView = dialog.findViewById(R.id.recipientNameTextView);
        senderSeatTextView = dialog.findViewById(R.id.senderSeatTextView);
        recipientNameSecTextView = dialog.findViewById(R.id.recipientNameSecTextView);
        recipientSeatTextView = dialog.findViewById(R.id.responderSeatNumTextView);

        //Display information on the dialog text views
        recipientSeatTextView.setText(flightPassenger.getPassengerSeat());
        displayRecipientName(); //1a
        displaySenderSeat();    //1b

    }

    //1a. Get recipient name from Firebase and display on a text views
    private void displayRecipientName() {

        rootRef.child("Users").child(responderId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    recipientNameTextView.setText(user.getUsername());
                    recipientNameSecTextView.setText(user.getUsername() + "'s ");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //1b. Get sender seat no. from Firebase and display on a text view
    private void displaySenderSeat() {

        rootRef.child("FlightPassengers").child(flightId).child(requesterId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FlightPassenger flightPassenger = dataSnapshot.getValue(FlightPassenger.class);
                if (flightPassenger != null)
                    senderSeatTextView.setText(flightPassenger.getPassengerSeat());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //2. Sent a push massage, double for each user to easier retrieve user messages
    private void updateMessagesDatabase() {

        String messageSenderRef = "Messages/" + requesterId;
        String messageReceiverRef = "Messages/" + responderId;
        DatabaseReference userMessageKeyRef = rootRef.child("Messages").child(requesterId).push();
        String messagePushID = userMessageKeyRef.getKey();

        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("messageId", messagePushID);
        messageMap.put("requester", requesterId);
        messageMap.put("responder", responderId);
        messageMap.put("flightId", flightId);
        messageMap.put("timeRequest", System.currentTimeMillis());
        messageMap.put("timeResponse", 0);
        messageMap.put("type", "pending request");
        messageMap.put("isread", false);
        messageMap.put("hide", false);

        Map<String, Object> messageBodyDetails = new HashMap<>();
        messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageMap);
        messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageMap);
        rootRef.updateChildren(messageBodyDetails);
        addResponderSeatToMessage(messagePushID); //2a
        addRequesterSeatToMessage(messagePushID); //2b

    }

    //2a. Retrieve responder seat number and pass it to the message database
    private void addResponderSeatToMessage(final String messageId) {

        rootRef.child("FlightPassengers").child(flightId).child(responderId)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        FlightPassenger flightPassenger = dataSnapshot.getValue(FlightPassenger.class);
                        if (flightPassenger != null) {
                            String responderSeat = flightPassenger.getPassengerSeat();

                            Map<String, Object> messageMap = new HashMap<>();
                            messageMap.put("responderSeat", responderSeat);

                            rootRef.child("Messages").child(requesterId).child(messageId).updateChildren(messageMap);
                            rootRef.child("Messages").child(responderId).child(messageId).updateChildren(messageMap);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }


    //2b. Retrieve requester seat number and pass it to the message database
    private void addRequesterSeatToMessage(final String messageId) {

        rootRef.child("FlightPassengers").child(flightId).child(requesterId)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        FlightPassenger flightPassenger = dataSnapshot.getValue(FlightPassenger.class);
                        if (flightPassenger != null) {
                            String requesterSeat = flightPassenger.getPassengerSeat();

                            Map<String, Object> messageMap = new HashMap<>();
                            messageMap.put("requesterSeat", requesterSeat);

                            rootRef.child("Messages").child(requesterId).child(messageId).updateChildren(messageMap);
                            rootRef.child("Messages").child(responderId).child(messageId).updateChildren(messageMap);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }


    //3. Set the status "is awaiting" so it prevents sending another request for the same flight
    private void updatePassengersDatabase() {

        Map<String, Object> passengersMap = new HashMap<>();
        passengersMap.put("isawaiting", true);
        rootRef.child("FlightPassengers").child(flightId).child(requesterId).updateChildren(passengersMap);


    }


}
