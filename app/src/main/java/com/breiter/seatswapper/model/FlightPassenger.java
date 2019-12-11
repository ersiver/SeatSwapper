package com.breiter.seatswapper.model;

public class FlightPassenger implements Comparable<FlightPassenger> {

    private String passengerId;
    private String passengerSeat;
    private String search;
    private String flightId;
    private boolean isawaiting;


    public FlightPassenger(String passengerId, String passengerSeat, String search, String flightId, boolean isawaiting) {
        this.passengerId = passengerId;
        this.passengerSeat = passengerSeat;
        this.search = search;
        this.flightId = flightId;
        this.isawaiting = isawaiting;
    }

    public FlightPassenger() {
    }

    public String getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }

    public String getPassengerSeat() {
        return passengerSeat;
    }

    public void setPassengerSeat(String passengerSeat) {
        this.passengerSeat = passengerSeat;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public boolean isIsawaiting() {
        return isawaiting;
    }

    public void setIswaiting(boolean isawaiting) {
        isawaiting = isawaiting;
    }

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }




    //To display passenger list in an order they seat
    @Override
    public int compareTo(FlightPassenger other) {

        String seatNumberThis = this.getSearch();

        String seatNumberOther = other.getSearch();

        //If the seat starts with the same numbers, then sort alphabetically in ascending order (12A, 12B etc.)
        if (getDigits(seatNumberThis) == getDigits(seatNumberOther))

            return getNonDigits(seatNumberThis).compareTo(getNonDigits(seatNumberOther));

        //If the seat numbers start with various numbers then sort them in ascending order (1F, 2B, 12C etc)
        else
            return getDigits(seatNumberThis) - getDigits(seatNumberOther);


    }

    //Remove all digits from the string
    private String getNonDigits(String s) {

        return s.replaceAll("\\d", "");

    }

    //Remove all digits from the string
    private int getDigits(String s) {

        String digits = s.replaceAll("\\D+", "");

        return Integer.parseInt(digits);

    }





}


