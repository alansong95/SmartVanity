package com.example.alan.smartvanity;

import android.appwidget.AppWidgetProviderInfo;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

public class Grid extends AppCompatActivity {
    Intent myIntent;
    String selected;

    Intent widgetIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid);

        GridView gridview = (GridView) findViewById(R.id.grid_view);
        gridview.setAdapter(new ImageAdapter(this));

        myIntent = getIntent();
        selected = myIntent.getStringExtra("Selected");

        widgetIntent = new Intent(this, Widget.class);
        widgetIntent.putExtra("Selected", selected);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                widgetIntent.putExtra("Position", position);
                startActivity(widgetIntent);
            }
        });
    }
}
