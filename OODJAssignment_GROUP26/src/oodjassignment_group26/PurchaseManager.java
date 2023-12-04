/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodjassignment_group26;

import java.util.Scanner;
import static oodjassignment_group26.Admin.adminMenu;
import static oodjassignment_group26.OODJAssignment_GROUP26.mainMenu;

/**
 *
 * @author aidan
 */
public class PurchaseManager extends Employee {

    public PurchaseManager(userInfo user) {
        super(user);
    }

    @Override
    public void Menu(userInfo loggedInUser) {
        ItemEntry IE = new ItemEntry();
        SupplierEntry SE = new SupplierEntry();
        Scanner scan = new Scanner(System.in);

        while (true) {
            System.out.println("\nWelcome to the Purchase Manager Menu!");
            System.out.println("--------------------------------------");
            System.out.println("1. List of items");
            System.out.println("2. List of suppliers");
            System.out.println("3. Display Requisition");
            System.out.println("4. Approve a Purchase Requisition");
            System.out.println("5. Reject a Purchase Requisition");
            System.out.println("6. Delete a Purchase Order");
            System.out.println("7. List of Purchaser Orders");
            System.out.println("8. Search a Purchase Order");
            System.out.println("9. Update profile info");
            if (loggedInUser.getRole().equals("sm")) {
                System.out.println("10. Logout");
            } else {
                System.out.println("10. Back");
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
                    IE.listObject();
                    break;
                case 2:
                    SE.listObject();
                    break;
                case 3:
                    displayPREMP(loggedInUser);
                    break;
                case 4:
                    generate(loggedInUser);
                    break;
                case 5:
                    reject(loggedInUser);
                    break;
                case 6:
                    delete(loggedInUser);
                    break;
                case 7:
                    displayPOEMP(loggedInUser);
                    break;
                case 8:
                    search(loggedInUser);
                    break;
                case 9:
                    updateUser(loggedInUser);
                    break;
                case 10:
                    if (loggedInUser.getID().equals("ADMIN")) {
                        adminMenu(loggedInUser);
                    } else {
                        mainMenu();
                    }
                    break;
                default:
                    System.out.println("Please enter a valid number!");
            }
        }
    }

    @Override
    public void search(userInfo user) {
        Scanner scan = new Scanner(System.in);

        System.out.println("Enter PO code:");
        String cd = scan.nextLine().toUpperCase();
        PurchaseOrder PO = new PurchaseOrder(user);
        if (PO.search(cd) != true) {
            System.out.println("PO not found.");
        }
    }

    @Override
    public void generate(userInfo user) {
        PurchaseOrder pm = new PurchaseOrder(user);

        pm.approvePR(user);
    }

    @Override
    public void delete(userInfo user) {
        Scanner scan = new Scanner(System.in);
        PurchaseOrder PO = new PurchaseOrder(user);
        displayPOEMP(user);
        System.out.println("Enter the PO ID you would like to delete:");
        String delPO = scan.nextLine().toUpperCase();

        if (PO.search(delPO) != false) {
            System.out.println("Are you sure you would like to delete " + delPO + "? Enter Y if yes and anything else if no.");
            String conf = scan.nextLine().toUpperCase();

            if (conf.equals("Y")) {
                PO.delete(delPO);
            } else {
                System.out.println("Deletion has been cancelled.");
                Menu(user);
            }
        } else {
            System.out.println("PO Code not found!");
            mainMenu();
        }
    }

    public void reject(userInfo user) {
        Scanner scan = new Scanner(System.in);

        PurchaseOrder PO = new PurchaseOrder(user);
        PurchaseReq PR = new PurchaseReq(user);

        PR.display(user);
        System.out.println("Enter the PR ID you would like to reject:");
        String prid = scan.nextLine();

        if (PR.search(prid) != false) {
            PO.rejectPR(prid);
        } else {
            System.out.println("PR Code not found!");
            Menu(user);
        }
    }
}

