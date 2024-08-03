package com.example.budgettracker;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import java.util.ArrayList;
import java.util.List;

public class IncomeActivity extends AppCompatActivity {
    IncomeAdapter incomeAdapter;
    private List<Expense> incomeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        // setting the status bar to black
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));

        RecyclerView recyclerView = findViewById(R.id.income_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        incomeList = new ArrayList<>();
        incomeAdapter = new IncomeAdapter(incomeList);
        recyclerView.setAdapter(incomeAdapter);

        loadIncomes();
    }

    void loadIncomes() {
        ParseQuery<ParseObject> expenseQuery = ParseQuery.getQuery("Income");
        expenseQuery.findInBackground(new FindCallback<ParseObject>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void done(List<ParseObject> incomes, ParseException e) {
                if (e == null) {
                    for (ParseObject income : incomes) {
                        String id = income.getObjectId();
                        double amount = income.getDouble("Price");
                        String name = income.getString("Person");
                        String date = income.getCreatedAt().toString();
                        incomeList.add(new Expense(id, amount, " ", date, name));
                    }
                    incomeAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(IncomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
