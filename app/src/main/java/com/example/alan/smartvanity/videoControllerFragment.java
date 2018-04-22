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
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import jp.wasabeef.blurry.Blurry;

/**
 * Created by Alan on 3/24/2018.
 */

public class videoControllerFragment extends Fragment {
    String TAG = "controllerFragment";

    View myView;

    ImageView mBackgroundImageView;
    Boolean mBackgroundBlurred = false;

    SeekBar volumeRocker;

    DatabaseReference myRef;
    FirebaseDatabase database;

    String uid;

    int count;

    Button playButton;
    Button forwardButton;
    Button backButton;

    boolean playButtonFlag;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.video_controller_layout, container, false);
        return myView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        mBackgroundImageView = (ImageView) getView().findViewById(R.id.activity_controller_background_image_view);

        if (mBackgroundBlurred) {
            Log.d(TAG, "Background is already blurred...");
        } else {
            blurBackground();
        }

        SharedPreferences id_sharedpreferences = this.getActivity().getSharedPreferences("id", Context.MODE_PRIVATE);
        uid = id_sharedpreferences.getString("uid", "");

        count = 0;

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
        myRef = myRef.child(uid).child("control").child("controller");

        volumeRocker = getView().findViewById(R.id.volume_rocker);
        volumeRocker.setMax(10);


        volumeRocker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.d("DEBUG345", seekBar.getProgress() + "");

                myRef = myRef.getParent().child("sound");
                myRef.setValue(seekBar.getProgress());

                myRef = myRef.getParent().child("controller");
                myRef.setValue("@15" + count);
                count++;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        playButtonFlag = true;

        playButton = getView().findViewById(R.id.play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRef.setValue("@17" + count);
                Log.d(TAG, "play");
                count++;

                if (playButtonFlag) {
                    playButton.setBackgroundResource(R.drawable.play_button);
                    playButtonFlag = false;
                } else {
                    playButtonFlag = true;
                    playButton.setBackgroundResource(R.drawable.pause_button);
                }
            }
        });

        forwardButton = getView().findViewById(R.id.forward_button);
        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRef.setValue("@18" + count);
                Log.d(TAG, "forward");
                count++;
            }
        });

        backButton = getView().findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRef.setValue("@19" + count);
                Log.d(TAG, "back");
                count++;
            }
        });



    }

    @Override
    public void onResume() {
        super.onResume();
    }



    @Override
    public void onDestroy() {
        Log.d("DEBUG123", "onDestroy: called.");
        super.onDestroy();
    }


    private void blurBackground() {
        mBackgroundImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mBackgroundBlurred) {
                    Log.d(TAG, "Background already blurred...");
                } else {
                    Log.d(TAG, "Blurring background...");
                    Blurry.with(getActivity())
                            .radius(44)
                            .animate(500)
                            .capture(mBackgroundImageView)
                            .into(mBackgroundImageView);
                    mBackgroundBlurred = true;
                }
            }
        });
    }
}
