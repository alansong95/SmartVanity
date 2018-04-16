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

    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

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

    ImageView mBackgroundImageView;
    Boolean mBackgroundBlurred = false;

    /**
     * Broadcast Receiver for listing devices that are not yet paired
     * -Executed by btnDiscover() method.
     */
    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);

                Log.d("DEBUG123", "name: " + device.getName());
                Log.d("DEBUG123", "address: " + device.getAddress());

                try {
                    if (device.getAddress().equals("1C:4D:70:A3:30:62")) {
                        mBluetoothAdapter.cancelDiscovery();

                        device.createBond();

                        mBTDevice = device;
                        mBluetoothConnection = new BluetoothConnectionService(getActivity());

                        startConnection();

//                        enableButton(true);

                        mProgressDialog.dismiss();
                    }
                } catch (Exception e) {
                    Log.d("DEBUG123", "broadcast3 error");
                }
            }
        }
    };

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
        mbtnDiscover = (Button) getView().findViewById(R.id.btnFindUnpairedDevices);


        mbtnDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnDiscover();
            }
        });


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
        endButton = getView().findViewById(R.id.button_end);

//        enableButton(false);
//        count = 0;

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

        try {
            Log.d(TAG, "Bond State: " + mBTDevice.getBondState());
        } catch (Exception e) {

        }


        if (mBTDevice != null && mBTDevice.getBondState() == BluetoothDevice.BOND_BONDED){
//            enableButton(true);
        }
    }

    //create method for starting connection
//***remember the conncction will fail and app will crash if you haven't paired first
    public void startConnection(){
        startBTConnection(mBTDevice,MY_UUID_INSECURE);
    }

    /**
     * starting chat service method
     */
    public void startBTConnection(BluetoothDevice device, UUID uuid){
        Log.d(TAG, "startBTConnection: Initializing RFCOM Bluetooth Connection.");

        mBluetoothConnection.startClient(device,uuid);
    }

    public void btnDiscover() {
        Log.d(TAG, "btnDiscover: Looking for unpaired devices.");

        mProgressDialog = ProgressDialog.show(getContext(),"Connecting Bluetooth"
                ,"Please Wait...",true);

//        mBluetoothAdapter.enable();

        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "btnDiscover: Canceling discovery.");

            //check BT permissions in manifest
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            getActivity().registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }

        if(!mBluetoothAdapter.isDiscovering()){

            //check BT permissions in manifest
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            getActivity().registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
    }

    @Override
    public void onDestroy() {
        Log.d("DEBUG123", "onDestroy: called.");
        super.onDestroy();

        try {
            getActivity().unregisterReceiver(mBroadcastReceiver3);
        } catch (IllegalArgumentException e) {

        }

        try {
            getActivity().unregisterReceiver(mBroadcastReceiver4);
        } catch (IllegalArgumentException e) {

        }


    }


    /**
     * This method is required for all devices running API23+
     * Android must programmatically check the permissions for bluetooth. Putting the proper permissions
     * in the manifest is not enough.
     *
     * NOTE: This will only execute on versions > LOLLIPOP because it is not needed otherwise.
     */
    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = getActivity().checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += getActivity().checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
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
