package com.example.budgettracker.database;


import android.content.Context;
import android.content.SharedPreferences;
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

                    // Save user info in SharedPreferences after successful login
                    SharedPreferences sp = context.getSharedPreferences("login", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("username", username);
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
                });
    }
}
