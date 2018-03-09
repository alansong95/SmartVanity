package com.example.alan.smartvanity;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by Alan on 3/8/2018.
 */

public class deleteWidget extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_widget);

        ArrayList<Integer> appWidgetIdList = new ArrayList<>();

        Intent mainIntent = getIntent();

        int widgetCount = mainIntent.getIntExtra("widgetCount", 0 );
        String idListString = mainIntent.getStringExtra("idList");
        appWidgetIdList = new Gson().fromJson(idListString, new TypeToken<ArrayList<Integer>>(){}.getType());

        AppWidgetProviderInfo info;
        for (int i = 0; i < widgetCount; i++) {
            info = AppWidgetManager.getInstance(this.getApplicationContext()).getAppWidgetInfo(appWidgetIdList.get(i));
            Log.d("debug123", info.label + "");
        }

        ListView listView = (ListView) findViewById(R.id.list_view);
    }
}
