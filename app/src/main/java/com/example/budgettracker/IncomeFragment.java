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

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class IncomeFragment extends Fragment {
    View view;
    IncomeAdapter incomeAdapter;
    private List<RecycleVIewPopulate> incomeList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_income, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.income_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        incomeList = new ArrayList<>();
        incomeAdapter = new IncomeAdapter(incomeList);
        recyclerView.setAdapter(incomeAdapter);

        loadIncomes();

        return view;
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
                        incomeList.add(new RecycleVIewPopulate(id, amount, " ", date, name));
                    }
                    incomeAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}