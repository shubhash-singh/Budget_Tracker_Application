package com.example.budgettracker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class AddDataActivity extends AppCompatActivity {

    public double remainingBalance, totalIncome, totalExpense;
    private EditText expenseAmount, expenseDescription;
    private EditText incomeAmount;
    TextView balanceTextView;
    Button addExpenseButton, addIncomeButton, viewExpensesButton, viewIncomeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);

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


        // Expense fields
        expenseAmount = findViewById(R.id.expenseAmount);
        expenseDescription = findViewById(R.id.expenseDescription);
        addExpenseButton = findViewById(R.id.addExpenseButton);

        // Income fields
        incomeAmount = findViewById(R.id.incomeAmount);
        addIncomeButton = findViewById(R.id.addIncomeButton);

        // Balance fields
        balanceTextView = findViewById(R.id.balanceTextView);

        // View all field
        viewExpensesButton = findViewById(R.id.show_all_expenses);
        viewIncomeButton = findViewById(R.id.show_all_income);

        balanceTextView.setOnClickListener(v -> calcRemaingBalance());

        addExpenseButton.setOnClickListener(v -> {

            String str_amount = expenseAmount.getText().toString().trim();
            String description = expenseDescription.getText().toString().trim();
            if(str_amount.isEmpty() || description.isEmpty()){
                Toast.makeText(AddDataActivity.this, "Fill all fields", Toast.LENGTH_SHORT).show();
            }
            else {
                double amount = Double.parseDouble(str_amount);
                expenseAmount.setText("");
                expenseDescription.setText("");

                // Getting the Name
                ParseUser user = ParseUser.getCurrentUser();
                String name = user.getString("Name");
                try {
                    ParseObject expense = new ParseObject("Expenses");
                    expense.put("Item_Description", description);
                    assert name != null;
                    expense.put("Added_by", name);
                    expense.put("Price", amount);
                    expense.save();
                    calcRemaingBalance();
                    Toast.makeText(this, "Expense Added", Toast.LENGTH_SHORT).show();
                } catch (Exception e){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Error");
                    builder.setMessage(e.getMessage());
                    builder.setPositiveButton("OK", null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

        addIncomeButton.setOnClickListener(v -> {
            String str_amount = incomeAmount.getText().toString().trim();



            if (str_amount.isEmpty()) {
                Toast.makeText(AddDataActivity.this, "Fill all fields", Toast.LENGTH_SHORT).show();
            }
            else {
                double amount = Double.parseDouble(str_amount);
                incomeAmount.setText("");

                // Getting the Name
                ParseUser user = ParseUser.getCurrentUser();
                String name = user.getString("Name");

                // Adding income Data
                try {
                    ParseObject income = new ParseObject("Income");
                    assert name != null;
                    income.put("Person", name);
                    income.put("Price", amount);
                    income.save();
                    calcRemaingBalance();
                    Toast.makeText(AddDataActivity.this, "Income Added", Toast.LENGTH_SHORT).show();
                } catch (Exception e){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Error");
                    builder.setMessage(e.getMessage());
                    builder.setPositiveButton("OK", null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });



        viewExpensesButton.setOnClickListener(v -> {
            Intent intent = new Intent(AddDataActivity.this, ExpensesActivity.class);
            startActivity(intent);
        });

        viewIncomeButton.setOnClickListener(v -> {
            Intent intent = new Intent(AddDataActivity.this, IncomeActivity.class);
            startActivity(intent);
        });
    }

    @SuppressLint("SetTextI18n")
    private void calcRemaingBalance(){
        remainingBalance = 0;
        totalExpense = 0;
        totalIncome = 0;
        ParseQuery<ParseObject> incomeQuery = ParseQuery.getQuery("Income");
        incomeQuery.findInBackground((incomes, e) -> {
            if (e == null) {
                for (ParseObject income : incomes) {
                    totalIncome += income.getDouble("Price");
                }
                ParseQuery<ParseObject> expenseQuery = ParseQuery.getQuery("Expenses");
                expenseQuery.findInBackground((expenses, ef) -> {
                    if (ef == null) {
                        for (ParseObject expense : expenses) {
                            totalIncome -= expense.getDouble("Price");
                        }
                    }
                    balanceTextView.setText("Remaining Balance: " + totalIncome);
                });
            } else {
                Toast.makeText(AddDataActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        incomeQuery.cancel();
        remainingBalance = totalIncome;
        //balanceTextView.setText("Remaining Balance: " + totalIncome+ " " + totalExpense);
    }
}
