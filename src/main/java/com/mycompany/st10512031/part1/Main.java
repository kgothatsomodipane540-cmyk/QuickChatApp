/*
 * Main.java
 * This is the main entry point for the QuickChat registration and login application.
 * Author: Student
 * Student Number: ST10512031
 */

package com.mycompany.st10512031.part1;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        Login loginSystem = new Login();
        
        System.out.println("========================================");
        System.out.println("   Welcome to QuickChat Registration    ");
        System.out.println("========================================");
        System.out.println();
        
        // ==================== REGISTRATION ====================
        System.out.println("--- REGISTRATION ---");
        
        System.out.print("Enter your first name: ");
        String firstName = scanner.nextLine();
        
        System.out.print("Enter your last name: ");
        String lastName = scanner.nextLine();
        
        System.out.print("Enter a username (must contain '_' and be no more than 5 characters): ");
        String username = scanner.nextLine();
        
        System.out.print("Enter a password (min 8 chars, 1 capital, 1 number, 1 special char): ");
        String password = scanner.nextLine();
        
        System.out.print("Enter your cell phone number (e.g., +27831234567): ");
        String cellPhone = scanner.nextLine();
        
        String registrationResult = loginSystem.registerUser(
                username, password, cellPhone, firstName, lastName
        );
        
        System.out.println();
        System.out.println(registrationResult);
        System.out.println();
        
        // ==================== LOGIN ====================
        if (registrationResult.contains("User registered successfully")) {
            System.out.println("--- LOGIN ---");
            
            System.out.print("Enter your username: ");
            String loginUsername = scanner.nextLine();
            
            System.out.print("Enter your password: ");
            String loginPassword = scanner.nextLine();
            
            boolean loginSuccess = loginSystem.loginUser(loginUsername, loginPassword);
            String statusMessage = loginSystem.returnLoginStatus(loginSuccess);
            
            System.out.println();
            System.out.println(statusMessage);
            
            // ==================== PART 2: START QUICKCHAT ====================
            if (loginSuccess) {
                runQuickChat(scanner);
            }
            
        } else {
            System.out.println("Registration failed. Please try again.");
        }
        
        scanner.close();
    }

    // ==================== PART 2 METHODS ====================
    
    public static void runQuickChat(Scanner input) {
        System.out.println("\nWelcome to QuickChat.");

        boolean running = true;
        while (running) {
            System.out.println("\nPlease choose an option:");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.print(">> ");
            
            int choice = input.nextInt();
            input.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    sendMessagesFlow(input);
                    break;
                case 2:
                    System.out.println("Coming Soon.");
                    break;
                case 3:
                    System.out.println("Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    public static void sendMessagesFlow(Scanner input) {
        System.out.print("\nHow many messages do you wish to enter? >> ");
        int numMessages = input.nextInt();
        input.nextLine(); 

        for (int i = 0; i < numMessages; i++) {
            System.out.println("\n--- Message " + (i + 1) + " of " + numMessages + " ---");
            
            System.out.print("Enter recipient cell number (with international code): ");
            String recipient = input.nextLine();
            
            System.out.print("Enter your message (max 250 characters): ");
            String messageText = input.nextLine();
            
            while (messageText.length() > 250) {
                System.out.println("Please enter a message of less than 250 characters.");
                System.out.print("Enter your message >> ");
                messageText = input.nextLine();
            }

            Message newMessage = new Message(recipient, messageText);
            
            System.out.println(newMessage.checkRecipientCell());
            System.out.println(newMessage.printMessages());
            
            String actionResult = newMessage.SentMessage();
            System.out.println(actionResult);
        }
        
        System.out.println("\nTotal messages sent: " + Message.returnTotalMessages());
    }
}