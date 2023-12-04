/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodjassignment_group26;

/**
 *
 * @author aidan
 */
public class Supplier {
    private String supplierID;
    private String name;
    private String number;
    private String email;
    private String address;
    public Supplier(){
    }
    
    public Supplier(String supplierID, String name, String number, String email, String address){
        this.supplierID = supplierID;
        this.name = name;
        this.number = number;
        this.email = email;
        this.address = address;
    }

    public String getEmail() {
        return this.email;
    }

    public String getAddress() {
        return this.address;
    }

    public String getName() {
        return this.name;
    }

    public String getNumber() {
        return this.number;
    }

    public String getSupplierID() {
        return this.supplierID;
    }

    //set the attribute of object
    public void setSupplierID(String supplierID){
        this.supplierID = supplierID;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(String number) {
        this.number = number;
    }
    
    
    public void displaySupplier(){
        System.out.printf("|%-12s |%-23s |%-15s |%-25s |%-40s|\n",
                this.supplierID, this.name, this.number,
                this.email, this.address);

    }
    
    @Override
    public String toString(){
        return this.supplierID + "," + this.name + "," + this.number + ","
                + this.email + "," +this.address;
    }
}
