/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodjassignment_group26;

/**
 *
 * @author aidan
 */
public class Item {
    private String itemID;
    private String name;
    private String desc;
    Supplier supplier;
    private String price;
    private String stock;
    
    public Item(){
        
    }
    
    public Item(String itemID, String name, String desc, Supplier supplier, String price, String stock){
        this.itemID = itemID;
        this.name = name;
        this.desc = desc;
        this.supplier = supplier;
        this.price = price;
        this.stock = stock;
    }

    public String getDesc() {
        return desc;
    }

    public String getName() {
        return name;
    }

    public String getitemID() {
        return itemID;
    }

    public Supplier getSupp() {
        return supplier;
    }

    public float getPrice() {
        return Float.parseFloat(price);
    }

    public int getStock() {
        return Integer.parseInt(stock);
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setitemID(String itemID) {
        this.itemID = itemID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public void setSupp(Supplier supplier) {
        this.supplier = supplier;
    }
    
    @Override
    public String toString() {
        return this.itemID + "," + this.name  + "," + this.price + "," + 
                this.stock + "," + this.supplier.getSupplierID() + "," + this.desc;
    }

    // This will display itemID
    // This is to list out all the item.
    public void displayItem() {
        System.out.printf("|%-12s |%-12s |%-30s |%-15s |%-10s |%-40s|\n", 
                this.itemID,this.supplier.getSupplierID(), this.name
                ,this.price ,this.stock, this.desc);

    }
    
}

