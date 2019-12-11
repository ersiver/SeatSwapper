package com.breiter.seatswapper.model;

public class User {

    private String username;
    private String userId;
    private String imageURL;
    private String email;


    public User(){}

    public User(String username, String userId, String imageURL, String email) {
        this.username = username;
        this.userId = userId;
        this.imageURL = imageURL;
        this.email = email;

    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}


