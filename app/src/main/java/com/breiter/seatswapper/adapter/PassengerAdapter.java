package com.breiter.seatswapper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.breiter.seatswapper.R;
import com.breiter.seatswapper.RequestSubmitter;
import com.breiter.seatswapper.model.FlightPassenger;
import com.breiter.seatswapper.model.User;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PassengerAdapter extends RecyclerView.Adapter<PassengerAdapter.ViewHolder> {

    private FirebaseUser currentUser;
    private DatabaseReference rootRef;
    private Context context;
    private List<FlightPassenger> passengersList;
    private RequestSubmitter requestSubmitter;

    public PassengerAdapter(Context context, List<FlightPassenger> passengersList) {
        this.context = context;
        this.passengersList = passengersList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_passenger, parent, false);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();
        return new PassengerAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final FlightPassenger selectedPassenger = passengersList.get(position);
        final String userId = selectedPassenger.getPassengerId();
        String flightId = selectedPassenger.getFlightId();

        holder.seatTextView.setText(selectedPassenger.getPassengerSeat()); //display all passengers seats
        displayPassengerDetails(holder, userId);  //1
        controlClickListener(holder, flightId, selectedPassenger); //2

    }


    //1. Display all passengers names and profile images on a list
    private void displayPassengerDetails(final ViewHolder holder, String userId) {

        rootRef.child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    holder.usernameTextView.setText(user.getUsername());
                    if (user.getImageURL().equals("default"))
                        holder.profileImageView.setImageResource(R.drawable.user);

                    else
                        Glide.with(context).load(user.getImageURL()).into(holder.profileImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //2. The user can send only 1 request for the same flight. Once the response comes, they can send another request
    private void controlClickListener(final ViewHolder holder, final String flightId, final FlightPassenger selectedPassenger) {

        rootRef.child("FlightPassengers").child(flightId).child(currentUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        FlightPassenger passenger = dataSnapshot.getValue(FlightPassenger.class);

                        if (passenger != null) {
                            final boolean isAwaiting = passenger.isIsawaiting();
                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    if (isAwaiting)
                                        Toast.makeText(context, "You already sent request for that flight!", Toast.LENGTH_SHORT).show();
                                    else {
                                        requestSubmitter = new RequestSubmitter(context, selectedPassenger, currentUser.getUid());
                                        requestSubmitter.displayRequestDialog();

                                    }

                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    @Override
    public int getItemCount() {
        return passengersList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView profileImageView;
        TextView usernameTextView;
        TextView seatTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImageView = itemView.findViewById(R.id.profileImageView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            seatTextView = itemView.findViewById(R.id.seatTextView);


        }
    }
}