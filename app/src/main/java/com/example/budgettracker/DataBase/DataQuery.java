package com.example.budgettracker.DataBase;

import com.example.budgettracker.CallBack.GetUserDataCallback;
import com.example.budgettracker.CallBack.QueryCallback;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.Date;
import java.util.List;

public class DataQuery {
    FirebaseFirestore db;

    public DataQuery(){
        db = FirebaseFirestore.getInstance();
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
                                userData.add(document.getData().get("Name").toString().trim());
                                userData.add(document.getData().get("Room_Id").toString().trim());
                            }
                        }
                    }
                    callback.onCallback(userData);
                });
    }

    public void loadAllExpenses(String username, QueryCallback callback){

        getCurrentUser(username, userData -> {
            if(userData.size() == 2){
                db.collection("Expenses")
                        .whereEqualTo("Room_Id", userData.get(1))
//                        .whereEqualTo("Name", "Shubhash Singh")
                        .orderBy("Created_At", Query.Direction.DESCENDING)
                        .limit(100)
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
                                    List<String> data = new ArrayList<>();
                                    data.add(documentId);
                                    data.add(String.valueOf(price));
                                    data.add(item);
                                    data.add(date);
                                    data.add(name);

                                    expenseData.add(data);
                                }
                                callback.onSuccess(expenseData);
                            }
                        }).addOnFailureListener(e ->{
                            callback.onFailure("Failure in query at: "+e.getMessage());
                            e.printStackTrace();
                        });

            }
            else {
                callback.onFailure("Unable to get user data");
            }
        });
    }

    public void loadAllIncome(String username, QueryCallback callback) {
        getCurrentUser(username, userData -> {
            if (userData.size() == 2) {
                db.collection("Income")
                        .whereEqualTo("Room_Id", userData.get(1))
                        .orderBy("Created_At",Query.Direction.DESCENDING)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                List<List<String>> expenseData = new ArrayList<>();
                                for (QueryDocumentSnapshot incomeQuery : task.getResult()) {

                                    String documentId = incomeQuery.getId();  // Document ID
                                    String createdAt = formatDate(incomeQuery.getTimestamp("Created_At"));
                                    String item = "";
                                    String name = incomeQuery.getString("Name");
                                    Double price = incomeQuery.getDouble("Price");

                                    List<String> data = new ArrayList<>();
                                    data.add(documentId);
                                    data.add(String.valueOf(price));
                                    data.add(item);
                                    data.add(createdAt);
                                    data.add(name);

                                    expenseData.add(data);
                                }
                                callback.onSuccess(expenseData);
                            }
                        }).addOnFailureListener(e -> {
                            e.printStackTrace();
                            callback.onFailure("Failure in query at: "+e.getMessage());
                        });
            } else {
                callback.onFailure("Unable to get user data");
            }
        });
    }

    public void personalIncome(String username, QueryCallback callback){
        getCurrentUser(username, userData -> {
            if (userData.size() == 2) {

                // Get the start and end of the current month
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_MONTH, 1);  // Set to first day of the month
                Date startOfMonth = calendar.getTime();
                Timestamp startTimestamp = new Timestamp(startOfMonth);

                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)); // Last day of the month
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                Date endOfMonth = calendar.getTime();
                Timestamp endTimestamp = new Timestamp(endOfMonth);


                db.collection("Income")
                        .whereEqualTo("Name", userData.get(0))
                        .whereEqualTo("Room_Id", userData.get(1))
//                        .whereGreaterThanOrEqualTo("Created_At", startTimestamp)  // Start of the current month
//                        .whereLessThanOrEqualTo("Created_At", endTimestamp)
                        .orderBy("Created_At")
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                List<List<String>> expenseData = new ArrayList<>();
                                for (QueryDocumentSnapshot incomeQuery : task.getResult()) {

                                    String documentId = incomeQuery.getId();  // Document ID
                                    String createdAt = formatDate(incomeQuery.getTimestamp("Created_At"));
                                    String item = "";
                                    String name = incomeQuery.getString("Name");
                                    Double price = incomeQuery.getDouble("Price");

                                    List<String> data = new ArrayList<>();
                                    data.add(documentId);
                                    data.add(String.valueOf(price));
                                    data.add(item);
                                    data.add(createdAt);
                                    data.add(name);

                                    expenseData.add(data);
                                }
                                callback.onSuccess(expenseData);
                            }
                        }).addOnFailureListener(e -> {
                            callback.onFailure("Failure in query at: " + e.getMessage());
                            e.printStackTrace();
                        });
            } else {
                callback.onFailure("Unable to get user data");
            }
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
            e.printStackTrace();
            return "Invalid Date";
        }
    }
}
