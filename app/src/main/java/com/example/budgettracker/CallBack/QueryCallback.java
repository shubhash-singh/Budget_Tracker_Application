package com.example.budgettracker.CallBack;

import java.util.List;

public interface QueryCallback {
    void onSuccess(List<List<String>> data);
    void onFailure(String message);
}
