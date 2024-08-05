package com.example.budgettracker;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.parse.Parse;
import com.parse.ParseUser;



public class MainActivity extends AppCompatActivity {
    Button loginButton;
    EditText emailEditText, passwordEditText;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build()
        );

        // setting the status bar to black
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));


        loginButton = findViewById(R.id.loginButton);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        sp = getSharedPreferences("login",MODE_PRIVATE);
        if(sp.getBoolean("logged",false)){
            Intent intent = new Intent(MainActivity.this, LoadFragment.class);
            startActivity(intent);
        }

        loginButton.setOnClickListener(v -> {

            try {
                String userName = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

//                boolean isUserNameVerified = false;
//                Pattern pattern = "^[A-Za-z]+[_]*[0-9A-Za-z]*";
                ParseUser.logInInBackground(userName, password, (parseUser, e) -> {
                    if (parseUser != null) {
                        sp.edit().putBoolean("logged",true).apply();
                        Intent intent = new Intent(MainActivity.this, LoadFragment.class);
                        startActivity(intent);
                    }
                    else{
                        ParseUser.logOut();
                        Toast.makeText(this, "Incorrect Username or Password", Toast.LENGTH_SHORT).show();
                    }
                });
            }catch (Exception e){
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