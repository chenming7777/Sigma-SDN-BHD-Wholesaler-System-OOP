/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodjassignment_group26;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author aidan
 */
public abstract class Employee {
    protected userInfo user;
    
    public Employee (userInfo user){
        this.user = user;
    }
    
    public void displayPOEMP(userInfo user){
        PurchaseOrder PO = new PurchaseOrder(user);
        PO.display(user);
    }
    
    public void displayPREMP(userInfo user){
        PurchaseReq PR = new PurchaseReq(user);
        PR.display(user);
    }
    
    public void updateUser(userInfo user) {
        SalesManager sm = new SalesManager(user);
        PurchaseManager pm = new PurchaseManager(user);
        Scanner scan = new Scanner(System.in);

        if (user.getID().equals("ADMIN")) {
            boolean foundUser = false;

            userInfo.printInfo();
            System.out.println("Enter user ID to change role, enter -1 to exit:");

            String uid = scan.nextLine().toUpperCase();

            if (uid.equals("-1")) {
                Admin.adminMenu(user);
            }

            try {
                List<String> itemLines = Files.readAllLines(Paths.get("userInfo.txt"));

                for (String read : itemLines) {
                    if (read.startsWith(uid)) {
                        foundUser = true;
                        System.out.println("Enter new role (sm/pm):");
                        System.out.println("1. Sales Manager");
                        System.out.println("2. Purchase Manager");

                        try {
                            int ch = Integer.valueOf(scan.nextLine());

                            String newRole;
                            switch (ch) {
                                case 1:
                                    newRole = "sm";
                                    break;
                                case 2:
                                    newRole = "pm";
                                    break;
                                default:
                                    System.out.println("PLEASE ENTER A VALID INPUT.");
                                    return;
                            }

                            userInfo editUser = userInfo.getUserInfoByID(uid);

                            if (editUser.getRole().equals(newRole)) {
                                System.out.println("ROLE REMAINS THE SAME.");
                            } else {
                                userInfo.updateRoleAndID(editUser, newRole);
                            }

                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input.");
                        }
                    }
                }

                if (!foundUser) {
                    System.out.println("USER NOT FOUND.");
                }

                Admin.adminMenu(user);

            } catch (IOException e) {
                System.out.println("ERROR READING USER FILE.");
            }
        } else {
            System.out.println("Which information would you like to update?");
            System.out.println("1. Name");
            System.out.println("2. Password");
            System.out.println("3. Back");

            int choice;

            try {
                choice = Integer.valueOf(scan.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number.");

                if (user.getRole().equals("pm")) {
                    pm.Menu(user);
                } else if (user.getRole().equals("sm")) {
                    sm.Menu(user);
                }

                return;
            }

            switch (choice) {
                case 1:
                    System.out.println("Enter new name:");
                    String newName = scan.nextLine();

                    if (userInfo.duplicateName(newName)) {
                        System.out.println("Name already exists, please try another one.");
                        return;
                    }

                    user.setName(newName);
                    break;
                case 2:
                    System.out.println("Enter new password:");
                    String newPassword = scan.nextLine();
                    
                    if (!userInfo.validPassword(newPassword)){
                        return;
                    }
                    
                    user.setPassword(newPassword);
                    break;
                case 3:
                    if (user.getRole().equals("pm")) {
                        pm.Menu(user);
                    } else if (user.getRole().equals("sm")) {
                        sm.Menu(user);
                    }
                    break;
                default:
                    System.out.println("Invalid choice.");
                    return;
            }

            // Update user info in the file
            userInfo.updateUserInfoFile(user);
        }
    }
    
    protected abstract void Menu(userInfo user);
    protected abstract void delete(userInfo user);
    protected abstract void generate(userInfo user);
    protected abstract void search(userInfo user);
}

