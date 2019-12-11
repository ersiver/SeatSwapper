package com.breiter.seatswapper.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.breiter.seatswapper.R;
import com.breiter.seatswapper.adapter.PassengerAdapter;
import com.breiter.seatswapper.model.Flight;
import com.breiter.seatswapper.model.FlightPassenger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PassengerActivity extends AppCompatActivity {

    private FirebaseUser currentUser;
    private DatabaseReference rootRef;

    private RecyclerView userRecyclerView;
    private List<FlightPassenger> passengersList;
    private PassengerAdapter passengerAdapter;
    private String flightId;
    private EditText searchEditText;
    private TextView dateTextView;
    private TextView timeTextView;
    private TextView departureTextView;
    private TextView destinationTextView;
    private TextView flightNoTextView;
    private TextView airlineTextView;
    private ImageView goBackImageView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger);

        flightId = getIntent().getStringExtra("flightId");

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        rootRef = FirebaseDatabase.getInstance().getReference();

        passengersList = new ArrayList<>();

        bindViews(); //1

        displayPassengers(); //2

        displayFlightInfo(); //3


        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                displaySearchedSeats(charSequence.toString().toLowerCase().trim()); //4

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        goBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish(); //finish and go back to previous activity


            }
        });

    }



    //1.
    private void bindViews() {

        userRecyclerView = findViewById(R.id.usersRecyclerView);

        userRecyclerView.setHasFixedSize(true);

        userRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        searchEditText = findViewById(R.id.searchEditText);

        dateTextView = findViewById(R.id.dateTextView);

        timeTextView = findViewById(R.id.timeTextView);

        departureTextView = findViewById(R.id.departureTextView);

        destinationTextView = findViewById(R.id.destinationTextView);

        flightNoTextView = findViewById(R.id.flightNoTextView);

        airlineTextView = findViewById(R.id.airlineTextView);

        goBackImageView = findViewById(R.id.goBackImageView);

    }


    /*
    2. Retrieve all passengers of the flight from Firebase
    Display passengers, except current user and those users,
    who already sent their requests and are awaiting response
    */
    private void displayPassengers() {

        DatabaseReference passengersRef = rootRef.child("FlightPassengers").child(flightId);

        passengersRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (searchEditText.getText().toString().equals("")) {

                    passengersList.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        FlightPassenger passenger = snapshot.getValue(FlightPassenger.class);

                        assert passenger != null;

                        assert currentUser != null;

                        if (!passenger.getPassengerId().equals(currentUser.getUid()) && (!passenger.isIsawaiting()))

                            passengersList.add(passenger);

                    }

                    Collections.sort(passengersList);

                    passengerAdapter = new PassengerAdapter(PassengerActivity.this, passengersList);

                    userRecyclerView.setAdapter(passengerAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    //3. Display information about the flight
    private void displayFlightInfo() {

        DatabaseReference flightRef = rootRef.child("Flights").child(flightId);

        flightRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Flight flight = dataSnapshot.getValue(Flight.class);

                departureTextView.setText(flight.getDeparture() + " ");

                destinationTextView.setText(flight.getDestination());

                dateTextView.setText(flight.getDate()+ ", ");

                timeTextView.setText(flight.getTime()+ ", ");

                flightNoTextView.setText(flight.getFlightNumber() + " ");

                airlineTextView.setText(flight.getAirlines());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    //4. Display passengers with particular seat no.
    private void displaySearchedSeats(final String searchText) {


        DatabaseReference passengersRef = rootRef.child("FlightPassengers").child(flightId);

        passengersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                passengersList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    FlightPassenger passenger = snapshot.getValue(FlightPassenger.class);

                    assert passenger != null;

                    assert currentUser != null;

                    if (passenger.getSearch().contains(searchText)) {

                        if (!passenger.getPassengerId().equals(currentUser.getUid()) && (!passenger.isIsawaiting()))

                            passengersList.add(passenger);

                    }
                }

                Collections.sort(passengersList);

                passengerAdapter = new PassengerAdapter(PassengerActivity.this, passengersList);

                userRecyclerView.setAdapter(passengerAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
