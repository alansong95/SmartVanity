package com.example.alan.smartvanity;

import android.app.Activity;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    public static final int REQUEST_PICK_APPWIDGET = 1;
    public static final int REQUEST_CREATE_APPWIDGET = 2;


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
    ArrayList<String> providerList;
    ArrayList<Integer> posListL;
    ArrayList<Integer> posListT;
    ArrayList<Integer> appWidgetIdList;
    ArrayList<String> savedInfoList;

    Button button;

    private static final String TAG = "MyActivity";

    int appWidgetId;

    String temp;

    Button syncButton;

    FirebaseDatabase database;

    String uid;

    Gson gson;
    int flag;


    SharedPreferences debug_sharedpreferences;

    public void initialize() {
        gson = new Gson();

        id_sharedpreferences = getSharedPreferences("id", Context.MODE_PRIVATE);
        sharedpreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        debug_sharedpreferences = getSharedPreferences("debug", Context.MODE_PRIVATE);

        flag = debug_sharedpreferences.getInt("flag", 0);

        widgetCount = sharedpreferences.getInt("WidgetCount", 0);

        providerList = new ArrayList<>();
        posListL = new ArrayList<>();
        posListT = new ArrayList<>();
        appWidgetIdList = new ArrayList<>();
        savedInfoList = new ArrayList<>();

        // get screen width and height
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        if(flag == 1) {
            Log.d("hello2", "dbug");
            Intent data = null;
            String dataString = debug_sharedpreferences.getString("data", null);
            try {
                data = Intent.getIntent(dataString);
            } catch (java.net.URISyntaxException e) {
                e.getMessage();
            }
            Log.d("hello3", intentToString(data) + "");

            createWidget(data);
        }

        int id = debug_sharedpreferences.getInt("id", 0);
        if (id != 0) {
            loadWidget(id);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initialize();

        mainLayout = (ViewGroup) findViewById(R.id.main_layout);
        button = findViewById(R.id.add_button);

        mAppWidgetHost = new AppWidgetHost(this, R.id.APPWIDGET_HOST_ID);
        mAppWidgetManager = AppWidgetManager.getInstance(this);

        infoList = mAppWidgetManager.getInstalledProviders();

        //selectIntent = new Intent(this, SelectWidget.class);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                selectWidget();
                //startActivity(selectIntent);
            }
        });
        //populateUI();
    }

    void selectWidget() {
        Log.d("selectWidget", "Started");
        int appWidgetId = this.mAppWidgetHost.allocateAppWidgetId();
        Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
        pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        addEmptyData(pickIntent);
        startActivityForResult(pickIntent, REQUEST_PICK_APPWIDGET);
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
//            if (myIntent.hasExtra("Position")) {
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
//                    position = myIntent.getExtras().getInt("Position");
//                    appWidgetIdList.add(myIntent.getExtras().getInt("WidgetId"));
//                    String infoString = myIntent.getStringExtra("info");
//                    savedInfoList.add(infoString);
//
//                    pos = findPosition(position);
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
    private void configureWidget(Intent data) {
        Log.d("configureWidget", "Started");
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
        if (appWidgetInfo.configure != null) {
            Log.d("configureWidget", "null");
            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.setComponent(appWidgetInfo.configure);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            startActivityForResult(intent, REQUEST_CREATE_APPWIDGET);
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

    public void createWidget(Intent data) {
        Log.d("createWidget", "started");
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);

        AppWidgetHostView hostView = mAppWidgetHost.createView(this, appWidgetId, appWidgetInfo);
        hostView.setAppWidget(appWidgetId, appWidgetInfo);
        mainLayout.addView(hostView);

        debug_sharedpreferences.edit().putString("info", gson.toJson(appWidgetInfo)).commit();
        debug_sharedpreferences.edit().putInt("flag", 1);
        debug_sharedpreferences.edit().putInt("id", appWidgetId);

        Log.d("hello4", gson.toJson(appWidgetInfo) + "");

        Toast.makeText(MainActivity.this, appWidgetId + "", Toast.LENGTH_SHORT).show();

        Log.i(TAG, "The widget size is: " + appWidgetInfo.minWidth + "*" + appWidgetInfo.minHeight);
        Log.d("createWidget", "ended");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult", "Started");
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_PICK_APPWIDGET) {
                Log.d("onActivityResult", "REQUEST_PICK_APPWIDGET");
                configureWidget(data);
            } else if (requestCode == REQUEST_CREATE_APPWIDGET) {
                Log.d("onActivityResult", "REQUEST_CREATE_APPWIDGET");
                createWidget(data);
                Log.d("onActivityResult", "REQUEST_CREATE_APPWIDGET END");
            }
        } else if (resultCode == RESULT_CANCELED && data != null) {
            Log.d("onActivityResult", "RESULT_CANCELED");
            int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            if (appWidgetId != -1) {
                mAppWidgetHost.deleteAppWidgetId(appWidgetId);
            }
        }
    }

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

    private int[] findPosition(int position) {
        int[] pos = new int[2];
        int left;
        int top;
        left = (position % 6) * width / 6;
        top = position * height / 6 / 6;

        pos[0] = left;
        pos[1] = top;
        //Toast.makeText(this, width + " " + height + " " + pos[0] +" " +  pos[1], Toast.LENGTH_SHORT).show();

        return pos;
    }

    private void saveData() {
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.clear().commit();
        editor.putInt("WidgetCount", widgetCount);

        Log.i(TAG, "WidgetCount: " + widgetCount);

        for (int i = 0; i < widgetCount; i++) {
            String key_selected = "selected" + i;
            String key_positionL = "positionL" + i;
            String key_positionT = "positionT" + i;
            String key_id = "id" + i;
            String key_info = "info" + i;

            editor.putString(key_selected, providerList.get(i));
            editor.putInt(key_positionL, posListL.get(i));
            editor.putInt(key_positionT, posListT.get(i));
            editor.putInt(key_id, appWidgetIdList.get(i));
            editor.putString(key_info, savedInfoList.get(i));
        }
        editor.commit();
    }

    private void loadData() {
        if (sharedpreferences.contains("selected0")) {
            for (int i = 0; i < widgetCount; i++) {
                providerList.add(sharedpreferences.getString("selected" + i, null));
                posListL.add(sharedpreferences.getInt("positionL" + i, -1));
                posListT.add(sharedpreferences.getInt("positionT" + i, -1));
                appWidgetIdList.add(sharedpreferences.getInt("id" + i, -1));
                savedInfoList.add(sharedpreferences.getString("info" + i, null));
            }
        }
    }


    private void installWidget() {
        AppWidgetProviderInfo appWidgetInfo = null;
        for (int j = 0; j < widgetCount; j++) {
            for (int i = 0; i < infoList.size(); i++) {
                temp = infoList.get(i).provider.toString();

                if (providerList.get(j).equals(temp)) {
                    appWidgetInfo = infoList.get(i);
                    break;
                }
            }
            String x = gson.toJson(appWidgetInfo);

            AppWidgetProviderInfo info = gson.fromJson(savedInfoList.get(j), AppWidgetProviderInfo.class);
            Log.d("rightnow3", ""+ x.equals(savedInfoList.get(j)));
            Log.d("rightnow3", ""+ x);
            Log.d("rightnow3", ""+ savedInfoList.get(j));

//            AppWidgetHostView hostView = new AppWidgetHostView(context);
//            hostView = mAppWidgetHost.createView(this, appWidgetIdList.get(j), appWidgetInfo);
//            hostView.setAppWidget(appWidgetIdList.get(j), appWidgetInfo);

            AppWidgetHostView hostView = new AppWidgetHostView(context);
            hostView = mAppWidgetHost.createView(this, appWidgetIdList.get(j), appWidgetInfo);
            hostView.setAppWidget(appWidgetIdList.get(j), appWidgetInfo);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = posListL.get(j);
            params.topMargin = posListT.get(j);
            hostView.setOnLongClickListener(new myListener(temp) {});

            mainLayout.addView(hostView, j, params);
        }
    }

    public void printList() {
        for (int i = 0; i < widgetCount; i++) {
            Log.i(TAG, "name: " + providerList.get(i));
            Log.i(TAG, "id: " + appWidgetIdList.get(i));
        }

    }

    class myListener implements View.OnLongClickListener {
        String selected;

        public myListener(String selected) {
            this.selected = selected;
        }

        //delete widget
        @Override
        public boolean onLongClick(View view) {
            for (int i = 0; i < providerList.size(); i++) {
                String temp2 = providerList.get(i);
                printList();
                Log.d("rightnow2", ""+providerList.get(i));
                Log.d("rightnow2", ""+selected);
                if (temp2.equals(selected)) {
                    Log.d("rightnow2", "matched");
                    view.setVisibility(View.GONE);

                    providerList.remove(i);
                    posListL.remove(i);
                    posListT.remove(i);
                    appWidgetIdList.remove(i);
                    savedInfoList.remove(i);
                    widgetCount--;

                    break;
                }
            }
            Toast.makeText(MainActivity.this, "hello", Toast.LENGTH_SHORT).show();
            saveData();
            finish();
            startActivity(getIntent());
            return false;
        }
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