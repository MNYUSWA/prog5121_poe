package com.mycompany.prog5121_part2;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MessageReportManagerTest {

    // Helper method to load Test Data Messages 1 to 5 from the brief
    private MessageReportManager createPopulatedManager() {
        MessageReportManager manager = new MessageReportManager();

        // Test Data Message 1 - Sent
        Message m1 = new Message("Developer", "+27834557896", "Did you get the cake?", 1);
        m1.setMessageID("1000000001");
        m1.sendMessage(1); // Sent
        manager.recordMessage(m1);

        // Test Data Message 2 - Stored
        Message m2 = new Message("Developer", "+27838884567",
                "Where are you? You are late! I have asked you to be on time.", 2);
        m2.setMessageID("1000000002");
        m2.sendMessage(3); // Stored
        manager.recordMessage(m2);

        // Test Data Message 3 - Disregard
        Message m3 = new Message("Developer", "+27834484567", "Yohoooo, I am at your gate.", 3);
        m3.setMessageID("1000000003");
        m3.sendMessage(2); // Disregard
        manager.recordMessage(m3);

        // Test Data Message 4 - Sent (Developer entry)
        Message m4 = new Message("Developer", "0838884567", "It is dinner time !", 4);
        m4.setMessageID("0838884567");
        m4.sendMessage(1); // Sent
        manager.recordMessage(m4);

        // Test Data Message 5 - Stored
        Message m5 = new Message("Developer", "+27838884567", "Ok, I am leaving without you.", 5);
        m5.setMessageID("1000000005");
        m5.sendMessage(3); // Stored
        manager.recordMessage(m5);

        return manager;
    }

    // 1. Sent Messages array correctly populated (Test data messages 1-4)
    @Test
    public void testSentMessagesArrayPopulated() {
        MessageReportManager manager = createPopulatedManager();
        ArrayList<Message> sent = manager.getSentMessages();

        assertEquals(2, sent.size());
        assertEquals("Did you get the cake?", sent.get(0).getMessageText());
        assertEquals("It is dinner time !", sent.get(1).getMessageText());
    }

    // 2. Display the longest Message (Test data: message 1-4)
    @Test
    public void testDisplayLongestMessage() {
        MessageReportManager manager = createPopulatedManager();

        String longest = manager.getLongestStoredMessage();
        assertEquals("Where are you? You are late! I have asked you to be on time.", longest);
    }

    // 3. Search for messageID ("0838884567")
    @Test
    public void testSearchByMessageID() {
        MessageReportManager manager = createPopulatedManager();

        String result = manager.searchByMessageID("0838884567");
        assertTrue(result.contains("It is dinner time !"));
    }

    // 4. Search all messages sent or stored for recipient "+27838884567"
    @Test
    public void testSearchMessagesByRecipient() {
        MessageReportManager manager = createPopulatedManager();

        ArrayList<String> matches = manager.searchMessagesByRecipient("+27838884567");
        assertEquals(2, matches.size());
        assertEquals("Where are you? You are late! I have asked you to be on time.", matches.get(0));
        assertEquals("Ok, I am leaving without you.", matches.get(1));
    }

    // 5. Delete a message using a message hash (Test Message 2)
    @Test
    public void testDeleteMessageByHash() {
        MessageReportManager manager = createPopulatedManager();

        Message msg2 = manager.getStoredMessages().get(0);
        String deleteResult = manager.deleteMessageByHash(msg2.getMessageHash());

        assertEquals("Message: \"Where are you? You are late! I have asked you to be on time.\" successfully deleted.",
                deleteResult);
    }

    // 6. Display Report shows Message Hash, Recipient, and Message
    @Test
    public void testDisplayReport() {
        MessageReportManager manager = createPopulatedManager();

        assertFalse(manager.getSentMessages().isEmpty());
        Message firstSent = manager.getSentMessages().get(0);
        assertNotNull(firstSent.getMessageHash());
        assertEquals("+27834557896", firstSent.getRecipientCell());
        assertEquals("Did you get the cake?", firstSent.getMessageText());
    }
}
