package org.example;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Scanner;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        defaultImpl = Customer.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value=Customer.class, name="customer")
})

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

    public Boolean login (String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    public void logout() {
        System.out.println("User " + username + " logged out.");
        System.out.println("\033[H\033[2J");
    }

    public static String maskPassword(Scanner scanner) {
        if (System.console() != null) {
            return new String(System.console().readPassword());
        } else {
            return scanner.nextLine();
        }
    }

    public String getUserID() { return userID; }
    public String getUsername() {
        return username;
    }
    public String getEmail() { return email; }
    public Object getPassword() { return password; }
}
