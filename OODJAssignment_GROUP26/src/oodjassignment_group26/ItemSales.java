/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodjassignment_group26;

/**
 *
 * @author aidan
 */
public class ItemSales {
    private String itemSalesID;
    private Item item;
    private String date;
    private String quantity;
    public ItemSales(){
        
    }
    public ItemSales(String itemSalesID, Item item, String date, String quantity ){
        this.itemSalesID = itemSalesID;
        this.item = item;
        this.date = date;
        this.quantity = quantity;
    }

    public String getDate() {
        return date;
    }

    public Item getItem() {
        return item;
    }

    public String getItemSalesID() {
        return itemSalesID;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void setItemSalesID(String itemSalesID) {
        this.itemSalesID = itemSalesID;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
    
    public void displayItemSales(){
        System.out.printf("|%-12s |%-12s |%-15s |%-10s|\n", this.itemSalesID, this.item.getitemID(), this.date, this.quantity);
    }
    
    
    @Override
    public String toString() {
        return this.itemSalesID + "," + this.item.getitemID() + "," + this.date + "," + this.quantity;
    }
    
}

