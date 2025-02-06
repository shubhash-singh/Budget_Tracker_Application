package com.example.budgettracker.DataBase;

import com.example.budgettracker.CallBack.FireStoreCallback;
import com.example.budgettracker.CallBack.GetUserDataCallback;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddData{
    FirebaseFirestore db;
//    AddNotification notification;
    public AddData(){

        db = FirebaseFirestore.getInstance();
//        notification = new AddNotification();
    }
    public void addExpense(String description, Double amount, String username, FireStoreCallback callback) {

        getCurrentUser(username, userData -> {
            if(!userData.isEmpty()){

                Date date = new Date();
                Map<String, Object> expenseData = new HashMap<>();
                expenseData.put("Name", userData.get(0));
                expenseData.put("Room_Id", userData.get(1));
                expenseData.put("Item", description);
                expenseData.put("Price", amount);
                expenseData.put("Created_At", date);

                db.collection("Expenses")
                        .add(expenseData)
                        .addOnSuccessListener(expense -> callback.onSuccess("Expense added successfully"))
                        .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
            }
        });
    }

    public void addIncome(Double amount, String username, FireStoreCallback callback){
        getCurrentUser(username, userData -> {
            if (!userData.isEmpty()) {

                Date date = new Date();
                Map<String, Object> incomeData = new HashMap<>();
                incomeData.put("Name", userData.get(0));
                incomeData.put("Room_Id", userData.get(1));
                incomeData.put("Price", amount);
                incomeData.put("Created_At", date);

                db.collection("Income")
                        .add(incomeData)
                        .addOnSuccessListener( e -> callback.onSuccess("Income added Successfully"))
                        .addOnFailureListener( e -> {
                            callback.onFailure("Error while adding Income !!!");
                        });
            }
        });

    }
    public void calcTotalIncome(String username, FireStoreCallback callback){

        getCurrentUser(username, userData -> {
            if (!userData.isEmpty()) {
                db.collection("Income")
                        .whereEqualTo("Room_Id", userData.get(1))
                        .get()
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful() && !task.getResult().isEmpty()){
                                Double totalIncome = 0.0;
                                for(QueryDocumentSnapshot document : task.getResult()){
                                    totalIncome += document.getDouble("Price");
                                }
                                callback.onSuccess(String.valueOf(totalIncome));
                            } else {
                                callback.onFailure("Unable to get total income");
                            }
                        })
                        .addOnFailureListener(e -> {
                            callback.onFailure(e.getMessage());
                        });
            }
        });
    }

    public void calcTotalExpense(String username, FireStoreCallback callback){

        getCurrentUser(username, userData -> {
            if (!userData.isEmpty()) {
                db.collection("Expenses")
                        .whereEqualTo("Room_Id", userData.get(1))
                        .get()
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful() && !task.getResult().isEmpty()){
                                Double totalExpense = 0.0;
                                for(QueryDocumentSnapshot document : task.getResult()){
                                    totalExpense += document.getDouble("Price");
                                }
                                callback.onSuccess(String.valueOf(totalExpense));
                            } else {
                                callback.onFailure("Unable to get total expense");
                            }
                        })
                        .addOnFailureListener(e -> {
                            callback.onFailure(e.getMessage());
                        });
            }
        });
    }
    // Method to calculate the difference between total income and total expenses
    public void calcRemainingBalance(String username, FireStoreCallback callback) {
        getCurrentUser(username, userData -> {
            if (!userData.isEmpty()) {

                // First, calculate total income
                calcTotalIncome(username, new FireStoreCallback() {
                    @Override
                    public void onSuccess(String totalIncomeString) {
                        Double totalIncome = Double.valueOf(totalIncomeString);

                        // Then, calculate total expenses
                        calcTotalExpense(username, new FireStoreCallback() {
                            @Override
                            public void onSuccess(String totalExpensesString) {
                                Double totalExpenses = Double.valueOf(totalExpensesString);

                                // Calculate the difference
                                Double difference = totalIncome - totalExpenses;
                                callback.onSuccess(String.valueOf(difference));
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                callback.onFailure("Error calculating total expenses: " + errorMessage);
                            }
                        });
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        callback.onFailure("Error calculating total income: " + errorMessage);
                    }
                });
            }
        });
    }

    private void getCurrentUser(String username, GetUserDataCallback callback){
        List<String> userData = new ArrayList<>();
        db.collection("users")
                .whereEqualTo("username", username)
                .limit(1)
                .get()
                .addOnCompleteListener(user -> {
                    if(user.isSuccessful()){
                        if(!user.getResult().isEmpty()){
                            for (QueryDocumentSnapshot document : user.getResult()) {
                                userData.add(document.getData().get("Name").toString());
                                userData.add(document.getData().get("Room_Id").toString());
                            }
                        }
                    }
                    callback.onCallback(userData);
                });
    }
}
