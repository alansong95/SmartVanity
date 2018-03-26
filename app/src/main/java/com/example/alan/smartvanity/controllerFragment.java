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

    int count;

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
        myRef = myRef.child(uid).child("control").child("controller");

        Button leftButton = getView().findViewById(R.id.button_left);
        Button dleftButton = getView().findViewById(R.id.button_dleft);
        Button tleftButton = getView().findViewById(R.id.button_tleft);
        Button rightButton = getView().findViewById(R.id.button_right);
        Button drightButton = getView().findViewById(R.id.button_dright);
        Button trightButton = getView().findViewById(R.id.button_tright);
        Button upButton = getView().findViewById(R.id.button_up);
        Button dupButton = getView().findViewById(R.id.button_dup);
        Button tupButton = getView().findViewById(R.id.button_tup);
        Button downButton = getView().findViewById(R.id.button_down);
        Button ddownButton = getView().findViewById(R.id.button_ddown);
        Button tdownButton = getView().findViewById(R.id.button_tdown);
        Button clickButton = getView().findViewById(R.id.button_click);
        Button sendButton = getView().findViewById(R.id.button_send);
        final EditText editText = getView().findViewById(R.id.edit_text);

        count = 0;

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.setValue("left" + count);
                Log.d(TAG, "left");
                count++;
            }
        });
        dleftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.setValue("dleft" + count);
                Log.d(TAG, "dleft");
                count++;
            }
        });
        tleftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.setValue("tleft" + count);
                Log.d(TAG, "tleft");
                count++;
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.setValue("right" + count);
                count++;
            }
        });
        drightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.setValue("dright" + count);
                count++;
            }
        });
        trightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.setValue("tright" + count);
                count++;
            }
        });

        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.setValue("up" + count);
                count++;
            }
        });
        dupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.setValue("dup" + count);
                count++;
            }
        });
        tupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.setValue("tup" + count);
                count++;
            }
        });

        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.setValue("down" + count);
                count++;
            }
        });
        ddownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.setValue("ddown" + count);
                count++;
            }
        });
        tdownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.setValue("tdown" + count);
                count++;
            }
        });

        clickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.setValue("click" + count);
                count++;
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                myRef = myRef.getParent().child("StringInput");
                myRef.setValue(editText.getText().toString());

                myRef = myRef.getParent().child("controller");
                myRef.setValue("send" + count);
                count++;
            }
        });
    }
}
