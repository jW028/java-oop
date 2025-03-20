package org.example;

public class Admin extends User {
    private String accessLevel;

    public Admin(String userID, String username, String email, String password, String accessLevel) {
        super(userID, username, email, password);
        this.accessLevel = accessLevel;
    }

    public void manageUsers() {
        //
    }


}
