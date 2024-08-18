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

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ShowDataPageFragment extends Fragment {

    private static final String ARG_PAGE_TYPE = "page_type";

    private ExpenseAdapter expenseAdapter, personalExpenseAdapter;
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

        // Getting the current user and user roomId
        ParseUser user = ParseUser.getCurrentUser();
        String roomId = user.getString("Room_Id");

        if (getArguments() != null) {
            String pageType = getArguments().getString(ARG_PAGE_TYPE);
            if ("income".equals(pageType)) {
                incomeAdapter = new IncomeAdapter(incomeList);
                recyclerView.setAdapter(incomeAdapter);
                loadIncomes(roomId);
            } else if ("expenses".equals(pageType)) {
                expenseAdapter = new ExpenseAdapter(recycleVIewPopulateList);
                recyclerView.setAdapter(expenseAdapter);
                loadExpenses(roomId);
            } else if ("personalExpense".equals(pageType)) {
                personalExpenseAdapter = new ExpenseAdapter(recycleVIewPopulateList);
                recyclerView.setAdapter(personalExpenseAdapter);
                loadPersonalExpenses(roomId);
            }
        }

        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    void loadExpenses(String roomId) {
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.whereEqualTo("Room_Id", roomId);

        ParseQuery<ParseObject> expenseQuery = ParseQuery.getQuery("Expenses");
        expenseQuery.whereMatchesQuery("Added_by", userQuery); // Using whereMatchesQuery to match users with the same Room_Id

        expenseQuery.findInBackground((expenses, e) -> {
            if (e == null) {
                for (ParseObject expense : expenses) {
                    String id = expense.getObjectId();
                    double amount = expense.getDouble("Price");
                    String description = expense.getString("Item_Description");
                    String date = expense.getCreatedAt().toString();

                    ParseUser addedByUser = expense.getParseUser("Added_by"); // Retrieve the user who added the expense
                    String name = addedByUser != null ? addedByUser.getUsername() : "Unknown";

                    recycleVIewPopulateList.add(new RecycleVIewPopulate(id, amount, description, date, name));
                }
                expenseAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    void loadIncomes(String roomId) {
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.whereEqualTo("Room_Id", roomId);

        ParseQuery<ParseObject> incomeQuery = ParseQuery.getQuery("Income");
        incomeQuery.whereMatchesQuery("Person", userQuery); // Same method to match users with the same Room_Id

        incomeQuery.findInBackground((incomes, e) -> {
            if (e == null) {
                for (ParseObject income : incomes) {
                    String id = income.getObjectId();
                    double amount = income.getDouble("Price");
                    String date = income.getCreatedAt().toString();

                    ParseUser person = income.getParseUser("Person"); // Retrieve the user associated with the income
                    String name = person != null ? person.getUsername() : "Unknown";

                    incomeList.add(new RecycleVIewPopulate(id, amount, " ", date, name));
                }
                incomeAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    void loadPersonalExpenses(String roomId) {
        ParseQuery<ParseObject> personalExpenseQuery = ParseQuery.getQuery("Expenses");
        personalExpenseQuery.whereEqualTo("Added_by", ParseUser.getCurrentUser()); // Directly match the current user
        personalExpenseQuery.whereEqualTo("Room_Id", roomId);
        personalExpenseQuery.orderByDescending("createdAt");

        personalExpenseQuery.findInBackground((expenses, e) -> {
            if (e == null) {
                for (ParseObject expense : expenses) {
                    String id = expense.getObjectId();
                    double amount = expense.getDouble("Price");
                    String description = expense.getString("Item_Description");
                    String date = expense.getCreatedAt().toString();

                    ParseUser addedByUser = expense.getParseUser("Added_by");
                    String name = addedByUser != null ? addedByUser.getUsername() : "Unknown";

                    recycleVIewPopulateList.add(new RecycleVIewPopulate(id, amount, description, date, name));
                }
                personalExpenseAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
