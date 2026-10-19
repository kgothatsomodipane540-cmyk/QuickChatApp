package com.mycompany.st10512031.part1;

import java.util.Random;
import java.util.Scanner;
import java.util.ArrayList;

public final class Message {

    private final String messageID;
    private final int messageNumber;
    private final String recipient;
    private final String messageText;
    private final String messageHash;
    private static int totalMessagesSent = 0;
    private static final ArrayList<Message> sentMessages = new ArrayList<>();

    public Message(String recipient, String messageText) {
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageID = generateMessageID();
        this.messageNumber = totalMessagesSent + 1;
        this.messageHash = createMessageHash();
    }

    public boolean checkMessageID() {
        return this.messageID.length() <= 10;
    }

    public String checkRecipientCell() {
        String regex = "^(\\+\\d{1,3}|0)\\d{9,10}$"; 
        if (this.recipient.matches(regex)) {
            return "Cell phone number successfully captured.";
        } else {
            return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        }
    }

    public String createMessageHash() {
        String firstTwoID = this.messageID.substring(0, 2);
        String firstWord = "";
        String lastWord = "";
        
        String[] words = this.messageText.trim().split("\\s+");
        if (words.length > 0) {
            firstWord = words[0];
            lastWord = words[words.length - 1];
        }

        String hash = firstTwoID + ":" + this.messageNumber + ":" + (firstWord + lastWord).toUpperCase();
        return hash.replaceAll("[^a-zA-Z0-9:]", "");
    }

    public String SentMessage() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n1) Send Message\n2) Disregard Message\n3) Store Message");
        System.out.print("Choice >> ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> {
                totalMessagesSent++;
                sentMessages.add(this);
                return "Message successfully sent.";
            }
            case 2 -> {
                return "Press 0 to delete the message.";
            }
            case 3 -> {
                return "Message successfully stored.";
            }
            default -> {
                return "Invalid choice.";
            }
        }
    }

    public String printMessages() {
        return "\nMessage ID: " + this.messageID +
               "\nMessage Hash: " + this.messageHash +
               "\nRecipient: " + this.recipient +
               "\nMessage: " + this.messageText;
    }

    public static int returnTotalMessages() {
        return totalMessagesSent;
    }

    private String generateMessageID() {
        Random rand = new Random();
        long id = 1000000000L + (long)(rand.nextDouble() * 9000000000L);
        return String.valueOf(id);
    }

    public String getMessageID() { return messageID; }
    public String getMessageHash() { return messageHash; }
    public String getRecipient() { return recipient; }
    public String getMessageText() { return messageText; }
    public static ArrayList<Message> getSentMessages() { return sentMessages; }
}