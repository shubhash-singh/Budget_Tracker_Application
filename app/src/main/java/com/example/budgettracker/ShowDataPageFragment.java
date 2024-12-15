package com.example.budgettracker;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.budgettracker.DataBase.DataQuery;
import com.example.budgettracker.CallBack.QueryCallback;
import com.example.budgettracker.DataBase.UserUtils;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ShowDataPageFragment extends Fragment {

    private static final String ARG_PAGE_TYPE = "page_type";

    private ExpenseAdapter expenseAdapter;
    private List<RecycleVIewPopulate> recycleVIewPopulateList, incomeList, personalIncomeList;
    private IncomeAdapter incomeAdapter, personalIncomeAdapter;

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
        personalIncomeList = new ArrayList<>();

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
                expenseAdapter = new ExpenseAdapter(recycleVIewPopulateList);
                recyclerView.setAdapter(expenseAdapter);
                loadExpenses(username);

            } else if ("personalIncome".equals(pageType)) {
                personalIncomeAdapter = new IncomeAdapter(incomeList);
                recyclerView.setAdapter(personalIncomeAdapter);
                loadPersonalIncome(username);
            }
        }

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

}


