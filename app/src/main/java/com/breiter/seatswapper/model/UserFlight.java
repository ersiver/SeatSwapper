package com.breiter.seatswapper.model;

import java.util.Date;

public class UserFlight {

    private String flightId;
    private Long timestamp;

    public UserFlight(String flightId, Long timestamp) {
        this.flightId = flightId;
        this.timestamp = timestamp;
    }

    public UserFlight() {

    }

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}