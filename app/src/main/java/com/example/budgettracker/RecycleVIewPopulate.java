package com.example.budgettracker;

public class RecycleVIewPopulate {
    String  id;
    double amount;
    String description;
    String date;
    String name;

    public RecycleVIewPopulate(String id, double amount, String description, String date, String name) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.name = name;
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
}
