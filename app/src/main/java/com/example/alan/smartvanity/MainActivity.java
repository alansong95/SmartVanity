package com.example.alan.smartvanity;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Intent selectIntent;
    Intent myIntent;

    String selected;
    int position;

    TextView textView;

    int height, width;

    ViewGroup mainLayout;

    AppWidgetManager mAppWidgetManager;
    AppWidgetHost mAppWidgetHost;

    int[] pos;

    List<AppWidgetProviderInfo> infoList;
    AppWidgetProviderInfo appWidgetInfo;

    SharedPreferences sharedpreferences;

    int widgetCount;
    ArrayList<String> providerList;
    ArrayList<Integer> posListL;
    ArrayList<Integer> posListT;

    Button button;

    private static final String TAG = "MyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedpreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        widgetCount = sharedpreferences.getInt("WidgetCount", 0);

        providerList = new ArrayList<>();
        posListL = new ArrayList<>();
        posListT = new ArrayList<>();

        // get screen width and height
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        mainLayout = (ViewGroup) findViewById(R.id.main_layout);
        textView = (TextView) findViewById(R.id.text_view);
        button = findViewById(R.id.add_button);

        mAppWidgetHost = new AppWidgetHost(this, R.id.APPWIDGET_HOST_ID);
        mAppWidgetManager = AppWidgetManager.getInstance(this);

        selectIntent = new Intent(this, SelectWidget.class);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(selectIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        myIntent = getIntent();
        if (myIntent.hasExtra("Position")) {
            selected = myIntent.getStringExtra("Selected");
            position = myIntent.getExtras().getInt("Position");
            //textView.setText("Selected: " + selected + "\nPosition: " + position + "\n");

            pos = findPosition(position);

            saveData(selected, pos);
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
        Toast.makeText(this, width + " " + height + " " + pos[0] +" " +  pos[1], Toast.LENGTH_SHORT).show();

        return pos;
    }

    private void saveData(String selected, int[] pos) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        String key_selected = "selected" + widgetCount;
        String key_positionL = "positionL" + widgetCount;
        String key_positionT = "positionT" + widgetCount;

        editor.putString(key_selected, selected);
        editor.putInt(key_positionL, pos[0]);
        editor.putInt(key_positionT, pos[1]);

        widgetCount++;
        editor.putInt("WidgetCount", widgetCount);
        editor.commit();
    }

    private void loadData() {
        if (sharedpreferences.contains("selected0")) {
            for (int i = 0; i < widgetCount; i++) {
                providerList.add(sharedpreferences.getString("selected" + i, null));
                posListL.add(sharedpreferences.getInt("positionL" + i, -1));
                posListT.add(sharedpreferences.getInt("positionT" + i, -1));
            }
        }
    }

    private void installWidget() {
        infoList = mAppWidgetManager.getInstalledProviders();

        for (int j = 0; j < providerList.size(); j++) {
            for (int i = 0; i < infoList.size(); i++) {
                String temp = infoList.get(i).provider.toString();

                if (providerList.get(j).equals(temp)) {
                    appWidgetInfo = infoList.get(i);
                }
            }

            AppWidgetHostView hostView = mAppWidgetHost.createView(this, 0, appWidgetInfo);
            hostView.setAppWidget(0, appWidgetInfo);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = posListL.get(j);
            params.topMargin = posListT.get(j);

            mainLayout.addView(hostView, params);
        }


    }
}