package com.breiter.seatswapper.model;

public class Message implements Comparable<Message> {


    private String messageId;
    private String requester;
    private String flightId;
    private String responder;
    private boolean isread;
    private String type;  //pending, accepted, rejected
    private Long timeRequest;
    private Long timeResponse;
    private String responderSeat;
    private String requesterSeat;
    private boolean hide;


    public Message() {
    }

    public Message(String messageId, String requester, String flightId, String responder, boolean isread, String type, Long timeRequest, Long timeResponse, String responderSeat, String requesterSeat, boolean hide) {
        this.messageId = messageId;
        this.requester = requester;
        this.flightId = flightId;
        this.responder = responder;
        this.isread = isread;
        this.type = type;
        this.timeRequest = timeRequest;
        this.timeResponse = timeResponse;
        this.responderSeat = responderSeat;
        this.requesterSeat = requesterSeat;
        this.hide = hide;

    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public String getResponder() {
        return responder;
    }

    public void setResponder(String responder) {
        this.responder = responder;
    }

    public boolean isIsread() {
        return isread;
    }

    public void setIsread(boolean isread) {
        this.isread = isread;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getTimeRequest() {
        return timeRequest;
    }

    public void setTimeRequest(Long timeRequest) {
        this.timeRequest = timeRequest;
    }

    public Long getTimeResponse() {
        return timeResponse;
    }

    public void setTimeResponse(Long timeResponse) {
        this.timeResponse = timeResponse;
    }

    public String getResponderSeat() {
        return responderSeat;
    }

    public void setResponderSeat(String responderSeat) {
        this.responderSeat = responderSeat;
    }

    public String getRequesterSeat() {
        return requesterSeat;
    }

    public void setRequesterSeat(String requesterSeat) {
        this.requesterSeat = requesterSeat;
    }


    public boolean isHide() {
        return hide;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }



    //Display messages in descending time order
    @Override
    public int compareTo(Message other) {

        if (this.timeResponse == 0 && other.timeResponse == 0) {
            return (int) (other.timeRequest - this.timeRequest);


        } else if (this.timeResponse == 0 && other.timeResponse != 0) {
            return (int) (other.timeResponse - this.timeRequest);


        } else if (this.timeResponse != 0 && other.timeResponse == 0) {
            return (int) (other.timeRequest - this.timeResponse);


        } else {
            return (int) (other.timeResponse - this.timeResponse);
        }
    }

}