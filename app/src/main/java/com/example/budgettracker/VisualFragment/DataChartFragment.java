package com.example.budgettracker.VisualFragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.budgettracker.DataBase.UserUtils;
import com.example.budgettracker.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class DataChartFragment extends Fragment {

   private FirebaseFirestore db;
    private UserUtils userUtils;
    private String roomId;
    private BarChart incomeBarChart;
    private BarChart expenseBarChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data_chart, container, false);

        db = FirebaseFirestore.getInstance();

        userUtils = new UserUtils();
        roomId = userUtils.getCurrentRoomId(requireContext());

        incomeBarChart = view.findViewById(R.id.incomeBarChart);
        expenseBarChart = view.findViewById(R.id.expenseBarChart);

        loadIncomeChart();
        loadExpenseChart();

        return view;
    }

    private void populateBarChartIncome(BarChart barChart, List<Double> data) {
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            entries.add(new BarEntry(i, data.get(i).floatValue()));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Income");
        dataSet.setColors(Color.GREEN);
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(Color.BLACK);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(new String[]{"Current Month", "Previous Month", "2 Months Ago"}));
        barChart.getXAxis().setTextSize(12f);
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        barChart.invalidate(); // Refresh chart
    }

    private void populateBarChartExpense(BarChart barChart, List<Double> data) {
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            entries.add(new BarEntry(i, data.get(i).floatValue()));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Expense");
        dataSet.setColors(Color.RED);
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(Color.BLACK);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(new String[]{"Current Month", "Previous Month", "2 Months Ago"}));
        barChart.getXAxis().setTextSize(12f);
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        barChart.invalidate(); // Refresh chart
    }

    private void loadIncomeChart() {
        List<Task<QuerySnapshot>> tasks = new ArrayList<>();

        // Get the current date
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1; // Firestore uses 0-based months

        // Prepare a list of target months and years
        List<Integer> targetMonths = new ArrayList<>();
        List<Integer> targetYears = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            int month = currentMonth - i;
            int year = currentYear;

            // Handle wrap-around for January (0-based month)
            if (month <= 0) {
                month += 12;
                year -= 1;
            }

            targetMonths.add(month);
            targetYears.add(year);
        }

        db.collection("Income")
                .whereEqualTo("Room_Id", roomId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Double> incomeData = new ArrayList<>();
                    for (int i = 0; i < 3; i++) {
                        incomeData.add(0.0); // Initialize income values for each month
                    }

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        if (doc.contains("Created_At")) {
                            Object createdAtObj = doc.get("Created_At");
                            if (createdAtObj instanceof com.google.firebase.Timestamp) {
                                Calendar docCalendar = Calendar.getInstance();
                                docCalendar.setTime(((com.google.firebase.Timestamp) createdAtObj).toDate());

                                int docMonth = docCalendar.get(Calendar.MONTH) + 1;
                                int docYear = docCalendar.get(Calendar.YEAR);

                                for (int i = 0; i < 3; i++) {
                                    if (docMonth == targetMonths.get(i) && docYear == targetYears.get(i)) {
                                        Double price = doc.getDouble("Price");
                                        if (price != null) {
                                            incomeData.set(i, incomeData.get(i) + price);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Log.d("FirestoreQuery", "Income Data: " + incomeData);
                    populateBarChartIncome(incomeBarChart, incomeData);
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching income data", e));
    }

    private void loadExpenseChart() {
        List<Task<QuerySnapshot>> tasks = new ArrayList<>();

        // Get the current date
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1; // Firestore uses 0-based months

        // Prepare a list of target months and years
        List<Integer> targetMonths = new ArrayList<>();
        List<Integer> targetYears = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            int month = currentMonth - i;
            int year = currentYear;

            // Handle wrap-around for January (0-based month)
            if (month <= 0) {
                month += 12;
                year -= 1;
            }

            targetMonths.add(month);
            targetYears.add(year);
        }

        db.collection("Expenses")
                .whereEqualTo("Room_Id", roomId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Double> expenseData = new ArrayList<>();
                    for (int i = 0; i < 3; i++) {
                        expenseData.add(0.0); // Initialize income values for each month
                    }

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        if (doc.contains("Created_At")) {
                            Object createdAtObj = doc.get("Created_At");
                            if (createdAtObj instanceof com.google.firebase.Timestamp) {
                                Calendar docCalendar = Calendar.getInstance();
                                docCalendar.setTime(((com.google.firebase.Timestamp) createdAtObj).toDate());

                                int docMonth = docCalendar.get(Calendar.MONTH) + 1;
                                int docYear = docCalendar.get(Calendar.YEAR);

                                for (int i = 0; i < 3; i++) {
                                    if (docMonth == targetMonths.get(i) && docYear == targetYears.get(i)) {
                                        Double price = doc.getDouble("Price");
                                        if (price != null) {
                                            expenseData.set(i, expenseData.get(i) + price);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Log.d("FirestoreQuery", "Income Data: " + expenseData);
                    populateBarChartExpense(expenseBarChart, expenseData);
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching income data", e));
    }

}
