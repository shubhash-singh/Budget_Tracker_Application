package com.example.budgettracker;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;


public class ProfileFragment extends Fragment {

    View view;
    TextView joinRoom, createRoom, roomIdTextView, nameTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);

//        joinRoom = view.findViewById(R.id.join_room_TextView);
//        createRoom = view.findViewById(R.id.create_room_TextView);
        roomIdTextView = view.findViewById(R.id.room_id_TextView);
        nameTextView = view.findViewById(R.id.nameTextView);

        ParseUser user = ParseUser.getCurrentUser();
        String Name = user.getString("Name");
        String roomId = user.getString("Room_Id");

        nameTextView.setText(Name);

        if (roomId == null){
            roomIdTextView.setText(R.string.please_join_a_room);
        }
        else{
            roomIdTextView.setText(roomId);
        }



        return view;
    }
}