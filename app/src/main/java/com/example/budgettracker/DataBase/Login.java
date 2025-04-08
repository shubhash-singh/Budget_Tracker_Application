package com.example.budgettracker.DataBase;


import android.content.Context;
import android.content.SharedPreferences;

import com.example.budgettracker.CallBack.FireStoreCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login {
    private String username, password;
    Context context;

    public Login(){
//        default constructor
    }

    public Login(String username, String password, Context context){
        this.username = username;
        this.password = password;
        this.context = context;
    }

    public void performLogin(FireStoreCallback callback){


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("username", username)
                .whereEqualTo("password", password)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    String roomId = "";
                    String name = "";
                    for(DocumentSnapshot doc : task.getResult()){
                        roomId = doc.getString("Room_Id");
                        name = doc.getString("Name");
                    }

                    // Save user info in SharedPreferences after successful login
                    SharedPreferences sp = context.getSharedPreferences("login", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("username", username);
                    editor.putString("roomid", roomId);
                    editor.putString("name", name);
                    editor.putBoolean("logged", true);  // Mark user as logged in
                    editor.apply();


                    String message;
                    if(task.isSuccessful()){
                        if (!task.getResult().isEmpty()) {
                            message = "Login Successful !!!";
                            callback.onSuccess(message);
                            return;
                        } else {
                            // No matching user found
                            message = "Invalid username or password.";
                        }
                    } else {
                        // If there's an error during the query
                        message = "Error getting documents: " + task.getException();
                    }
                    callback.onFailure(message);
                })
                .addOnFailureListener(e ->{
                    callback.onFailure("Login:" + e.getMessage());
                });
    }
}
