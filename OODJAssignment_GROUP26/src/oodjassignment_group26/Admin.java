/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodjassignment_group26;

import java.util.Scanner;
import static oodjassignment_group26.OODJAssignment_GROUP26.mainMenu;

/**
 *
 * @author aidan
 */
public class Admin{
    protected userInfo user;
    
    public Admin(userInfo user){
        this.user = user;
    }
    
    public static void adminMenu(userInfo loggedInUser) {
        Scanner scan = new Scanner(System.in);
        SalesManager sm = new SalesManager(loggedInUser);
        PurchaseManager pm = new PurchaseManager(loggedInUser);

        while (true) {
            System.out.println("\nWelcome to the Administrator's Menu!");
            System.out.println("--------------------------------------");
            System.out.println("1. Sales Manager Functions");
            System.out.println("2. Purchase Manager Functions");
            System.out.println("3. Register New User");
            System.out.println("4. Menu");

            System.out.println("\nPlease enter your choice: ");
            int choice;

            try {
                choice = Integer.valueOf(scan.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:  
                    sm.Menu(loggedInUser);
                    break;
                case 2:
                    pm.Menu(loggedInUser);
                    break;
                case 3:
                    userReg();
                    break;
                case 4:
                    mainMenu();
                    break;
                default:
                    System.out.println("Please enter a valid number!");
            }
        }
    }
    
    public static void adminLogin() {
        Scanner scan = new Scanner(System.in);

        int count = 3;
        while (count != 0) {
            System.out.println("\nPlease enter your name: ");
            String aName = scan.nextLine();

            System.out.println("Please enter your password: ");
            String aPw = scan.nextLine();

            if (aName.equals("Admin") && aPw.equals("Pa$$w0rd")) {
                System.out.println("Login successful!\n");
                userInfo loggedInUser = new userInfo("ADMIN", "Administrator", "", "", "System Administrator");
                adminMenu(loggedInUser);
                break;
            } else {
                count -= 1;

                if (count == 0) {
                    System.out.println("\nNo more attempts left!\n");
                    mainMenu();
                }

                System.out.println("\nWrong credentials! " + count + " attempts left!");
            }
        }
    }
    
    // registering new user for admin
    public static void userReg() {
        Scanner scan = new Scanner(System.in);

        while (true) {
            System.out.println("Please enter your name:");
            String name = scan.nextLine();

            if (name.isEmpty()) {
                System.out.println("Cannot be left blank!");
                System.out.println();
            }

            if (userInfo.duplicateName(name)) {
                System.out.println("NAME ALREADY EXISTS. PLEASE TRY ANOTHER ONE.");
                continue;
            }

            System.out.println("Please enter your password:");
            String pw = scan.nextLine();

            if (!userInfo.validPassword(pw)) {
                continue;
            }

            System.out.println("Please enter your email address:");
            String em = scan.nextLine();

            if (!userInfo.validEmail(em)) {
                continue;
            }
            
            if (userInfo.duplicateEmail(em)){
                System.out.println("EMAIL ALREADY EXISTS. PLEASE TRY ANOTHER ONE");
                continue;
            }

            int rc;

            System.out.println("Please select your role:");
            System.out.println("1. Sales Manager (SM)");
            System.out.println("2. Purchase Manager (PM)");
            try {
                rc = Integer.valueOf(scan.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
                continue;
            }

            String role;
            switch (rc) {
                case 1:
                    role = "sm";
                    break;
                case 2:
                    role = "pm";
                    break;
                default:
                    System.out.println("Invalid choice.");
                    continue;
            }

            String id = userInfo.getNextID(role);
            if (id == null) {
                System.out.println("Error generating user ID.");
                continue;
            }

            userInfo newUser = new userInfo(id, name, em, pw, role);
            newUser.saveFile();

            System.out.println(role + " with ID " + id + " successfully registered!");
            break;
        }
    }
}
