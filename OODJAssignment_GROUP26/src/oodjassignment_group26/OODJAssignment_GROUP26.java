/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package oodjassignment_group26;

import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author aidan, chenming, haerish
 */
public class OODJAssignment_GROUP26 {

    /**
     * @param args the command line arguments
     */
    private static userInfo loggedInUser = null;

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        mainMenu();
        
    }

    // main menu of application, the first page you see when you run the program
    public static void mainMenu() {
        Scanner scan = new Scanner(System.in);
        int ch;

        while (true) {
            System.out.println("Welcome to the SIGMA SDN BHD Application!");
            System.out.println("-----------------------------------------");
            System.out.println("1. Administrator login");
            System.out.println("2. User login");
            System.out.println("3. Exit Program");

            System.out.println("\nPlease enter your choice: ");
            try {
                ch = Integer.parseInt(scan.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
                continue;
            }

            switch (ch) {
                case 1:
                    Admin.adminLogin();
                    break;
                case 2:
                    loggedInUser = userLogin();
                    break;
                case 3:
                    System.exit(0);
                default:
                    System.out.println("INVALID NUMBER.");
            }
        }
    }
    
    public static userInfo userLogin() {
        Scanner scan = new Scanner(System.in);

        int count = 3;
        userInfo loggedInUser = null;

        while (count != 0) {
            System.out.println("Please enter your email:");
            String email = scan.nextLine();

            System.out.println("Please enter your password:");
            String password = scan.nextLine();

            if (userInfo.userLogin(email, password)) {
                System.out.println("Login successful!\n");
                loggedInUser = userInfo.getUserInfoByEmailAndPassword(email, password);
                if (loggedInUser.getRole().equals("sm")) {
                    SalesManager sm = new SalesManager(loggedInUser);
                    sm.Menu(loggedInUser);
                } else {
                    PurchaseManager pm = new PurchaseManager(loggedInUser);
                    pm.Menu(loggedInUser);
                }
            } else {
                count -= 1;
                if (count == 0) {
                    System.out.println("\nNo more attempts left!\n");
                    mainMenu();
                }

                System.out.println("Wrong credentials! " + count + " attempts left.");
            }
        }

        return loggedInUser;
    }
    
}
