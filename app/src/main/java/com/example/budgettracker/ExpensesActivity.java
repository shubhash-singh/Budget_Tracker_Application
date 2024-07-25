package com.example.budgettracker;
import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ExpensesActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    ExpenseAdapter expenseAdapter;
    private List<Expense> expenseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);

        dbHelper = new DatabaseHelper(this);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadExpenses();
        expenseAdapter = new ExpenseAdapter(expenseList);
        recyclerView.setAdapter(expenseAdapter);
    }

    private void loadExpenses() {
        expenseList = new ArrayList<>();
        Cursor cursor = dbHelper.getAllExpenses();
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
                @SuppressLint("Range") double amount = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_EXPENSE_AMOUNT));
                @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_EXPENSE_DESCRIPTION));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_EXPENSE_DATE));
                expenseList.add(new Expense(id, amount, description, date));
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
}
