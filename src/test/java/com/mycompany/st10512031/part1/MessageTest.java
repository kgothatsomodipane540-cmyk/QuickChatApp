package com.mycompany.st10512031.part1;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MessageTest {

    @Test
    public void testMessageLengthSuccess() {
        String msg = "Hi Mike, can you join us for dinner tonight?";
        assertTrue(msg.length() <= 250);
    }

    @Test
    public void testMessageLengthFailure() {
        String longMsg = "";
        for (int i = 0; i < 251; i++) {
            longMsg = longMsg + "a";
        }
        assertTrue(longMsg.length() > 250);
    }

    @Test
    public void testRecipientCellSuccess() {
        Message m = new Message("+27838968976", "Test message");
        assertEquals("Cell phone number successfully captured.", m.checkRecipientCell());
    }

    @Test
    public void testRecipientCellFailure() {
        Message m = new Message("08966553", "Test message");
        assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.", m.checkRecipientCell());
    }

    @Test
    public void testCreateMessageHash() {
        Message m = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?");
        assertTrue(m.getMessageHash().endsWith("HITONIGHT"));
    }
    
    @Test
    public void testMessageIDLength() {
        Message m = new Message("+27718693002", "Test");
        assertTrue(m.checkMessageID());
    }
}