package com.mycompany.st10512031.part1;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        try (Scanner input = new Scanner(System.in)) {
            Login loginSystem = new Login();

            System.out.println("========================================");
            System.out.println("   Welcome to QuickChat Registration    ");
            System.out.println("========================================");
            System.out.println();

            // REGISTRATION
            System.out.println("--- REGISTRATION ---");

            System.out.print("Enter your first name: ");
            String firstName = input.nextLine();

            System.out.print("Enter your last name: ");
            String lastName = input.nextLine();

            System.out.print("Enter a username (must contain '_' and be no more than 5 characters): ");
            String username = input.nextLine();

            System.out.print("Enter a password (min 8 chars, 1 capital, 1 number, 1 special char): ");
            String password = input.nextLine();

            System.out.print("Enter your cell phone number (e.g., +27831234567): ");
            String cellPhone = input.nextLine();

            String registrationResult = loginSystem.registerUser(
                    username, password, cellPhone, firstName, lastName
            );

            System.out.println();
            System.out.println(registrationResult);
            System.out.println();

            // LOGIN
            if (registrationResult.contains("User registered successfully")) {
                System.out.println("--- LOGIN ---");

                System.out.print("Enter your username: ");
                String loginUsername = input.nextLine();

                System.out.print("Enter your password: ");
                String loginPassword = input.nextLine();

                boolean loginSuccess = loginSystem.loginUser(loginUsername, loginPassword);
                String statusMessage = loginSystem.returnLoginStatus(loginSuccess);

                System.out.println();
                System.out.println(statusMessage);

                // PART 2 + 3 START
                if (loginSuccess) {
                    // The registered cell number is the sender of every message
                    Message.setSender(cellPhone);
                    runQuickChat(input);
                }

            } else {
                System.out.println("Registration failed. Please try again.");
            }
        }
    }

    // Keeps asking until the user types a whole number (no crash on text)
    public static int readNumber(Scanner input, String prompt) {
        while (true) {
            System.out.print(prompt);
            String text = input.nextLine().trim();
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    public static void runQuickChat(Scanner input) {
        System.out.println("\nWelcome to QuickChat.");

        // Load any messages stored in earlier runs into the stored array
        Message.loadStoredMessagesFromJson();

        boolean running = true;
        while (running) {
            System.out.println("\nPlease choose an option:");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages");

            int choice = readNumber(input, ">> ");

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
                case 4:
                    storedMessagesMenu(input);
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    public static void sendMessagesFlow(Scanner input) {
        int numMessages = readNumber(input, "\nHow many messages do you wish to enter? >> ");

        // Traditional for loop (Chapter 6)
        for (int i = 1; i <= numMessages; i++) {
            System.out.println("\n--- Message " + i + " of " + numMessages + " ---");

            // Re-ask until the cell number is valid (+27 only)
            System.out.print("Enter recipient cell number (e.g. +27831234567): ");
            String recipient = input.nextLine();
            while (!Message.isValidRecipient(recipient)) {
                System.out.println("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.");
                System.out.print("Enter recipient cell number (e.g. +27831234567): ");
                recipient = input.nextLine();
            }
            System.out.println("Cell phone number successfully captured.");

            System.out.print("Enter your message (max 250 characters): ");
            String messageText = input.nextLine();

            // Traditional while loop (Chapter 6)
            while (messageText.length() > 250) {
                System.out.println("Please enter a message of less than 250 characters.");
                System.out.print("Enter your message >> ");
                messageText = input.nextLine();
            }

            // The loop counter is the message number
            Message newMessage = new Message(i, recipient, messageText);

            System.out.println("\n1) Send Message\n2) Disregard Message\n3) Store Message");
            int choice = readNumber(input, "Choice >> ");

            System.out.println(newMessage.SentMessage(choice));

            // Details only show after the message has been sent
            if (choice == 1) {
                System.out.println("\n" + newMessage.printMessages());
            }
        }

        System.out.println("\nTotal messages sent: " + Message.returnTotalMessages());
    }

    // Menu option 4: reads the JSON file into the stored array, then shows the six features
    public static void storedMessagesMenu(Scanner input) {
        Message.loadStoredMessagesFromJson();

        boolean back = false;
        while (!back) {
            System.out.println("\n--- STORED MESSAGES ---");
            System.out.println("1) Display sender and recipient of all stored messages");
            System.out.println("2) Display the longest stored message");
            System.out.println("3) Search for a message ID");
            System.out.println("4) Search for all messages for a recipient");
            System.out.println("5) Delete a message using its hash");
            System.out.println("6) Display a report of all stored messages");
            System.out.println("7) Back to main menu");

            int choice = readNumber(input, ">> ");

            switch (choice) {
                case 1:
                    System.out.println("\n" + Message.displaySenderAndRecipient());
                    break;
                case 2:
                    String longest = Message.getLongestStoredMessage();
                    if (longest.isEmpty()) {
                        System.out.println("\nNo stored messages.");
                    } else {
                        System.out.println("\nLongest stored message: " + longest);
                    }
                    break;
                case 3:
                    System.out.print("Enter the message ID: ");
                    String id = input.nextLine().trim();
                    Message found = Message.findByMessageID(id);
                    if (found == null) {
                        System.out.println("\nNo message found with that ID.");
                    } else {
                        System.out.println("\nRecipient: " + found.getRecipient());
                        System.out.println("Message: " + found.getMessageText());
                    }
                    break;
                case 4:
                    System.out.print("Enter the recipient cell number: ");
                    String recipient = input.nextLine().trim();
                    String[] results = Message.searchByRecipient(recipient);
                    if (results.length == 0) {
                        System.out.println("\nNo messages found for that recipient.");
                    } else {
                        System.out.println();
                        for (int i = 0; i < results.length; i++) {
                            System.out.println((i + 1) + ") " + results[i]);
                        }
                    }
                    break;
                case 5:
                    System.out.print("Enter the message hash: ");
                    String hash = input.nextLine().trim().toUpperCase();
                    System.out.println("\n" + Message.deleteByHash(hash));
                    break;
                case 6:
                    System.out.println(Message.buildReport(Message.getStoredMessages()));
                    break;
                case 7:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}