package com.mycompany.prog5121_part2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class MessageReportManager {

    private final ArrayList<Message> sentMessages = new ArrayList<>();
    private final ArrayList<Message> disregardedMessages = new ArrayList<>();
    private final ArrayList<Message> storedMessages = new ArrayList<>();
    private final ArrayList<String> messageHashes = new ArrayList<>();
    private final ArrayList<String> messageIDs = new ArrayList<>();

    // Record a message into the matching lists/arrays
    public void recordMessage(Message msg) {
        if (msg == null) {
            return;
        }

        if (msg.getMessageID() != null && !messageIDs.contains(msg.getMessageID())) {
            messageIDs.add(msg.getMessageID());
        }
        if (msg.getMessageHash() != null && !messageHashes.contains(msg.getMessageHash())) {
            messageHashes.add(msg.getMessageHash());
        }

        if ("Sent".equalsIgnoreCase(msg.getStatus())) {
            sentMessages.add(msg);
        } else if ("Disregard".equalsIgnoreCase(msg.getStatus())) {
            disregardedMessages.add(msg);
        } else if ("Stored".equalsIgnoreCase(msg.getStatus())) {
            if (!containsMessageID(storedMessages, msg.getMessageID())) {
                storedMessages.add(msg);
            }
        }
    }

    private boolean containsMessageID(ArrayList<Message> list, String id) {
        if (id == null) {
            return false;
        }
        for (int i = 0; i < list.size(); i++) {
            if (id.equals(list.get(i).getMessageID())) {
                return true;
            }
        }
        return false;
    }

    // Load stored messages from saved message_*.json files in the project folder
    public void loadStoredMessagesFromJson() {
        storedMessages.clear();
        File folder = new File(".");
        File[] files = folder.listFiles();

        if (files == null) {
            return;
        }

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isFile() && file.getName().startsWith("message_") && file.getName().endsWith(".json")) {
                try {
                    String content = Files.readString(file.toPath());
                    Message loaded = parseSimpleJsonMessage(content);
                    if (loaded != null) {
                        storedMessages.add(loaded);
                        if (loaded.getMessageID() != null && !messageIDs.contains(loaded.getMessageID())) {
                            messageIDs.add(loaded.getMessageID());
                        }
                        if (loaded.getMessageHash() != null && !messageHashes.contains(loaded.getMessageHash())) {
                            messageHashes.add(loaded.getMessageHash());
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Could not read JSON file: " + file.getName());
                }
            }
        }
    }

    // Simple string helper to read fields from our JSON file
    private Message parseSimpleJsonMessage(String json) {
        String id = extractJsonValue(json, "messageID");
        String numStr = extractJsonValue(json, "numSent");
        String sender = extractJsonValue(json, "sender");
        String recipient = extractJsonValue(json, "recipient");
        String text = extractJsonValue(json, "message");
        String hash = extractJsonValue(json, "hash");

        int num = 1;
        try {
            if (!numStr.isEmpty()) {
                num = Integer.parseInt(numStr);
            }
        } catch (NumberFormatException ignored) {
        }

        Message msg = new Message(sender.isEmpty() ? "SystemUser" : sender, recipient, text, num);
        if (!id.isEmpty()) {
            msg.setMessageID(id);
        }
        if (!hash.isEmpty()) {
            msg.setMessageHash(hash);
        }
        msg.setStatus("Stored");
        return msg;
    }

    private String extractJsonValue(String json, String key) {
        String search = "\"" + key + "\"";
        int keyIndex = json.indexOf(search);
        if (keyIndex == -1) {
            return "";
        }
        int colonIndex = json.indexOf(":", keyIndex + search.length());
        if (colonIndex == -1) {
            return "";
        }

        String afterColon = json.substring(colonIndex + 1).trim();
        if (afterColon.startsWith("\"")) {
            int endQuote = afterColon.indexOf("\"", 1);
            if (endQuote != -1) {
                return afterColon.substring(1, endQuote).replace("\\\"", "\"");
            }
            return "";
        } else {
            int commaIndex = afterColon.indexOf(",");
            int braceIndex = afterColon.indexOf("}");
            int endIndex = (commaIndex != -1) ? commaIndex : braceIndex;
            if (endIndex != -1) {
                return afterColon.substring(0, endIndex).trim();
            }
            return afterColon.trim();
        }
    }

    // a. Display sender and recipient of all stored messages
    public void displaySendersAndRecipientsOfStored() {
        loadStoredMessagesFromJson();
        if (storedMessages.isEmpty()) {
            System.out.println("No stored messages found.");
            return;
        }
        for (int i = 0; i < storedMessages.size(); i++) {
            Message m = storedMessages.get(i);
            System.out.println((i + 1) + ") Sender: " + m.getSender() + " | Recipient: " + m.getRecipientCell());
        }
    }

    // b. Display the longest stored message
    public String getLongestStoredMessage() {
        loadStoredMessagesFromJson();
        if (storedMessages.isEmpty()) {
            return "No stored messages found.";
        }
        Message longest = storedMessages.get(0);
        for (int i = 1; i < storedMessages.size(); i++) {
            Message current = storedMessages.get(i);
            if (current.getMessageText().length() > longest.getMessageText().length()) {
                longest = current;
            }
        }
        return longest.getMessageText();
    }

    // c. Search for a message ID and display corresponding recipient and message
    public String searchByMessageID(String targetID) {
        for (int i = 0; i < storedMessages.size(); i++) {
            Message m = storedMessages.get(i);
            if (targetID.equals(m.getMessageID())) {
                return "Recipient: " + m.getRecipientCell() + "\nMessage: " + m.getMessageText();
            }
        }
        for (int i = 0; i < sentMessages.size(); i++) {
            Message m = sentMessages.get(i);
            if (targetID.equals(m.getMessageID())) {
                return "Recipient: " + m.getRecipientCell() + "\nMessage: " + m.getMessageText();
            }
        }
        return "Message ID not found.";
    }

    // d. Search for all messages stored/sent for a particular recipient
    public ArrayList<String> searchMessagesByRecipient(String recipient) {
        ArrayList<String> matches = new ArrayList<>();

        for (int i = 0; i < storedMessages.size(); i++) {
            Message m = storedMessages.get(i);
            if (recipient.equals(m.getRecipientCell())) {
                matches.add(m.getMessageText());
            }
        }
        for (int i = 0; i < sentMessages.size(); i++) {
            Message m = sentMessages.get(i);
            if (recipient.equals(m.getRecipientCell())) {
                matches.add(m.getMessageText());
            }
        }
        return matches;
    }

    // e. Delete a message using the message hash
    public String deleteMessageByHash(String targetHash) {
        for (int i = 0; i < storedMessages.size(); i++) {
            Message m = storedMessages.get(i);
            if (targetHash.equals(m.getMessageHash())) {
                String text = m.getMessageText();
                storedMessages.remove(i);
                messageHashes.remove(targetHash);
                messageIDs.remove(m.getMessageID());

                File jsonFile = new File("message_" + m.getMessageID() + ".json");
                if (jsonFile.exists()) {
                    jsonFile.delete();
                }
                return "Message: \"" + text + "\" successfully deleted.";
            }
        }

        for (int i = 0; i < sentMessages.size(); i++) {
            Message m = sentMessages.get(i);
            if (targetHash.equals(m.getMessageHash())) {
                String text = m.getMessageText();
                sentMessages.remove(i);
                messageHashes.remove(targetHash);
                messageIDs.remove(m.getMessageID());
                return "Message: \"" + text + "\" successfully deleted.";
            }
        }

        return "Message with hash not found.";
    }

    // f. Display a report listing full details of stored messages
    public void displayStoredMessagesReport() {
        loadStoredMessagesFromJson();
        if (storedMessages.isEmpty()) {
            System.out.println("No stored messages in report.");
            return;
        }
        System.out.println("\n=== STORED MESSAGES REPORT ===");
        for (int i = 0; i < storedMessages.size(); i++) {
            Message m = storedMessages.get(i);
            System.out.println("------------------------------");
            System.out.println("Message ID   : " + m.getMessageID());
            System.out.println("Message Hash : " + m.getMessageHash());
            System.out.println("Sender       : " + m.getSender());
            System.out.println("Recipient    : " + m.getRecipientCell());
            System.out.println("Message      : " + m.getMessageText());
        }
    }

    public ArrayList<Message> getSentMessages() {
        return sentMessages;
    }

    public ArrayList<Message> getDisregardedMessages() {
        return disregardedMessages;
    }

    public ArrayList<Message> getStoredMessages() {
        return storedMessages;
    }

    public ArrayList<String> getMessageHashes() {
        return messageHashes;
    }

    public ArrayList<String> getMessageIDs() {
        return messageIDs;
    }

    // Entry point so running this file directly works without a missing-main error
    public static void main(String[] args) {
        MessageReportManager manager = new MessageReportManager();
        manager.loadStoredMessagesFromJson();
        System.out.println("MessageReportManager ready. Loaded stored messages: " + manager.getStoredMessages().size());
    }
}
