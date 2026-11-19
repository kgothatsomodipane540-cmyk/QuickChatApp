package com.mycompany.st10512031.part1;

import java.io.File;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MessageTest {

    private static final String CELL_OK = "Cell phone number successfully captured.";
    private static final String CELL_BAD = "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
    private static final String TEST_JSON_FILE = "testStoredMessages.json";

    // Part 2 test data
    private static final String P2_RECIPIENT_1 = "+27718693002";
    private static final String P2_TEXT_1 = "Hi Mike, can you join us for dinner tonight?";
    private static final String P2_RECIPIENT_2 = "08575975889";
    private static final String P2_TEXT_2 = "Hi Keegan, did you receive the payment?";

    // Part 3 test data
    private static final String TEXT_1 = "Did you get the cake?";
    private static final String TEXT_2 = "Where are you? You are late! I have asked you to be on time.";
    private static final String TEXT_3 = "Yohoooo, I am at your gate.";
    private static final String TEXT_4 = "It is dinner time!";
    private static final String TEXT_5 = "Ok, I am leaving without you.";

    // Each test starts with empty arrays and uses a test JSON file,
    // so the real storedMessages.json is never touched.
    @BeforeEach
    public void setUp() {
        Message.clearAll();
        Message.setJsonFilePath(TEST_JSON_FILE);
    }

    @AfterEach
    public void tearDown() {
        new File(TEST_JSON_FILE).delete();
        Message.clearAll();
        Message.setJsonFilePath("storedMessages.json");
        Message.setSender("Unknown");
    }

    // Fills the arrays with the 5 messages from the Part 3 brief
    private void loadPart3TestData() {
        Message m1 = new Message("1000000001", 0, "+27834557896", TEXT_1);
        m1.SentMessage(1); // Sent

        Message m2 = new Message("2000000002", 1, "+27838884567", TEXT_2);
        m2.SentMessage(3); // Stored

        Message m3 = new Message("3000000003", 2, "+27834484567", TEXT_3);
        m3.SentMessage(2); // Disregard

        Message m4 = new Message("0838884567", 3, "0838884567", TEXT_4);
        m4.SentMessage(1); // Sent

        Message m5 = new Message("5000000005", 4, "+27838884567", TEXT_5);
        m5.SentMessage(3); // Stored
    }

    // ---------------- Part 2 tests ----------------

    @Test
    public void testMessageLengthSuccess() {
        Message m = new Message(0, P2_RECIPIENT_1, P2_TEXT_1);
        assertEquals("Message ready to send.", m.checkMessageLength());
    }

    @Test
    public void testMessageLengthFailure() {
        String longText = "";
        for (int i = 0; i < 260; i++) {
            longText = longText + "a";
        }
        Message m = new Message(0, P2_RECIPIENT_1, longText);
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.", m.checkMessageLength());
    }

    @Test
    public void testRecipientCellSuccess() {
        Message m = new Message(0, P2_RECIPIENT_1, P2_TEXT_1);
        assertEquals(CELL_OK, m.checkRecipientCell());
    }

    @Test
    public void testRecipientCellFailure() {
        Message m = new Message(1, P2_RECIPIENT_2, P2_TEXT_2);
        assertEquals(CELL_BAD, m.checkRecipientCell());
    }

    @Test
    public void testOnlyPlus27Accepted() {
        assertTrue(Message.isValidRecipient("+27718693002"));
        assertFalse(Message.isValidRecipient("+44718693002"));
        assertFalse(Message.isValidRecipient("0718693002"));
    }

    // Exact hash from the brief, using Test Case 1
    @Test
    public void testExactMessageHash() {
        Message m = new Message("0012345678", 0, P2_RECIPIENT_1, P2_TEXT_1);
        assertEquals("00:0:HITONIGHT", m.getMessageHash());
    }

    // The remaining message hashes tested in a loop, using the brief's test data.
    // The four arrays line up: position i in each one belongs to the same message.
    @Test
    public void testMessageHashLoop() {
        String[] ids = {"0912345678", "1000000001", "2000000002", "3000000003", "0838884567", "5000000005"};
        int[] numbers = {1, 0, 1, 2, 3, 4};
        String[] texts = {P2_TEXT_2, TEXT_1, TEXT_2, TEXT_3, TEXT_4, TEXT_5};
        String[] expectedHashes = {
            "09:1:HIPAYMENT",
            "10:0:DIDCAKE",
            "20:1:WHERETIME",
            "30:2:YOHOOOOGATE",
            "08:3:ITTIME",
            "50:4:OKYOU"
        };

        for (int i = 0; i < texts.length; i++) {
            Message m = new Message(ids[i], numbers[i], P2_RECIPIENT_1, texts[i]);
            assertEquals(expectedHashes[i], m.getMessageHash());
        }
    }

    @Test
    public void testMessageIDLength() {
        Message m = new Message(0, P2_RECIPIENT_1, P2_TEXT_1);
        assertTrue(m.checkMessageID());
        assertEquals(10, m.getMessageID().length());
    }

    @Test
    public void testMessageIDGenerated() {
        Message m = new Message("1234567890", 0, P2_RECIPIENT_1, P2_TEXT_1);
        assertEquals("Message ID generated: 1234567890", m.messageIDGenerated());
    }

    // Details must show in this order: ID, hash, recipient, message
    @Test
    public void testPrintMessagesOrder() {
        Message m = new Message("0012345678", 0, P2_RECIPIENT_1, P2_TEXT_1);
        String expected = "Message ID: 0012345678"
                + "\nMessage Hash: 00:0:HITONIGHT"
                + "\nRecipient: +27718693002"
                + "\nMessage: Hi Mike, can you join us for dinner tonight?";
        assertEquals(expected, m.printMessages());
    }

    @Test
    public void testSendMessage() {
        Message m = new Message(0, P2_RECIPIENT_1, P2_TEXT_1);
        assertEquals("Message successfully sent.", m.SentMessage(1));
    }

    @Test
    public void testDisregardMessage() {
        Message m = new Message(1, P2_RECIPIENT_2, P2_TEXT_2);
        assertEquals("Press 0 to delete the message.", m.SentMessage(2));
    }

    @Test
    public void testStoreMessage() {
        Message m = new Message(2, P2_RECIPIENT_1, P2_TEXT_1);
        assertEquals("Message successfully stored.", m.SentMessage(3));
    }

    @Test
    public void testTotalMessagesSent() {
        new Message(0, P2_RECIPIENT_1, P2_TEXT_1).SentMessage(1); // sent
        new Message(1, P2_RECIPIENT_2, P2_TEXT_2).SentMessage(2); // disregarded
        assertEquals(1, Message.returnTotalMessages());
    }

    // ---------------- Part 3 tests ----------------

    @Test
    public void testSentMessagesArrayPopulated() {
        loadPart3TestData();
        Message[] sent = Message.getSentMessages();

        assertEquals(2, sent.length);
        assertEquals("Did you get the cake?", sent[0].getMessageText());
        assertEquals("It is dinner time!", sent[1].getMessageText());
    }

    @Test
    public void testDisregardedAndTrackingArrays() {
        loadPart3TestData();

        assertEquals(1, Message.getDisregardedMessages().length);
        assertEquals(TEXT_3, Message.getDisregardedMessages()[0].getMessageText());
        assertEquals(2, Message.getStoredMessages().length);
        assertEquals(5, Message.getMessageHashes().length);
        assertEquals(5, Message.getMessageIDs().length);
    }

    // The hash and ID arrays hold every message's hash and ID, in the order they were added
    @Test
    public void testHashAndIDArrayContents() {
        loadPart3TestData();

        String[] expectedHashes = {
            "10:0:DIDCAKE", "20:1:WHERETIME", "30:2:YOHOOOOGATE", "08:3:ITTIME", "50:4:OKYOU"
        };
        String[] expectedIDs = {
            "1000000001", "2000000002", "3000000003", "0838884567", "5000000005"
        };

        assertArrayEquals(expectedHashes, Message.getMessageHashes());
        assertArrayEquals(expectedIDs, Message.getMessageIDs());
    }

    @Test
    public void testStoredMessagesReadFromJson() {
        loadPart3TestData();

        // Read the file back into the stored array
        Message.loadStoredMessagesFromJson();
        Message[] stored = Message.getStoredMessages();

        assertEquals(2, stored.length);
        assertEquals(TEXT_2, stored[0].getMessageText());
        assertEquals(TEXT_5, stored[1].getMessageText());
    }

    // Menu feature (a): sender and recipient of every stored message
    @Test
    public void testDisplaySenderAndRecipient() {
        Message.setSender("+27682610145");
        loadPart3TestData();

        String expected = "Sender: +27682610145 | Recipient: +27838884567\n"
                + "Sender: +27682610145 | Recipient: +27838884567";
        assertEquals(expected, Message.displaySenderAndRecipient());
    }

    @Test
    public void testLongestMessage() {
        loadPart3TestData();
        assertEquals("Where are you? You are late! I have asked you to be on time.",
                Message.getLongestStoredMessage());
    }

    @Test
    public void testSearchByMessageID() {
        loadPart3TestData();
        Message found = Message.findByMessageID("0838884567");

        assertEquals("It is dinner time!", found.getMessageText());
    }

    @Test
    public void testSearchByUnknownMessageID() {
        loadPart3TestData();
        assertNull(Message.findByMessageID("0000000000"));
    }

    @Test
    public void testSearchByRecipient() {
        loadPart3TestData();
        String[] expected = {
            "Where are you? You are late! I have asked you to be on time.",
            "Ok, I am leaving without you."
        };
        assertArrayEquals(expected, Message.searchByRecipient("+27838884567"));
    }

    @Test
    public void testSearchByUnknownRecipient() {
        loadPart3TestData();
        assertEquals(0, Message.searchByRecipient("+27000000000").length);
    }

    @Test
    public void testDeleteMessageUsingHash() {
        loadPart3TestData();
        String hash = Message.getStoredMessages()[0].getMessageHash(); // hash of Test Message 2

        String result = Message.deleteByHash(hash);

        assertEquals("Message: \"Where are you? You are late! I have asked you to be on time\" successfully deleted.", result);
        assertEquals(1, Message.getStoredMessages().length);
    }

    @Test
    public void testDeleteUnknownHash() {
        loadPart3TestData();
        assertEquals("No stored message found with that hash.", Message.deleteByHash("99:9:NOTHING"));
    }

    @Test
    public void testDisplayReport() {
        loadPart3TestData();
        Message[] sent = Message.getSentMessages();
        String report = Message.buildReport(sent);

        for (Message sentMessage : sent) {
            assertTrue(report.contains(sentMessage.getMessageHash()));
            assertTrue(report.contains(sentMessage.getRecipient()));
            assertTrue(report.contains(sentMessage.getMessageText()));
        }
    }

    @Test
    public void testEmptyReport() {
        assertEquals("No messages to report.", Message.buildReport(Message.getStoredMessages()));
    }
}