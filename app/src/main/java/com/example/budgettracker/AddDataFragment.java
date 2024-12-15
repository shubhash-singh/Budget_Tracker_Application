package com.example.budgettracker;

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

import com.example.budgettracker.DataBase.AddData;
import com.example.budgettracker.CallBack.FireStoreCallback;
import com.example.budgettracker.DataBase.UserUtils;

public class AddDataFragment extends Fragment {
    private EditText expenseAmount, expenseDescription;
    private EditText incomeAmount;
    TextView balanceTextView, incomeTextView, expenseTextView;
    Button addExpenseButton, addIncomeButton;
    View view;
    String username;
    AddData addData;
    Double totalIncome, totalExpense;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_data, container, false);

        // Initialize the AddData class
        addData = new AddData();
        UserUtils userUtils = new UserUtils();

        // RecycleView Populate fields
        expenseAmount = view.findViewById(R.id.expenseAmount);
        expenseDescription = view.findViewById(R.id.expenseDescription);
        addExpenseButton = view.findViewById(R.id.addExpenseButton);

        // Income fields
        incomeAmount = view.findViewById(R.id.incomeAmount);
        addIncomeButton = view.findViewById(R.id.addIncomeButton);

        // Balance fields
        balanceTextView = view.findViewById(R.id.balanceTextView);
        incomeTextView = view.findViewById(R.id.incomeTextView);
        expenseTextView = view.findViewById(R.id.expenseTextView);

        username = userUtils.getLoggedInUsername(getContext());
        calcRemainingBalance(username);
        addExpenseButton.setOnClickListener(v -> {
            String str_amount = expenseAmount.getText().toString().trim();
            String description = expenseDescription.getText().toString().trim();

            // Getting 1st char is Caps
            String firstLetter = description.substring(0, 1).toUpperCase();
            String restOfString = description.substring(1);
            String str_description = firstLetter.concat(restOfString);

            if(str_amount.isEmpty() || str_description.isEmpty()){
                Toast.makeText(getActivity(), "Fill all fields", Toast.LENGTH_SHORT).show();
            } else if (isValidAmount(str_amount)) {
                double amount = Double.parseDouble(str_amount);
                expenseAmount.setText("");
                expenseDescription.setText("");
                // Adding expense to Firestore
                try {

                    if(username != null){
                        addData.addExpense(str_description, amount, username, new FireStoreCallback(){

                            @Override
                            public void onSuccess(String message) {
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(String message) {
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        });
                        calcRemainingBalance(username);
                    } else {
                        Toast.makeText(getActivity(), "Unable to get username", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                    builder.setTitle("Error");
                    builder.setMessage(e.getMessage());
                    builder.setPositiveButton("OK", null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            } else {
                Toast.makeText(getActivity(), "Invalid Amount", Toast.LENGTH_SHORT).show();
            }
        });

        addIncomeButton.setOnClickListener(v -> {
            String str_amount = incomeAmount.getText().toString().trim();
            if (str_amount.isEmpty()) {
                Toast.makeText(getActivity(), "Fill all fields", Toast.LENGTH_SHORT).show();
            } else if (isValidAmount(str_amount)){
                incomeAmount.setText("");

                try {
                    double amount = Double.parseDouble(str_amount);

                    if(username != null ){
                        addData.addIncome(amount, username, new FireStoreCallback() {
                            @Override
                            public void onSuccess(String message) {
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(String message) {
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        });
                        calcRemainingBalance(username);
                    } else {
                        Toast.makeText(getActivity(), "Unable to get username", Toast.LENGTH_SHORT).show();
                    }
        

                } catch (Exception e) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                    builder.setTitle("Error");
                    builder.setMessage(e.getMessage());
                    builder.setPositiveButton("OK", null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            } else {
                Toast.makeText(getContext(), "Invalid Amount", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void calcRemainingBalance(String username){

        addData.calcTotalIncome(username, new FireStoreCallback() {
            @Override
            public void onSuccess(String message) {
                incomeTextView.setText(message);
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
        addData.calcTotalExpense(username, new FireStoreCallback() {
            @Override
            public void onSuccess(String message) {
                expenseTextView.setText(message);
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
        addData.calcRemainingBalance(username, new FireStoreCallback() {
            @Override
            public void onSuccess(String message) {
                balanceTextView.setText(message);
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidAmount(String str){
        double amount = Double.parseDouble(str);
        return !(amount < 0) && !(amount > 10000);
    }
}
