package com.example.budgettracker;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class IncomeActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    IncomeAdapter incomeAdapter;
    private List<Expense> incomeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        dbHelper = new DatabaseHelper(this);
        RecyclerView recyclerView = findViewById(R.id.income_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadIncomes();
        incomeAdapter = new IncomeAdapter(incomeList);
        recyclerView.setAdapter(incomeAdapter);
    }

    private void loadIncomes(){
        incomeList = new ArrayList<>();
        Cursor cursor = dbHelper.getAllIncomes();
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
                @SuppressLint("Range") double amount = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_INCOME_AMOUNT));
                @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_INCOME_DESCRIPTION));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_INCOME_DATE));
                incomeList.add(new Expense(id, amount, description, date));
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
}