/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodjassignment_group26;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author aidan
 */
public class ItemSalesEntry extends Entry {

    public ItemSalesEntry() {
    }

    @Override
    protected void addObject() throws IOException {
        Scanner scan = new Scanner(System.in);
        ArrayList<Item> items = fileToItemList();
        while (true) {
            // not putting scan in here the prompt will be skip
            System.out.print("type 'exit' to return or other input to continue: ");
            String id = scan.nextLine();
            if (id.equalsIgnoreCase("exit")) {
                System.out.println("Exiting add daily item sale...");
                return;
            }
            String itemSalesID = addID();
            Item selectedItem = chooseItem();
            String quantity = addQuantity(selectedItem);
            String date = generateDate();
            ItemSales itemSale = new ItemSales(itemSalesID, selectedItem, date, quantity);
            System.out.println("Please check is the information is correct?");
            System.out.printf("|%-12s |%-12s |%-15s |%-6s|\n", "itemSalesID", "itemID", "date", "quantity");
            itemSale.displayItemSales();
            System.out.print("Is this correct?('Y' to save, other to quit adding item): ");
            String choice = scan.next();
            if (choice.equals("Y")) {
                appendLineToTextFile("ItemSales_database.txt", itemSale.toString());
                System.out.println("The item quantity is deducted from the stock");
                for (Item item : items) {
                    if (item.getitemID().equals(selectedItem.getitemID())) {
                        int deductQuantity = Integer.parseInt(quantity);
                        if (item.getStock() >= deductQuantity) {
                            int finalStock = item.getStock() - deductQuantity;
                            System.out.println("The stock will be change from " + item.getStock() + " to " + finalStock);
                            item.setStock(Integer.toString(finalStock));
                            ItemEntry IE = new ItemEntry();
                            IE.saveObject(items);
                            return;
                        } else {
                            System.out.println("Stock not enough, Try again");
                            scan.nextLine();
                        }
                    }
                }

            } else {
                System.out.println("Returning to sales item menu");
                return;
            }
        }
    }

    @Override
    protected void editObject() throws IOException {
        ArrayList<ItemSales> itemSales = fileToItemSalesList();
        ArrayList<Item> items = fileToItemList();
        listObject(itemSales);
        ItemSales targetItemSale = null;
        Item targetItem = null;
        while (true) {
            Scanner scan = new Scanner(System.in);
            System.out.print("Enter itemSales ID to edit or (or type 'exit' to return): ");
            String id = scan.nextLine();
            if (id.equals("exit")) {
                System.out.println("Exiting item edit.");
                return;
            }
            if (!checkExistItemSalesID(id, itemSales)) {
                System.out.println("ItemSalesID is not exist");
                continue;
            }
            targetItemSale = null;
            for (ItemSales itemSale : itemSales) {
                if (itemSale.getItemSalesID().equals(id)) {
                    targetItemSale = itemSale;
                    break;
                }
            }
            for (Item item : items) {
                if (item.getitemID().equals(targetItemSale.getItem().getitemID())) {
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
            System.out.println("Which attribute you want to edit?(1 to 3)");
            System.out.println("1. Date = " + targetItemSale.getDate());
            System.out.println("2. Quantity = " + targetItemSale.getQuantity());
            System.out.print("\nPlease enter your choice: ");
            // Get user input
            String choice = scan.nextLine();
            // Edit item based on choice
            switch (choice) {
                case "1":
                    targetItemSale.setDate(editDate());
                    break;
                case "2":
                    editQuantity(targetItem, targetItemSale);
                    break;
                default:
                    System.out.println("INVALID INPUT.");
                    continue;
            }

            // Confirm save
            System.out.print("Save changes? Press 'Y' to confirm: ");
            String confirm = scan.nextLine();
            if (confirm.equals("Y")) {
                saveObject(itemSales);
                ItemEntry IE = new ItemEntry();
                IE.saveObject(items);
                return;
            } else {
                return;
            }
        }
    }

    @Override
    public void listObject() {
        ArrayList<ItemSales> itemSales = fileToItemSalesList();
        System.out.println("This is the item list");
        System.out.printf("|%-12s |%-12s |%-15s |%-10s|\n", "itemSalesID", "itemID", "date", "quantity");
        for (ItemSales itemSale : itemSales) {
            itemSale.displayItemSales();
        }
        System.out.println("");
    }

    public void listObject(ArrayList<ItemSales> itemSales) {
        System.out.println("This is the item list");
        System.out.printf("|%-12s |%-12s |%-15s |%-6s|\n", "itemSalesID", "itemID", "date", "quantity");
        for(int i = 0; i < 57; i++){
            System.out.print("-");
        }
        System.out.println("");
        for (ItemSales itemSale : itemSales) {
            itemSale.displayItemSales();
        }
        System.out.println("");
    }

    @Override
    protected void deleteObject() throws IOException {
        Scanner scan = new Scanner(System.in);
        ArrayList<Item> items = fileToItemList();
        ArrayList<ItemSales> itemSales = fileToItemSalesList();
        ItemEntry IE = new ItemEntry();
        listObject(itemSales);
        int returnQuantity = 0;
        String returnItemID = null;
        while (true) {
            System.out.print("Enter itemSales ID to delete or (or type 'exit' to return): ");
            String id = scan.nextLine();
            if (id.equals("exit")) {
                System.out.println("Exiting itemSales delete...");
                return;
            }
            if (!checkExistItemSalesID(id, itemSales)) {
                System.out.println("NON-EXISTENT ITEM SALES ID.");
                continue;
            }
            for (ItemSales itemSale : itemSales) {
                if (itemSale.getItemSalesID().equals(id)) {
                    itemSales.remove(itemSale);
                    returnQuantity = Integer.parseInt(itemSale.getQuantity());
                    returnItemID = itemSale.getItem().getitemID();
                    break;
                }
            }
            break;

        }
        System.out.println("Updated Sales item list:");
        listObject(itemSales);
        System.out.println("The item quantity has been refill back to the stock");
        for (Item item : items) {
            if (item.getitemID().equals(returnItemID)) {
                int finalStock = returnQuantity + item.getStock();
                System.out.println("The stock will be changed from " + item.getStock() + " to " + finalStock);
                item.setStock(Integer.toString(finalStock));
            }
        }
        System.out.print("Enter 'Y' if you want to save the changes, other input to cancel deletion: ");

        String selection = scan.nextLine();
        if (selection.equalsIgnoreCase("Y")) {
            saveObject(itemSales);
            IE.saveObject(items);

        }
    }

    @Override
    protected void menu() throws IOException {
        Scanner scan = new Scanner(System.in);
        OUTER:
        while (true) {
            System.out.println("""
                               Welcome to the Item Sales Entry Menu!
                               -------------------------------------""");
            System.out.println("Choose the action you want to take(1 to 5)");
            System.out.println("1. Add Daily Item-wise Sales ");
            System.out.println("2. List Daily Item-wise Sales");
            System.out.println("3. Delete Daily Item-wise Sales");
            System.out.println("4. Edit Daily Item-wise Sales");
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
                    System.out.println("Wrong input, try again");
                    break;
            }
        }
    }

    @Override
    public String addID() {
        Scanner scan = new Scanner(System.in);
        ArrayList<ItemSales> itemSales = fileToItemSalesList();
        String regexPattern = "^IS\\d{5}$";
        //^I\\d{5}$ mean is should be start by 'IS' and end by 5 digits
        while (true) {
            System.out.print("Please enter the itemSalesID(start with 'IS' then"
                    + " follow by 5 digit number): ");
            String ID = scan.next();
            Pattern pattern = Pattern.compile(regexPattern);
            Matcher matcher = pattern.matcher(ID);
            if (matcher.matches() && !checkExistItemSalesID(ID, itemSales)) {
                return ID;
            } else {
                System.out.println("INVALID ITEM SALES ID OR ID IS USED.");
            }
        }
    }

    // This is the save object 
    public void saveObject(ArrayList<ItemSales> itemSales) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("ItemSales_database.txt"))) {
            for (ItemSales itemSale : itemSales) {
                writer.write(itemSale.toString());
                writer.newLine();
            }
        }
    }

    //Choose item for itemSales
    public Item chooseItem() {
        Scanner scan = new Scanner(System.in);
        ItemEntry IE = new ItemEntry();
        ArrayList<Item> items = fileToItemList();
        while (true) {
            IE.listObject();
            System.out.println("Please select the itemID");
            System.out.print("Selection: ");
            String itemID = scan.nextLine();
            for (Item item : items) {
                if (item.getitemID().equals(itemID)) {
                    return item;
                }
            }
            System.out.println("Please enter the correct itemID");
        }
    }

    // This is for adding quantity (stock must be higher than request quantity)
    public String addQuantity(Item item) {
        Scanner scan = new Scanner(System.in);
        int quantity;
        String regexpattern = "\\d{1,5}";
        /*
        This only allow 1 to 5 integer
         */
        while (true) {
            System.out.print("Please enter the quantity of the item (max 5 integer): ");
            String temp = scan.nextLine();
            Pattern pattern = Pattern.compile(regexpattern);
            Matcher matcher = pattern.matcher(temp);
            if (matcher.matches()) {
                quantity = Integer.parseInt(temp);
                if (quantity > item.getStock()) {
                    System.out.println("NOT ENOUGH STOCKS. TRY AGAIN.");
                } else {
                    return temp;
                }
            } else {
                System.out.println("INVALID QUANTITY.");
            }
        }
    }

    //This will generate the date of today to mark the daily sales item
    public String generateDate() {
        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        String todayDate = date.format(now);
        return todayDate;
    }

    // make a function that can edit the number of the quantity once they return by edit or delete or add(assist function)
    // Check exist supplierID
    public boolean checkExistItemSalesID(String inputID, ArrayList<ItemSales> itemSales) {
        for (ItemSales itemSale : itemSales) {
            if (itemSale.getItemSalesID().equals(inputID)) {
                return true;
            }
        }
        return false;
    }

    //This is for changing date
    public String editDate() {
        Scanner scan = new Scanner(System.in);
        while (true) {
            System.out.println("Please enter a new date");
            String date = scan.nextLine();
            if (checkValidDate(date)) {
                return date;
            } else {
                System.out.println("Please enter a valid date");
            }
        }
    }

// This will check whether it is correct date format and before today on today
// As if SM want to edit they can only change the past date
    public static boolean checkValidDate(String dateStr) {

        try {
            // Specify date format  
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            // Get today's date
            LocalDate today = LocalDate.now();

            // Check date is before today
            if (date.isAfter(today)) {
                return false;
            }

        } catch (DateTimeParseException e) {
            // Invalid date format
            return false;
        }

        return true;
    }

    public void editQuantity(Item item, ItemSales itemSale) {
        Scanner scan = new Scanner(System.in);
        int itemQuantity = Integer.parseInt(itemSale.getQuantity());
        int newQuantity;
        String regexpattern = "\\d{1,5}";
        /*
        This only allow 1 to 5 integer
         */
        while (true) {
            while (true) {
                System.out.print("Please enter the quantity of the item(max 5 integer): ");
                String temp = scan.nextLine();
                Pattern pattern = Pattern.compile(regexpattern);
                Matcher matcher = pattern.matcher(temp);
                if (matcher.matches()) {
                    newQuantity = Integer.parseInt(temp);
                    break;
                } else {
                    System.out.println("Invalid Quantity! Try again.");
                }
            }
            if (newQuantity > itemQuantity) {
                int more = newQuantity - itemQuantity;
                if (more > item.getStock()) {
                    System.out.println("NOT ENOUGH STOCKS. TRY AGAIN.");
                } else {
                    int finalStock = item.getStock() - more;
                    System.out.println("The " + item.getName() + " stock will be "
                            + "change from " + item.getStock() + " to " + finalStock);
                    item.setStock(Integer.toString(finalStock));
                    itemSale.setQuantity(Integer.toString(newQuantity));
                    return;
                }
            } else if (newQuantity == itemQuantity) {
                System.out.println("There will be no changes made");
                return;
            } else {
                int refill = itemQuantity - newQuantity;
                int finalStock = item.getStock() + refill;
                System.out.println("The " + item.getName() + " stock will be "
                        + "change from " + item.getStock() + " to " + finalStock);
                item.setStock(Integer.toString(finalStock));
                itemSale.setQuantity(Integer.toString(newQuantity));
                return;
            }
        }
    }
}

