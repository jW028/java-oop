package com.gsports.java.oop;

import java.util.InputMismatchException;
import java.util.Scanner;

public class MenuUtils {
    private static final Scanner scanner = new Scanner(System.in);

    public static Scanner getScanner() {
        return scanner;
    }

    public static void closeScanner() {
        scanner.close();
    }

    public static int validateDigit(String inputMsg, int minChoice, int maxChoice) {
        int result;
        while (true) {
            try {
                System.out.printf("%s:" + minChoice + "-" + maxChoice + "): ", inputMsg);
                result = scanner.nextInt();
                scanner.nextLine();

                if (result >= minChoice && result <= maxChoice) {
                    return result;
                } else {
                    System.out.println("Invalid input. Please enter a number between " + minChoice + " and " + maxChoice + ".");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear the invalid input
            }
        } 
    }

    public static int validateDigit(int minChoice, int maxChoice) {
        int result;
        while (true) {
            try {
                System.out.print("Enter your choice (" + minChoice + "-" + maxChoice + "): ");
                result = scanner.nextInt();
                scanner.nextLine();

                if (result >= minChoice && result <= maxChoice) {
                    return result;
                } else {
                    System.out.println("Invalid input. Please enter a number between " + minChoice + " and " + maxChoice + ".");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear the invalid input
            }
        } 
    }

    public static String maskPassword(Scanner scanner) {
        if (System.console() != null) {
            return new String(System.console().readPassword());
        } else {
            return scanner.nextLine();
        }
    }

    public static void waitEnter(Scanner scanner) {
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }

    public static void cls() {
        try {
            // Check the operating system
            String os = System.getProperty("os.name").toLowerCase();
            
            if (os.contains("win")) {
                // For Windows
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                // For Unix/Linux/Mac
                System.out.print("\033[H\033[2J");
                System.out.flush();
                
            }
        } catch (Exception e) {
            // Fallback: print many new lines to "clear" screen
            for (int i = 0; i < 100; i++) {
                System.out.println();
            }
            System.out.println("Screen cleared");
        }
    }

}
