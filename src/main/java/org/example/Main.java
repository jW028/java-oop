package org.example;

import java.io.*;
import java.util.Map;
import java.util.HashMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        Map<String,User> customers = JsonDataHandler.loadCustomers();
        Map<String,Admin> admins = JsonDataHandler.loadAdmins();

        UserMenu userMenu = new UserMenu(customers, admins);
        userMenu.displayMenu();


    }

}

