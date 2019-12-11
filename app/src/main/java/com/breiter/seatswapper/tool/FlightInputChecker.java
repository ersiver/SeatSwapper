package com.breiter.seatswapper.tool;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.breiter.seatswapper.R;

public class FlightInputChecker {

    private String flightNumber;
    private String date;
    private String time;
    private String departureAirport;
    private String destination;
    private String airlines;
    private String seatNumber;

    private boolean isFlightNumberValid;
    private boolean isTimeValid;
    private boolean isDateValid;
    private boolean isDepartureAirportValid;
    private boolean isDestinationValid;
    private boolean isAirlinesValid;
    private boolean isSeatNumeberValid;

    private Button submitFlightButton;

    public FlightInputChecker(Button submitFlightButton){

        this.submitFlightButton = submitFlightButton;
    }

    //Validates, if the input is not empty
    public void checkInput(final EditText inputEditText) {

        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

                String flightInfo = inputEditText.getText().toString().trim();
                boolean isValid;

                if (inputEditText.getId() == R.id.seatNumberEditText) {
                    seatNumber = flightInfo;
                    isValid = isInputValid(seatNumber);
                    isSeatNumeberValid = isValid;

                } else if (inputEditText.getId() == R.id.flightNumberEditText) {
                    flightNumber = flightInfo;
                    isValid = isInputValid(flightNumber);
                    isFlightNumberValid = isValid;

                } else if (inputEditText.getId() == R.id.dateEditTetxiView) {
                    date = flightInfo;
                    isValid = isInputValid(date);
                    isDateValid = isValid;

                } else if (inputEditText.getId() == R.id.timeEditTetxView) {
                    time = flightInfo;
                    isValid = isInputValid(time);
                    isTimeValid = isValid;

                } else if (inputEditText.getId() == R.id.departureEditTetxiView) {
                    departureAirport = flightInfo;
                    isValid = isInputValid(departureAirport);
                    isDepartureAirportValid = isValid;

                } else if (inputEditText.getId() == R.id.destinationEditTetxiView) {
                    destination = flightInfo;
                    isValid = isInputValid(destination);
                    isDestinationValid = isValid;

                } else {


                    airlines = flightInfo;
                    isValid = isInputValid(airlines);
                    isAirlinesValid = isValid;
                }

                updateSubmitButton();

            }
        });
    }


    private boolean isInputValid(String userInput) {
        return userInput.length() >= 2 && userInput.length() <= 30;

    }

    // Enable submit button, when all credentials meet requirements
    private void updateSubmitButton() {

        if (isFlightNumberValid && isDateValid && isDepartureAirportValid && isTimeValid
                && isDestinationValid && isAirlinesValid && isSeatNumeberValid)

            submitFlightButton.setEnabled(true);

        else
            submitFlightButton.setEnabled(false);
    }



    //Getters
    public String getUniqeId() {
        String validDate = date.replaceAll("-","");

        String validTime = time.replaceAll(":", "");

       return airlines.trim()+validDate+validTime+flightNumber.trim()+
               departureAirport.trim()+destination.trim();


    }


    public String getTime() {
        return time;
    }


    public String getDate() {
        return date;
    }


    public String getSeatNumber() {
        return seatNumber.toUpperCase();
    }


    public String getFlightNumber() {
        return flightNumber.toUpperCase();
    }


    public String getDeparture() {
        return departureAirport.substring(0,1).toUpperCase().concat(departureAirport.substring(1));
    }


    public String getDestination() {
        return destination.substring(0,1).toUpperCase().concat(destination.substring(1));
    }


    public String getAirlines() {
        return airlines.substring(0,1).toUpperCase().concat(airlines.substring(1));
    }
}