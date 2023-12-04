/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodjassignment_group26;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author aidan
 */
public class PurchaseReq extends Purchase {

    private static int nextPRID = 1;
    private String prID, reqBy, smID, status;
    private List<PurchaseReqItem> items;
    private Date date;

    public PurchaseReq(userInfo loggedInUser) {
        this.prID = generateID();
        this.reqBy = loggedInUser.getName(); // automatically get name from user logged in info
        this.smID = loggedInUser.getID(); // automatically get id from user logged in info
        this.items = new ArrayList<>();
        this.status = "PENDING";
    }

    public Date getDate() {
        return date;
    }

    public List<PurchaseReqItem> getItems() {
        return items;
    }

    public static int getNextPRID() {
        return nextPRID;
    }

    public String getPrID() {
        return prID;
    }

    public String getReqBy() {
        return reqBy;
    }

    public String getSmID() {
        return smID;
    }

    public String getStatus() {
        return status;
    }

    // generate PR method
    public void generatePR(userInfo loggedInUser) {
        Scanner scan = new Scanner(System.in);
        ItemEntry IE = new ItemEntry();
        System.out.println("Generating Purchase Requisition...");
        System.out.println("----------------------------------");

        displayItems(loggedInUser);

        File pr = new File("pr.txt");
        if (!pr.exists()) {
            try {
                pr.createNewFile();
            } catch (IOException e) {
                System.out.println("ERROR CREATING NEW PR FILE.");
            }
        }

        try (BufferedReader br = new BufferedReader(new FileReader("pr.txt"))) {
            List<PurchaseReqItem> newItems = new ArrayList<>();
            List<String> reqItems = new ArrayList<>();
            while (true) {
                System.out.println("Enter item code to add to PR, enter '-1' to stop:");
                String itemID = scan.nextLine().toUpperCase();

                if (itemID.equals("-1")) {
                    break;
                }

                if (itemID.isEmpty()) {
                    System.out.println("itemID cannot be empty");
                    continue;
                }

                Item item = findItem(itemID);
                if (item != null) {

                    if (reqItems.contains(itemID)) {
                        System.out.println("DUPLICATE ITEM IN PR.");
                        continue;
                    }

                    System.out.println("Enter quantity:");
                    String quantityStr = scan.nextLine();

                    if (quantityStr.isEmpty()) {
                        System.out.println("QUANTITY CANNOT BE LEFT EMPTY.");
                    } else {
                        try {
                            int quantity = Integer.parseInt(quantityStr);
                            if (quantity <= 0) {
                                System.out.println("QUANTITY CANNOT BE LESS THAN OR EQUAL TO 0!");
                                continue;
                            }

                            newItems.add(new PurchaseReqItem(item, quantity));
                            reqItems.add(itemID);
                            System.out.println("Item " + item.getitemID() + " added to PR with quantity " + quantity);
                        } catch (NumberFormatException e) {
                            System.out.println("INVALID NUMBER. PLEASE REINPUT ITEM INFO.");
                        }
                    }
                } else {
                    System.out.println("ITEM CODE NOT FOUND.");
                }
            }

            if (newItems.isEmpty()) {
                System.out.println("NO ITEMS ADDED TO PR. REDIRECTING USER BACK TO MENU...");
                directUser(loggedInUser);
            }

            boolean validDate = false;
            Date requiredDate = null;
            while (!validDate) {
                System.out.println("Enter the required date in YYYY-MM-DD format:");
                String dateStr = scan.nextLine();

                if (isValidDate(dateStr)) {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        requiredDate = df.parse(dateStr);
                        validDate = true;
                    } catch (ParseException e) {
                        System.out.println("INVALID DATE FORMAT.");
                    }
                } else {
                    System.out.println("INVALID DATE. MUST BE AT LEAST 1 WEEK FROM CURRENT DATE AND LESS THAN 1 YEAR FROM CURRENT DATE.");
                }
            }

            float tp = calculateTotalPrice(newItems);
            System.out.println("Total Price: RM" + tp);

            String prID = generateID();

            saveToPRFile(prID, loggedInUser.getName(), loggedInUser.getID(), requiredDate, "PENDING", tp);
            saveToPRItemsFile(prID, newItems);

            System.out.println("Purchase Requisition saved successfully.");
        } catch (IOException e) {
            System.out.println("ERROR READING PR FILE.");
            directUser(loggedInUser);
        }
    }

    // after user enters -1 to stop adding items into new pr, this method will total up the price of all items
    private float calculateTotalPrice(List<PurchaseReqItem> items) {
        float totalPrice = 0;
        for (PurchaseReqItem item : items) {
            Item product = item.getItem();
            float itemPrice = product.getPrice();
            int quantity = item.getQuantity();
            totalPrice += itemPrice * quantity;
        }
        return totalPrice;
    }

    // displays the items (changed)
    private static void displayItems(userInfo loggedInUser) {
        ItemEntry IE = new ItemEntry();
        IE.listObject();
    }

    @Override
    // generates next prID based on last number in the txt file. eg. if last number is PR002, itll take last 3 digits and +001
    public String generateID() {
        int maxPRID = 0;
        try (BufferedReader br = new BufferedReader(new FileReader("pr.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 1 && parts[0].startsWith("PR")) {
                    int prNumber = Integer.parseInt(parts[0].substring(2));
                    maxPRID = Math.max(maxPRID, prNumber);
                }
            }
        } catch (IOException e) {
            System.out.println("ERROR READING PR FILE.");
        }

        nextPRID = maxPRID + 1;
        return String.format("PR%03d", nextPRID);
    }

    @Override
    // displays all PRs from txt file(change print format)
    public void display(userInfo loggedInUser) {
        try {
            BufferedReader prReader = new BufferedReader(new FileReader("pr.txt"));
            String prLine;
            System.out.printf("|%-12s |%-20s |%-15s |%-20s |%-15s |%-17s|\n", "PR ID", "Requestor", "Requestor ID", "Date Required By", "Status", "Total Price");
            while ((prLine = prReader.readLine()) != null) {
                String[] prParts = prLine.split(",");
                System.out.printf("|%-12s |%-20s |%-15s |%-20s |%-15s |%-17s|\n", prParts[0], prParts[1], prParts[2], prParts[3], prParts[4], prParts[5].substring(11));
            }

        } catch (IOException e) {
            System.out.println("ERROR DISPLAYING PR(S).");
        }
    }

    @Override
    // searches through pr txt file to see if there is existing prID in the txt file
    public boolean search(String prID) {
        if(!checkExistPRID(prID)){
            return false;
        }
        try {
            BufferedReader prReader = new BufferedReader(new FileReader("pr.txt"));
            BufferedReader itemReader = new BufferedReader(new FileReader("prItems.txt"));
            String prLine;
            while ((prLine = prReader.readLine()) != null) {
                if (prLine.startsWith(prID)) {
                    String[] prParts = prLine.split(",");
                    System.out.println("PR ID: " + prID);
                    System.out.println("Requestor: " + prParts[1]);
                    System.out.println("Requestor ID: " + prParts[2]);
                    System.out.println("Date Required By: " + prParts[3]);
                    System.out.println("Status: " + prParts[4]);
                    System.out.println("Total Price: " + prParts[5].substring(11));
                    System.out.println("\nItemList:");
                    System.out.printf("|%-12s |%-12s |%-12s |%-20s|\n", "ItemID", "Quantity", "SupplierID", "Total Item Price");
                    String itemLine;
                    while ((itemLine = itemReader.readLine()) != null) {
                        if (itemLine.startsWith(prID)) {
                            String[] split = itemLine.split(",");
                            System.out.printf("|%-12s |%-12s |%-12s |%-20s|\n", split[1], split[2], split[3], split[4]);
                        }
                    }
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            System.out.println("ERROR READING PR AND/OR PR ITEMS FILE.");
            return false;
        }
    }

    public static boolean checkExistPRID(String prID) {
        try {
            BufferedReader prReader = new BufferedReader(new FileReader("pr.txt"));
            String prLine;
            while ((prLine = prReader.readLine()) != null) {
                String[] prParts = prLine.split(",");
                if (prParts[0].equals(prID)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("ERROR READING PR AND/OR PR ITEMS FILE.");
            return false;
        }
        return false;
    }

    // see if itemID exists in txt file (have to change)
    public static boolean isValidItem(String itemCode) {

        try {
            List<String> itemLines = Files.readAllLines(Paths.get("Item_database.txt"));

            for (String line : itemLines) {
                String[] parts = line.split(",");
                String code = parts[0];

                if (code.equals(itemCode)) {
                    return true;
                }
            }

        } catch (IOException e) {
            System.out.println("ERROR READING ITEMS INFO FILE.");
        }

        return false;
    }

    // save to pr file after generating new pr
    private void saveToPRFile(String prID, String reqBy, String smID, Date requiredDate, String status, float tp) {
        boolean append = false;

        try { // try catch statement is just to prevent first line in txt file from being empty
            if (Files.size(Paths.get("pr.txt")) > 0) {
                append = true;
            }
        } catch (IOException e) {
            System.out.println("PR FILE NOT FOUND. CREATING PR FILE...");
        }

        try (FileWriter fw = new FileWriter("pr.txt", append)) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

            fw.write(prID + "," + reqBy + "," + smID + "," + df.format(requiredDate) + "," + status
                    + ",TotalPrice=RM" + tp + System.lineSeparator());

        } catch (IOException e) {
            System.out.println("AN ERROR OCCURRED WHILE TRYING TO SAVE TO PR FILE.");
        }
    }

    // save to prItems file after generating pr
    private void saveToPRItemsFile(String prID, List<PurchaseReqItem> items) {
        boolean append = false;

        try {
            if (Files.size(Paths.get("prItems.txt")) > 0) {
                append = true;
            }
        } catch (IOException e) {
            System.out.println("PR ITEMS FILE NOT FOUND. CREATING PR ITEMS FILE...");
        }

        try (FileWriter fw = new FileWriter("prItems.txt", append)) {
            for (PurchaseReqItem read : items) {

                float price = read.getItem().getPrice();
                int quantity = read.getQuantity();
                float totalPrice = price * quantity;

                fw.write(prID + "," + read.getItem().getitemID() + "," + quantity
                        + "," + read.getItem().getSupp().getSupplierID() + ",RM" + totalPrice + System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println("ERROR SAVING TO PR ITEMS FILE.");
        }
    }

    // for editing the pr for date, changing/adding item and changing quantity/status (cancel for normal user, other status for admin)
    public static void updatePR(String prCode, userInfo loggedInUser) {
        try {
            int opt = 0;
            boolean stop = false;

            Scanner ch = new Scanner(System.in);

            List<String> lines = Files.readAllLines(Paths.get("pr.txt"));
            List<String> Itemlines = Files.readAllLines(Paths.get("prItems.txt"));

            List<String> updatedLines = new ArrayList<>();
            List<String> updatedLines2 = new ArrayList<>();

            if (loggedInUser.getRole().equals("sm")) { // admin can update PR despite any status just in case they want to update status
                for (String readline : lines) { // check status of selected PR
                    if (readline.startsWith(prCode)) {
                        String[] splitline = readline.split(",");
                        if (splitline[4].equals("APPROVED")) {
                            stop = true;
                            break;
                        }
                    }
                }

                if (stop) {
                    System.out.println("CANNOT EDIT PR AS IT IS APPROVED.");
                    directUser(loggedInUser);
                }
            }

            System.out.println("1. Date");
            System.out.println("2. Item");
            System.out.println("3. Quantity");
            System.out.println("4. Add item");
            if (loggedInUser.getRole().equals("sm")) {
                System.out.println("5. Cancel PR Status");
            } else {
                System.out.println("5. Change PR Status"); // if role is not admin, then user can only cancel; if it is admin, admin can change to whatever status they want
            }
            System.out.println("6. Back to Menu");

            // change status in separate method as update is only for SM and admin but PM must change status
            System.out.println("Enter your choice:");
            try {
                opt = Integer.valueOf(ch.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }

            switch (opt) {
                case 1:
                    System.out.println("Enter the new date:");
                    String newDate = ch.nextLine();
                    if (isValidDate(newDate)) {
                        for (String read : lines) {

                            if (read.startsWith(prCode)) {

                                String[] parts = read.split(",");
                                parts[3] = newDate;

                                read = String.join(",", parts);
                            }

                            updatedLines.add(read);
                        }
                        Files.write(Paths.get("pr.txt"), updatedLines); // overwrite file with updated list
                        System.out.println("Purchase Requisition date successfully updated.");
                    } else {
                        System.out.println("INVALID DATE. MUST BE AT LEAST 1 WEEK FROM CURRENT DATE AND LESS THAN 1 YEAR FROM CURRENT DATE.");
                        directUser(loggedInUser);
                    }
                    break;
                case 2:
                    Scanner scan = new Scanner(System.in);

                    boolean itemFound = false;

                    float oldPrice2;
                    float newPrice2;
                    float totalPR2;

                    String newSupp;

                    displayItems(loggedInUser);

                    System.out.println("Enter item ID to change:");
                    String oldItemID = scan.nextLine().toUpperCase();

                    System.out.println("Enter new item ID:");
                    String newItemID = scan.nextLine().toUpperCase();

                    if (!isValidItem(newItemID)) {
                        System.out.println("INVALID ITEM.");
                        directUser(loggedInUser); // if itemID does not exist, it will redirect user back to respective menu
                    }

                    if (oldItemID.equals(newItemID)) {
                        System.out.println("IT IS THE SAME ITEM.");
                        directUser(loggedInUser); // if same item, also will redirect user back to respective menu
                    }

                    boolean foundItem = false;

                    for (String readItemInPr : Itemlines) {
                        if (readItemInPr.startsWith(prCode) && readItemInPr.contains(oldItemID)) {
                            foundItem = true;
                            break;
                        } else {
                            foundItem = false;
                        }
                    }

                    if (!foundItem) {
                        System.out.println("ITEM NOT FOUND IN PR.");
                        directUser(loggedInUser);
                    }

                    for (String readPR : lines) {
                        if (readPR.startsWith(prCode)) {
                            totalPR2 = getTotalPricePR(readPR);

                            for (String read : Itemlines) {

                                if (read.startsWith(prCode) && read.contains(oldItemID)) {
                                    Item item = findItem(newItemID);

                                    if (item != null) {
                                        newSupp = String.valueOf(item.getSupp().getSupplierID());
                                        newPrice2 = item.getPrice();

                                        // getting old item price and old item quantity
                                        oldPrice2 = getItemTotalPrice(read);
                                        int quant = getItemTotalQuantity(read);

                                        // updating totals
                                        newPrice2 *= quant;
                                        totalPR2 -= oldPrice2;
                                        totalPR2 += newPrice2;

                                        String updatedPR = updatePRTotal(readPR, totalPR2);
                                        String updatedItem = updateItemPrice(read, newItemID, newPrice2, newSupp);
                                        updatedLines.add(updatedPR);
                                        updatedLines2.add(updatedItem);
                                        itemFound = true;
                                    }

                                } else {
                                    updatedLines2.add(read);
                                }
                            }
                        } else {
                            updatedLines.add(readPR);
                        }
                    }

                    if (!itemFound) {
                        System.out.println("ITEM NOT FOUND IN PR.");
                    }

                    Files.write(Paths.get("pr.txt"), updatedLines);
                    Files.write(Paths.get("prItems.txt"), updatedLines2);
                    System.out.println("Purchase Requisition successfully updated.");
                    break;
                case 3:
                    Scanner sc = new Scanner(System.in);
                    float oldPriceQ;
                    float newPriceQ;
                    float priceItem;
                    float totalPriceQ;

                    boolean itemFoundQ = false;

                    System.out.println("Enter item ID to change:");
                    String ItemIDQ = sc.nextLine().toUpperCase();

                    boolean foundItem2 = false;

                    for (String readItemInPr : Itemlines) {
                        if (readItemInPr.startsWith(prCode) && readItemInPr.contains(ItemIDQ)) {
                            foundItem2 = true;
                            break;
                        } else {
                            foundItem2 = false;
                        }
                    }

                    if (!foundItem2) {
                        System.out.println("ITEM NOT FOUND IN PR.");
                        directUser(loggedInUser);
                    }

                    System.out.println("Enter new quantity:");
                    String newQtyStr = sc.nextLine();

                    if (newQtyStr.isEmpty()) {
                        System.out.println("QUANTITY CANNOT BE LEFT EMPTY.");
                        directUser(loggedInUser);
                    } else {
                        try {
                            int newQty = Integer.parseInt(newQtyStr);

                            if (newQty <= 0) {
                                System.out.println("QUANTITY CANNOT BE LESS THAN OR EQUAL TO 0!");
                                directUser(loggedInUser);
                            }

                            for (String readPR : lines) {
                                if (readPR.startsWith(prCode)) {
                                    totalPriceQ = getTotalPricePR(readPR);

                                    for (String readItem : Itemlines) {

                                        if (readItem.startsWith(prCode) && readItem.contains(ItemIDQ)) {
                                            Item item = findItem(ItemIDQ);

                                            if (item != null) {
                                                priceItem = item.getPrice();
                                                int oldQty = getItemTotalQuantity(readItem);

                                                oldPriceQ = getItemTotalPrice(readItem);

                                                // updating item total price and pr total price
                                                String updatedItem = updateItemPrice(readItem, newQty, priceItem);
                                                newPriceQ = getItemTotalPrice(updatedItem);
                                                totalPriceQ -= oldPriceQ;
                                                totalPriceQ += newPriceQ;

                                                String updatedPR = updatePRTotal(readPR, totalPriceQ);
                                                updatedLines.add(updatedPR);
                                                updatedLines2.add(updatedItem);
                                                itemFoundQ = true;
                                            }

                                        } else {
                                            updatedLines2.add(readItem);
                                        }
                                    }
                                } else {
                                    updatedLines.add(readPR);
                                }
                            }

                            if (!itemFoundQ) {
                                System.out.println("ITEM NOT FOUND IN PR.");
                            }

                            Files.write(Paths.get("pr.txt"), updatedLines);
                            Files.write(Paths.get("prItems.txt"), updatedLines2);
                            System.out.println("Purchase Requisition successfully updated.");
                        } catch (NumberFormatException e) {
                            System.out.println("INVALID NUMBER.");
                            break;
                        }
                    }

                    break;
                case 4:
                    Scanner scanA = new Scanner(System.in);
                    float itemAPrice,
                     totalAPrice,
                     totalPricePR;
                    int qAddItem;
                    String newSuppID,
                     newAItemID,
                     addNewLine;
                    int count = 0;

                    displayItems(loggedInUser);
                    System.out.println("Enter item ID to add:");
                    String addID = scanA.nextLine().toUpperCase();

                    if (!isValidItem(addID)) {
                        System.out.println("NOT A VALID ITEM!");
                        directUser(loggedInUser);
                    }

                    for (String read : Itemlines) {
                        if (read.startsWith(prCode) && read.contains(addID)) {
                            System.out.println("ITEM IS ALREADY IN PR, NO DUPLICATE ITEMS ALLOWED.");
                            directUser(loggedInUser);
                        }
                    }

                    System.out.println("Enter quantity of item to add:");
                    String qaddID = scanA.nextLine();

                    if (qaddID.isEmpty()) {
                        System.out.println("QUANTITY CANNOT BE LEFT EMPTY.");
                        break;
                    }

                    try {
                        qAddItem = Integer.parseInt(qaddID);

                        if (qAddItem <= 0) {
                            System.out.println("QUANTITY CANNOT BE LESS THAN OR EQUAL TO 0!");
                            directUser(loggedInUser);
                        }

                        Item item = findItem(addID);

                        if (item == null) {
                            directUser(loggedInUser);
                        }

                        itemAPrice = item.getPrice();
                        totalAPrice = itemAPrice * qAddItem;

                        newSuppID = String.valueOf(item.getSupp().getSupplierID());
                        newAItemID = item.getitemID();

                        addNewLine = prCode + "," + newAItemID + "," + qAddItem + "," + newSuppID
                                + ",RM" + totalAPrice;

                        for (String read : lines) {
                            if (read.startsWith(prCode)) {
                                totalPricePR = getTotalPricePR(read);
                                totalPricePR += totalAPrice;
                                String updatedPR = updatePRTotal(read, totalPricePR);

                                updatedLines.add(updatedPR);

                            } else {
                                updatedLines.add(read);
                            }
                        }

                        for (String readitemline : Itemlines) {
                            if (!readitemline.startsWith(prCode)) {
                                updatedLines2.add(readitemline);
                            } else if (count == 0) {
                                // this for loop is only for the item which shares the same prID
                                // if this for loop didn't exist, items which share the same prID will be overwritten with ONLY the new item
                                for (String readitems : Itemlines) {
                                    if (readitems.startsWith(prCode)) {
                                        updatedLines2.add(readitems);
                                    }
                                }

                                updatedLines2.add(addNewLine);
                                count = 1;
                            } else if (count == 1 && !readitemline.startsWith(prCode)) {
                                // if item with same prID added (which means new item already added into list), add the other items which do not share the same prID
                                // eg. selected prID to have item added is PR002, PR001 items are executed in IF statement, PR002 items are executed in IF ELSE statement before this, the rest of prID items will be added using this loop
                                // important loops because the displayPR code reads line by line, not prID by prID. meaning everything has to be in order
                                updatedLines2.add(readitemline);
                            }
                        }

                        Files.write(Paths.get("pr.txt"), updatedLines);
                        Files.write(Paths.get("prItems.txt"), updatedLines2);
                        System.out.println("Purchase Requisition successfully updated.");

                    } catch (NumberFormatException e) {
                        System.out.println("INVALID NUMBER.");
                        directUser(loggedInUser);
                    }

                    break;
                case 5:
                    if (loggedInUser.getRole().equals("sm")) {
                        updatePRStatus(prCode); // if user is sales manager, redirect to other method
                    } else {
                        Scanner scanStat = new Scanner(System.in); // else, admin can change to whichever status depending on current status
                        int choiceStat;
                        String stat;

                        boolean approvedADM = false;
                        boolean cancelledADM = false;
                        boolean pendingADM = false;
                        boolean rejADM = false;

                        OUTER:
                        for (String readline : lines) {
                            if (readline.startsWith(prCode)) {
                                String[] splitline = readline.split(",");
                                switch (splitline[4]) {
                                    case "APPROVED":
                                        approvedADM = true;
                                        break OUTER;
                                    case "CANCELLED":
                                        cancelledADM = true;
                                        break OUTER;
                                    case "PENDING":
                                        pendingADM = true;
                                        break OUTER;
                                    case "REJECTED":
                                        rejADM = true;
                                        break OUTER;
                                    default:
                                        break;
                                }
                            }
                        }
                        System.out.println("Please choose which status you would like to change to:");
                        if (approvedADM) {
                            System.out.println("1. CANCELLED");
                            System.out.println("2. REJECTED");
                            System.out.println("3. PENDING");
                        } else if (cancelledADM) {
                            System.out.println("1. APPROVED");
                            System.out.println("2. REJECTED");
                            System.out.println("3. PENDING");
                        } else if (pendingADM) {
                            System.out.println("1. APPROVED");
                            System.out.println("2. REJECTED");
                            System.out.println("3. CANCELLED");
                        } else if (rejADM) {
                            System.out.println("1. APPROVED");
                            System.out.println("2. CANCELLED");
                            System.out.println("3. PENDING");
                        }

                        try {
                            choiceStat = Integer.parseInt(scanStat.nextLine());

                            switch (choiceStat) {
                                case 1:
                                    if (approvedADM) {
                                        stat = "CANCELLED";
                                    } else {
                                        stat = "APPROVED";
                                    }
                                    updatePRStatus(prCode, stat);
                                    break;
                                case 2:
                                    if (rejADM) {
                                        stat = "CANCELLED";
                                    } else {
                                        stat = "REJECTED";
                                    }
                                    updatePRStatus(prCode, stat);
                                    break;
                                case 3:
                                    if (pendingADM) {
                                        stat = "CANCELLED";
                                    } else {
                                        stat = "PENDING";
                                    }
                                    updatePRStatus(prCode, stat);
                                    break;
                            }

                        } catch (NumberFormatException e) {
                            System.out.println("Please enter a valid number.");
                            Admin.adminMenu(loggedInUser);
                        }
                    }
                    break;
                case 6:
                    directUser(loggedInUser);
                    break;
                default:
                    System.out.println("INVALID INPUT.");
            }
        } catch (IOException e) {
            System.out.println("ERROR READING PR FILE.");
        }
    }

    @Override
    // delete PR method, deletes the whole PR
    public void delete(String delPR) {
        try {

            List<String> prLines = Files.readAllLines(Paths.get("pr.txt"));
            List<String> itemLines = Files.readAllLines(Paths.get("prItems.txt"));

            List<String> updatedPRLines = new ArrayList();
            List<String> updatedItemLines = new ArrayList();

            for (String line : prLines) {
                if (!line.startsWith(delPR)) {
                    updatedPRLines.add(line);
                }
            }

            for (String line : itemLines) {
                if (!line.startsWith(delPR)) {
                    updatedItemLines.add(line);
                }
            }

            Files.write(Paths.get("pr.txt"), updatedPRLines);
            Files.write(Paths.get("prItems.txt"), updatedItemLines);

            System.out.println("PR deleted successfully");

        } catch (IOException e) {
            System.out.println("ERROR DELETING PR.");
        }
    }

    // deletes an INDIVIDUAL item from PR
    public static void deleteItemFromPR(String pid, String iid, userInfo user) {

        try {

            List<String> lines = Files.readAllLines(Paths.get("pr.txt"));
            List<String> itemLines = Files.readAllLines(Paths.get("prItems.txt"));

            List<String> updatedItemLines = new ArrayList();
            List<String> updatedPRLines = new ArrayList();

            float tp = 0;
            float itemP;
            int itemQ;
            boolean found = false;
            boolean stop = false;

            for (String readPR : lines) {
                if (readPR.startsWith(pid)) {
                    String[] splitline = readPR.split(",");
                    if (splitline[4].equals("APPROVED")) {
                        stop = true;
                        break;
                    }
                }
            }

            if (stop) {
                System.out.println("PR CANNOT BE EDITED AS IT IS APPROVED.");
            }

            for (String readPR : lines) {
                if (readPR.startsWith(pid)) {
                    tp = getTotalPricePR(readPR);

                    for (String read : itemLines) {
                        if (read.startsWith(pid) && read.contains(iid)) {
                            itemP = getItemTotalPrice(read);
                            itemQ = getItemTotalQuantity(read);
                            tp -= itemP;
                            String updatedline = updatePRTotal(readPR, tp);

                            if (tp != 0) {
                                updatedPRLines.add(updatedline);
                            }

                            found = true;
                        } else {
                            updatedItemLines.add(read);
                        }
                    }
                } else {
                    updatedPRLines.add(readPR);
                }
            }

            if (!found) {
                System.out.println("ITEM NOT FOUND IN PR.");
                directUser(user);
            }

            Files.write(Paths.get("pr.txt"), updatedPRLines);
            Files.write(Paths.get("prItems.txt"), updatedItemLines);

            System.out.println("Item deleted successfully.");

        } catch (IOException e) {
            System.out.println("ERROR DELETING ITEM.");
        }

    }

    // gets total price of the pr by splitting 
    private static float getTotalPricePR(String line) {

        if (line.isEmpty()) {
            return 0;
        }

        String[] split = line.split(",");
        String priceStr = split[split.length - 1];

        return Float.parseFloat(priceStr.substring(13));
    }

    // gets total price of the item in the PR by splitting (eg. 10 items of I001 is RM100)
    private static float getItemTotalPrice(String line) {

        if (line.isEmpty()) {
            return 0;
        }

        String[] parts = line.split(",");
        String priceStr = parts[parts.length - 1];

        return Float.parseFloat(priceStr.substring(2));
    }

    // get total quantity of an item in the pr by splitting
    private static int getItemTotalQuantity(String line) {

        if (line.isEmpty()) {
            return 0;
        }

        String[] parts = line.split(",");
        String quantStr = parts[parts.length - 3];

        return Integer.parseInt(quantStr);
    }

    // changes the total item price in pr based on new quantity (changing the quantity in a pr, but same item)
    public static String updateItemPrice(String read, int newQty, float priceItem) {
        String[] split = read.split(",");

        float newPrice = newQty * priceItem;

        split[2] = String.valueOf(newQty);
        split[split.length - 1] = "RM" + newPrice;

        return String.join(",", split);
    }

    // changes the total item price in pr based on item id (changing the item in a pr, but same quantity)
    public static String updateItemPrice(String read, String newItemID, float newPrice, String newSupp) {

        String[] split = read.split(",");

        split[1] = newItemID;

        split[split.length - 2] = newSupp;
        split[split.length - 1] = "RM" + newPrice;

        return String.join(",", split);
    }

    // updating the selected prID's total price
    public static String updatePRTotal(String read, float total) {
        String[] split = read.split(",");

        split[split.length - 1] = "TotalPrice=RM" + total;

        return String.join(",", split);
    }

    // (SALES MANAGER ONLY) cancelling the selected prID's status
    public static void updatePRStatus(String prCode) {
        try {
            List<String> lines = Files.readAllLines(Paths.get("pr.txt"));

            List<String> updatedLines = new ArrayList<>();

            for (String read : lines) {
                if (read.startsWith(prCode)) {
                    String[] split = read.split(",");
                    
                    if (!split[4].equals("PENDING")){
                        System.out.println("CANNOT CANCEL A PR THAT IS EITHER APPROVED OR REJECTED OR ALREADY CANCELLED.");
                    }else{
                        split[4] = "CANCELLED";
                    }
                    
                    updatedLines.add(String.join(",", split));
                } else {
                    updatedLines.add(read);
                }
            }

            Files.write(Paths.get("pr.txt"), updatedLines);

        } catch (IOException e) {
            System.out.println("ERROR READING PR FILE.");
        }
    }

    // (ADMIN ONLY) updating the selected prID's status based on what was selected 
    public static void updatePRStatus(String prCode, String status) {

        try {
            List<String> lines = Files.readAllLines(Paths.get("pr.txt"));

            List<String> updatedLines = new ArrayList<>();

            for (String read : lines) {
                if (read.startsWith(prCode)) {
                    String[] split = read.split(",");

                    split[4] = status;
                    updatedLines.add(String.join(",", split));
                } else {
                    updatedLines.add(read);
                }
            }

            Files.write(Paths.get("pr.txt"), updatedLines);

        } catch (IOException e) {
            System.out.println("ERROR READING PR FILE.");
        }
    }

}
