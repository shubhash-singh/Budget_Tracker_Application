package com.example.budgettracker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgettracker.DataBase.UserUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;

import java.util.ArrayList;

import  com.example.budgettracker.NotificationSystem.Notification;
import  com.example.budgettracker.NotificationSystem.NotificationAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


public class LoadFragment extends AppCompatActivity {
    BottomNavigationView navbar;
    TextView logout, dropMenuButton;
    SharedPreferences sp;


    RecyclerView notificationList;
    FrameLayout notificationContainer;
    NotificationAdapter notificationAdapter;
    ArrayList<Notification> notifications;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_fragment);

        FirebaseApp.initializeApp(this);


        // setting the status bar to black
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));

        navbar = findViewById(R.id.bottom_navigation_view);
        loadFragment(new AddDataFragment());
        logout = findViewById(R.id.logout_icon);
        dropMenuButton = findViewById(R.id.notification_button);
        db = FirebaseFirestore.getInstance();

        logout.setOnClickListener(v ->{
            sp = getSharedPreferences("login",MODE_PRIVATE);
            if(sp.getBoolean("logged",false)){
                sp.edit().putBoolean("logged",false).apply();
                Intent intent = new Intent(LoadFragment.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
            else{
                Toast.makeText(this, "Log in first !!!", Toast.LENGTH_SHORT).show();
            }
        });

        notificationContainer = findViewById(R.id.notification_container);
        notificationList = findViewById(R.id.notification_list);
        notifications = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(this, notifications);

        notificationList.setLayoutManager(new LinearLayoutManager(this));
        notificationList.setAdapter(notificationAdapter);

        dropMenuButton.setOnClickListener(v -> {
            if (notificationContainer.getVisibility() == View.GONE) {
                notificationContainer.setVisibility(View.VISIBLE);
                fetchNotifications();
            } else {
                notificationContainer.setVisibility(View.GONE);
            }
        });


        navbar.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if(id == R.id.add_data){
                loadFragment(new AddDataFragment());
            }
            else if(id == R.id.show_data){
                loadFragment(new ScreenSlidePager());
            }
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,fragment).commit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchNotifications() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("notifications")
                .whereEqualTo("roomId", "5580251186")
                .orderBy("createdAt")
                .limit(5)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    notifications.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Notification notification = document.toObject(Notification.class);
                        notifications.add(notification);
                    }
                    notificationAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load notifications", Toast.LENGTH_SHORT).show());
    }
    private void performLogout(){


    }
}