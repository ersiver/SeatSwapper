package com.breiter.seatswapper;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.breiter.seatswapper.model.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponseSubmitter {

    private DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();


    //1. Accepting the request from MessageAdapter
    public void acceptRequest(final Context context, final Message message, final String flightId) {

        final Map<String, Object> hashMap = new HashMap<>();

        hashMap.put("timeResponse", System.currentTimeMillis());

        hashMap.put("type", "accepted response");

        hashMap.put("isread", false);

        hashMap.put("hide", false);

        rootRef.child("Messages").child(message.getRequester()).child(message.getMessageId()).updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            rootRef.child("Messages").child(message.getResponder()).child(message.getMessageId()).updateChildren(hashMap);

                            updateAwaitingStatus(false, flightId, message.getRequester()); //3

                            replacePassengersSeats(flightId, message.getRequester(), message.getResponder(), //4
                                    message.getResponderSeat(), message.getRequesterSeat());

                            unlockResponderRequests(context, message, flightId); //5

                            unlockRequesterOtherRequests(context, message, flightId); //6


                        }

                    }
                });


    }


    //2. Rejecting the request from MessageAdapter
    public void rejectRequest(final Context context, final Message message, final String flightId) {

        final Map<String, Object> hashMap = new HashMap<>();

        hashMap.put("timeResponse", System.currentTimeMillis());

        hashMap.put("type", "rejected response");

        hashMap.put("isread", false);

        hashMap.put("hide", false);

        rootRef.child("Messages").child(message.getRequester()).child(message.getMessageId()).updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            rootRef.child("Messages").child(message.getResponder()).child(message.getMessageId()).updateChildren(hashMap);

                            updateAwaitingStatus(false, flightId, message.getRequester()); //3.


                        }

                    }
                });


    }


    //3. Unlock "is awaiting" for request status, so the user is no lonegr blocked against sanding request for that flight
    public void updateAwaitingStatus(boolean isawaiting, String flightId, String requesterId) {

        Map<String, Object> passengersMap = new HashMap<>();

        passengersMap.put("isawaiting", isawaiting);

        rootRef.child("FlightPassengers").child(flightId).child(requesterId).updateChildren(passengersMap);


    }


    //4. When the request is approved swap seats of the 2 passengers
    public void replacePassengersSeats(String flightId, String requesterId, String responderId, String newRequesterSeat, String newResponderSeat) {


        HashMap<String, Object> requesterMap = new HashMap<>();

        requesterMap.put("passengerSeat", newRequesterSeat);

        requesterMap.put("search", newRequesterSeat.toLowerCase());

        rootRef.child("FlightPassengers").child(flightId).child(requesterId).updateChildren(requesterMap);


        HashMap<String, Object> responderMap = new HashMap<>();

        responderMap.put("passengerSeat", newResponderSeat);

        responderMap.put("search", newResponderSeat.toLowerCase());

        rootRef.child("FlightPassengers").child(flightId).child(responderId).updateChildren(responderMap);

        Log.i("INFO", "replacePassengers seats done!");


    }


    //5. Once the responder accepts the request, all the other requests for that flight sent to them are rejected
    public void unlockResponderRequests(final Context context, final Message message, final String flightId) {


        rootRef.child("Messages").child(message.getResponder())

                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            Message otherRequests = snapshot.getValue(Message.class);

                            if (otherRequests.getType().equals("pending request")

                                    && otherRequests.getFlightId().equals(flightId)

                                    && (otherRequests.getResponder().equals(message.getResponder()))) {

                                rejectRequest(context, otherRequests, flightId);

                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }



    //6. Once the requester is approved, all their pending request for that flight are rejected
    public void unlockRequesterOtherRequests(final Context context, final Message message, final String flightId) {

        rootRef.child("Messages").child(message.getRequester())

                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            Message otherRequests = snapshot.getValue(Message.class);

                            if (otherRequests.getType().equals("pending request")

                                    && otherRequests.getFlightId().equals(flightId)

                                    && (otherRequests.getResponder().equals(message.getRequester()))) {

                                rejectRequest(context, otherRequests, flightId);

                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

}
