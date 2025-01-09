package com.example.budgettracker.SearchOption;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgettracker.R;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.UserViewHolder> {

    private List<Search> searchList;
    private final OnUserClickListener clickListener;

    public interface OnUserClickListener {
        void onUserClick(Search clickedUser);
    }

    public SearchAdapter(List<Search> userList, OnUserClickListener clickListener) {
        this.searchList = userList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Search search = searchList.get(position);
        holder.amountTextView.setText(String.valueOf(search.getAmount()));
        holder.descriptionTextView.setText(search.getItem());
        holder.dateTextView.setText(search.getDate());
        holder.nameTextView.setText(search.getName());

        // Handle item click
        holder.itemView.setOnClickListener(v -> clickListener.onUserClick(search));
    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }

    public void setFilteredList(List<Search> filteredList) {
        this.searchList = new ArrayList<>(filteredList); // Avoid referencing external lists
        notifyDataSetChanged();
    }


    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, amountTextView, dateTextView, descriptionTextView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
        }
    }
}
