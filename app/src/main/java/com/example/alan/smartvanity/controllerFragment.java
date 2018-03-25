package com.example.alan.smartvanity;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Alan on 3/24/2018.
 */

public class controllerFragment extends Fragment {
    String TAG = "controllerFragment";

    View myView;

    String uid;

    FirebaseDatabase database;
    DatabaseReference myRef;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.controller_layout, container, false);
        return myView;
    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences id_sharedpreferences = this.getActivity().getSharedPreferences("id", Context.MODE_PRIVATE);
        uid = id_sharedpreferences.getString("uid", "");

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
        myRef = myRef.child(uid).child("controller");


        Button leftButton = getView().findViewById(R.id.button_left);
        Button rightButton = getView().findViewById(R.id.button_right);
        Button upButton = getView().findViewById(R.id.button_up);
        Button downButton = getView().findViewById(R.id.button_down);
        Button clickButton = getView().findViewById(R.id.button_click);
        Button sendButton = getView().findViewById(R.id.button_send);
        final EditText editText = getView().findViewById(R.id.edit_text);

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.setValue("left");
                Log.d(TAG, "left");
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.setValue("right");
            }
        });

        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.setValue("up");
            }
        });

        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.setValue("down");
            }
        });

        clickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.setValue("click");
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.setValue("send");
                myRef = myRef.getParent().child("StringInput");
                myRef.setValue(editText.getText().toString());
                myRef = myRef.getParent().child("controller");
            }
        });
    }
}
