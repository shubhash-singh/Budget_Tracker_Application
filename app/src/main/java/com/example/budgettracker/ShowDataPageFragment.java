package com.example.budgettracker;
import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class ShowDataPageFragment extends Fragment {

    private static final String ARG_PAGE_TYPE = "page_type";

    private ExpenseAdapter expenseAdapter;
    private List<RecycleVIewPopulate> recycleVIewPopulateList;
    private IncomeAdapter incomeAdapter;
    private List<RecycleVIewPopulate> incomeList;

    public static ShowDataPageFragment newInstance(String pageType) {
        ShowDataPageFragment fragment = new ShowDataPageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PAGE_TYPE, pageType);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen_slide_page, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recycleVIewPopulateList = new ArrayList<>();
        incomeList = new ArrayList<>();

        if (getArguments() != null) {
            String pageType = getArguments().getString(ARG_PAGE_TYPE);
            if ("income".equals(pageType)) {
                incomeAdapter = new IncomeAdapter(incomeList);
                recyclerView.setAdapter(incomeAdapter);
                loadIncomes();
            } else if ("expenses".equals(pageType)) {
                expenseAdapter = new ExpenseAdapter(recycleVIewPopulateList);
                recyclerView.setAdapter(expenseAdapter);
                loadExpenses();
            }
        }

        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    void loadExpenses() {
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

    @SuppressLint("NotifyDataSetChanged")
    void loadIncomes() {
        ParseQuery<ParseObject> expenseQuery = ParseQuery.getQuery("Income");
        expenseQuery.findInBackground(new FindCallback<ParseObject>() {
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
