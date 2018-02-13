package com.example.alan.smartvanity;

import android.app.Activity;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Alan on 2/5/2018.
 */

public class Widget extends Activity {
    Intent myIntent;

    TextView text;

    int appWidgetId;
    AppWidgetProviderInfo appWidgetInfo;

    AppWidgetManager mAppWidgetManager;

    AppWidgetHost mAppWidgetHost;

    ViewGroup mainlayout;

    int position;

    int[] pos;

    int height, width;

    List<AppWidgetProviderInfo> infoList;

    @SuppressWarnings("deprecated")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.widget);

        text = (TextView) findViewById(R.id.text);

        // get screen width and height
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        mainlayout = (ViewGroup) findViewById(R.id.main_layout);

        mAppWidgetHost = new AppWidgetHost(this, R.id.APPWIDGET_HOST_ID);

        myIntent = getIntent();
        mAppWidgetManager = AppWidgetManager.getInstance(this);
        AppWidgetProviderInfo appWidgetInfo = (AppWidgetProviderInfo) myIntent.getParcelableExtra("WidgetInfo");
        position = myIntent.getExtras().getInt("Position");
        int appWidgetId = myIntent.getExtras().getInt("WidgetId");

        pos = findPosition(position);

        infoList = mAppWidgetManager.getInstalledProviders();

        // example setting email accounts
//        if (appWidgetInfo.configure != null) {
//            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
//            intent.setComponent(appWidgetInfo.configure);
//            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
//            startActivityForResult(intent, R.id.REQUEST_CREATE_APPWIDGET);
//        } else {
            AppWidgetHostView hostView = mAppWidgetHost.createView(this, 0, appWidgetInfo);
            hostView.setAppWidget(0, appWidgetInfo);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = pos[0];
            params.topMargin = pos[1];

            mainlayout.addView(hostView, params);
        //}


    }

//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == RESULT_OK) {
//            /*if (requestCode == R.id.REQUEST_CREATE_APPWIDGET) {
//                AppWidgetHostView hostView = mAppWidgetHost.createView(this, appWidgetId, appWidgetInfo);
//                hostView.setAppWidget(appWidgetId, appWidgetInfo);
//                mainlayout.addView(hostView);
//            }
//            */
//            text.setText("DEBUG");
//        } else if (resultCode == RESULT_CANCELED && data != null) {
//            int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
//            if (appWidgetId != -1) {
//                mAppWidgetHost.deleteAppWidgetId(appWidgetId);
//            }
//        }
//    }

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

}
