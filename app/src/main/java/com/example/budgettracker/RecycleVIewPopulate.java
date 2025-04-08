package com.example.budgettracker;

public class RecycleVIewPopulate {
    String  id;
    double amount;
    String description;
    String date;
    String name;
    String isSettled;

    public RecycleVIewPopulate(String id, double amount, String description, String date, String name, String isSettled) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.name = name;
        this.isSettled = isSettled;
    }

    public String  getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }
    public String getName() {
        return name;
    }
    public Boolean isSettled(){
        return isSettled.equals("true");
    }
    public void setIsSettled(String isSettled) {
        this.isSettled = isSettled;
    }
}
