/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodjassignment_group26;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author aidan
 */
public class ItemEntry extends Entry {

    public ItemEntry() {

    }

    @Override
    // This function will help you to add item
    public void addObject() throws IOException {
        Scanner scan = new Scanner(System.in);
        while (true) {
            // not putting scan in here the prompt will be skip
            System.out.print("type 'exit' to return or other input to continue: ");
            String id = scan.nextLine();
            if (id.equalsIgnoreCase("exit")) {
                System.out.println("Exiting add item...");
                return;
            }
            String itemID = addID();
            String name = addName();
            System.out.println("Please enter your description:");
            String description = addString();
            String price = addPrice();
            String stock = addStock();
            Supplier supplier = chooseSupplier();
            Item item = new Item(itemID, name, description, supplier, price, stock);
            System.out.println("Please check is the information is correct?");
            System.out.printf("|%-12s |%-12s |%-30s |%-15s |%-10s |%-40s|\n", "ItemID", "SupplierID", "Name", "Price", "Stock", "Description");
            item.displayItem();
            System.out.print("Is this correct?('Y' to save, other to quit adding item): ");
            String choice = scan.next();
            if (choice.equals("Y")) {
                appendLineToTextFile("Item_database.txt", item.toString());
                break;
            } else {
                return;
            }
        }
    }

    @Override
    // This function will edit item info,if itemID not exist it will kick out.
    public void editObject() throws IOException {
        ArrayList<Item> items = fileToItemList();
        listObject(items);
        String id;
        Item targetItem = null;
        while (true) {
            Scanner scan = new Scanner(System.in);
            System.out.print("Enter item ID to edit or (or type 'exit' to return): ");
            id = scan.nextLine();
            if (id.equals("exit")) {
                System.out.println("Exiting item edit...");
                return;
            }
            if (!checkExistItemID(id, items)) {
                System.out.println("NON-EXISTENT ITEM ID.");
                continue;
            }
            targetItem = null;
            for (Item item : items) {
                if (item.getitemID().equals(id)) {
                    targetItem = item;
                    break;
                }
            }
            break;
        }
        while (true) {
            // Print menu
            // ItemID cannot be change 
            Scanner scan = new Scanner(System.in);
            System.out.println("Which attribute you want to edit? (1 to 5)");
            System.out.println("1. Name = " + targetItem.getName());
            System.out.println("2. Description = " + targetItem.getDesc());
            System.out.println("3. Price = " + targetItem.getPrice());
            System.out.println("4. Stock = " + targetItem.getStock());
            System.out.println("5. Supplier = " + targetItem.getSupp().getSupplierID());
            System.out.print("Selection: ");
            // Get user input
            String choice = scan.next();
            // Edit item based on choice
            switch (choice) {
                case "1":
                    targetItem.setName(addName());
                    break;
                case "2":
                    targetItem.setDesc(addString());
                    break;
                case "3":
                    targetItem.setPrice(addPrice());
                    break;
                case "4":
                    targetItem.setStock(addStock());
                    break;
                case "5":
                    targetItem.setSupp(chooseSupplier());
                    break;
                default:
                    System.out.println("INVALID INPUT.");
                    continue;
            }
            // Confirm save
            System.out.println("Updated Item List");
            listObject(items);
            System.out.print("Save changes? Press 'Y' to confirm: ");
            scan.nextLine();
            String confirm = scan.nextLine();
            if (confirm.equals("Y")) {
                saveObject(items);
                return;
            } else {
                return;
            }
        }
    }

    @Override
    // This function will help you to list out all the items
    public void listObject() {
        ArrayList<Item> items = fileToItemList();
        System.out.println("This is the item list");
        System.out.printf("|%-12s |%-12s |%-30s |%-15s |%-10s |%-40s|\n", "ItemID", "SupplierID", "Name", "Price", "Stock", "Description");
        for (Item item : items) {
            item.displayItem();
        }
        System.out.println("");
    }

    // This function is used when the arraylist need to be used    
    public void listObject(ArrayList<Item> items) {
        System.out.println("This is the item list");
        System.out.printf("|%-12s |%-12s |%-30s |%-15s |%-10s |%-40s|\n", "ItemID", "SupplierID", "Name", "Price", "Stock", "Description");
        for (Item item : items) {
            item.displayItem();
        }
        System.out.println("");
    }

    @Override
    // This function will first list out all the item then user can pick to delete
    public void deleteObject() throws IOException {
        Scanner scan = new Scanner(System.in);
        ArrayList<Item> items = fileToItemList();
        listObject();
        while (true) {
            System.out.print("Enter item ID to delete or (or type 'exit' to return): ");
            String id = scan.nextLine();
            if (id.equalsIgnoreCase("exit")) {
                System.out.println("Exiting item delete...");
                return;
            }
            if (!checkExistItemID(id, items)) {
                System.out.println("NON-EXISTENT ITEM ID.");
                continue;
            }
            for (Item item : items) {
                if (item.getitemID().equals(id)) {
                    items.remove(item);
                    System.out.println(item.getName() + " deleted");
                    break;
                }
            }
            break;
        }
        System.out.println("Updated list:");
        listObject(items);
        System.out.print("Enter 'Y' if you want to save the changes, other input to cancel deletion: ");
        String selection = scan.nextLine();
        if (selection.equalsIgnoreCase("Y")) {
            saveObject(items);
        }
    }

    // save the item info into the text file
    public void saveObject(ArrayList<Item> items) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Item_database.txt"))) {
            for (Item item : items) {
                writer.write(item.toString());
                writer.newLine();
            }
        }
    }

    @Override
    // add item function will prevent duplicate itemID
    public String addID() {
        Scanner scan = new Scanner(System.in);
        ArrayList<Item> items = fileToItemList();
        String regexPattern = "^I\\d{5}$";
        //^I\\d{5}$ mean is should be start by 'I' and end by 5 digits
        while (true) {
            System.out.print("Please enter the itemID(start with 'I' then"
                    + " follow by 5 digit number): ");
            String ID = scan.next();
            Pattern pattern = Pattern.compile(regexPattern);
            Matcher matcher = pattern.matcher(ID);
            if (matcher.matches() && !checkExistItemID(ID, items)) {
                return ID;
            } else {
                System.out.println("INVALID ITEM ID OR ID IS USED.");
            }
        }
    }

    //Choose supplier for item
    public Supplier chooseSupplier() {
        Scanner scan = new Scanner(System.in);
        ArrayList<Supplier> suppliers = fileToSupplierList();
        SupplierEntry SE = new SupplierEntry();
        while (true) {
            SE.listObject();
            System.out.println("Please select the SupplierID who supplied the item");
            System.out.print("Selection: ");
            String supplierID = scan.nextLine();
            for (Supplier supplier : suppliers) {
                if (supplier.getSupplierID().equals(supplierID)) {
                    return supplier;
                }
            }
            System.out.println("INVALID SUPPLIER ID.");
        }
    }

    // This is for adding quantity
    public String addStock() {
        Scanner scan = new Scanner(System.in);
        String regexpattern = "\\d{1,5}";
        /*
        This only allow 1 to 5 integer
         */
        while (true) {
            System.out.print("Please enter the stock of the item (max 5 character): ");
            String stock = scan.nextLine();
            Pattern pattern = Pattern.compile(regexpattern);
            Matcher matcher = pattern.matcher(stock);
            if (matcher.matches()) {
                return stock;
            } else {
                System.out.println("INVALID QUANTITY.");
            }
        }
    }

    //This is for adding the price
    public String addPrice() {
        Scanner scan = new Scanner(System.in);
        String regexpattern = "\\d+\\.\\d{2}";
        while (true) {
            System.out.print("Please enter the price of the item (numeric value with 2 decimal places): ");
            String price = scan.nextLine();
            Pattern pattern = Pattern.compile(regexpattern);
            Matcher matcher = pattern.matcher(price);
            if (matcher.matches()) {
                return price;
            } else {
                System.out.println("INVALID PRICE.");
            }
        }
    }

    @Override
    // This is the menu of item
    public void menu() throws IOException {
        Scanner scan = new Scanner(System.in);
        OUTER:
        while (true) {
            System.out.println("""
                               Welcome to the Item Entry Menu!
                               -------------------------------""");
            System.out.println("Choose the action you want to take (1 to 6)");
            System.out.println("1. Add Item");
            System.out.println("2. List Item");
            System.out.println("3. Delete Item");
            System.out.println("4. Edit Item");
            System.out.println("5. Quit");
            System.out.print("\nPlease enter your choice:");
            String choice = scan.next();
            switch (choice) {
                case "1":
                    addObject();
                    break;
                case "2":
                    listObject();
                    break;
                case "3":
                    deleteObject();
                    break;
                case "4":
                    editObject();
                    break;
                case "5":
                    break OUTER;
                default:
                    System.out.println("INVALID INPUT.");
                    break;
            }
        }
    }

    public void notifyLowStock() {
        ArrayList<Item> items = fileToItemList();
        boolean flag = false;
        for (Item item : items) {
            if (item.getStock() <= 20) {
                flag = true;
                break;
            }
        }
        if (flag) {
            System.out.println("**LOW STOCK REMINDER**");
            System.out.print("ItemID: ");
            for (Item item : items) {
                if (item.getStock() <= 20) {
                    System.out.print(item.getitemID() + " ");
                }
            }
            System.out.println("is lower than 20.");
        }
    }
}

