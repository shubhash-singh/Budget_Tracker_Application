package com.example.budgettracker;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.budgettracker.DataBase.DataQuery;
import com.example.budgettracker.CallBack.QueryCallback;
import com.example.budgettracker.DataBase.UserUtils;
import com.example.budgettracker.SearchOption.Search;
import com.example.budgettracker.SearchOption.SearchAdapter;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShowDataPageFragment extends Fragment {

    private static final String ARG_PAGE_TYPE = "page_type";

    private ExpenseAdapter expenseAdapter;
    private List<RecycleVIewPopulate> recycleVIewPopulateList, incomeList;
    private IncomeAdapter incomeAdapter, personalIncomeAdapter;

    DataQuery db;
    private EditText searchEditText;
    private RecyclerView searchRecycleView;
    private SearchAdapter searchAdapter;
    private List<Search> searchList;
    RecyclerView recyclerView;

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

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        db = new DataQuery();
        searchEditText = view.findViewById(R.id.searchEditText);
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
                loadIncome(username);

            } else if ("expenses".equals(pageType)) {
                searchEditText.setVisibility(View.VISIBLE);
                expenseAdapter = new ExpenseAdapter(recycleVIewPopulateList);
                recyclerView.setAdapter(expenseAdapter);
                loadExpenses(username);

            } else if ("personalIncome".equals(pageType)) {
                personalIncomeAdapter = new IncomeAdapter(incomeList);
                recyclerView.setAdapter(personalIncomeAdapter);
                loadPersonalIncome(username);
            }
        }

        searchRecycleView = view.findViewById(R.id.searchRecycleView);
        searchRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));


        searchList = new ArrayList<>();
        searchAdapter = new SearchAdapter(searchList, clickedSearch -> {
            searchEditText.setText(clickedSearch.getItem());
            searchRecycleView.setVisibility(View.GONE);
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        fetchSearch();
        return view;
    }
    private void loadIncome(String username) {

        db.loadAllIncome(username, new QueryCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(List<List<String>> data) {
                incomeList.clear();  // Update this list instead
                for (List<String> item : data) {
                    incomeList.add(new RecycleVIewPopulate(item.get(0), Double.parseDouble(item.get(1)), item.get(2), item.get(3), item.get(4)));
                }
                incomeAdapter.notifyDataSetChanged();  // Notify incomeAdapter
            }

            @Override
            public void onFailure(String message) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Error");
                builder.setMessage(message);
                builder.setPositiveButton("OK", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void loadExpenses(String username){

        db.loadAllExpenses(username, new QueryCallback() {
            @SuppressLint("NotifyDataSetChanged")
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

    private void loadPersonalIncome(String username){
        db.personalIncome(username, new QueryCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(List<List<String>> data) {
                incomeList.clear();  // Update this list instead
                for (List<String> item : data) {
                    incomeList.add(new RecycleVIewPopulate(item.get(0), Double.parseDouble(item.get(1)), item.get(2), item.get(3), item.get(4)));
                }
                personalIncomeAdapter.notifyDataSetChanged();  // Notify incomeAdapter
            }

            @Override
            public void onFailure(String message) {

            }
        });
    }

    public String formatDate(Timestamp timestamp) {
        try {
            // Convert the timestamp string to a Date object
            Date date = timestamp.toDate();

            // Format the date to the desired format: DD-MON-YYYY at HH:MM AM/PM
            SimpleDateFormat desiredFormat = new SimpleDateFormat("dd-MMM-yyyy 'at' hh:mm a");
            return desiredFormat.format(date);

        } catch (Exception e) {
            e.printStackTrace();
            return "Invalid Date";
        }
    }
    private void fetchSearch() {
        FirebaseFirestore fdb = FirebaseFirestore.getInstance();
        fdb.collection("Expenses")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        searchList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String item = document.getString("Item");
                            String name = document.getString("Name");
                            String amount = document.getDouble("Price").toString();
                            String date = formatDate(document.getTimestamp("Created_At"));

                            if (name != null && item != null && date != null) {
                                searchList.add(new Search(amount, item, date, name));
                            }
                        }
                        searchAdapter.notifyDataSetChanged(); // Notify adapter
                        Log.d("SearchDebug", "Fetched " + searchList.size() + " items");
                    } else {
                        Log.e("SearchError", "Failed to fetch users: " + task.getException().getMessage());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("SearchError", e.getMessage());
                });
    }



    private void filterUsers(String query) {
        if (query.isEmpty()) {
            searchRecycleView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE); // Show main data when search is empty
            return;
        }

        List<Search> filteredList = new ArrayList<>();
        for (Search search : searchList) {
            if (search.getItem().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(search);
            }
        }

        if (!filteredList.isEmpty()) {
            searchRecycleView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE); // Hide main data during search
            searchAdapter.setFilteredList(filteredList);
        } else {
            searchRecycleView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }


}


