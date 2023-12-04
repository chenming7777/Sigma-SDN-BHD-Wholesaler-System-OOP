/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodjassignment_group26;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author aidan
 */
public abstract class Purchase {
    // from PR
    public static void directUser(userInfo loggedInUser) {
        SalesManager sm = new SalesManager(loggedInUser);
        PurchaseManager pm = new PurchaseManager(loggedInUser);

        if (loggedInUser.getRole().equals("sm")) {
            sm.Menu(loggedInUser);
        } else if (loggedInUser.getRole().equals("pm")) {
            pm.Menu(loggedInUser);
        } else{
            Admin.adminMenu(loggedInUser);
        }
    }
    
    // find item in the txt file using item code(changed) from PR
    public static Item findItem(String itemCode) {
        try {
            ItemEntry IE = new ItemEntry();
            ArrayList<Item> items = IE.fileToItemList();
            for (Item item : items) {
                if (item.getitemID().equals(itemCode)) {
                    return item;
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR READING ITEMS INFO FILE.");
        }
        return null;
    }
    
    // see if it is valid date for generating/editing date in pr
    public static boolean isValidDate(String dateStr) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setLenient(false);

        Date currentDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.DATE, 7); // add 1 week to the current date for min date
        Date minDate = cal.getTime();
        cal.add(Calendar.YEAR, 1); // add 1 year to the current date for max date
        Date maxDate = cal.getTime();

        try {
            Date newDate = df.parse(dateStr);
            return newDate.after(currentDate) && newDate.after(minDate) && newDate.before(maxDate);
        } catch (ParseException e) {
            return false; // Invalid date format
        }
    }
    
    // generates next prID based on last number in the txt file. eg. if last number is PR002, itll take last 3 digits and +001
    public abstract String generateID();
    
    // displays all PRs from txt file(change print format)
    public abstract void display(userInfo loggedInUser);
    public abstract void delete(String del);
    public abstract boolean search(String prID);
}
