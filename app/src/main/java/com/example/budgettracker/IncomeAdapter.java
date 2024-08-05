package com.example.budgettracker;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder> {
    private List<RecycleVIewPopulate> incomeList;

    public IncomeAdapter(List<RecycleVIewPopulate> incomeList) {
        this.incomeList = incomeList;
    }

    @NonNull
    @Override
    public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
        return new IncomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IncomeViewHolder holder, int position) {
        RecycleVIewPopulate income = incomeList.get(position);
        holder.amountTextView.setText(String.valueOf(income.getAmount()));
        holder.nameTextView.setText(income.getName());
        holder.dateTextView.setText(income.getDate());
    }

    @Override
    public int getItemCount() {
        return incomeList.size();
    }

    public static class IncomeViewHolder extends RecyclerView.ViewHolder {
        TextView amountTextView;
        TextView nameTextView;
        TextView dateTextView;

        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateIncomeList(List<RecycleVIewPopulate> newIncomeList) {
        this.incomeList = newIncomeList;
        notifyDataSetChanged();
    }
}
