package com.example.budgettracker.database;

public interface FireStoreCallback {
    void onSuccess(String message);
    void onFailure(String message);
}
