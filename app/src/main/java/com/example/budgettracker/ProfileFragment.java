package com.example.budgettracker;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    View view;
    TextView joinRoom, createRoom, roomIdTextView, nameTextView;
    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Firebase Firestore and FirebaseAuth
        db = FirebaseFirestore.getInstance();

        // Find views
        roomIdTextView = view.findViewById(R.id.room_id_TextView);
        nameTextView = view.findViewById(R.id.nameTextView);



        return view;
    }
}
