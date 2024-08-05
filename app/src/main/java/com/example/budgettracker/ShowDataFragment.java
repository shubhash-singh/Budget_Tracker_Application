package com.example.budgettracker;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Objects;


public class ShowDataFragment extends Fragment {

    Button allIncome, allExpense;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_show_data, container, false);

        allIncome = view.findViewById(R.id.show_all_income);
        allExpense = view.findViewById(R.id.show_all_expenses);

        allExpense.setOnClickListener(v -> {
            loadFragment(new ExpenseFragment());
        });

        allIncome.setOnClickListener(v -> {
            loadFragment(new IncomeFragment());
        });

        return view;
    }
    private void loadFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,fragment).commit();

    }
}