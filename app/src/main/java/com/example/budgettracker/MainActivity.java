package com.example.budgettracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int MIN_SPLASH_TIME = 2000; // Minimum 2 seconds
    private static final String PREFS_NAME = "login";
    private static final String VERSION_KEY = "AppVersion";

    private Thread internetCheckThread;
    private boolean isRunning = true; // Flag to stop thread when activity is destroyed
    private boolean isInternetAvailable = false; // Track internet status

    private long splashStartTime; // Track splash start time

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Splash screen layout

        splashStartTime = System.currentTimeMillis(); // Start time of splash
        startInternetCheckThread(); // Start checking internet every 3s
    }

    // ✅ Thread to check internet connection every 3 seconds
    private void startInternetCheckThread() {
        internetCheckThread = new Thread(() -> {
            while (isRunning) {
                runOnUiThread(() -> {
                    boolean currentStatus = checkInternet(MainActivity.this);
                    if (currentStatus != isInternetAvailable) {
                        isInternetAvailable = currentStatus;
                    }

                    if (!isInternetAvailable) {
                        Toast.makeText(MainActivity.this, "No stable internet connection!", Toast.LENGTH_SHORT).show();
                    } else {
                        // If internet is available, stop thread and proceed
                        proceedAfterSplash();
                    }
                });

                try {
                    Thread.sleep(3000); // Wait for 3 seconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        internetCheckThread.start();
    }

    // ✅ Ensure splash screen is shown for at least 2 seconds
    private void proceedAfterSplash() {
        long elapsedTime = System.currentTimeMillis() - splashStartTime;
        long remainingTime = MIN_SPLASH_TIME - elapsedTime;

        new Handler().postDelayed(this::checkAppStatus, Math.max(remainingTime, 0));
    }

    private void checkAppStatus() {
        if (isInternetAvailable) {
            // Check if app is updated
            if (isAppUpdated()) {
                logoutUser();
            } else {
                // If app is not updated, go to HomeActivity
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        }
    }

    // ✅ Check internet connectivity
    private boolean checkInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
                return capabilities != null &&
                        (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
            } else {
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                return activeNetwork != null && activeNetwork.isConnected();
            }
        }
        return false;
    }

    // ✅ Check if the app has been updated
    private boolean isAppUpdated() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int savedVersion = prefs.getInt(VERSION_KEY, -1);
        int currentVersion = getCurrentAppVersion();

        if (savedVersion != -1 && savedVersion != currentVersion) {
            Log.d("AppUpdate", "App version changed! Logging out...");
            return true;
        }

        prefs.edit().putInt(VERSION_KEY, currentVersion).apply();
        return false;
    }

    // ✅ Get current installed app version
    private int getCurrentAppVersion() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    // ✅ Logout user and go to LoginActivity
    private void logoutUser() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunning = false; // Stop the thread when activity is destroyed
    }
}
