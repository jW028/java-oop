package org.example;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.regex.Pattern;

public class Customer extends User {

    private String address;
    private String phoneNum;

    public Customer() {}

    public Customer(String userID, String username, String email, String password, String address, String phoneNum) {
        super(userID, username, email, password);
        this.address = address;
        this.phoneNum = phoneNum;
    }

    public static String generateCustomerID() {
        return UUID.randomUUID().toString();
    }

    public static Customer register(String username, String email, String password, String address, String phoneNum) {
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        if (!isValidPassword(password)) {
            throw new IllegalArgumentException("Password must be at least 8 characters long.");
        }

        String customerID = generateCustomerID();

        return new Customer(customerID, username, email, password, address, phoneNum);
    }

    private static boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email != null && email.matches(regex);
    }

    private static boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNum() {
        return phoneNum;
    }
}
