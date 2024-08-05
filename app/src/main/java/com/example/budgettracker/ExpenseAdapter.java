package com.example.budgettracker;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {
    private List<RecycleVIewPopulate> recycleVIewPopulateList;

    public ExpenseAdapter(List<RecycleVIewPopulate> recycleVIewPopulateList) {
        this.recycleVIewPopulateList = recycleVIewPopulateList;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        RecycleVIewPopulate recycleVIewPopulate = recycleVIewPopulateList.get(position);
        holder.amountTextView.setText(String.valueOf(recycleVIewPopulate.getAmount()));
        holder.descriptionTextView.setText(recycleVIewPopulate.getDescription());
        holder.dateTextView.setText(recycleVIewPopulate.getDate());
        holder.nameTextView.setText(recycleVIewPopulate.getName());
    }

    @Override
    public int getItemCount() {
        return recycleVIewPopulateList.size();
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView amountTextView;
        TextView descriptionTextView;
        TextView dateTextView;
        TextView nameTextView;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateExpenseList(List<RecycleVIewPopulate> newRecycleVIewPopulateList) {
        this.recycleVIewPopulateList = newRecycleVIewPopulateList;
        notifyDataSetChanged();
    }
}
