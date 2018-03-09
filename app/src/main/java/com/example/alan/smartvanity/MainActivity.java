package com.example.alan.smartvanity;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    Intent selectIntent;
    Intent myIntent;

    Context context = MainActivity.this;

    String selected;
    int position;

    int height, width;

    ViewGroup mainLayout;

    AppWidgetManager mAppWidgetManager;
    AppWidgetHost mAppWidgetHost;

    int[] pos;

    List<AppWidgetProviderInfo> infoList;


    SharedPreferences sharedpreferences;
    SharedPreferences id_sharedpreferences;


    int widgetCount;

    ArrayList<String> savedInfoList;

    ArrayList<String> providerList;
    ArrayList<Integer> posListL;
    ArrayList<Integer> posListT;
    ArrayList<Integer> appWidgetIdList;
    ArrayList<String> dataList;

    private static final String TAG = "MyActivity";

    int appWidgetId;

    FirebaseDatabase database;

    String uid;

    Gson gson;
    int flag;

    RelativeLayout.LayoutParams params;


    AppWidgetProviderInfo newInfo;
    int newAppWidgetId;

    public void initialize() {
        gson = new Gson();

        id_sharedpreferences = getSharedPreferences("id", Context.MODE_PRIVATE);
        sharedpreferences = getSharedPreferences("data", Context.MODE_PRIVATE);

        widgetCount = sharedpreferences.getInt("WidgetCount", 0);

        providerList = new ArrayList<>();
        posListL = new ArrayList<>();
        posListT = new ArrayList<>();
        appWidgetIdList = new ArrayList<>();
        savedInfoList = new ArrayList<>();
        dataList = new ArrayList<>();

        // get screen width and height
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        mAppWidgetHost = new AppWidgetHost(this, R.id.APPWIDGET_HOST_ID);
        mAppWidgetManager = AppWidgetManager.getInstance(this);

        infoList = mAppWidgetManager.getInstalledProviders();

        mainLayout = (ViewGroup) findViewById(R.id.main_layout);
        mainLayout.setBackgroundColor(Color.BLACK);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();


        Button addButton = findViewById(R.id.add_button);
        Button deleteButton = findViewById(R.id.delete_button);
        registerForContextMenu(deleteButton);
        //selectIntent = new Intent(this, SelectWidget.class);

        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                selectWidget();
                //startActivity(selectIntent);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppWidgetProviderInfo info;
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
                //builderSingle.setIcon(R.drawable.ic_launcher);
                builderSingle.setTitle("Choose widget");

                final ListAdapter listAdapter = new ListAdapter(MainActivity.this, appWidgetIdList);
                builderSingle.setAdapter(listAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteWidget(listAdapter.getIndex(which));
                    }
                });
                builderSingle.show();
            }
        });

        Button syncButton = (Button) findViewById(R.id.sync_button);
        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncData();
            }
        });

        //populateUI();
        handleRestoreWidgets();
    }

    @Override
    protected void onResume() {
        super.onResume();

//        if (widgetCount > 0) {
//            loadData();
//            installWidget();
//        }
//        int flag = 0;
//
//        String temp2;
//        int newIntent = sharedpreferences.getInt("NewIntent", 0);
//
//        if (newIntent == 1) {
//            myIntent = getIntent();
//            if (myIntent.hasExtra("position")) {
//                for (int i = 0; i < widgetCount; i++) {
//                    temp2 = myIntent.getStringExtra("Selected");
//                    if (temp2.equals(providerList.get(i))) {
//                        flag = 1;
//                        break;
//                    }
//                }
//                if (flag == 1) {
//
//                } else { // if widget is not in the saved list (first time added)
//                    String selected = myIntent.getStringExtra("Selected");
//                    providerList.add(myIntent.getStringExtra("Selected"));
//                    position = myIntent.getExtras().getInt("position");
//                    appWidgetIdList.add(myIntent.getExtras().getInt("WidgetId"));
//                    String infoString = myIntent.getStringExtra("info");
//                    savedInfoList.add(infoString);
//
//                    pos = getPosition(position);
//                    posListL.add(pos[0]);
//                    posListT.add(pos[1]);
//                    widgetCount++;
//
//                    SharedPreferences.Editor editor = sharedpreferences.edit();
//                    editor.putInt("WidgetCount", widgetCount);
//                    editor.putInt("NewIntent", 0);
//                    editor.commit();
//
//                    //AppWidgetProviderInfo appWidgetInfo = gson.fromJson(infoString, AppWidgetProviderInfo.class);
//
//
//                    AppWidgetProviderInfo appWidgetInfo = null;
//
//                    for (int i = 0; i < infoList.size(); i++) {
//                        temp = infoList.get(i).provider.toString();
//
//                        if (selected.equals(temp)) {
//                            appWidgetInfo = infoList.get(i);
//                            break;
//                        }
//                    }
//
//
//                    if (appWidgetInfo.configure != null) {
//                        Log.d("rightnow5", appWidgetInfo + "");
//                        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
//                        intent.setComponent(appWidgetInfo.configure);
//                        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
//                        startActivityForResult(intent, 0000000000000001);
//                    }
//
//                    saveData();
//
//
//                    //AppWidgetProviderInfo tempInfo = mAppWidgetManager.getAppWidgetInfo(myIntent.getExtras().getInt("WidgetId"));
//
////                    if (tempInfo.configure != null) {
////                        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
////                        intent.setComponent(tempInfo.configure);
////                        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
////                        startActivityForResult(intent, R.id.REQUEST_CREATE_APPWIDGET);
////                    }
//                }
//
//                printList();
//            }
//        }
//        if (widgetCount > 0) {
//            loadData();
//            installWidget();
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult", "Started");
        if (resultCode == RESULT_OK) {
            if (requestCode == R.integer.REQUEST_PICK_APPWIDGET) {
                //getPosition();
                Log.d("pika", "2");
                configureWidget(data);
            } else if (requestCode == R.integer.REQUEST_CREATE_APPWIDGET) {
                Log.d("onActivityResult", "REQUEST_CREATE_APPWIDGET");
                Log.d("pika", "3");
                createWidget(data);
                Log.d("onActivityResult", "REQUEST_CREATE_APPWIDGET END");
            } else if (requestCode == R.integer.REQUEST_PICK_GRID) {
                Log.d("pika", "1");
                setPosition(data);
            }
        } else if (resultCode == RESULT_CANCELED && data != null) {
            Log.d("onActivityResult", "RESULT_CANCELED");
        }
    }

    void selectWidget() {
        Log.d("selectWidget", "Started");
        int appWidgetId = this.mAppWidgetHost.allocateAppWidgetId();
        Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
        pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        addEmptyData(pickIntent);
        startActivityForResult(pickIntent, R.integer.REQUEST_PICK_APPWIDGET);
        Log.d("selectWidget", "Ended");
    }

    void addEmptyData(Intent pickIntent) {
        Log.d("addEmptyData", "Started");
        ArrayList<AppWidgetProviderInfo> customInfo = new ArrayList<AppWidgetProviderInfo>();
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_INFO, customInfo);
        ArrayList<Bundle> customExtras = new ArrayList<Bundle>();
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_EXTRAS, customExtras);
        Log.d("addEmptyData", "Ended");
    }


    private void configureWidget(Intent data) {
        Log.d("configureWidget", "Started");
        Bundle extras = data.getExtras();
        newAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        newInfo = mAppWidgetManager.getAppWidgetInfo(newAppWidgetId);
        if (newInfo.configure != null) {
            Log.d("configureWidget", "null");
            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.setComponent(newInfo.configure);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, newAppWidgetId);
            startActivityForResult(intent, R.integer.REQUEST_CREATE_APPWIDGET);
            Log.d("configureWidget", "null ended");
        } else {
            Log.d("configureWidget", "else started");
            createWidget(data);
            Log.d("configureWidget", "else ended");
        }
        Log.d("configureWidget", "Ended");
    }

    public void loadWidget(int appWidgetId) {
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);

        AppWidgetHostView hostView = mAppWidgetHost.createView(this, appWidgetId, appWidgetInfo);
        hostView.setAppWidget(appWidgetId, appWidgetInfo);
        mainLayout.addView(hostView);
    }

    // pika
    private void handleAddNewAppWidget(int newAppWidgetId, AppWidgetProviderInfo newInfo) {
        AppWidgetHostView hostView = mAppWidgetHost.createView(context.getApplicationContext(), newAppWidgetId, newInfo);
        hostView.setAppWidget(newAppWidgetId, newInfo);

        hostView.setId(newAppWidgetId);
        mainLayout.addView(hostView, widgetCount, params);

        widgetCount++;

        addDataToList(newAppWidgetId, newInfo);
        saveData();
    }

    public void addDataToList(int newAppWidgetId, AppWidgetProviderInfo newInfo) {
        String infoString = gson.toJson(newInfo);
        WidgetHolder holder = new WidgetHolder( newAppWidgetId, newInfo.minWidth, newInfo.minHeight);
        String serialized = holder.serialize();

        dataList.add(serialized);
        appWidgetIdList.add(newAppWidgetId);
    }

    public void saveData() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear().commit();
        editor.putInt("WidgetCount", widgetCount);
        Log.i(TAG, "WidgetCount: " + widgetCount);

        for (int i = 0; i < widgetCount; i++) {
            String key_data = "data" + i;
            String key_id = "id" + i;
            String key_positionL = "positionL" + i;
            String key_positionT = "positionT" + i;

            editor.putString(key_data, dataList.get(i));
            editor.putInt(key_id, appWidgetIdList.get(i));
            editor.putInt(key_positionL, posListL.get(i));
            editor.putInt(key_positionT, posListT.get(i));
        }
        editor.commit();
    }

    public void refresh() {
        mainLayout.removeAllViews();
        putWidget();
    }

    public void putWidget() {
        int appWidgetId;
        String data;
        WidgetHolder holder;
        AppWidgetProviderInfo info;
        RelativeLayout.LayoutParams params;
        AppWidgetHostView hostView;

        for (int i = 0; i < widgetCount; i++) {
            appWidgetId = appWidgetIdList.get(i);
            Log.d("DEBUG123", appWidgetId + "");
            //data = dataList.get(i);
            //holder = WidgetHolder.deserialize(data);
            info = AppWidgetManager.getInstance(this.getApplicationContext()).getAppWidgetInfo(appWidgetId);

            hostView = mAppWidgetHost.createView(context.getApplicationContext(), appWidgetId, info);
            hostView.setAppWidget(appWidgetId, info);

            params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = posListL.get(i);
            params.topMargin = posListT.get(i);

            hostView.setId(appWidgetId);

            mainLayout.addView(hostView, i, params);
        }
    }

    private void handleRestoreWidgets() {
        loadData();
        putWidget();
    }

    public void createWidget(Intent data) {
        Log.d("createWidget", "started");
        Bundle extras = data.getExtras();
        newInfo = mAppWidgetManager.getAppWidgetInfo(newAppWidgetId);
        getPosition();
    }

    public void getPosition() {
        Log.d("pika", "0");
        Intent gridIntent = new Intent(this, Grid.class);
        startActivityForResult(gridIntent, R.integer.REQUEST_PICK_GRID);
    }

    public void syncData() {
        AppWidgetProviderInfo info;
        database = FirebaseDatabase.getInstance();
        uid = id_sharedpreferences.getString("uid", "");

        DatabaseReference myRef = database.getReference("users");

        myRef= myRef.child(uid).child("widgets");
        myRef.setValue(null);

        myRef= myRef.child("widget_count").child("val");
        myRef.setValue(Integer.toString(widgetCount));

        for (int i = 0; i < widgetCount; i++) {
            info = AppWidgetManager.getInstance(context.getApplicationContext()).getAppWidgetInfo(appWidgetIdList.get(i));

            myRef = myRef.getParent().getParent().child("positionL").child("val" + i);
            myRef.setValue(posListL.get(i));

            myRef = myRef.getParent().getParent().child("positionT").child("val" + i);
            myRef.setValue(posListT.get(i));

            myRef = myRef.getParent().getParent().child("provider").child("val" + i);
            myRef.setValue(info.provider.toString());

            myRef = myRef.getParent().getParent().child("id").child("val" + i);
            myRef.setValue(appWidgetIdList.get(i));
        }
    }

    // pika
//    private void populateUI() {
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayShowCustomEnabled(true);
//        actionBar.setTitle("Your Mirror");
//        actionBar.setDisplayShowTitleEnabled(true);
//        syncButton = (Button) findViewById(R.id.sync_button);
//
//        database = FirebaseDatabase.getInstance();
//
//        syncButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                uid = id_sharedpreferences.getString("uid", "");
//                Log.d("uid", "debug " + uid);
//                DatabaseReference myRef = database.getReference("users");
//
//                myRef= myRef.child(uid).child("widgets");
//                myRef.setValue(null);
//
//                myRef= myRef.child("widget count").child("val");
//                myRef.setValue(Integer.toString(widgetCount));
//
//                for (int i =0; i < widgetCount; i++) {
//                    myRef = myRef.getParent().getParent().child("selected").child("val" + i);
//                    myRef.setValue(providerList.get(i));
//
//                    myRef = myRef.getParent().getParent().child("positionL").child("val" + i);
//                    myRef.setValue(posListL.get(i));
//
//                    myRef = myRef.getParent().getParent().child("positionT").child("val" + i);
//                    myRef.setValue(posListT.get(i));
//
//                    myRef = myRef.getParent().getParent().child("info").child("val" + i);
//                    myRef.setValue(savedInfoList.get(i));
//                }
//                myRef= myRef.getParent().getParent().child("updated").child("val");
//                myRef.setValue(true);
//
//            }
//        });
//    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "mainactivity on pause");
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAppWidgetHost.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAppWidgetHost.stopListening();
        Log.i(TAG, "mainactivity on stop");
    }

    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "mainactivity on destroy");
    }

    private void setPosition(Intent data) {
        int position = data.getExtras().getInt("position");

        int left;
        int top;
        left = (position % 6) * width / 6;
        top = position * height / 6 / 6;

        posListL.add(left);
        posListT.add(top);

        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.leftMargin = left;
        params.topMargin = top;

        handleAddNewAppWidget(newAppWidgetId, newInfo);
    }



    public void loadData() {
        if (sharedpreferences.contains("data0")) {
            for (int i = 0; i < widgetCount; i++) {
                //providerList.add(sharedpreferences.getString("selected" + i, null));
                dataList.add(sharedpreferences.getString("data" + i, ""));
                appWidgetIdList.add(sharedpreferences.getInt("id" + i, -1));
                posListL.add(sharedpreferences.getInt("positionL" + i, -1));
                posListT.add(sharedpreferences.getInt("positionT" + i, -1));

                //savedInfoList.add(sharedpreferences.getString("info" + i, null));
            }
        }
    }


//    private void installWidget() {
//        AppWidgetProviderInfo appWidgetInfo = null;
//        for (int j = 0; j < widgetCount; j++) {
//            for (int i = 0; i < infoList.size(); i++) {
//                //temp = infoList.get(i).provider.toString();
//
//                if (providerList.get(j).equals(temp)) {
//                    appWidgetInfo = infoList.get(i);
//                    break;
//                }
//            }
//            String x = gson.toJson(appWidgetInfo);
//
//            AppWidgetProviderInfo info = gson.fromJson(savedInfoList.get(j), AppWidgetProviderInfo.class);
//            Log.d("rightnow3", ""+ x.equals(savedInfoList.get(j)));
//            Log.d("rightnow3", ""+ x);
//            Log.d("rightnow3", ""+ savedInfoList.get(j));
//
////            AppWidgetHostView hostView = new AppWidgetHostView(context);
////            hostView = mAppWidgetHost.eView(this, appWidgetIdList.get(j), appWidgetInfo);
////            hostView.setAppWidget(appWidgetIdList.get(j), appWidgetInfo);
//
//            AppWidgetHostView hostView = new AppWidgetHostView(context);
//            hostView = mAppWidgetHost.createView(this, appWidgetIdList.get(j), appWidgetInfo);
//            hostView.setAppWidget(appWidgetIdList.get(j), appWidgetInfo);
//
//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
//            params.leftMargin = posListL.get(j);
//            params.topMargin = posListT.get(j);
//            hostView.setOnLongClickListener(new myListener(temp) {});
//
//            mainLayout.addView(hostView, j, params);
//        }
//    }

    public void printList() {
        for (int i = 0; i < widgetCount; i++) {
            Log.i(TAG, "name: " + providerList.get(i));
            Log.i(TAG, "id: " + appWidgetIdList.get(i));
        }

    }

    public void deleteWidget(int index) {
        Log.d("rightnow2", "matched");
        mainLayout.getChildAt(index).setVisibility(View.GONE);

        posListL.remove(index);
        posListT.remove(index);
        appWidgetIdList.remove(index);
        dataList.remove(index);
        widgetCount--;


        Toast.makeText(MainActivity.this, "hello", Toast.LENGTH_SHORT).show();
        saveData();
        refresh();
    }

    public void printChildViews() {

    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public static String intentToString(Intent intent) {
        if (intent == null)
            return "";

        StringBuilder stringBuilder = new StringBuilder("action: ")
                .append(intent.getAction())
                .append(" data: ")
                .append(intent.getDataString())
                .append(" extras: ")
                ;
        for (String key : intent.getExtras().keySet())
            stringBuilder.append(key).append("=").append(intent.getExtras().get(key)).append(" ");

        return stringBuilder.toString();

    }
}