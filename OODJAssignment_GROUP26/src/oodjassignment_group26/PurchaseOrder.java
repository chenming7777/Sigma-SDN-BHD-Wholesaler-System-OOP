/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodjassignment_group26;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
public class PurchaseOrder extends Purchase {

    private static int nextPOID = 1;
    private String poID, reqBy, pmID, supplierID;
    private Date date;

    public PurchaseOrder(userInfo loggedInUser) {
        this.poID = generateID();
        this.reqBy = loggedInUser.getName();
        this.pmID = loggedInUser.getID();
    }

    public Date getDate() {
        return date;
    }

    public static int getNextPOID() {
        return nextPOID;
    }

    public String getPmID() {
        return pmID;
    }

    public String getPoID() {
        return poID;
    }

    public String getReqBy() {
        return reqBy;
    }

    public String getSupplierID() {
        return supplierID;
    }

    // This will generate generatePOID
    @Override
    public String generateID() {
        int maxPOID = 0;
        try (BufferedReader br = new BufferedReader(new FileReader("po.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 1 && parts[0].startsWith("PO")) {
                    int prNumber = Integer.parseInt(parts[0].substring(2));
                    maxPOID = Math.max(maxPOID, prNumber);
                }
            }
        } catch (IOException e) {
            System.out.println("ERROR READING PO FILE.");
        }

        nextPOID = maxPOID + 1;
        return String.format("PO%03d", nextPOID);
    }

    public void approvePR(userInfo loggedInUser) {
        try {
            List<String> prLines = Files.readAllLines(Paths.get("pr.txt"));
            List<String> updatedLines = new ArrayList<>();
            Scanner sc = new Scanner(System.in);
            Date date = null;
            PurchaseReq PR = new PurchaseReq(loggedInUser);
            displayPending();
            System.out.println("Please enter the PRID which you would like to approve for Purchase Order, input 'exit' to go back:");
            String prID = sc.nextLine().toUpperCase();
            if (prID.equals("exit")) {
                return;
            }
            if(!checkPending(prID)){
                System.out.println("The Purchase Requisition ID is not in pendings status");
                directUser(loggedInUser);
                return;
            }
            if (!PR.search(prID)) {
                System.out.println("Purchase Requisition ID NOT FOUND.");
                directUser(loggedInUser);
            }
            

            for (String read : prLines) {
                String[] split = read.split(",");
                if (read.startsWith(prID) && (!split[4].equals("CANCELLED"))) {
                    if (isValidDate(split[3])) {
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            date = df.parse(split[3]);
                        } catch (ParseException e) {
                            System.out.println("ERROR CONVERTING TO DATE.");
                        }

                        split[4] = "APPROVED";

                        String newLine = String.join(",", split);
                        updatedLines.add(newLine);
                    } else {
                        System.out.println("UNACCEPTED DATE DURATION.");
                        updatedLines.add(read);
                    }
                } else if (read.startsWith(prID) && (!split[4].equals("PENDING"))) {
                    System.out.println("ONLY PENDING STATUS CAN BE APPROVED.");
                    updatedLines.add(read);
                    
                } else {
                    updatedLines.add(read);
                }
            }

            Files.write(Paths.get("pr.txt"), updatedLines);
            generatePO(loggedInUser, prID, date); // generate PO right after approving PR
        } catch (IOException e) {
            System.out.println("ERROR READING PR FILE.");
        }
    }

    public void generatePO(userInfo loggedInUser, String prID, Date date) {
        try {
            List<String> prItems = Files.readAllLines(Paths.get("prItems.txt"));
            ArrayList<String> targetPRItems = new ArrayList<>();
            ArrayList<String> addedSuppliers = new ArrayList<>();
            File po = new File("po.txt");
            if (!po.exists()) {
                try {
                    po.createNewFile();
                } catch (IOException e) {
                    System.out.println("ERROR CREATING NEW PR FILE.");
                }
            }

            // This is to add the supplier and item in that involved in that PRitem
            for (String prItem : prItems) {
                String[] prItemParts = prItem.split(",");
                if (prID.equals(prItemParts[0])) {
                    targetPRItems.add(prItem);
                    if (!addedSuppliers.contains(prItemParts[3])) {
                        addedSuppliers.add(prItemParts[3]);
                    }
                }
            }
            for (String addedSupplier : addedSuppliers) {
                float totalPrice = 0;
                String poID = generateID();
                for (String targetPRItem : targetPRItems) {
                    String[] targetPRItemParts = targetPRItem.split(",");
                    if (addedSupplier.equals(targetPRItemParts[3])) {
                        totalPrice += Float.parseFloat(targetPRItemParts[4].substring(2));
                        saveToPOItemsFile(poID, targetPRItemParts[1], targetPRItemParts[2], prID, targetPRItemParts[4]);
                    }
                }
                saveToPOFile(poID, loggedInUser.getName(), loggedInUser.getID(), addedSupplier, date, totalPrice);
            }
        } catch (IOException e) {
            System.out.println("ERROR READING FILES.");
        }
    }

    public void saveToPOFile(String poID, String reqBy, String pmID, String supp, Date date, float tp) {
        boolean append = false;

        try { // try catch statement is just to prevent first line in txt file from being empty
            if (Files.size(Paths.get("po.txt")) > 0) {
                append = true;
            }
        } catch (IOException e) {
            System.out.println("PO FILE NOT FOUND. CREATING PO FILE...");
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("po.txt", append))) {

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

            bw.write(poID + "," + reqBy + "," + pmID + "," + supp + "," + df.format(date)
                    + ",TotalPrice=RM" + tp + System.lineSeparator());

        } catch (IOException e) {

            System.out.println("AN ERROR OCCURRED WHILE TRYING TO SAVE TO PO FILE.");

        }
    }

    private void saveToPOItemsFile(String poID, String itemID, String quantity, String prID, String totalPrice) {
        boolean append = false;
        try {
            if (Files.size(Paths.get("poItems.txt")) > 0) {
                append = true;
            }
        } catch (IOException e) {
            System.out.println("PO ITEMS FILE NOT FOUND. CREATING PO ITEMS FILE...");
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("poItems.txt", append))) {
            bw.write(poID + "," + itemID + "," + quantity + "," + prID + "," + totalPrice + System.lineSeparator());
        } catch (IOException e) {
            System.out.println("ERROR SAVING TO PO ITEMS FILE.");
        }
    }   

    public static void displayPending() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader("pr.txt"))) {
            String line;
            System.out.printf("|%-12s |%-20s |%-15s |%-20s |%-20s |%-17s|\n", "PO ID", "Requestor", "Requestor ID", "SupplierID", "Date Required By", "Total Price");
            while ((line = br.readLine()) != null) {
                if (line.contains("PENDING")) {
                    String[] lineParts = line.split(",");
                    System.out.printf("|%-12s |%-20s |%-15s |%-20s |%-20s |%-17s|\n", lineParts[0], lineParts[1], lineParts[2], lineParts[3], lineParts[4], lineParts[5].substring(11));
                }
            }
        }
    }
    public static boolean checkPending(String PRID) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader("pr.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("PENDING") && line.contains(PRID)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean search(String poID) {

        try {

            BufferedReader poReader = new BufferedReader(new FileReader("po.txt"));
            BufferedReader poitemReader = new BufferedReader(new FileReader("poItems.txt"));

            String poLine;
            while ((poLine = poReader.readLine()) != null) {

                if (poLine.startsWith(poID)) {
                    String[] prParts = poLine.split(",");
                    System.out.println("PO ID: " + poID);
                    System.out.println("Requestor: " + prParts[1]);
                    System.out.println("Requestor ID: " + prParts[2]);
                    System.out.println("Supplier ID: " + prParts[3]);
                    System.out.println("Date Required By: " + prParts[4]);
                    System.out.println("Total Price: " + prParts[5].substring(11));
                    System.out.println("\nItemList:");
                    System.out.printf("|%-12s |%-12s |%-12s |%-20s|\n", "ItemID", "Quantity", "SupplierID", "Total Item Price");
                    String itemLine;
                    while ((itemLine = poitemReader.readLine()) != null) {
                        if (itemLine.startsWith(poID)) {
                            String[] split = itemLine.split(",");
                            System.out.printf("|%-12s |%-12s |%-12s |%-20s|\n", split[1], split[2], split[3], split[4]);
                        }
                    }
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            System.out.println("ERROR READING PO AND/OR PO ITEMS FILE.");
            return false;
        }
    }

    @Override
    public void delete(String delPO) {
        try {
            String prID = null;
            List<String> poLines = Files.readAllLines(Paths.get("po.txt"));
            List<String> poitemLines = Files.readAllLines(Paths.get("poItems.txt"));

            List<String> updatedPOLines = new ArrayList();
            List<String> updatedItemLines = new ArrayList();

            for (String line : poLines) {
                if (!line.startsWith(delPO)) {
                    updatedPOLines.add(line);
                }
            }

            for (String line : poitemLines) {
                if (!line.startsWith(delPO)) {
                    String[] split = line.split(",");
                    prID = split[3];

                    updatedItemLines.add(line);
                }
            }

            Files.write(Paths.get("po.txt"), updatedPOLines);
            Files.write(Paths.get("poItems.txt"), updatedItemLines);

            System.out.println("PO deleted successfully");
        } catch (IOException e) {
            System.out.println("ERROR DELETING PO.");
        }
    }

    @Override
    public void display(userInfo user) {
        try {

            BufferedReader poReader = new BufferedReader(new FileReader("po.txt"));
            System.out.printf("|%-12s |%-20s |%-15s |%-20s |%-20s |%-17s|\n", "PO ID", "Requestor", "Requestor ID", "SupplierID", "Date Required By", "Total Price");
            String prLine;
            while ((prLine = poReader.readLine()) != null) {
                String[] prParts = prLine.split(",");
                System.out.printf("|%-12s |%-20s |%-15s |%-20s |%-20s |%-17s|\n", prParts[0], prParts[1], prParts[2], prParts[3], prParts[4], prParts[5].substring(11));
            }

        } catch (IOException e) {
            System.out.println("ERROR DISPLAYING PO(S).");
        }
    }

    public static void rejectPR(String poCode) {
        try {
            List<String> lines = Files.readAllLines(Paths.get("pr.txt"));
            List<String> updatedLines = new ArrayList<>();

            for (String read : lines) {
                if (read.startsWith(poCode)) {
                    String[] split = read.split(",");
                    if (split[4].equals("CANCELLED")){
                        System.out.println("CANNOT REJECT A PR THAT IS CANCELLED.");
                    }else{
                        split[4] = "REJECTED";
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
}

