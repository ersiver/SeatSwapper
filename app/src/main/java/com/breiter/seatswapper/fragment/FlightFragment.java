package com.breiter.seatswapper.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.breiter.seatswapper.R;
import com.breiter.seatswapper.activity.SubmitFlightActivity;
import com.breiter.seatswapper.adapter.FlightAdapter;
import com.breiter.seatswapper.model.UserFlight;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FlightFragment extends Fragment {

    private FirebaseUser currentUser;
    private DatabaseReference rootRef;
    private RecyclerView flightsRecyclerView;
    private List<UserFlight> flightList;
    private FlightAdapter flightAdapter;
    private TextView noFlightsDescrTextView;
    private TextView noFlightsTextView;
    private TextView addFlight;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_flight, container, false);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();
        flightList = new ArrayList<>();

        bindViews(view);  //1
        getUserFlights(); //2
        updateNoFlightTextViews(); //3

        //Redirect to new activiy
        addFlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireContext(), SubmitFlightActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    //1.
    private void bindViews(View view) {

        noFlightsDescrTextView = view.findViewById(R.id.noFlightsDescrTextView);
        noFlightsTextView = view.findViewById(R.id.noFlightsTextView);
        addFlight = view.findViewById(R.id.addFlight);
        flightsRecyclerView = view.findViewById(R.id.flightRecyclerView);
        flightsRecyclerView.setHasFixedSize(true);
        flightsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    //2. Get current users upcoming flights from Firebase and display on a list
    private void getUserFlights() {

        Query query = rootRef.child("UserFlights").child(currentUser.getUid()).orderByChild("timestamp");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                flightList.clear();
                deletePastFlights(dataSnapshot); //2a
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserFlight userFlight = snapshot.getValue(UserFlight.class);
                    assert userFlight != null;
                    flightList.add(userFlight);
                }

                updateNoFlightTextViews();
                flightAdapter = new FlightAdapter(requireContext(), flightList);
                flightsRecyclerView.setAdapter(flightAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //2a
    private void deletePastFlights(DataSnapshot dataSnapshot) {

        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            UserFlight userFlight = snapshot.getValue(UserFlight.class);
            assert userFlight != null;
            Timestamp timestamp = new Timestamp(userFlight.getTimestamp());
            Date flightDate = new Date(timestamp.getTime());

            if (flightDate.before(Calendar.getInstance().getTime()))
                rootRef.child("UserFlights").child(currentUser.getUid()).child(userFlight.getFlightId()).setValue(null);
        }
    }


    //3. If there are no flight reveal the "No FLight" info, otherwise hide it
    private void updateNoFlightTextViews() {

        if (flightList.isEmpty()) {
            noFlightsDescrTextView.setVisibility(View.VISIBLE);
            noFlightsTextView.setVisibility(View.VISIBLE);

        } else {
            noFlightsDescrTextView.setVisibility(View.INVISIBLE);
            noFlightsTextView.setVisibility(View.INVISIBLE);
        }
    }


}
