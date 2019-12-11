package com.breiter.seatswapper.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.breiter.seatswapper.R;
import com.breiter.seatswapper.activity.PassengerActivity;
import com.breiter.seatswapper.model.Flight;
import com.breiter.seatswapper.model.FlightPassenger;
import com.breiter.seatswapper.model.UserFlight;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class FlightAdapter extends RecyclerView.Adapter<FlightAdapter.ViewHolder> {

    private DatabaseReference rootRef;

    private FirebaseUser currentuser;
    private Context context;
    private List<UserFlight> flightList;

    public FlightAdapter(Context context, List<UserFlight> flightList) {
        this.context = context;
        this.flightList = flightList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_flight, parent, false);

        rootRef = FirebaseDatabase.getInstance().getReference();

        currentuser = FirebaseAuth.getInstance().getCurrentUser();

        return new FlightAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final UserFlight userFlight = flightList.get(position);

        displayUserSeat(holder, userFlight.getFlightId());      //1

        displayFlightDetails(holder, userFlight.getFlightId()); //2


        //Redirect to individual flight
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, PassengerActivity.class);

                intent.putExtra("flightId", userFlight.getFlightId());

                context.startActivity(intent);

            }
        });

    }



    //1. Get current user seat no. from Firebase and display on a text view
    private void displayUserSeat(final ViewHolder holder, String flightId) {

        rootRef.child("FlightPassengers")
                .child(flightId)
                .child(currentuser.getUid())
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        FlightPassenger flightPassenger = dataSnapshot.getValue(FlightPassenger.class);

                        holder.seatTextView.setText(flightPassenger.getPassengerSeat());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }



    //2. Get the flight info from Firebase and display on a text view
    private void displayFlightDetails(final ViewHolder holder, String flightId) {

        rootRef.child("Flights").child(flightId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Flight flight = dataSnapshot.getValue(Flight.class);

                        holder.departureTextView.setText(flight.getDeparture());

                        holder.destinationTextView.setText(flight.getDestination());

                        holder.dateTextView.setText(flight.getDate() + ",");

                        holder.timeTextView.setText(flight.getTime() + ",");


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }



    @Override
    public int getItemCount() {
        return flightList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView departureTextView;
        TextView destinationTextView;
        TextView dateTextView;
        TextView timeTextView;
        TextView seatTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            departureTextView = itemView.findViewById(R.id.departureTextView);

            destinationTextView = itemView.findViewById(R.id.destinationTextView);

            dateTextView = itemView.findViewById(R.id.dateTextView);

            timeTextView = itemView.findViewById(R.id.timeTextView);

            seatTextView = itemView.findViewById(R.id.usernameTextView);

        }
    }
}