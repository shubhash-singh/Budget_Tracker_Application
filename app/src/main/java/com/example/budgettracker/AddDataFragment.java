package com.example.budgettracker;

import android.annotation.SuppressLint;
import android.os.Bundle;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class AddDataFragment extends Fragment {
    public double remainingBalance, totalIncome, totalExpense;
    private EditText expenseAmount, expenseDescription;
    private EditText incomeAmount;
    TextView balanceTextView;
    Button addExpenseButton, addIncomeButton, viewExpensesButton, viewIncomeButton;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_data, container, false);

        // RecycleVIewPopulate fields
        expenseAmount = view.findViewById(R.id.expenseAmount);
        expenseDescription = view.findViewById(R.id.expenseDescription);
        addExpenseButton = view.findViewById(R.id.addExpenseButton);

        // Income fields
        incomeAmount = view.findViewById(R.id.incomeAmount);
        addIncomeButton = view.findViewById(R.id.addIncomeButton);

        // Balance fields
        balanceTextView = view.findViewById(R.id.balanceTextView);

        // View all field
        viewExpensesButton = view.findViewById(R.id.show_all_expenses);
        viewIncomeButton = view.findViewById(R.id.show_all_income);

        calcRemainingBalance();

        addExpenseButton.setOnClickListener(v -> {

            String str_amount = expenseAmount.getText().toString().trim();
            
            String description = expenseDescription.getText().toString().trim();
            String firstLetter = description.substring(0, 1).toUpperCase();
            String restOfString = description.substring(1);
            String str_description = firstLetter.concat(restOfString);

            if(str_amount.isEmpty() || str_description.isEmpty()){
                Toast.makeText(getActivity(), "Fill all fields", Toast.LENGTH_SHORT).show();
            }
            else if (isValidAmount(str_amount)) {
                double amount = Double.parseDouble(str_amount);
                expenseAmount.setText("");
                expenseDescription.setText("");

                // Getting the Name
                ParseUser user = ParseUser.getCurrentUser();
                String name = user.getString("Name");
                try {
                    ParseObject expense = new ParseObject("Expenses");
                    expense.put("Item_Description", str_description);
                    assert name != null;
                    expense.put("Added_by", name);
                    expense.put("Price", amount);
                    expense.save();
                    calcRemainingBalance();
                    Toast.makeText(getActivity(), "Expense Added", Toast.LENGTH_SHORT).show();
                } catch (Exception e){
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                    builder.setTitle("Error");
                    builder.setMessage(e.getMessage());
                    builder.setPositiveButton("OK", null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
            else{
                Toast.makeText(getActivity(), "Invalid Amount", Toast.LENGTH_SHORT).show();
            }
        });

        addIncomeButton.setOnClickListener(v -> {
            String str_amount = incomeAmount.getText().toString().trim();
            if (str_amount.isEmpty()) {
                Toast.makeText(getActivity(), "Fill all fields", Toast.LENGTH_SHORT).show();
            }
            else if (isValidAmount(str_amount)){
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
                    calcRemainingBalance();
                    Toast.makeText(getActivity(), "Income Added", Toast.LENGTH_SHORT).show();
                } catch (Exception e){
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                    builder.setTitle("Error");
                    builder.setMessage(e.getMessage());
                    builder.setPositiveButton("OK", null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
            else {
                Toast.makeText(getContext(), "Invalid Amount", Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }

    @SuppressLint("SetTextI18n")
    private void calcRemainingBalance(){
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
                Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();

            }
        });
        incomeQuery.cancel();
        remainingBalance = totalIncome;

    }

    private boolean isValidAmount(String str){
        double amount = Double.parseDouble(str);
        return !(amount < 0) && !(amount > 10000);
    }
}