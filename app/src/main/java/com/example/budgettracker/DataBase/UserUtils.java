package com.example.budgettracker.DataBase;

import android.content.Context;
import android.content.SharedPreferences;

public class UserUtils {
    public String getLoggedInUsername(Context context) {
        SharedPreferences sp = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        return sp.getString("username", null);  // Returns username or null if not logged in
    }
    public String getCurrentUser(Context context){
        SharedPreferences sp = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        return sp.getString("name", null);
    }
    public String getCurrentRoomId(Context context){
        SharedPreferences sp = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        return sp.getString("roomid", null);
    }
    public boolean isLoggedIn(Context context) {
        SharedPreferences sp = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        return sp.getBoolean("logged", false);  // Returns true if user is logged in
    }
}
