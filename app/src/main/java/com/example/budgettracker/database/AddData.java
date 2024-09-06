package com.example.budgettracker.database;

import android.annotation.SuppressLint;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddData{
    FirebaseFirestore db;
    public AddData(){

        db = FirebaseFirestore.getInstance();
    }
    public void addExpense(String description, Double amount, String username, FireStoreCallback callback) {

        getCurrentUser(username, userData -> {
            if(!userData.isEmpty()){

                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
                String createdDate = dateFormat.format(date);

                Map<String, Object> expenseData = new HashMap<>();

                expenseData.put("Name", userData.get(0));
                expenseData.put("Room_Id", userData.get(1));
                expenseData.put("Item", description);
                expenseData.put("Price", amount);
                expenseData.put("Created_At", createdDate);

                db.collection("Expenses")
                        .add(expenseData)
                        .addOnSuccessListener(expense -> {
                            // return message for successful data written
                            callback.onSuccess("Expense added");

                        })
                        .addOnFailureListener(e -> {
                            // Call onFailure method of the callback
                            callback.onFailure(e.getMessage());
                        });
            }
        });
    }

    public void addIncome(Double amount, String username, FireStoreCallback callback){
        getCurrentUser(username, userData -> {
            if (!userData.isEmpty()) {

                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
                String createdDate = dateFormat.format(date);


                Map<String, Object> incomeData = new HashMap<>();

                incomeData.put("Name", userData.get(0));
                incomeData.put("Room_Id", userData.get(1));
                incomeData.put("Price", amount);
                incomeData.put("Created_At", createdDate);

                db.collection("Income")
                        .add(incomeData)
                        .addOnSuccessListener( e -> {
                            callback.onSuccess("Income added");
                        })
                        .addOnFailureListener( e -> {
                            callback.onFailure("Error while adding Income !!!");
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
