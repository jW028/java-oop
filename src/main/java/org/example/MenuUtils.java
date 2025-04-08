package org.example;

import java.util.Scanner;

public class MenuUtils {
    private static final Scanner scanner = new Scanner(System.in);

    public static Scanner getScanner() {
        return scanner;
    }

    public static void closeScanner() {
        scanner.close();
    }

    public static int getMenuChoice(int minChoice, int maxChoice) {
        int choice;
        do {
            System.out.print("Enter your choice (" + minChoice + "-" + maxChoice + "): ");
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next();
            }
            choice = scanner.nextInt();
            scanner.nextLine();

            if (choice < minChoice || choice > maxChoice) {
                System.out.println("Invalid choice. Please enter a number between " + minChoice + " and " + maxChoice + ".");
            }
        } while (choice < minChoice || choice > maxChoice);
        return choice;
    }

    public static String maskPassword(Scanner scanner) {
        if (System.console() != null) {
            return new String(System.console().readPassword());
        } else {
            return scanner.nextLine();
        }
    }
}
