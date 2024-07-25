package com.example.budgettracker;

import android.content.Intent;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private String date;
    private EditText expenseAmount, expenseDescription;
    private EditText incomeAmount, incomeDescription;
    private TextView balanceTextView;
    Button addExpenseButton, addIncomeButton, viewExpensesButton, viewIncomeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        // Expense fields
        expenseAmount = findViewById(R.id.expenseAmount);
        expenseDescription = findViewById(R.id.expenseDescription);
        addExpenseButton = findViewById(R.id.addExpenseButton);

        // Income fields
        incomeAmount = findViewById(R.id.incomeAmount);
        incomeDescription = findViewById(R.id.incomeDescription);
        addIncomeButton = findViewById(R.id.addIncomeButton);

        // Balance fields
        balanceTextView = findViewById(R.id.balanceTextView);

        // View all field
        viewExpensesButton = findViewById(R.id.show_all_expenses);
        viewIncomeButton = findViewById(R.id.show_all_income);

        // Get the current date
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM, yyyy", Locale.ENGLISH);
            date = LocalDate.now().format(formatter);
        }
        balanceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRemaingBalance();
            }
        });
        addExpenseButton.setOnClickListener(v -> {

            String str_amount = expenseAmount.getText().toString().trim();
            String description = expenseDescription.getText().toString().trim();
            if(str_amount.isEmpty() || description.isEmpty()){
                Toast.makeText(MainActivity.this, "Fill all fields", Toast.LENGTH_SHORT).show();
            }
            else {
                double amount = Double.parseDouble(str_amount);
                expenseAmount.setText("");
                expenseDescription.setText("");
                dbHelper.addExpense(amount, description, date);
                Toast.makeText(MainActivity.this, "Expense added", Toast.LENGTH_SHORT).show();
                setRemaingBalance();
            }
        });

        addIncomeButton.setOnClickListener(v -> {
            String str_amount = incomeAmount.getText().toString().trim();
            String description = incomeDescription.getText().toString().trim();
            if (str_amount.isEmpty() || description.isEmpty()) {
                Toast.makeText(MainActivity.this, "Fill all fields", Toast.LENGTH_SHORT).show();
            }
            else {
                double amount = Double.parseDouble(str_amount);
                incomeAmount.setText("");
                incomeDescription.setText("");
                dbHelper.addIncome(amount, description, date);
                Toast.makeText(MainActivity.this, "Income added", Toast.LENGTH_SHORT).show();
                setRemaingBalance();
            }
        });



        viewExpensesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ExpensesActivity.class);
                startActivity(intent);
            }
        });

        viewIncomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, IncomeActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }

    @SuppressLint("SetTextI18n")
    private void setRemaingBalance(){
        double remainingBalance = dbHelper.getRemainingBalance();
        balanceTextView.setText("Remaining Balance: " + remainingBalance);
    }
}
