package com.example.budgettracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class AddUserActivity extends AppCompatActivity {

    Button signUp;
    EditText nameEditText, usernameEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

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

        signUp = findViewById(R.id.signUpButton);
        nameEditText = findViewById(R.id.nameEditText);
        usernameEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        signUp.setOnClickListener(v -> {
            ParseUser user = new ParseUser();

            String userName = usernameEditText.getText().toString().trim();
            String name = nameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            nameEditText.setText("");
            passwordEditText.setText("");
            usernameEditText.setText("");

            user.put("username", userName);
            user.put("Name", name);
            user.put("password", password);


            user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(AddUserActivity.this, "SignUp successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddUserActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        ParseUser.logOut();
                        Toast.makeText(AddUserActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        });

    }
}