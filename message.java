package com.mycompany.prog5121_part2;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public final class Message {
    private String messageID;
    private int numMessagesSent;
    private String sender;
    private String recipientCell;
    private String messageText;
    private String messageHash;
    private String status; // "Sent", "Stored", or "Disregard"
    private String sendOptionStatus;

    public Message(String sender, String recipientCell, String messageText, int numMessagesSent) {
        this.sender = sender;
        this.recipientCell = recipientCell;
        this.messageText = messageText;
        this.numMessagesSent = numMessagesSent;
        this.messageID = generateMessageID();
        this.messageHash = createMessageHash();
        this.status = "Pending";
    }

    // Overloaded constructor in case tests don't pass sender separately
    public Message(String recipientCell, String messageText, int numMessagesSent) {
        this("SystemUser", recipientCell, messageText, numMessagesSent);
    }

    public String generateMessageID() {
        Random rand = new Random();
        long randomNum = (long) (rand.nextDouble() * 9000000000L) + 1000000000L;
        return String.valueOf(randomNum);
    }

    public boolean checkMessageID() {
        return this.messageID != null && this.messageID.matches("\\d{10}");
    }

    public boolean checkRecipientCell() {
        if (this.recipientCell == null) {
            return false;
        }
        return this.recipientCell.matches("\\+27\\d{9}");
    }

    public String getRecipientCellStatus() {
        if (checkRecipientCell()) {
            return "Cell phone number successfully captured.";
        } else {
            return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        }
    }

    public String checkMessageLengthStatus() {
        if (this.messageText == null) {
            return "Message exceeds 250 characters by 0; please reduce the size.";
        }
        if (this.messageText.length() <= 250) {
            return "Message ready to send.";
        } else {
            int excess = this.messageText.length() - 250;
            return "Message exceeds " + excess + " characters; please reduce the size.";
        }
    }

    public boolean checkMessageLength() {
        return this.messageText != null && this.messageText.length() <= 250;
    }

    public String createMessageHash() {
        String firstTwoID = (messageID != null && messageID.length() >= 2) ? messageID.substring(0, 2) : "00";
        String firstWord = "";
        String lastWord = "";

        if (messageText != null && !messageText.trim().isEmpty()) {
            String[] words = messageText.trim().split("\\s+");
            firstWord = words[0];
            lastWord = words[words.length - 1];
        }

        return (firstTwoID + ":" + (numMessagesSent - 1) + ":" + firstWord + ":" + lastWord).toUpperCase();
    }

    public String sendMessage(int choice) {
        switch (choice) {
            case 1 -> {
                this.status = "Sent";
                this.sendOptionStatus = "Message successfully sent.";
                return this.sendOptionStatus;
            }
            case 2 -> {
                this.status = "Disregard";
                this.sendOptionStatus = "Message disregarded.";
                return this.sendOptionStatus;
            }
            case 3 -> {
                this.status = "Stored";
                this.sendOptionStatus = "Message successfully stored.";
                storeMessage();
                return this.sendOptionStatus;
            }
            default -> {
                return "Invalid selection.";
            }
        }
    }

    public void storeMessage() {
        this.status = "Stored";
        String safeSender = (this.sender == null) ? "" : this.sender.replace("\"", "\\\"");
        String safeRecipient = (this.recipientCell == null) ? "" : this.recipientCell.replace("\"", "\\\"");
        String safeText = (this.messageText == null) ? "" : this.messageText.replace("\"", "\\\"");
        String safeHash = (this.messageHash == null) ? "" : this.messageHash.replace("\"", "\\\"");

        String jsonPayload = "{\n"
                + "  \"messageID\": \"" + this.messageID + "\",\n"
                + "  \"numSent\": " + this.numMessagesSent + ",\n"
                + "  \"sender\": \"" + safeSender + "\",\n"
                + "  \"recipient\": \"" + safeRecipient + "\",\n"
                + "  \"message\": \"" + safeText + "\",\n"
                + "  \"hash\": \"" + safeHash + "\",\n"
                + "  \"status\": \"" + this.status + "\"\n"
                + "}\n";

        try (FileWriter writer = new FileWriter("message_" + this.messageID + ".json")) {
            writer.write(jsonPayload);
        } catch (IOException e) {
            System.out.println("Error saving message to JSON.");
        }
    }

    public String printMessages() {
        return "Message ID: " + messageID
                + "\nMessage Hash: " + messageHash
                + "\nSender: " + sender
                + "\nRecipient: " + recipientCell
                + "\nMessage: " + messageText
                + "\nStatus: " + status;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public int getNumMessagesSent() {
        return numMessagesSent;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipientCell() {
        return recipientCell;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageHash() {
        return messageHash;
    }

    public void setMessageHash(String messageHash) {
        this.messageHash = messageHash;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSendOptionStatus() {
        return sendOptionStatus;
    }
}
