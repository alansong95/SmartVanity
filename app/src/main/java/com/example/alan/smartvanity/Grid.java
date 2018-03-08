package com.example.alan.smartvanity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

public class Grid extends AppCompatActivity {
    Intent returnIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid);

        GridView gridview = (GridView) findViewById(R.id.grid_view);
        gridview.setAdapter(new ImageAdapter(this));

        returnIntent = new Intent(this, MainActivity.class);
        //mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                returnIntent.putExtra("position", position);
//                SharedPreferences sharedpreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedpreferences.edit();
//                editor.putInt("NewIntent", 1);
//                editor.commit();

                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
    }
}
