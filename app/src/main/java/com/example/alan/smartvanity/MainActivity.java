package com.example.alan.smartvanity;

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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
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
    AppWidgetProviderInfo appWidgetInfo;

    SharedPreferences sharedpreferences;
    SharedPreferences id_sharedpreferences;

    int widgetCount;
    ArrayList<String> providerList;
    ArrayList<Integer> posListL;
    ArrayList<Integer> posListT;
    ArrayList<Integer> appWidgetIdList;

    Button button;

    private static final String TAG = "MyActivity";

    int appWidgetId;

    String temp;

    Button syncButton;

    FirebaseDatabase database;

    Intent login_intent;

    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        id_sharedpreferences = getSharedPreferences("id", Context.MODE_PRIVATE);
        sharedpreferences = getSharedPreferences("data", Context.MODE_PRIVATE);

        widgetCount = sharedpreferences.getInt("WidgetCount", 0);

        providerList = new ArrayList<>();
        posListL = new ArrayList<>();
        posListT = new ArrayList<>();
        appWidgetIdList = new ArrayList<>();

        // get screen width and height
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        login_intent = getIntent();


        mainLayout = (ViewGroup) findViewById(R.id.main_layout);
        button = findViewById(R.id.add_button);

        mAppWidgetHost = new AppWidgetHost(this, R.id.APPWIDGET_HOST_ID);
        mAppWidgetManager = AppWidgetManager.getInstance(this);

        selectIntent = new Intent(this, SelectWidget.class);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(selectIntent);
            }
        });


        populateUI();

    }

    private void populateUI() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("Your Mirror");
        actionBar.setDisplayShowTitleEnabled(true);
        syncButton = (Button) findViewById(R.id.sync_button);

        database = FirebaseDatabase.getInstance();

        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uid = id_sharedpreferences.getString("uid", "");
                Log.d("uid", "debug " + uid);
                DatabaseReference myRef = database.getReference("users");

                myRef= myRef.child(uid).child("widgets");
                myRef.setValue(null);

                myRef= myRef.child("widget count").child("val");
                myRef.setValue(Integer.toString(widgetCount));

                for (int i =0; i < widgetCount; i++) {
                    myRef = myRef.getParent().getParent().child("selected").child("val" + i);
                    myRef.setValue(providerList.get(i));

                    myRef = myRef.getParent().getParent().child("positionL").child("val" + i);
                    myRef.setValue(posListL.get(i));

                    myRef = myRef.getParent().getParent().child("positionT").child("val" + i);
                    myRef.setValue(posListT.get(i));
                }
                myRef= myRef.getParent().getParent().child("updated").child("val");
                myRef.setValue(true);

            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (widgetCount > 0) {
            loadData();
            installWidget();
        }

        int flag = 0;
        String temp2;

        int newIntent = sharedpreferences.getInt("NewIntent", 0);

        if (newIntent == 1) {
            myIntent = getIntent();

            if (myIntent.hasExtra("Position")) {
                for (int i = 0; i < widgetCount; i++) {
                    temp2 = myIntent.getStringExtra("Selected");
                    if (temp2.equals(providerList.get(i))) {
                        flag = 1;
                        break;
                    }
                }
                if (flag == 1) {

                } else {
                    providerList.add(myIntent.getStringExtra("Selected"));
                    position = myIntent.getExtras().getInt("Position");
                    appWidgetIdList.add(myIntent.getExtras().getInt("WidgetId"));
                    pos = findPosition(position);
                    posListL.add(pos[0]);
                    posListT.add(pos[1]);
                    widgetCount++;

                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putInt("WidgetCount", widgetCount);
                    editor.putInt("NewIntent", 0);
                    editor.commit();
                    saveData();
                }
                printList();
            }
        }


        if (widgetCount > 0) {
            loadData();
            installWidget();
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "mainactivity on pause");
    }

    @Override
    protected void onStop() {
        super.onStop();
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

            editor.putString(key_selected, providerList.get(i));
            editor.putInt(key_positionL, posListL.get(i));
            editor.putInt(key_positionT, posListT.get(i));
            editor.putInt(key_id, appWidgetIdList.get(i));
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
            }
        }
    }

    private void installWidget() {
        infoList = mAppWidgetManager.getInstalledProviders();

        for (int j = 0; j < widgetCount; j++) {
            for (int i = 0; i < infoList.size(); i++) {
                temp = infoList.get(i).provider.toString();

                if (providerList.get(j).equals(temp)) {
                    appWidgetInfo = infoList.get(i);
                    break;
                }
            }

            AppWidgetHostView hostView = new AppWidgetHostView(context);
            hostView = mAppWidgetHost.createView(context, appWidgetIdList.get(j), appWidgetInfo);
            hostView.setAppWidget(appWidgetIdList.get(j), appWidgetInfo);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = posListL.get(j);
            params.topMargin = posListT.get(j);
            hostView.setId(appWidgetIdList.get(j));
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
                if (temp2.equals(selected)) {
                    view.setVisibility(View.GONE);

                    providerList.remove(i);
                    posListL.remove(i);
                    posListT.remove(i);
                    appWidgetIdList.remove(i);
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
}