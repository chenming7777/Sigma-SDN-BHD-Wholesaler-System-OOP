/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodjassignment_group26;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
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
public abstract class Entry {
    // This function will turn the text file into the an item Arraylist
    public ArrayList<Item> fileToItemList() {
        ArrayList<Item> items = new ArrayList<>();
        ArrayList<Supplier> suppliers = fileToSupplierList();
        try {
            try (BufferedReader reader = new BufferedReader(new FileReader("Item_database.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    String id = parts[0];
                    String name = parts[1];
                    String price = parts[2];
                    String stock = parts[3];
                    String supplierID = parts[4];
                    String desc = "";
                    for (int i = 5; i < parts.length; i++) {
                        desc += parts[i];
                        if (i < parts.length - 1) {
                            desc += ",";
                        }
                    }
                    Supplier supplier = null;
                    for (Supplier sup : suppliers) {
                        if (sup.getSupplierID().equals(supplierID)) {
                            supplier = sup;
                            break;
                        }
                    }
                    if (supplier != null) {
                        Item item = new Item(id, name, desc, supplier, price, stock);
                        items.add(item);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("file error");
            return null;
        }
        return items;
    }

    // This function will turn the text file into the an supplier Arraylist
    public ArrayList<Supplier> fileToSupplierList() {
        ArrayList<Supplier> suppliers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("Supplier_database.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String id = parts[0];
                String name = parts[1];
                String number = parts[2];
                String email = parts[3];
                String address = "";
                for (int i = 4; i < parts.length; i++) {
                    address += parts[i];
                    if (i < parts.length - 1) {
                        address += ",";
                    }
                }
                Supplier supplier = new Supplier(id, name, number, email, address);
                suppliers.add(supplier);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return suppliers;
    }

    // This will put the itemsales file into a list
    public ArrayList<ItemSales> fileToItemSalesList() {
        ArrayList<ItemSales> itemSales = new ArrayList<>();
        ArrayList<Item> items = fileToItemList();
        try (BufferedReader reader = new BufferedReader(new FileReader("ItemSales_database.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String id = parts[0];
                String itemID = parts[1];
                String date = parts[2];
                String quantity = parts[3];
                Item targetItem = null;
                for (Item item : items) {
                    if (item.getitemID().equals(itemID)) {
                        targetItem = item;
                        break;
                    }
                }
                ItemSales itemsale = new ItemSales(id, targetItem, date, quantity);
                itemSales.add(itemsale);
            }
        } catch (IOException e) {
            System.out.println("File not exist");
        }
        return itemSales;
    }
    
    // append new line to text file
    protected void appendLineToTextFile(String filename, String lineToAppend) throws IOException {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename, true))) {
            bufferedWriter.write(lineToAppend);
            bufferedWriter.newLine();
        }
    }

    // Check exist item ID
    protected boolean checkExistItemID(String inputID, ArrayList<Item> items) {
        for (Item item : items) {
            if (item.getitemID().equals(inputID)) {
                return true;
            }
        }
        return false;
    }

    // Check exist supplierID
    protected boolean checkExistSupplierID(String inputID, ArrayList<Supplier> suppliers) {
        for (Supplier supplier : suppliers) {
            if (supplier.getSupplierID().equals(inputID)) {
                return true;
            }
        }
        return false;
    }

    // add Name for supplier or item
    protected String addName() {
        String regexPattern = "^[a-zA-Z]+(\\s[a-zA-Z]+)*$";
        Scanner scan = new Scanner(System.in);
        /*
            The ^ is the start of the string
            [a-zA-Z]+ means string should be start by one or more capital 
            or lower alphabet
            (\\s[a-zA-Z]+)* means the whitespace must be follow by at least one 
            alphabet
            * mean this string in bracket can be happen zero or more times
            $ is the end of the string
         */
        while (true) {
            System.out.print("Please enter the name(only alphabet and space and with total 20 character): ");
            String name = scan.nextLine();
            if (name.length() > 20) {
                System.out.println("Name is too long, Try again!");
                continue;
            }

            Pattern pattern = Pattern.compile(regexPattern);
            Matcher matcher = pattern.matcher(name);
            if (matcher.matches()) {
                return name;

            } else {
                System.out.println("Invalid item name! Try Again");
            }
        }
    }

    // This can be entering description for item or address for supplier
    protected String addString() {
        Scanner scan = new Scanner(System.in);
        while (true) {
            System.out.print("Please enter the string(40 characters max): ");
            String string = scan.nextLine();
            if (string.length() > 40) {
                System.out.println("Input is too long, Try again!");
                continue;
            }
            string = string.trim();
            System.out.print("Is the string correct? [" + string
                    + "] Y to confirm, other to retype: ");
            String temp = scan.nextLine();
            String choice = temp.trim();
            if (choice.equals("Y")) {
                return string;
            } else {
                System.out.println("Please enter again");
            }
        }
    }

    protected abstract void addObject() throws IOException;

    protected abstract void editObject() throws IOException;

    protected abstract void listObject();

    protected abstract void deleteObject() throws IOException;
    
    protected abstract void menu() throws IOException;
    
    protected abstract String addID();

}
