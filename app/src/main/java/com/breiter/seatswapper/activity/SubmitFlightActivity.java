package com.breiter.seatswapper.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.breiter.seatswapper.R;
import com.breiter.seatswapper.tool.DatePickerDialog;
import com.breiter.seatswapper.tool.FlightInputChecker;
import com.breiter.seatswapper.tool.TimePickerManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubmitFlightActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference rootRef;
    private FirebaseUser currentUser;

    private FlightInputChecker flightChecker;
    private DatePickerDialog dateManager;
    private TimePickerManager timeManager;

    private Button submitFlightButton;
    private EditText flightNumberEditText;
    private EditText dateEditTetxiView;
    private EditText timeEditTextView;
    private EditText seatNumberEditText;
    private AutoCompleteTextView departureEditTetxView;
    private AutoCompleteTextView destinationEditTextView;
    private AutoCompleteTextView airlinesEditTetxView;
    private List<EditText> editTextList;

    private String flightId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_flight);

        rootRef = FirebaseDatabase.getInstance().getReference();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        bindViews(); //1

        displayDatePickerDialog();  //2

        displayTimePickerDialog();  //3

        makeAirportsAutocomplete(); //4

        makeAirlinesAutocomplete(); //5

        checkUserInputs(); //6

        addViewsToList(); //7

        setClickListeners(); //8

    }


    //1.
    private void bindViews() {

        submitFlightButton = findViewById(R.id.submitFlightButton);

        submitFlightButton.setEnabled(false);

        flightNumberEditText = findViewById(R.id.flightNumberEditText);

        dateEditTetxiView = findViewById(R.id.dateEditTetxiView);

        timeEditTextView = findViewById(R.id.timeEditTetxView);

        departureEditTetxView = findViewById(R.id.departureEditTetxiView);

        destinationEditTextView = findViewById(R.id.destinationEditTetxiView);

        airlinesEditTetxView = findViewById(R.id.airlinesEditTetxiView);

        seatNumberEditText = findViewById(R.id.seatNumberEditText);

    }



    //2.
    private void displayDatePickerDialog() {

        dateManager = new DatePickerDialog(SubmitFlightActivity.this, dateEditTetxiView);

        dateManager.pickDate();
    }



    //3.
    private void displayTimePickerDialog() {

        timeManager = new TimePickerManager(SubmitFlightActivity.this, timeEditTextView);

        timeManager.pickTime();
    }



    //4.
    private void makeAirportsAutocomplete() {

        String[] airports = getResources().getStringArray(R.array.list_of_airports);

        ArrayAdapter<String> adapterCities = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, airports);

        departureEditTetxView.setAdapter(adapterCities);

        departureEditTetxView.setThreshold(2);

        destinationEditTextView.setAdapter(adapterCities);

        destinationEditTextView.setThreshold(2);
    }



    //5.
    private void makeAirlinesAutocomplete() {

        String[] airlines = getResources().getStringArray(R.array.list_of_airlines);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, airlines);

        airlinesEditTetxView.setAdapter(adapter);

        airlinesEditTetxView.setThreshold(2);

    }



    //6.
    private void checkUserInputs() {

        flightChecker = new FlightInputChecker(submitFlightButton);

        flightChecker.checkInput(flightNumberEditText);

        flightChecker.checkInput(seatNumberEditText);

        flightChecker.checkInput(dateEditTetxiView);

        flightChecker.checkInput(timeEditTextView);

        flightChecker.checkInput(departureEditTetxView);

        flightChecker.checkInput(destinationEditTextView);

        flightChecker.checkInput(airlinesEditTetxView);

    }



    //7. Used to clear all EditTextViews
    private void addViewsToList() {

        editTextList = new ArrayList<>();

        editTextList.add(flightNumberEditText);

        editTextList.add(dateEditTetxiView);

        editTextList.add(timeEditTextView);

        editTextList.add(departureEditTetxView);

        editTextList.add(destinationEditTextView);

        editTextList.add(airlinesEditTetxView);

        editTextList.add(seatNumberEditText);
    }



    //8.
    private void setClickListeners() {
        submitFlightButton.setOnClickListener(this);
        findViewById(R.id.mainLayout).setOnClickListener(this);
        findViewById(R.id.toolbarLayout).setOnClickListener(this);
        findViewById(R.id.goBackImageView).setOnClickListener(this);

    }



    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.submitFlightButton)
            updateFlightDatabase(); //9.


        else if (view.getId() == R.id.toolbarLayout || view.getId() == R.id.mainLayout)
            dismissKeyboard(); //10


        else if (view.getId() == R.id.goBackImageView)
            finish();

    }


    //9. Once submit-button is clicked, the new Flight is created in Flirebase database
    public void updateFlightDatabase() {

        flightId = flightChecker.getUniqeId();

        Map<String, String> flightMap = new HashMap<>();

        flightMap.put("flightId", flightId);

        flightMap.put("departure", flightChecker.getDeparture());

        flightMap.put("destination", flightChecker.getDestination());

        flightMap.put("airlines", flightChecker.getAirlines());

        flightMap.put("flightNumber", flightChecker.getFlightNumber());

        flightMap.put("date", flightChecker.getDate());

        flightMap.put("time", flightChecker.getTime());


        flightId = flightChecker.getUniqeId();

        rootRef.child("Flights").child(flightId).setValue(flightMap).addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    updateUserFlightDatabase(); //9a

                    updatePassengersDatabase(); //9b

                    Toast.makeText(SubmitFlightActivity.this, "Your flight was " +
                            "successfully submitted! You can add another flight", Toast.LENGTH_LONG).show();

                    submitAnotherFlight(); //9c


                } else
                    Toast.makeText(SubmitFlightActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }


    //9a Update Firebase: add a new flight to the list of current user's flights
    private void updateUserFlightDatabase() {

       rootRef.child("UserFlights").child(currentUser.getUid()).child(flightId);

        Map<String, Object> userFlightMap = new HashMap<>();

        userFlightMap.put("flightId", flightId);

        userFlightMap.put("timestamp", dateManager.getDate().getTime());

        rootRef.child("UserFlights").child(currentUser.getUid()).child(flightId).setValue(userFlightMap);

    }


    //9b Update Firebase: add a new passenger to the particular flight
    private void updatePassengersDatabase() {

        Map<String, Object> passengersMap = new HashMap<>();

        passengersMap.put("passengerId", currentUser.getUid());

        passengersMap.put("passengerSeat", flightChecker.getSeatNumber());

        passengersMap.put("search", flightChecker.getSeatNumber().toLowerCase());

        passengersMap.put("isawaiting", false);

        passengersMap.put("flightId", flightId);

        rootRef.child("FlightPassengers").child(flightId).child(currentUser.getUid()).setValue(passengersMap);

    }


    //9c. Once the flight is added, edit texts are cleared and ready to add another flight
    private void submitAnotherFlight() {

        for (EditText editText : editTextList)

            editText.getText().clear();

        submitFlightButton.setEnabled(false);

    }


    //10. Dismiss keyboard once layout or logo are tapped
    public void dismissKeyboard() {

        InputMethodManager inputMethodManager = (InputMethodManager) SubmitFlightActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);

        if (SubmitFlightActivity.this.getCurrentFocus() != null)
            inputMethodManager.hideSoftInputFromWindow(SubmitFlightActivity.this.getCurrentFocus().getWindowToken(), 0);

    }


}


