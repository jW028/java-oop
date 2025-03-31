package org.example;

import java.io.*;
import java.util.Map;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;


public class Main {
    public static void main(String[] args) {
        Map<String,User> customers = JsonDataHandler.loadCustomers();

        UserMenu userMenu = new UserMenu(customers);
        userMenu.displayMenu();

    }

}