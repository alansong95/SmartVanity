package com.example.alan.smartvanity;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class SelectWidget extends AppCompatActivity {
    ListView listView;
    ListAdapter listAdapter;

    AppWidgetManager manager;
    List<AppWidgetProviderInfo> infoList;

    Intent gridIntent;

    AppWidgetHost mAppWidgetHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_widget);

        listView = (ListView) findViewById(R.id.list_view);

        manager = AppWidgetManager.getInstance(SelectWidget.this);
        infoList = manager.getInstalledProviders();

        listAdapter = new ListAdapter(SelectWidget.this);

        listView.setAdapter(listAdapter);

        gridIntent = new Intent(this, Grid.class);

        mAppWidgetHost = new AppWidgetHost(this, R.id.APPWIDGET_HOST_ID);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int appWidgetId = mAppWidgetHost.allocateAppWidgetId();
                gridIntent.putExtra("WidgetId", appWidgetId);
                Toast.makeText(SelectWidget.this, appWidgetId + "" , Toast.LENGTH_SHORT).show();
                gridIntent.putExtra("Selected", infoList.get(i).provider.toString());
                startActivity(gridIntent);
                finish();
            }
        });
    }
}