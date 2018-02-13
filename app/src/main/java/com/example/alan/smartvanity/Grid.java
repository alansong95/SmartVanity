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

    Intent mainWidget;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid);

        GridView gridview = (GridView) findViewById(R.id.grid_view);
        gridview.setAdapter(new ImageAdapter(this));

        myIntent = getIntent();
        selected = myIntent.getStringExtra("Selected");

        mainWidget = new Intent(this, MainActivity.class);
        mainWidget.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mainWidget.putExtra("Selected", selected);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                mainWidget.putExtra("Position", position);
                startActivity(mainWidget);
                finish();
            }
        });
    }
}
