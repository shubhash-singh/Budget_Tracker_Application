package com.example.budgettracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;


public class IncomeAdapter  extends RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder>{
    List<Expense> incomeList;

    public IncomeAdapter(List<Expense> expenseList) {
        this.incomeList = expenseList;
    }

    @NonNull
    @Override
    public IncomeAdapter.IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_income_layout, parent, false);
        return new IncomeAdapter.IncomeViewHolder(view);
    }
    public void onBindViewHolder(@NonNull IncomeAdapter.IncomeViewHolder holder, int position) {
        Expense expense = incomeList.get(position);
        holder.amountTextView.setText(String.valueOf(expense.getAmount()));
        holder.descriptionTextView.setText(expense.getDescription());
        holder.dateTextView.setText(expense.getDate());
    }

    @Override
    public int getItemCount() {
        return incomeList.size();
    }

    public static class IncomeViewHolder extends RecyclerView.ViewHolder {
        TextView amountTextView;
        TextView descriptionTextView;
        TextView dateTextView;

        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }
}
