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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ShowDataPageFragment extends Fragment {

    private static final String ARG_PAGE_TYPE = "page_type";

    private ExpenseAdapter expenseAdapter, personalExpenseAdapter,monthlyExpenseAdapter;
    private List<RecycleVIewPopulate> recycleVIewPopulateList;
    private IncomeAdapter incomeAdapter,personalIncome;
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
            } else if ("monthlyExpense".equals(pageType)) {
                monthlyExpenseAdapter = new ExpenseAdapter(recycleVIewPopulateList);
                recyclerView.setAdapter(monthlyExpenseAdapter);
                loadMonthlyExpenses(roomId);
            }
            else if("personalExpense".equals(pageType)){
                personalExpenseAdapter = new ExpenseAdapter(recycleVIewPopulateList);
                recyclerView.setAdapter(personalExpenseAdapter);
                loadPersonalExpenses();
            }
            else if ("personalIncome".equals(pageType)){
                personalIncome = new IncomeAdapter(recycleVIewPopulateList);

                loadPersonalIncome();
            }
        }

        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    void loadExpenses(String roomId) {
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.whereEqualTo("Room_Id", roomId);

        userQuery.findInBackground((users, e) -> {
            if (e == null) {
//                Create a list to store all the users
                List<String> usernames = new ArrayList<>();
                for (ParseUser user : users){
                    usernames.add(user.getString("Name"));

                }
                ParseQuery<ParseObject> expenseQuery = ParseQuery.getQuery("Expenses");
                expenseQuery.whereContainedIn("Added_by", usernames);
                expenseQuery.orderByDescending("CreatedAt");

                expenseQuery.findInBackground((expenses , e1) ->{
                    if (e1 == null){
                        for (ParseObject expense : expenses) {
                            String id = expense.getObjectId();
                            double amount = expense.getDouble("Price");
                            String description = expense.getString("Item_Description");
                            String date = expense.getCreatedAt().toString();
                            String name = expense.getString("Added_by");

                            recycleVIewPopulateList.add(new RecycleVIewPopulate(id, amount, description, date, name));
                        }
                        expenseAdapter.notifyDataSetChanged();
                    }
                    else {
                        Toast.makeText(getActivity(), e1.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    void loadIncomes(String roomId) {
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.whereEqualTo("Room_Id", roomId);

        userQuery.findInBackground((users, e)->{
            if (e == null){
                List<String> usernames = new ArrayList<>();
                for(ParseUser user : users){
                    usernames.add(user.getString("Name"));
                }

                ParseQuery<ParseObject> incomeQuery = ParseQuery.getQuery("Income");
                incomeQuery.whereContainedIn("Person", usernames);
                incomeQuery.orderByDescending("CreatedAt");

                incomeQuery.findInBackground((incomes, e1) -> {
                    if (e1 == null) {
                        for (ParseObject income : incomes) {
                            String id = income.getObjectId();
                            double amount = income.getDouble("Price");
                            String date = income.getCreatedAt().toString();
                            String name = income.getString("Person");
                            incomeList.add(new RecycleVIewPopulate(id, amount, " ", date, name));
                        }
                        incomeAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getActivity(), e1.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    void loadMonthlyExpenses(String roomId) {
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1; // Calendar.MONTH is zero-based
        int currentYear = calendar.get(Calendar.YEAR);

        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.whereEqualTo("Room_Id", roomId);

        userQuery.findInBackground((users, e) -> {
            if (e == null) {
                List<String> usernames = new ArrayList<>();
                for (ParseUser user : users) {
                    usernames.add(user.getString("Name"));
                }

                ParseQuery<ParseObject> expenseQuery = ParseQuery.getQuery("Expenses");
                expenseQuery.whereContainedIn("Added_by", usernames);

                expenseQuery.findInBackground((expenses, e1) -> {
                    if (e1 == null) {
                        for (ParseObject expense : expenses) {
                            Date createdAt = expense.getCreatedAt();
                            Calendar expenseCalendar = Calendar.getInstance();
                            expenseCalendar.setTime(createdAt);

                            int expenseMonth = expenseCalendar.get(Calendar.MONTH) + 1;
                            int expenseYear = expenseCalendar.get(Calendar.YEAR);

                            if (expenseMonth == currentMonth && expenseYear == currentYear) {
                                String id = expense.getObjectId();
                                double amount = expense.getDouble("Price");
                                String description = expense.getString("Item_Description");
                                String date = createdAt.toString();
                                String name = expense.getString("Added_by");

                                recycleVIewPopulateList.add(new RecycleVIewPopulate(id, amount, description, date, name));
                            }
                        }
                        monthlyExpenseAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getActivity(), e1.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    void loadPersonalExpenses() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        String currentUserName = currentUser.getString("Name");

        ParseQuery<ParseObject> expenseQuery = ParseQuery.getQuery("Expenses");
        expenseQuery.whereEqualTo("Added_by", currentUserName);
        expenseQuery.orderByDescending("CreatedAt");

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
                personalExpenseAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    void loadPersonalIncome() {
        ParseUser currentUser = ParseUser.getCurrentUser(); // Get the current logged-in user
        String currentUserName = currentUser.getString("Name"); // Assuming the 'b  Person' field stores the name of the user

        ParseQuery<ParseObject> incomeQuery = ParseQuery.getQuery("Income");
        incomeQuery.whereEqualTo("Person", currentUserName);
        incomeQuery.orderByDescending("CreatedAt");

        incomeQuery.findInBackground((incomes, e) -> {
            if (e == null) {
                for (ParseObject income : incomes) {
                    String id = income.getObjectId();
                    double amount = income.getDouble("Price");
                    String date = income.getCreatedAt().toString();
                    String name = income.getString("Person");

                    incomeList.add(new RecycleVIewPopulate(id, amount, " ", date, name));
                }
                personalIncome.notifyDataSetChanged();
            } else {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


}
