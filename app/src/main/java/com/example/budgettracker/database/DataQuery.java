package com.example.budgettracker.database;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DataQuery {
    FirebaseFirestore db;

    public DataQuery(){
        db = FirebaseFirestore.getInstance();
    }

    public void loadAllExpenses(String username, QueryCallback callback){

        getCurrentUser(username, userData -> {
            if(!userData.isEmpty()){
                db.collection("Expenses")
                        .whereEqualTo("Room_Id", userData.get(1))
                        .get()
                        .addOnCompleteListener(task ->{
                            if(task.isSuccessful() && !task.getResult().isEmpty()) {
                                List<List<String>> expenseData = new ArrayList<>();
                                for (QueryDocumentSnapshot expenseQuery : task.getResult()) {

                                    String documentId = expenseQuery.getId();  // Document ID
                                    String createdAt = expenseQuery.getString("Created_At");
                                    String item = expenseQuery.getString("Item");
                                    String name = expenseQuery.getString("Name");
                                    Double price = expenseQuery.getDouble("Price");

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
                        }).addOnFailureListener(e ->{
                            callback.onFailure("Failure in query at: "+e.getMessage());
                        });
            }
            else {
                callback.onFailure("Unable to get user data");
            }
        });
    }

    public void loadAllIncome(String username, QueryCallback callback){
        getCurrentUser(username, userData -> {
            if(!userData.isEmpty()){
                db.collection("Income")
                        .whereEqualTo("Room_Id", userData.get(1))
                        .get()
                        .addOnCompleteListener(task ->{
                            if(task.isSuccessful() && !task.getResult().isEmpty()){
                                List<List<String>> incomeQuery = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()){
                                    String documentId = document.getId();  // Document ID
                                    String createdAt = document.getString("Created_At");
                                    String item = "";
                                    String name = document.getString("Name");
                                    Double price = document.getDouble("Price");

                                    List<String> data = new ArrayList<>();
                                    data.add(documentId);
                                    data.add(String.valueOf(price));
                                    data.add(item);
                                    data.add(createdAt);
                                    data.add(name);

                                    incomeQuery.add(data);
                                }
                                callback.onSuccess(incomeQuery);
                            }
                        }).addOnFailureListener(e ->{
                            callback.onFailure("Failure in query at: "+e.getMessage());
                        });
            }else{
                callback.onFailure("Unable to get user data");
            }

        });
    }

    public void loadMonthlyIncome(String username, QueryCallback callback){

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
