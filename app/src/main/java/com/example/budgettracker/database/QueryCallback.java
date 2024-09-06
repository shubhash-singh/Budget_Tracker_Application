package com.example.budgettracker.database;

import java.util.List;
import java.util.Objects;

public interface QueryCallback {
    void onSuccess(List<List<String>> data);
    void onFailure(String message);
}
