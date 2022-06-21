
package com.tec.cmc.mongodb;

import static com.mongodb.client.model.Filters.eq;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.result.DeleteResult;

public class ContactList {

    private static final String URL = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "contacts";
    private static final String THUMBS_UP = "\ud83d\udc4d";
    private static final String THUMBS_DOWN = "\ud83d\udc4e";

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
            println("1) List Contact Lists");
            println("2) Create Contact List");
            println("3) Remove Contact List");
            println("4) List Contacts");
            println("5) Create Contact");
            println("6) Remove Contact");
            println("7) Exit");
            println("---------------------------------------");
            print("> ");

            String cmd = readConsole();
            clearConsole();

            switch (cmd) {
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
                    listContacts();
                    break;
                case "5":
                    createContact();
                    break;
                case "6":
                    removeContact();
                    break;
                case "7":
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
        String contactListName = readConsole();
        database.createCollection(contactListName);
        pressAnyKey("Contact list created. " + THUMBS_UP);
    }

    private void removeContactList() {
        print("Contact list to be removed: ");
        String contactListName = readConsole();
        database.getCollection(contactListName).drop();
        pressAnyKey("Contact list removed." + THUMBS_UP);
    }

    private void listContacts() {
        MongoCollection<Document> collection = getCollection();
        clearConsole();
        FindIterable<Document> iterDoc = collection.find();
        Iterator<Document> it = iterDoc.iterator();
        while (it.hasNext()) {
            JSONObject jso = new JSONObject(it.next().toJson());
            println("---------------------------------------------------");
            println(jso.toString(3));
        }
        pressAnyKey();
    }

    private void createContact() {
        MongoCollection<Document> collection = getCollection();
        print("Document: ");
        String json = readConsole();
        try {
            collection.insertOne(Document.parse(json));
            pressAnyKey("Contact list created. " + THUMBS_UP);
        } catch (Exception e) {
            pressAnyKey("Error creating contact: " + e.getMessage() + " " + THUMBS_DOWN);
        }
    }

    private void removeContact() {
        MongoCollection<Document> collection = getCollection();
        print("Contact name to be deleted: ");
        String name = readConsole();
        Bson filter = eq("name", name);
        DeleteResult result = collection.deleteOne(filter);
        if (result.getDeletedCount() == 1) {
            pressAnyKey("Contact removed. "+ THUMBS_UP);
        } else {
            pressAnyKey("Could not find '" + name + "''. " + THUMBS_DOWN);
        }
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

    private String readConsole() {
        return System.console().readLine();
    }

    private void pressAnyKey() {
        pressAnyKey("");
    }

    private void pressAnyKey(String msg) {
        if (!msg.isEmpty()) {
            println("\n" + msg);
        }
        println("\nPress any key to continue.");
        readConsole();
        clearConsole();
    }

    private void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private MongoCollection<Document> getCollection() {
        print("Contact list name: ");
        String collectionName = readConsole();
        return database.getCollection(collectionName);
    }
    
}
