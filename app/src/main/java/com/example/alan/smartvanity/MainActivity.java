package com.example.alan.smartvanity;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get screen width and height
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        mainLayout = (ViewGroup) findViewById(R.id.main_layout);
        textView = (TextView) findViewById(R.id.text_view);
        final Button button = findViewById(R.id.add_button);

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
            textView.setText("Selected: " + selected + "\nPosition: " + position + "\n");

            pos = findPosition(position);

            infoList = mAppWidgetManager.getInstalledProviders();

            for (int i = 0; i < infoList.size(); i++) {
                String temp = infoList.get(i).provider.toString();

                if (selected.equals(temp)) {
                    appWidgetInfo = infoList.get(i);
                }
            }

            AppWidgetHostView hostView = mAppWidgetHost.createView(this, 0, appWidgetInfo);
            hostView.setAppWidget(0, appWidgetInfo);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = pos[0];
            params.topMargin = pos[1];

            mainLayout.addView(hostView, params);
        }
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
}