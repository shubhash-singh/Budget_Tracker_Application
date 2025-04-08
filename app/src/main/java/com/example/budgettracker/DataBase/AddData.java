package com.example.budgettracker.DataBase;

import android.content.Context;
import android.util.Log;

import com.example.budgettracker.CallBack.FireStoreCallback;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddData{
    FirebaseFirestore db;
    UserUtils userUtils;



    String username, roomId, name;
    public AddData(Context context){

        db = FirebaseFirestore.getInstance();
        userUtils = new UserUtils();
        username = userUtils.getLoggedInUsername(context);
        roomId = userUtils.getCurrentRoomId(context);
        name = userUtils.getCurrentUser(context);
    }
    public void addExpense(String description, Double amount, FireStoreCallback callback) {

        Date date = new Date();



        Map<String, Object> expenseData = new HashMap<>();
        expenseData.put("Name",name);
        expenseData.put("Room_Id", roomId);
        expenseData.put("Item", description);
        expenseData.put("Price", amount);
        expenseData.put("Created_At", date);
        expenseData.put("is_settled", false);



        db.collection("Expenses")
                .add(expenseData)
                .addOnSuccessListener(expense -> callback.onSuccess("Expense added successfully"))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void addIncome(Double amount, FireStoreCallback callback){
        Date date = new Date();
        Map<String, Object> incomeData = new HashMap<>();
        incomeData.put("Name", name);
        incomeData.put("Room_Id", roomId);
        incomeData.put("Price", amount);
        incomeData.put("Created_At", date);
        incomeData.put("is_settled", false);

        db.collection("Income")
                .add(incomeData)
                .addOnSuccessListener( e -> callback.onSuccess("Income added Successfully"))
                .addOnFailureListener( e -> callback.onFailure("Error while adding Income !!!"));

    }
    public void calcRemainingBalance(FireStoreCallback callback) {
        // Create two queries to fetch income and expenses in parallel
        Task<QuerySnapshot> incomeTask = db.collection("Income")
                .whereEqualTo("Room_Id", roomId)
                .get();

        Task<QuerySnapshot> expenseTask = db.collection("Expenses")
                .whereEqualTo("Room_Id", roomId)
                .get();

        // Run both tasks in parallel and process the results when both are complete
        Tasks.whenAllSuccess(incomeTask, expenseTask)
                .addOnSuccessListener(results -> {
                    double totalIncome = 0.0;
                    double totalExpense = 0.0;

                    // Process income results
                    QuerySnapshot incomeSnapshot = (QuerySnapshot) results.get(0);
                    for (QueryDocumentSnapshot document : incomeSnapshot) {
                        totalIncome += document.getDouble("Price");
                    }

                    // Process expense results
                    QuerySnapshot expenseSnapshot = (QuerySnapshot) results.get(1);
                    for (QueryDocumentSnapshot document : expenseSnapshot) {
                        totalExpense += document.getDouble("Price");
                    }

                    // Calculate remaining balance
                    double remainingBalance = totalIncome - totalExpense;
                    callback.onSuccess(String.valueOf(remainingBalance));
                })
                .addOnFailureListener(e -> {
                    Log.e("RemainingBalance", "Error fetching data: " + e.getMessage());
                    callback.onFailure("Error fetching income/expenses: " + e.getMessage());
                });
    }





}
