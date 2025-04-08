package com.example.budgettracker.DataBase;

import android.content.Context;
import android.util.Log;

import com.example.budgettracker.CallBack.GetUserDataCallback;
import com.example.budgettracker.CallBack.QueryCallback;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class DataQuery {
    FirebaseFirestore db;
    String roomId, username;
    UserUtils userUtils;

    public DataQuery(Context context){
        db = FirebaseFirestore.getInstance();
        userUtils = new UserUtils();
        roomId = userUtils.getCurrentRoomId(context);
        username = userUtils.getLoggedInUsername(context);


    }
    public void loadAllExpenses(QueryCallback callback){

        db.collection("Expenses")
                .whereEqualTo("Room_Id",roomId)
                .orderBy("Created_At", Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .addOnCompleteListener(task ->{
                    if(task.isSuccessful() && !task.getResult().isEmpty()) {
                        List<List<String>> expenseData = new ArrayList<>();
                        for (QueryDocumentSnapshot expenseQuery : task.getResult()) {

                            String documentId = expenseQuery.getId();
                            String item = expenseQuery.getString("Item");
                            String name = expenseQuery.getString("Name");
                            Double price = expenseQuery.getDouble("Price");
                            String date = formatDate(expenseQuery.getTimestamp("Created_At"));
                            String isSettled;
                            if(expenseQuery.get("is_settled") != null){
                                isSettled = expenseQuery.get("is_settled").toString();
                            }
                            else{
                                isSettled = "true";
                            }

                            List<String> data = new ArrayList<>();
                            data.add(documentId);
                            data.add(String.valueOf(price));
                            data.add(item);
                            data.add(date);
                            data.add(name);
                            data.add(isSettled);

                            expenseData.add(data);
                        }
                        callback.onSuccess(expenseData);
                    }
                }).addOnFailureListener(e ->{
                    callback.onFailure("Failure in query at: "+e.getMessage());
                    e.printStackTrace();
                });
    }

    public void loadAllIncome(QueryCallback callback) {
        db.collection("Income")
                .whereEqualTo("Room_Id",roomId)
                .orderBy("Created_At",Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        List<List<String>> incomeData = new ArrayList<>();
                        for (QueryDocumentSnapshot incomeQuery : task.getResult()) {

                            String documentId = incomeQuery.getId();  // Document ID
                            String createdAt = formatDate(incomeQuery.getTimestamp("Created_At"));
                            String item = "";
                            String name = incomeQuery.getString("Name");
                            Double price = incomeQuery.getDouble("Price");
                            Boolean isSettled = incomeQuery.getBoolean("is_settled");

                            List<String> data = new ArrayList<>();
                            data.add(documentId);
                            data.add(String.valueOf(price));
                            data.add(item);
                            data.add(createdAt);
                            data.add(name);
                            if(isSettled != null){
                                data.add(isSettled.toString());
                            }
                            else{
                                data.add("false");
                            }

                            incomeData.add(data);
                        }
                        callback.onSuccess(incomeData);
                    }
                }).addOnFailureListener(e -> {
                    Log.e("DataQuery", "Load Income :" + e.getMessage());
                    callback.onFailure("Failure in query at: "+e.getMessage());
                });
    }

    public String formatDate(Timestamp timestamp) {
        try {
            // Convert the timestamp string to a Date object
            Date date = timestamp.toDate();

            // Format the date to the desired format: DD-MON-YYYY at HH:MM AM/PM
            SimpleDateFormat desiredFormat = new SimpleDateFormat("dd-MMM-yyyy 'at' hh:mm a");
            return desiredFormat.format(date);

        } catch (Exception e) {
            Log.e("QueryData", "Format Date: " + e.getMessage());
            return "Invalid Date";
        }
    }
}
