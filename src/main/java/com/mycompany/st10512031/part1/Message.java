package com.mycompany.st10512031.part1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Message class for the QuickChat app.
 * Holds one message (ID, number, recipient, text and hash) and keeps the
 * static arrays of sent, disregarded and stored messages, hashes and IDs.
 * Also contains the search, delete and report features for stored messages,
 * and saves/reads the stored messages to/from a JSON file.
 */
public class Message {

    // Maximum number of messages each array can hold
    private static final int MAX_MESSAGES = 100;

    private final String messageID;
    private final int messageNumber;
    private final String recipient;
    private final String messageText;
    private final String messageHash;

    // The five arrays, each with a counter for how many slots are used
    private static Message[] sentMessages = new Message[MAX_MESSAGES];
    private static Message[] disregardedMessages = new Message[MAX_MESSAGES];
    private static Message[] storedMessages = new Message[MAX_MESSAGES];
    private static String[] messageHashes = new String[MAX_MESSAGES];
    private static String[] messageIDs = new String[MAX_MESSAGES];

    private static int sentCount = 0;
    private static int disregardedCount = 0;
    private static int storedCount = 0;
    private static int trackedCount = 0; // used by both messageHashes and messageIDs

    // The logged-in user's cell number, shown as the sender
    private static String sender = "Unknown";

    // The JSON file used for storing messages (tests can change this)
    private static String jsonFilePath = "storedMessages.json";

    // Normal constructor: message number comes from the loop counter in Main
    public Message(int messageNumber, String recipient, String messageText) {
        this(generateMessageID(), messageNumber, recipient, messageText);
    }

    // Constructor with a fixed ID, so tests can check exact values (e.g. 00:0:HITONIGHT)
    public Message(String messageID, int messageNumber, String recipient, String messageText) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageHash = createMessageHash();
    }

    public boolean checkMessageID() {
        return this.messageID.length() <= 10;
    }

    // Checks the message length against the 250 character limit
    public String checkMessageLength() {
        int length = this.messageText.length();
        if (length <= 250) {
            return "Message ready to send.";
        }
        return "Message exceeds 250 characters by " + (length - 250) + "; please reduce the size.";
    }

    // Only South African numbers: +27 followed by 9 digits
    public static boolean isValidRecipient(String cell) {
        return cell.matches("^\\+27\\d{9}$");
    }

    public String checkRecipientCell() {
        if (isValidRecipient(this.recipient)) {
            return "Cell phone number successfully captured.";
        } else {
            return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        }
    }

    public final String createMessageHash() {
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

    // 1 = send, 2 = disregard, 3 = store. Fills the matching arrays.
    public String SentMessage(int choice) {
        if (trackedCount >= MAX_MESSAGES) {
            return "Message limit reached.";
        }

        switch (choice) {
            case 1:
                sentMessages[sentCount] = this;
                sentCount++;
                trackMessage();
                return "Message successfully sent.";
            case 2:
                disregardedMessages[disregardedCount] = this;
                disregardedCount++;
                trackMessage();
                return "Press 0 to delete the message.";
            case 3:
                storedMessages[storedCount] = this;
                storedCount++;
                trackMessage();
                saveStoredMessagesToJson(jsonFilePath);
                return "Message successfully stored.";
            default:
                return "Invalid choice.";
        }
    }

    // Adds this message's hash and ID to their arrays
    private void trackMessage() {
        messageHashes[trackedCount] = this.messageHash;
        messageIDs[trackedCount] = this.messageID;
        trackedCount++;
    }

    // Order: ID, hash, recipient, message - each on its own line
    public String printMessages() {
        return "Message ID: " + this.messageID
                + "\nMessage Hash: " + this.messageHash
                + "\nRecipient: " + this.recipient
                + "\nMessage: " + this.messageText;
    }

    // Returns the text shown when a message ID is created
    public String messageIDGenerated() {
        return "Message ID generated: " + this.messageID;
    }

    // ---------------------------------------------------------------
    // JSON storage
    // ---------------------------------------------------------------

    // Writes all stored messages to a JSON file.
    // Source: writing the file with BufferedWriter (write and newLine) follows
    // Farrell (2016), Java Programming, 8th edn, Chapter 13, Figure 13-21 and
    // Table 13-6, pp. 688-689.
    public static void saveStoredMessagesToJson(String filePath) {
        Path file = Paths.get(filePath);

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(Files.newOutputStream(file)))) {

            writeLine(writer, "[");
            for (int i = 0; i < storedCount; i++) {
                Message m = storedMessages[i];
                writeLine(writer, "  {");
                writeLine(writer, "    \"messageID\": \"" + escapeJson(m.messageID) + "\",");
                writeLine(writer, "    \"messageNumber\": " + m.messageNumber + ",");
                writeLine(writer, "    \"messageHash\": \"" + escapeJson(m.messageHash) + "\",");
                writeLine(writer, "    \"recipient\": \"" + escapeJson(m.recipient) + "\",");
                writeLine(writer, "    \"message\": \"" + escapeJson(m.messageText) + "\"");
                writeLine(writer, "  }" + (i < storedCount - 1 ? "," : ""));
            }
            writeLine(writer, "]");
        } catch (IOException e) {
            System.out.println("Could not save messages to JSON file.");
        }
    }

    // Writes one line of text followed by the platform's line separator
    private static void writeLine(BufferedWriter writer, String text) throws IOException {
        writer.write(text, 0, text.length());
        writer.newLine();
    }

    // Reads the default JSON file into the storedMessages array
    public static void loadStoredMessagesFromJson() {
        loadStoredMessagesFromJson(jsonFilePath);
    }

    // Reads the JSON file back into the storedMessages array.
    // Source: file reading with BufferedReader follows Farrell (2016),
    // Java Programming, 8th edn, Chapter 13, Figure 13-19, p. 686.
    public static void loadStoredMessagesFromJson(String filePath) {
        // Start with an empty array, then fill it from the file
        storedMessages = new Message[MAX_MESSAGES];
        storedCount = 0;

        Path file = Paths.get(filePath);
        if (!Files.exists(file)) {
            return; // nothing stored yet
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Files.newInputStream(file)))) {

            String line;
            String id = "";
            int number = 0;
            String recipient = "";
            String text = "";

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("\"messageID\"")) {
                    id = readJsonValue(line);
                } else if (line.startsWith("\"messageNumber\"")) {
                    number = Integer.parseInt(readJsonValue(line));
                } else if (line.startsWith("\"recipient\"")) {
                    recipient = readJsonValue(line);
                } else if (line.startsWith("\"message\"")) {
                    text = readJsonValue(line);
                } else if (line.startsWith("}") && storedCount < MAX_MESSAGES) {
                    // End of one message object: build it and add it to the array
                    storedMessages[storedCount] = new Message(id, number, recipient, text);
                    storedCount++;
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Could not read messages from JSON file.");
        }
    }

    // Takes a line like:   "recipient": "+27831234567",
    // and returns just the value: +27831234567
    private static String readJsonValue(String line) {
        String value = line.substring(line.indexOf(':') + 1).trim();

        if (value.endsWith(",")) {
            value = value.substring(0, value.length() - 1);
        }
        if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
            value = value.substring(1, value.length() - 1);
        }
        // Undo the escaping done in escapeJson
        return value.replace("\\\"", "\"").replace("\\n", "\n").replace("\\\\", "\\");
    }

    private static String escapeJson(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }

    // ---------------------------------------------------------------
    // Stored Messages menu features (Part 3)
    // ---------------------------------------------------------------

    // (a) Sender and recipient of every stored message
    public static String displaySenderAndRecipient() {
        if (storedCount == 0) {
            return "No stored messages.";
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < storedCount; i++) {
            result.append("Sender: ").append(sender)
                  .append(" | Recipient: ").append(storedMessages[i].recipient)
                  .append("\n");
        }
        return result.toString().trim();
    }

    // (b) Longest stored message (compares the length of each message text)
    public static String getLongestStoredMessage() {
        if (storedCount == 0) {
            return "";
        }
        Message longest = storedMessages[0];
        for (int i = 1; i < storedCount; i++) {
            if (storedMessages[i].messageText.length() > longest.messageText.length()) {
                longest = storedMessages[i];
            }
        }
        return longest.messageText;
    }

    // (c) Find a message by its ID in the sent, stored and disregarded arrays
    public static Message findByMessageID(String id) {
        Message found = searchArrayForID(sentMessages, sentCount, id);
        if (found == null) {
            found = searchArrayForID(storedMessages, storedCount, id);
        }
        if (found == null) {
            found = searchArrayForID(disregardedMessages, disregardedCount, id);
        }
        return found;
    }

    private static Message searchArrayForID(Message[] array, int count, String id) {
        for (int i = 0; i < count; i++) {
            if (array[i].messageID.equals(id)) {
                return array[i];
            }
        }
        return null;
    }

    // (d) All sent and stored message texts for one recipient
    public static String[] searchByRecipient(String recipientNumber) {
        String[] found = new String[MAX_MESSAGES * 2];
        int foundCount = 0;

        for (int i = 0; i < sentCount; i++) {
            if (sentMessages[i].recipient.equals(recipientNumber)) {
                found[foundCount] = sentMessages[i].messageText;
                foundCount++;
            }
        }
        for (int i = 0; i < storedCount; i++) {
            if (storedMessages[i].recipient.equals(recipientNumber)) {
                found[foundCount] = storedMessages[i].messageText;
                foundCount++;
            }
        }
        return Arrays.copyOf(found, foundCount);
    }

    // (e) Delete a stored message using its hash, then update the JSON file
    public static String deleteByHash(String hash) {
        for (int i = 0; i < storedCount; i++) {
            if (storedMessages[i].messageHash.equals(hash)) {
                String text = storedMessages[i].messageText;

                // Shift the rest of the array left to close the gap
                for (int j = i; j < storedCount - 1; j++) {
                    storedMessages[j] = storedMessages[j + 1];
                }
                storedMessages[storedCount - 1] = null;
                storedCount--;

                removeFromTracking(hash);
                saveStoredMessagesToJson(jsonFilePath);

                // The brief's expected wording leaves out the final full stop
                String shownText = text.endsWith(".") ? text.substring(0, text.length() - 1) : text;
                return "Message: \"" + shownText + "\" successfully deleted.";
            }
        }
        return "No stored message found with that hash.";
    }

    // Removes a hash and its matching ID from the two tracking arrays
    private static void removeFromTracking(String hash) {
        for (int i = 0; i < trackedCount; i++) {
            if (messageHashes[i].equals(hash)) {
                for (int j = i; j < trackedCount - 1; j++) {
                    messageHashes[j] = messageHashes[j + 1];
                    messageIDs[j] = messageIDs[j + 1];
                }
                messageHashes[trackedCount - 1] = null;
                messageIDs[trackedCount - 1] = null;
                trackedCount--;
                return;
            }
        }
    }

    // (f) Report with the full details of the messages in the array given
    public static String buildReport(Message[] messages) {
        if (messages.length == 0) {
            return "No messages to report.";
        }
        StringBuilder report = new StringBuilder();
        for (int i = 0; i < messages.length; i++) {
            report.append("\nMessage ").append(i + 1).append("\n")
                  .append(messages[i].printMessages()).append("\n");
        }
        return report.toString();
    }

    // ---------------------------------------------------------------
    // Helpers, getters and setters
    // ---------------------------------------------------------------

    public static int returnTotalMessages() {
        return sentCount;
    }

    // Empties all arrays and counters (used by unit tests)
    public static void clearAll() {
        sentMessages = new Message[MAX_MESSAGES];
        disregardedMessages = new Message[MAX_MESSAGES];
        storedMessages = new Message[MAX_MESSAGES];
        messageHashes = new String[MAX_MESSAGES];
        messageIDs = new String[MAX_MESSAGES];
        sentCount = 0;
        disregardedCount = 0;
        storedCount = 0;
        trackedCount = 0;
    }

    // ID built with string manipulation: 10 random digits appended one by one
    private static String generateMessageID() {
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            id.append((int) (Math.random() * 10));
        }
        return id.toString();
    }

    public static void setSender(String senderNumber) { sender = senderNumber; }
    public static String getSender() { return sender; }
    public static void setJsonFilePath(String path) { jsonFilePath = path; }
    public static String getJsonFilePath() { return jsonFilePath; }

    public String getMessageID() { return messageID; }
    public int getMessageNumber() { return messageNumber; }
    public String getMessageHash() { return messageHash; }
    public String getRecipient() { return recipient; }
    public String getMessageText() { return messageText; }

    // Return only the filled part of each array
    public static Message[] getSentMessages() { return Arrays.copyOf(sentMessages, sentCount); }
    public static Message[] getDisregardedMessages() { return Arrays.copyOf(disregardedMessages, disregardedCount); }
    public static Message[] getStoredMessages() { return Arrays.copyOf(storedMessages, storedCount); }
    public static String[] getMessageHashes() { return Arrays.copyOf(messageHashes, trackedCount); }
    public static String[] getMessageIDs() { return Arrays.copyOf(messageIDs, trackedCount); }
}