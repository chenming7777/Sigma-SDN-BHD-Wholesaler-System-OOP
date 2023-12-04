/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodjassignment_group26;

/**
 *
 * @author aidan
 */
public class PurchaseReqItem{
    private Item item;
    private int quantity;

    public PurchaseReqItem(Item item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public Item getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }
    
    @Override
    public String toString() {
        return quantity + " orders of " + item.getitemID();
    }
}
