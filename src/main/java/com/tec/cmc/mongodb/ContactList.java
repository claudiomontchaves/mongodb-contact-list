
package com.tec.cmc.mongodb;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

public class ContactList {

    private static final String URL = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "contacts";

    private MongoClient mongoClient;
    private MongoDatabase database;

    private Logger logger = Logger.getLogger("org.mongodb.driver");

    public static void main(String[] args) {
        (new ContactList()).run();
    }
    
    public ContactList() {
        logger.setLevel(Level.SEVERE);
        mongoClient = MongoClients.create(URL);
        database = mongoClient.getDatabase(DATABASE_NAME);
    }
    
    public void run() {

        clearConsole();

        while (true) {
            println("---------------------------------------");
            println("1. List Contact Lists");
            println("2. Create Contact List");
            println("3. Remove Contact List");
            println("4. Exit");
            println("---------------------------------------");
            print("> ");

            String[] cmd = readConsole();
            clearConsole();

            switch (cmd[0]) {
                case "1":
                    listContactLists();
                    break;
                case "2":
                    createContactList();
                    break;
                case "3":
                    removeContactList();
                    break;
                case "4":
                    exit();
                    break;
                default:
                    println("Invalid option!");
            }
        }
    }
    
    private void listContactLists() {
        MongoIterable<String> collectionNames = database.listCollectionNames();
        Iterator<String> it = collectionNames.iterator();
        println("Contact-Lists:");
        while (it.hasNext()) {
            println(" - " + it.next());
        }
        pressAnyKey();
    }

    private void createContactList() {
        print("New contact list name: ");
        String[] values = readConsole();
        database.createCollection(values[0]);
        pressAnyKey("Contact list created.");
    }

    private void removeContactList() {
        print("Contact list to be removed: ");
        String[] values = readConsole();
        database.getCollection(values[0]).drop();
        pressAnyKey("Contact list removed.");
    }

    private void exit() {
        mongoClient.close();
        System.exit(0);
    }

    private void print(String msg) {
        System.out.print(msg);
    }

    private void println(String msg) {
        System.out.println(msg);
    }

    private String[] readConsole() {
        String line = System.console().readLine();
        return line.split(" ");
    }

    private void pressAnyKey() {
        pressAnyKey("");
    }

    private void pressAnyKey(String msg) {
        if (!msg.isEmpty()) {
            println(msg);
        }
        println("\nPress any key to continue.");
        readConsole();
        clearConsole();
    }

    private void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
    
}
