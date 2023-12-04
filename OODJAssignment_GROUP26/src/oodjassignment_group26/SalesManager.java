/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodjassignment_group26;

import java.io.IOException;
import java.util.Scanner;
import static oodjassignment_group26.Admin.adminMenu;
import static oodjassignment_group26.OODJAssignment_GROUP26.mainMenu;

/**
 *
 * @author aidan
 */
public class SalesManager extends Employee {

    public SalesManager(userInfo user) {
        super(user);
    }

    @Override
    public void Menu(userInfo loggedInUser) {
        
        
        Scanner scan = new Scanner(System.in);

        while (true) {
            System.out.println("\nWelcome to the Sales Manager Menu!");
            System.out.println("--------------------------------------");
            new ItemEntry().notifyLowStock();
            System.out.println("1. Item Functions");
            System.out.println("2. Supplier Functions");
            System.out.println("3. Daily Item-wise Sales Entry");
            System.out.println("4. Create a Purchase Requisition");
            System.out.println("5. Display Requisition");
            System.out.println("6. Edit Purchase Requisition");
            System.out.println("7. Search a Purchase Requisition");
            System.out.println("8. Delete Purchase Requisition");
            System.out.println("9. List of Purchase Orders");
            System.out.println("10. Update profile info");
            if (loggedInUser.getRole().equals("sm")) {
                System.out.println("11. Logout");
            } else {
                System.out.println("11. Back");
            }
            System.out.println("\nPlease enter your choice: ");
            int choice;

            try {
                choice = Integer.parseInt(scan.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
                continue;
            }
            switch (choice) {
                case 1:
                    try {
                        ItemEntry IE = new ItemEntry();
                        IE.menu();
                        break;
                    } catch (IOException e) {
                        System.out.println("RAN INTO AN ISSUE WHILE ATTEMPTING TO RUN ITEM MENU.");
                    }
                case 2:
                    try{
                        SupplierEntry SE = new SupplierEntry();
                        SE.menu();
                        break;
                    } catch (IOException e){
                        System.out.println("RAN INTO AN ISSUE WHILE ATTEMPTING TO RUN SUPPLIER MENU.");
                    }
                case 3:
                    try{
                        ItemSalesEntry ISE = new ItemSalesEntry();
                        ISE.menu();
                        break;
                    } catch (IOException e){
                        System.out.println("RAN INTO AN ISSUE WHILE ATTEMPTING TO RUN ITEM SALES MENU.");
                    }
                case 4:
                    generate(loggedInUser);
                    break;
                case 5:
                    displayPREMP(loggedInUser);
                    break;
                case 6:
                    update(loggedInUser);
                    break;
                case 7:
                    search(loggedInUser);
                    break;
                case 8:
                    while (true) {
                        System.out.println("1. Delete whole PR");
                        System.out.println("2. Delete one item from PR");
                        System.out.println("3. Back");
                        System.out.println("\nPlease enter your choice:");
                        int chD;
                        try {
                            chD = Integer.parseInt(scan.nextLine());
                        } catch (NumberFormatException e) {
                            System.out.println("INVALID NUMBER.");
                            continue;
                        }
                        switch (chD) {
                            case 1:
                                delete(loggedInUser);
                                break;
                            case 2:
                                deleteItem(loggedInUser);
                                break;
                            case 3:
                                Menu(loggedInUser);
                                break;
                            default:
                                System.out.println("INVALID NUMBER.");
                        }
                    }
                case 9:
                    PurchaseOrder PO = new PurchaseOrder(loggedInUser);
                    PO.display(loggedInUser);
                    break;
                case 10:
                    updateUser(loggedInUser);
                    break;
                case 11:
                    if (loggedInUser.getID().equals("ADMIN")) {
                        adminMenu(loggedInUser);
                    } else {
                        mainMenu();
                    }
                    break;
                default:
                    System.out.println("INVALID NUMBER.");
            }
        }
    }

    @Override
    public void delete(userInfo loggedInUser) {
        Scanner scan = new Scanner(System.in);
        PurchaseReq PR = new PurchaseReq(user);
        displayPREMP(loggedInUser);
        System.out.println("Enter the PR ID you would like to delete:");
        String delPR = scan.nextLine().toUpperCase();

        if (PR.search(delPR) != false) {
            System.out.println("Are you sure you would like to delete " + delPR + "? Enter Y if yes and anything else if no.");
            String conf = scan.nextLine().toUpperCase();

            if (conf.equals("Y")) {
                PR.delete(delPR);
            } else {
                System.out.println("Deletion has been cancelled.");
                mainMenu();
            }
        } else {
            System.out.println("PR Code not found!");
            mainMenu();
        }
    }

    public void deleteItem(userInfo loggedInUser) {
        displayPREMP(loggedInUser);
        PurchaseReq PR = new PurchaseReq(user);
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter PR ID:");
        String pid = scan.nextLine().toUpperCase();

        if (PR.search(pid) != false) {
            System.out.println("Enter Item ID to delete from PR:");
            String iid = scan.nextLine().toUpperCase();
            PurchaseReq.deleteItemFromPR(pid, iid, loggedInUser);
        } else {
            System.out.println("PR ID not found!");
        }

        System.out.println("");
        displayPREMP(loggedInUser);
        Menu(loggedInUser);
    }

    @Override
    public void generate(userInfo loggedInUser) {
        PurchaseReq newPR = new PurchaseReq(loggedInUser);

        newPR.generatePR(loggedInUser);
    }

    @Override
    public void search(userInfo loggedInUser) {
        Scanner scan = new Scanner(System.in);
        PurchaseReq PR = new PurchaseReq(user);
        System.out.println("Enter PR code:");
        String cd = scan.nextLine().toUpperCase();

        if (PR.search(cd) != true) {
            System.out.println("PR not found.");
        }
    }

    public void update(userInfo loggedInUser) {
        Scanner scan = new Scanner(System.in);
        PurchaseReq PR = new PurchaseReq(user);
        displayPREMP(loggedInUser);

        System.out.println("Enter the PR Code that you would like to edit:");
        String prCodeToUpdate = scan.nextLine().toUpperCase();

        if (PR.search(prCodeToUpdate) != false) {
            PurchaseReq.updatePR(prCodeToUpdate, loggedInUser);
            displayPREMP(loggedInUser);
        } else {
            System.out.println("PR Code not found!");
        }

        Menu(loggedInUser);
    }
}

