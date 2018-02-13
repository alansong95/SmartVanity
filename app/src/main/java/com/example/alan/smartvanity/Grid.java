package com.example.alan.smartvanity;

import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

public class Grid extends AppCompatActivity {
    Intent myIntent;
    String selected;

    Intent mainIntent;

    int appWidgetId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid);

        GridView gridview = (GridView) findViewById(R.id.grid_view);
        gridview.setAdapter(new ImageAdapter(this));

        myIntent = getIntent();
        selected = myIntent.getStringExtra("Selected");
        appWidgetId = myIntent.getExtras().getInt("WidgetId");

        mainIntent = new Intent(this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mainIntent.putExtra("Selected", selected);
        mainIntent.putExtra("WidgetId", appWidgetId);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                mainIntent.putExtra("Position", position);
                Toast.makeText(Grid.this, appWidgetId + "" , Toast.LENGTH_SHORT).show();

                SharedPreferences sharedpreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putInt("NewIntent", 1);
                editor.commit();

                startActivity(mainIntent);
                finish();
            }
        });
    }
}
