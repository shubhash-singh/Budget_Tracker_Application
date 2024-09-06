package com.example.budgettracker;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.budgettracker.database.DataQuery;
import com.example.budgettracker.database.QueryCallback;
import com.example.budgettracker.database.UserUtils;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ShowDataPageFragment extends Fragment {

    private static final String ARG_PAGE_TYPE = "page_type";

    private ExpenseAdapter expenseAdapter, personalExpenseAdapter, monthlyExpenseAdapter;
    private List<RecycleVIewPopulate> recycleVIewPopulateList;
    private IncomeAdapter incomeAdapter, personalIncome;
    private List<RecycleVIewPopulate> incomeList;
    DataQuery db;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

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

        db = new DataQuery();


        recycleVIewPopulateList = new ArrayList<>();
        incomeList = new ArrayList<>();

        // Getting the current username
        UserUtils userUtils = new UserUtils();
        String username = userUtils.getLoggedInUsername(getContext());

        // Initialize the Query class
        DataQuery db = new DataQuery();

        if (getArguments() != null) {
            String pageType = getArguments().getString(ARG_PAGE_TYPE);
            if ("income".equals(pageType)) {
                incomeAdapter = new IncomeAdapter(incomeList);
                recyclerView.setAdapter(incomeAdapter);
//                loadIncome();
            } else if ("expenses".equals(pageType)) {
                expenseAdapter = new ExpenseAdapter(recycleVIewPopulateList);
                recyclerView.setAdapter(expenseAdapter);
                loadExpenses(username);
            } else if ("monthlyExpense".equals(pageType)) {
                monthlyExpenseAdapter = new ExpenseAdapter(recycleVIewPopulateList);
                recyclerView.setAdapter(monthlyExpenseAdapter);
//                loadMonthlyExpenses();
            } else if ("personalExpense".equals(pageType)) {
                personalExpenseAdapter = new ExpenseAdapter(recycleVIewPopulateList);
                recyclerView.setAdapter(personalExpenseAdapter);
//                loadPersonalExpenses();
            } else if ("personalIncome".equals(pageType)) {
                personalIncome = new IncomeAdapter(incomeList);
                recyclerView.setAdapter(personalIncome);
//                loadPersonalIncome();
            }
        }

        return view;
    }
    private void loadIncome(String username){
        db.loadAllIncome(username, new QueryCallback() {
            @Override
            public void onSuccess(List<List<String>> data) {
                recycleVIewPopulateList.clear();
                for(List<String> item : data){
                    recycleVIewPopulateList.add(new RecycleVIewPopulate(item.get(0), Double.parseDouble(item.get(1)), item.get(2), item.get(3), item.get(4)));
                }
                incomeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String message) {

            }
        });
    }
    private void loadExpenses(String username){

        db.loadAllExpenses(username, new QueryCallback() {
            @Override
            public void onSuccess(List<List<String>> data) {
                recycleVIewPopulateList.clear(); // Clear previous data
                for (List<String> item : data) {
                    recycleVIewPopulateList.add(new RecycleVIewPopulate(item.get(0), Double.parseDouble(item.get(1)), item.get(2), item.get(3), item.get(4)));
                }
                expenseAdapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(String message) {

            }
        });

    }

//    @SuppressLint("NotifyDataSetChanged")
//    void loadMonthlyExpenses(String roomId) {
//        Calendar calendar = Calendar.getInstance();
//        int currentMonth = calendar.get(Calendar.MONTH) + 1; // Calendar.MONTH is zero-based
//        int currentYear = calendar.get(Calendar.YEAR);
//
//        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
//        userQuery.whereEqualTo("Room_Id", roomId);
//
//        userQuery.findInBackground((users, e) -> {
//            if (e == null) {
//                List<String> usernames = new ArrayList<>();
//                for (ParseUser user : users) {
//                    usernames.add(user.getString("Name"));
//                }
//
//                ParseQuery<ParseObject> expenseQuery = ParseQuery.getQuery("Expenses");
//                expenseQuery.whereContainedIn("Added_by", usernames);
//                expenseQuery.orderByDescending("createdAt");
//
//                expenseQuery.findInBackground((expenses, e1) -> {
//                    if (e1 == null) {
//                        for (ParseObject expense : expenses) {
//                            Date createdAt = expense.getCreatedAt();
//                            Calendar expenseCalendar = Calendar.getInstance();
//                            expenseCalendar.setTime(createdAt);
//
//                            int expenseMonth = expenseCalendar.get(Calendar.MONTH) + 1;
//                            int expenseYear = expenseCalendar.get(Calendar.YEAR);
//
//                            if (expenseMonth == currentMonth && expenseYear == currentYear) {
//                                String id = expense.getObjectId();
//                                double amount = expense.getDouble("Price");
//                                String description = expense.getString("Item_Description");
//                                String date = createdAt.toString();
//                                String name = expense.getString("Added_by");
//
//                                recycleVIewPopulateList.add(new RecycleVIewPopulate(id, amount, description, date, name));
//                            }
//                        }
//                        monthlyExpenseAdapter.notifyDataSetChanged();
//                    } else {
//                        Toast.makeText(getActivity(), e1.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        });
//    }
}


