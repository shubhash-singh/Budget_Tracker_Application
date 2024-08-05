package com.example.budgettracker;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class ExpenseFragment extends Fragment {
    ExpenseAdapter expenseAdapter;
    private List<RecycleVIewPopulate> recycleVIewPopulateList;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_expense, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recycleVIewPopulateList = new ArrayList<>();
        expenseAdapter = new ExpenseAdapter(recycleVIewPopulateList);
        recyclerView.setAdapter(expenseAdapter);

        loadExpenses();
        return view;
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
                    recycleVIewPopulateList.add(new RecycleVIewPopulate(id, amount, description, date, name));
                }
                expenseAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}