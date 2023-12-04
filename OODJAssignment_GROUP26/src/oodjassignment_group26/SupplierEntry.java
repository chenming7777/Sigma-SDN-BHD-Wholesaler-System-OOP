/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodjassignment_group26;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author aidan
 */
public class SupplierEntry extends Entry {

    public SupplierEntry() {

    }

    @Override
    // This os the add supplier function
    public void addObject() throws IOException {
        Scanner scan = new Scanner(System.in);
        System.out.print("type 'exit' to return or other input to continue: ");
        String id = scan.nextLine();
        if (id.equalsIgnoreCase("exit")) {
            System.out.println("Exiting add supplier...");
            return;
        }
        while (true) {
            String SupplierID = addID();
            String name = addName();
            String number = addNumber();
            String email = addEmail();
            System.out.println("Please enter the Supplier address");
            String address = addString();
            Supplier supplier = new Supplier(SupplierID, name, number, email, address);
            System.out.println("Please check if the information is correct?");
            System.out.printf("|%-12s |%-23s |%-15s |%-25s |%-40s|\n", "SupplierID", "Name", "Number", "Email", "Address");
            supplier.displaySupplier();
            System.out.print("Is this correct?('Y' to save, other to rewrite)");
            String choice = scan.nextLine();
            if (choice.equals("Y")) {
                appendLineToTextFile("Supplier_database.txt", supplier.toString());
                break;
            } else {
                System.out.println("You may enter again");
            }
        }
    }

    @Override
    // This will list supplier and let them edit
    public void editObject() throws IOException {
        Scanner scan = new Scanner(System.in);
        ArrayList<Supplier> suppliers = fileToSupplierList();
        String id;
        listObject(suppliers);
        Supplier targetSupplier = null;
        while (true) {
            System.out.print("Enter Supplier ID to edit (or type 'exit' to return): ");
            id = scan.nextLine();
            if (id.equals("exit")) {
                System.out.println("Exiting supplier edit...");
                return;
            }
            if (!checkExistSupplierID(id, suppliers)) {
                System.out.println("NON-EXISTENT SUPPLIER ID.");
                continue;
            }
            targetSupplier = null;
            for (Supplier supplier : suppliers) {
                if (supplier.getSupplierID().equals(id)) {
                    targetSupplier = supplier;
                    break;
                }
            }

            break;
        }
        while (true) {
            // Print menu
            System.out.println("Which attribute do you want to edit? (1 to 4)");
            System.out.println("1. Name = " + targetSupplier.getName());
            System.out.println("2. Number = " + targetSupplier.getNumber());
            System.out.println("3. Email = " + targetSupplier.getEmail());
            System.out.println("4. Address = " + targetSupplier.getAddress());
            System.out.print("Selection: ");
            String choice = scan.nextLine();
            switch (choice) {
                case "1":
                    targetSupplier.setName(addName());
                    break;
                case "2":
                    targetSupplier.setNumber(addNumber());
                    break;
                case "3":
                    targetSupplier.setEmail(addEmail());
                    break;
                case "4":
                    targetSupplier.setAddress(addString());
                    break;
                default:
                    System.out.println("INVALID INPUT.");
                    continue;
            }
            System.out.println("Updated Supplier List");
            listObject(suppliers);
            System.out.print("Save changes? Press 'Y' to confirm: ");
            String confirm = scan.nextLine();
            if (confirm.equals("Y")) {
                saveObject(suppliers);
                return;
            } else {
                return;
            }
        }
    }

    @Override
// this shows the list of supplier without input
    public void listObject() {
        ArrayList<Supplier> suppliers = fileToSupplierList();
        System.out.println("These are the available suppliers");
        System.out.printf("|%-12s |%-23s |%-15s |%-25s |%-40s|\n", "SupplierID", "Name", "Number", "Email", "Address");
        for (Supplier supplier : suppliers) {
            supplier.displaySupplier();
        }
        System.out.println("");
    }

    // this will show the list of supplier based on input
    public void listObject(ArrayList<Supplier> suppliers) {
        System.out.println("These are the available suppliers");
        System.out.printf("|%-12s |%-23s |%-15s |%-25s |%-40s|\n", "SupplierID", "Name", "Number", "Email", "Address");
        for (Supplier supplier : suppliers) {
            supplier.displaySupplier();
        }
        System.out.println("");
    }

    @Override
// This function will first list out all the supplier then user can pick to delete
    public void deleteObject() throws IOException {
        Scanner scan = new Scanner(System.in);
        ArrayList<Item> items = fileToItemList();
        ArrayList<Supplier> suppliers = fileToSupplierList();
        listObject();
        while (true) {
            System.out.print("Enter Supplier ID to delete or (or type 'exit' to return) ");
            String id = scan.nextLine();
            if (id.equalsIgnoreCase("exit")) {
                System.out.println("Exiting supplier delete...");
                return;
            }
            if (!checkExistSupplierID(id, suppliers)) {
                System.out.println("NON-EXISTENT SUPPLIER ID.");
                continue;
            }
            for (Item item : items) {
                if (id.equals(item.getSupp().getSupplierID())) {
                    System.out.println("This ID is supplying item if you continue"
                            + " delete all the item will be gone too");
                    System.out.print("Press 'Y' to delete other input to return");
                    String selection = scan.nextLine();
                    if (!selection.equals("Y")) {
                        System.out.println("You will be returned to supplier menu");
                        return;
                    } else {
                        break;
                    }
                }
            }

            for (Supplier supplier : suppliers) {
                if (supplier.getSupplierID().equals(id)) {
                    suppliers.remove(supplier);
                    System.out.println(supplier.getName() + " deleted");
                    break;
                }
            }
            Iterator<Item> iterator = items.iterator();
            while (iterator.hasNext()) {
                Item item = iterator.next();
                if (id.equals(item.getSupp().getSupplierID())) {
                    iterator.remove();
                    System.out.println(item.getName() + " deleted");
                }
            }
            break;
        }
        System.out.println("Updated Supplier List");
        listObject(suppliers);
        System.out.println("=====================================");
        System.out.println("Updated Item List:");
        ItemEntry IE = new ItemEntry();
        IE.listObject(items);
        System.out.println("Enter 'Y' if you want to save the changes, other input to cancel deletion");
        String selection = scan.nextLine();
        if (selection.equalsIgnoreCase("Y")) {
            IE.saveObject(items);
            saveObject(suppliers);
        }
    }

    // save the item info into the text file
    public void saveObject(ArrayList<Supplier> suppliers) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Supplier_database.txt"))) {
            for (Supplier supplier : suppliers) {
                writer.write(supplier.toString());
                writer.newLine();
            }
        }
    }

    @Override
    // This is the menu of supplier
    public void menu() throws IOException {
        Scanner scan = new Scanner(System.in);
        OUTER:
        while (true) {
            System.out.println("""
                               Welcome to the Supplier Entry Menu!
                               -----------------------------------""");
            System.out.println("Choose the action you want to take(1 to 5)");
            System.out.println("1. Add Supplier");
            System.out.println("2. List Supplier");
            System.out.println("3. Delete Supplier");
            System.out.println("4. Edit Supplier");
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

    @Override
    // This will add the supplierID
    public String addID() {
        Scanner scan = new Scanner(System.in);
        ArrayList<Supplier> suppliers = fileToSupplierList();
        String regexPattern = "^S\\d{5}$";
        // This means that it will start with S and follow by 5 digit
        while (true) {
            System.out.print("Enter supplier ID (start with 'S' followed by 5 digits): ");
            String id = scan.nextLine();
            Pattern pattern = Pattern.compile(regexPattern);
            Matcher matcher = pattern.matcher(id);
            if (matcher.matches() && !checkExistSupplierID(id, suppliers)) {
                return id;
            } else {
                System.out.println("INVALID ID. TRY AGAIN.");
            }
        }
    }

    //This is to add number
    public String addNumber() {
        Scanner scan = new Scanner(System.in);
        String regexPattern = "^01\\d{8,9}$";
        /*
            The ^ is the start of the string
            01 means it can only be start with 01 as malaysia number also start
            with 01
            \\d(8,9}$ means it will be end by 8 or 9 following numbers
         */
        while (true) {
            System.out.print("Please enter Supplier contact number: ");
            String number = scan.next();
            number = number.trim();
            Pattern pattern = Pattern.compile(regexPattern);
            Matcher matcher = pattern.matcher(number);
            if (matcher.matches()) {
                return number;
            } else {
                System.out.println("INVALID NUMBER FORMAT.");
            }
        }
    }

    //This is to add email
    public String addEmail() {
        Scanner scan = new Scanner(System.in);
        String regexPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        /*
            ^: Asserts the start of the string.
            [a-zA-Z0-9._%+-]+: Matches one or more of the allowed characters for the username part of the email.
            @: Matches the '@' symbol literally.
            [a-zA-Z0-9.-]+: Matches one or more of the allowed characters for the domain name part of the email.
            \\.: Matches the '.' character literally (needs to be escaped with backslash).
            [a-zA-Z]{2,}: Matches the top-level domain (TLD) part of the email, which consists of at least two letters.
            $: Asserts the end of the string.
         */
        while (true) {
            System.out.print("Please enter Supplier email: ");
            String email = scan.next();
            email = email.trim();
            Pattern pattern = Pattern.compile(regexPattern);
            Matcher matcher = pattern.matcher(email);
            if (matcher.matches()) {
                return email;
            } else {
                System.out.println("INVALID EMAIL FORMAT.");
            }
        }
    }

}

