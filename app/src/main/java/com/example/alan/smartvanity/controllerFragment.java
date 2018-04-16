package com.example.alan.smartvanity;

import android.app.Fragment;
import android.app.ProgressDialog;
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
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.Manifest;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.UUID;

import jp.wasabeef.blurry.Blurry;

/**
 * Created by Alan on 3/24/2018.
 */

public class controllerFragment extends Fragment {
    String TAG = "controllerFragment";

    View myView;

    String uid;

//    FirebaseDatabase database;
//    DatabaseReference myRef;

//    int count;

    ProgressDialog mProgressDialog;

    BluetoothAdapter mBluetoothAdapter;
    Button mbtnDiscover;

    BluetoothConnectionService mBluetoothConnection;

    Button btnSend;

    EditText etSend;

    BluetoothDevice mBTDevice;

    FirebaseDatabase database;
    DatabaseReference myRef;

    int count;

    Button leftButton;
    Button dleftButton;
    Button tleftButton;
    Button rightButton;
    Button drightButton;
    Button trightButton;
    Button upButton;
    Button dupButton;
    Button tupButton;
    Button downButton;
    Button ddownButton;
    Button tdownButton;
    Button clickButton;
    Button endButton;

    SeekBar seekBar;

    ImageView mBackgroundImageView;
    Boolean mBackgroundBlurred = false;


    public void enableButton(boolean en) {
        leftButton.setEnabled(en);
        dleftButton.setEnabled(en);
        tleftButton.setEnabled(en);
        rightButton.setEnabled(en);
        drightButton.setEnabled(en);
        trightButton.setEnabled(en);
        upButton.setEnabled(en);
        dupButton.setEnabled(en);
        tupButton.setEnabled(en);
        downButton.setEnabled(en);
        ddownButton.setEnabled(en);
        tdownButton.setEnabled(en);
        clickButton.setEnabled(en);
        btnSend.setEnabled(en);
        endButton.setEnabled(en);
    }

    /**
     * Broadcast Receiver that detects bond state changes (Pairing status changes)
     */
    private final BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 cases:
                //case1: bonded already
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED.");
                    //inside BroadcastReceiver4
                    mBTDevice = mDevice;
                }
                //case2: creating a bone
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING.");
                }
                //case3: breaking a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE.");
                }
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.controller_layout, container, false);
        return myView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Broadcasts when bond state changes (ie:pairing)
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        getActivity().registerReceiver(mBroadcastReceiver4, filter);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d(TAG, "ONSTART STARTING");

        btnSend = (Button) getView().findViewById(R.id.button_send);
        etSend = (EditText) getView().findViewById(R.id.edit_text);


        SharedPreferences id_sharedpreferences = this.getActivity().getSharedPreferences("id", Context.MODE_PRIVATE);
        uid = id_sharedpreferences.getString("uid", "");

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
        myRef = myRef.child(uid).child("control").child("controller");

        count = 0;

        leftButton = getView().findViewById(R.id.button_left);
        dleftButton = getView().findViewById(R.id.button_dleft);
        tleftButton = getView().findViewById(R.id.button_tleft);
        rightButton = getView().findViewById(R.id.button_right);
        drightButton = getView().findViewById(R.id.button_dright);
        trightButton = getView().findViewById(R.id.button_tright);
        upButton = getView().findViewById(R.id.button_up);
        dupButton = getView().findViewById(R.id.button_dup);
        tupButton = getView().findViewById(R.id.button_tup);
        downButton = getView().findViewById(R.id.button_down);
        ddownButton = getView().findViewById(R.id.button_ddown);
        tdownButton = getView().findViewById(R.id.button_tdown);
        clickButton = getView().findViewById(R.id.button_click);

        seekBar = getView().findViewById(R.id.seekBar);
        seekBar.setMax(10);

//        enableButton(false);
//        count = 0;

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.d("DEBUG345", seekBar.getProgress() + "");

                myRef = myRef.getParent().child("sound");
                myRef.setValue(seekBar.getProgress());


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // left
                myRef.setValue("@01" + count);
                Log.d(TAG, "left");
                count++;
            }
        });
        dleftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // double left
                myRef.setValue("@02" + count);
                Log.d(TAG, "dleft");
                count++;
            }
        });
        tleftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // triple left
                myRef.setValue("@03" + count);
                Log.d(TAG, "tleft");
                count++;
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // right
                myRef.setValue("@04" + count);
                Log.d(TAG, "right");
                count++;
            }
        });
        drightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // double right
                myRef.setValue("@05" + count);
                Log.d(TAG, "dright");
                count++;
            }
        });
        trightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // triple right
                myRef.setValue("@06" + count);
                Log.d(TAG, "tright");
                count++;
            }
        });

        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // up
                myRef.setValue("@07" + count);
                Log.d(TAG, "up");
                count++;
            }
        });
        dupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // double up
                myRef.setValue("@08" + count);
                Log.d(TAG, "dup");
                count++;
            }
        });
        tupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // triple up
                myRef.setValue("@09" + count);
                Log.d(TAG, "tup");
                count++;
            }
        });

        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // down
                myRef.setValue("@10" + count);
                Log.d(TAG, "down");
                count++;
            }
        });
        ddownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // double down
                myRef.setValue("@11" + count);
                Log.d(TAG, "ddown");
                count++;
            }
        });
        tdownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // triple down
                myRef.setValue("@12" + count);
                Log.d(TAG, "tdown");
                count++;
            }
        });

        clickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // click
                myRef.setValue("@13" + count);
                Log.d(TAG, "click");
                count++;
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRef = myRef.getParent().child("input");
                myRef.setValue(etSend.getText().toString());

                etSend.setText("");

                myRef = myRef.getParent().child("controller");
                myRef.setValue("@14" + count);
                count++;
            }
        });
//
//        endButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // triple right
//                byte[] bytes = "!14".getBytes(Charset.defaultCharset());
//                mBluetoothConnection.write(bytes);
//                mBluetoothConnection.stop();
//                enableButton(false);
//            }
//        });

//        sendButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                myRef = myRef.getParent().child("StringInput");
//                myRef.setValue(editText.getText().toString());
//
//                editText.setText("");
//
//                myRef = myRef.getParent().child("controller");
//                myRef.setValue("send" + count);
//                count++;
//            }
//        });

        mBackgroundImageView = (ImageView) getView().findViewById(R.id.activity_controller_background_image_view);

        if (mBackgroundBlurred) {
            Log.d(TAG, "Background is already blurred...");
        } else {
            blurBackground();
        }
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
