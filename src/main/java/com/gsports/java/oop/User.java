package com.gsports.java.oop;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = UserDeserializer.class)

public abstract class User {
    protected String userID;
    protected String username;
    protected String email;
    protected String password;

    public User() {}

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }


    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserID() { return userID; }
    public String getUsername() {
        return username;
    }
    public String getEmail() { return email; }
    public Object getPassword() { return password; }

    // These setter methods look good
    public void setUsername(String username) {
        this.username = username;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    

}
