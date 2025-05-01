package com.gsports.java.oop;

public class Admin extends User{
    private static int adminCount = 0;
    private String accessLevel;

    public Admin() {}

    public Admin(String userID, String username, String email, String password, String accessLevel) {
        super(username, email, password);
        this.setUserID(generateAdminID()); 
        this.accessLevel = accessLevel;
    }

    public static int getAdminCount() {
        return Admin.adminCount;
    }

    public static void setAdminCount (int adminCount) {
        Admin.adminCount = adminCount;
    }

    public String getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(String accessLevel) {
        this.accessLevel = accessLevel;
    }

    private String generateAdminID() {
        return "A" + String.format("%03d", ++Admin.adminCount);
    }

    // to be implemented
    public String generateReport() {
        return "";
    }
}
