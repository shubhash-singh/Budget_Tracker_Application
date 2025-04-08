package com.example.budgettracker;

import android.annotation.SuppressLint;
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

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {
    private final List<RecycleVIewPopulate> recycleVIewPopulateList;
    private UserUtils userUtils;

    public ExpenseAdapter(List<RecycleVIewPopulate> recycleVIewPopulateList) {
        this.recycleVIewPopulateList = recycleVIewPopulateList;
        userUtils = new UserUtils();
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        RecycleVIewPopulate recycleVIewPopulate = recycleVIewPopulateList.get(position);
        holder.amountTextView.setText(String.format("-%s", recycleVIewPopulate.getAmount()));
        holder.descriptionTextView.setText(recycleVIewPopulate.getDescription());
        holder.dateTextView.setText(recycleVIewPopulate.getDate());
        holder.nameTextView.setText(recycleVIewPopulate.getName());

        if (!recycleVIewPopulate.isSettled()){
            holder.amountTextView.setBackgroundResource(R.drawable.bg_button);
        }
        String user = userUtils.getCurrentUser(holder.itemView.getContext());
        if(user.equals("dareme")){
            holder.amountTextView.setOnClickListener(aVoid -> {
                if(!recycleVIewPopulate.isSettled()){
                    Log.d("ExpenseClicked", "onBindViewHolder: "+"Expense clicked >>>>>>>>>>>>>>>>>>>>>");
                    markAsSettled(recycleVIewPopulate, holder);
                    notifyDataSetChanged();
                }
            });
        }
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
    private void markAsSettled(RecycleVIewPopulate expense, ExpenseViewHolder holder) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Update Firestore document
        db.collection("Expenses")
                .document(expense.getId()) // Assuming you have a method to get the Firestore document ID
                .update("is_settled", true)
                .addOnSuccessListener(aVoid -> {
                    // Update local object
                    expense.setIsSettled("true");

                    // Change UI to reflect the update
                    holder.amountTextView.setBackground(new ColorDrawable(ContextCompat.getColor(holder.itemView.getContext(), R.color.white)));
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Log.e("Firestore", "Error updating expense", e);
                });
    }
}
