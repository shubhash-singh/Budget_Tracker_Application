package com.example.budgettracker;

import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgettracker.DataBase.UserUtils;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder> {
    private List<RecycleVIewPopulate> incomeList;
    private final UserUtils userUtils = new UserUtils();

    public IncomeAdapter(List<RecycleVIewPopulate> incomeList) {
        this.incomeList = incomeList;
    }

    @NonNull
    @Override
    public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_income_layout, parent, false);
        return new IncomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IncomeViewHolder holder, int position) {
        RecycleVIewPopulate income = incomeList.get(position);
        holder.amountTextView.setText(String.format("+%s", income.getAmount()));
        holder.nameTextView.setText(income.getName());
        holder.dateTextView.setText(income.getDate());

        if (!income.isSettled()){
            holder.amountTextView.setBackgroundResource(R.drawable.bg_button);
        }
        if (userUtils.getLoggedInUsername(holder.itemView.getContext()).equals("dareme")){
            holder.amountTextView.setOnClickListener(view -> {
                if(!income.isSettled()){
                    markAsSettled(income, holder);
                }
            });
        }
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

    private void markAsSettled(RecycleVIewPopulate income, IncomeAdapter.IncomeViewHolder holder) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Update Firestore document
        db.collection("Income")
                .document(income.getId()) // Assuming you have a method to get the Firestore document ID
                .update("is_settled", true)
                .addOnSuccessListener(aVoid -> {
                    // Update local object
                    income.setIsSettled("true");
                    holder.amountTextView.setBackground(new ColorDrawable(ContextCompat.getColor(holder.itemView.getContext(), R.color.white)));
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Log.e("Firestore", "Error updating expense", e);
                });
    }
}
