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

    String[] names;

    Intent gridIntent;

    AppWidgetHost mAppWidgetHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_widget);

        listView = (ListView) findViewById(R.id.list_view);

        manager = AppWidgetManager.getInstance(SelectWidget.this);
        infoList = manager.getInstalledProviders();

        names = new String[infoList.size()];

        for (int i = 0; i < infoList.size(); i++) {
            names[i] = infoList.get(i).label;
        }

        listAdapter = new ListAdapter(SelectWidget.this);

        listView.setAdapter(listAdapter);

        gridIntent = new Intent(this, Grid.class);

        mAppWidgetHost = new AppWidgetHost(this, R.id.APPWIDGET_HOST_ID);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int appWidgetId = mAppWidgetHost.allocateAppWidgetId();

                Toast.makeText(SelectWidget.this, infoList.get(i).label , Toast.LENGTH_SHORT).show();
                gridIntent.putExtra("Selected", names[i]);
                gridIntent.putExtra("WidgetId", appWidgetId);
                gridIntent.putExtra("WidgetInfo", infoList.get(i));
                startActivity(gridIntent);
            }
        });

    }
}