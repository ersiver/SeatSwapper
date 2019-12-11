package com.breiter.seatswapper.model;

public class Flight {

    private String flightId;
    private String departure;
    private String destination;
    private String airlines;
    private String flightNumber;
    private String date;
    private String time;

    public Flight(String flightId, String departure, String destination, String airlines, String flightNumber, String date, String time) {
        this.flightId = flightId;
        this.departure = departure;
        this.destination = destination;
        this.airlines = airlines;
        this.flightNumber = flightNumber;
        this.date = date;
        this.time = time;
    }

    public Flight() {
    }

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getAirlines() {
        return airlines;
    }

    public void setAirlines(String airlines) {
        this.airlines = airlines;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
