package com.example.budgettracker;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import java.util.ArrayList;
import java.util.List;

public class ExpensesActivity extends AppCompatActivity {
    ExpenseAdapter expenseAdapter;
    private List<Expense> expenseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);

        // setting the status bar to black
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        expenseList = new ArrayList<>();
        expenseAdapter = new ExpenseAdapter(expenseList);
        recyclerView.setAdapter(expenseAdapter);

        loadExpenses();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadExpenses() {
        ParseQuery<ParseObject> expenseQuery = ParseQuery.getQuery("Expenses");
        expenseQuery.findInBackground((expenses, e) -> {
            if (e == null) {
                for (ParseObject expense : expenses) {
                    String id = expense.getObjectId();
                    double amount = expense.getDouble("Price");
                    String description = expense.getString("Item_Description");
                    String date = expense.getCreatedAt().toString();
                    String name = expense.getString("Added_by");
                    expenseList.add(new Expense(id, amount, description, date, name));
                }
                expenseAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
