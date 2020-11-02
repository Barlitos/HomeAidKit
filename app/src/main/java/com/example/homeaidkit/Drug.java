package com.example.homeaidkit;

public class Drug {
    private int id;
    private String name;
    private String expDate;
    private int quantity;

    public Drug(int id, String name, String expDate, int quantity) {
        this.id = id;
        this.name = name;
        this.expDate = expDate;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
