package com.example.budgettracker;


import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.budgettracker.CallBack.FireStoreCallback;
import com.example.budgettracker.DataBase.Login;
import com.example.budgettracker.DataBase.UserUtils;
import com.google.firebase.FirebaseApp;


public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is already logged in
        UserUtils userUtils = new UserUtils();



        setContentView(R.layout.activity_login);

        FirebaseApp.initializeApp(this);

        // Setting the status bar to black
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));

        if (userUtils.isLoggedIn(this)){
            Intent intent = new Intent(LoginActivity.this, LoadFragment.class);
            startActivity(intent);
            finish();
        }

        Button loginButton = findViewById(R.id.loginButton);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        loginButton.setOnClickListener(v -> {
            try {
                String username = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                Login login = new Login(username, password, this);

                login.performLogin(new FireStoreCallback() {
                    @Override
                    public void onSuccess(String message) {
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        emailEditText.setText("");
                        passwordEditText.setText("");

                        Intent intent = new Intent(LoginActivity.this, LoadFragment.class);
                        startActivity(intent);
                        finish();

                    }

                    @Override
                    public void onFailure(String message) {
                        emailEditText.setText("");
                        passwordEditText.setText("");
                    }
                });


            } catch (Exception e) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Error");
                builder.setMessage(e.getMessage());
                builder.setPositiveButton("OK", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
}