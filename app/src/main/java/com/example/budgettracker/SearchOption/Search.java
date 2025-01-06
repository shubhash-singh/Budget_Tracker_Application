package com.example.budgettracker.SearchOption;

public class Search {
    String amount;
    String item;
    String date;
    String name;

    public Search(String amount, String item, String date, String name) {
        this.amount = amount;
        this.item = item;
        this.date = date;
        this.name = name;
    }
    public String getAmount() {
        return amount;
    }
    public String getItem() {
        return item;
    }
    public String getDate() {
        return date;
    }
    public String getName() {
        return name;
    }
}
