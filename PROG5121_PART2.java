package com.mycompany.prog5121_part2;

import java.util.ArrayList;
import java.util.Scanner;

public class PROG5121_PART2 {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            Login authApp = new Login();

            System.out.println("=== USER REGISTRATION ===");
            System.out.print("Enter First Name: ");
            String firstName = scanner.nextLine();
            System.out.print("Enter Last Name: ");
            String lastName = scanner.nextLine();
            System.out.print("Enter Username: ");
            String username = scanner.nextLine();
            System.out.print("Enter Password: ");
            String password = scanner.nextLine();
            System.out.print("Enter Cell Phone Number (+27...): ");
            String cellNumber = scanner.nextLine();

            String regStatus = authApp.registerUser(firstName, lastName, username, password, cellNumber);
            System.out.println("\n" + regStatus);

            if (!regStatus.equals("Username successfully captured.")) {
                System.out.println("Registration failed. Exiting.");
                return;
            }

            System.out.println("\n=== USER LOGIN ===");
            System.out.print("Enter Username: ");
            String inputUser = scanner.nextLine();
            System.out.print("Enter Password: ");
            String inputPass = scanner.nextLine();

            boolean loginSuccess = authApp.loginUser(inputUser, inputPass);
            System.out.println("\n" + authApp.returnLoginStatus(loginSuccess));

            if (!loginSuccess) {
                return;
            }

            System.out.println("\nWelcome to QuickChat.");

            MessageReportManager manager = new MessageReportManager();
            manager.loadStoredMessagesFromJson();

            boolean running = true;

            while (running) {
                System.out.println("\n--- QUICKCHAT MENU ---");
                System.out.println("1) Send Messages");
                System.out.println("2) Show recently sent messages");
                System.out.println("3) Quit");
                System.out.println("4) Stored Messages");
                System.out.print("Choose an option (1-4): ");

                String choiceStr = scanner.nextLine();
                int choice;
                try {
                    choice = Integer.parseInt(choiceStr);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter 1, 2, 3, or 4.");
                    continue;
                }

                switch (choice) {
                    case 1 -> {
                        System.out.print("How many messages would you like to send? ");
                        int totalMessages;
                        try {
                            totalMessages = Integer.parseInt(scanner.nextLine());
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number. Returning to menu.");
                            break;
                        }

                        for (int i = 1; i <= totalMessages; i++) {
                            System.out.println("\n--- Message " + i + " of " + totalMessages + " ---");

                            System.out.print("Enter recipient cell phone number (+27...): ");
                            String recipient = scanner.nextLine();

                            System.out.print("Enter your message (max 250 characters): ");
                            String text = scanner.nextLine();

                            Message msg = new Message(username, recipient, text, i);

                            System.out.println(msg.checkMessageLengthStatus());
                            if (!msg.checkMessageLength()) {
                                i--;
                                continue;
                            }

                            System.out.println(msg.getRecipientCellStatus());

                            System.out.println("\nChoose action:");
                            System.out.println("1) Send Message");
                            System.out.println("2) Disregard Message");
                            System.out.println("3) Store Message to send later");
                            System.out.print("Enter choice (1-3): ");

                            int actionChoice = 1;
                            try {
                                actionChoice = Integer.parseInt(scanner.nextLine());
                            } catch (NumberFormatException ignored) {
                            }

                            String actionResult = msg.sendMessage(actionChoice);
                            System.out.println(actionResult);
                            manager.recordMessage(msg);

                            if (actionChoice == 2) {
                                System.out.println("Message was disregarded and recorded in disregarded messages.");
                            } else {
                                System.out.println("\n--- Message Summary ---");
                                System.out.println(msg.printMessages());
                            }
                        }

                        System.out.println("\nTotal sent messages in session: " + manager.getSentMessages().size());
                    }

                    case 2 -> {
                        System.out.println("\n--- RECENTLY SENT MESSAGES ---");
                        ArrayList<Message> sent = manager.getSentMessages();
                        if (sent.isEmpty()) {
                            System.out.println("No sent messages yet.");
                        } else {
                            for (int i = 0; i < sent.size(); i++) {
                                System.out.println("\nMessage #" + (i + 1));
                                System.out.println(sent.get(i).printMessages());
                            }
                        }
                    }

                    case 3 -> {
                        System.out.println("\nExiting QuickChat. Goodbye!");
                        running = false;
                    }

                    case 4 -> {
                        manager.loadStoredMessagesFromJson();
                        System.out.println("\n--- STORED MESSAGES MENU ---");
                        System.out.println("a) Display sender and recipient of all stored messages");
                        System.out.println("b) Display the longest stored message");
                        System.out.println("c) Search for a message ID");
                        System.out.println("d) Search all messages for a recipient");
                        System.out.println("e) Delete a message using message hash");
                        System.out.println("f) Display report of all stored messages");
                        System.out.print("Choose an option (a-f): ");

                        String subChoice = scanner.nextLine().trim().toLowerCase();

                        switch (subChoice) {
                            case "a" -> manager.displaySendersAndRecipientsOfStored();
                            case "b" -> System.out.println("Longest stored message: " + manager.getLongestStoredMessage());
                            case "c" -> {
                                System.out.print("Enter Message ID to search: ");
                                String searchId = scanner.nextLine().trim();
                                System.out.println(manager.searchByMessageID(searchId));
                            }
                            case "d" -> {
                                System.out.print("Enter recipient number: ");
                                String rec = scanner.nextLine().trim();
                                ArrayList<String> found = manager.searchMessagesByRecipient(rec);
                                if (found.isEmpty()) {
                                    System.out.println("No messages found for " + rec);
                                } else {
                                    for (int i = 0; i < found.size(); i++) {
                                        System.out.println((i + 1) + ") " + found.get(i));
                                    }
                                }
                            }
                            case "e" -> {
                                System.out.print("Enter Message Hash to delete: ");
                                String hashToDelete = scanner.nextLine().trim();
                                System.out.println(manager.deleteMessageByHash(hashToDelete));
                            }
                            case "f" -> manager.displayStoredMessagesReport();
                            default -> System.out.println("Invalid sub-option. Please enter a through f.");
                        }
                    }

                    default -> System.out.println("Invalid option. Please choose 1, 2, 3, or 4.");
                }
            }
        }
    }
}
