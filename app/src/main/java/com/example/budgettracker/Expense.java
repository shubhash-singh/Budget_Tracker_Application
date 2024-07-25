package com.example.budgettracker;

public class Expense {
    int id;
    double amount;
    String description;
    String date;

    public Expense(int id, double amount, String description, String date) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }

    public int getId() {
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
}
