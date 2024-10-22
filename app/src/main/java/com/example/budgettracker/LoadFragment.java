package com.example.budgettracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;


public class LoadFragment extends AppCompatActivity {
    BottomNavigationView navbar;
    TextView logout;
    SharedPreferences sp;

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


        navbar.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if(id == R.id.add_data){
                loadFragment(new AddDataFragment());
            }
            else if(id == R.id.show_data){
                loadFragment(new ScreenSlidePager());
            }
            else if(id == R.id.profile){
                loadFragment(new ProfileFragment());
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
}