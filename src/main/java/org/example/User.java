package org.example;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Scanner;

// @JsonTypeInfo(
//         use = JsonTypeInfo.Id.NAME,
//         include = JsonTypeInfo.As.PROPERTY,
//         property = "type",
//         defaultImpl = Customer.class
// )
// @JsonSubTypes({
//         @JsonSubTypes.Type(value=Customer.class, name="customer"),
//         @JsonSubTypes.Type(value=Admin.class, name="admin")
// })

@JsonDeserialize(using = UserDeserializer.class)

public abstract class User {
    private String userID;
    private String username;
    private String email;
    private String password;

    public User() {}

    public User(String userID, String username, String email, String password) {
        this.userID = userID;
        this.username = username;
        this.email = email;
        this.password = password;
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
}
