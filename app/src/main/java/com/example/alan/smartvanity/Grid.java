package com.example.alan.smartvanity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

import com.google.gson.Gson;

import java.lang.reflect.Type;

import jp.wasabeef.blurry.Blurry;

public class Grid extends AppCompatActivity {
    Intent returnIntent;
    ImageView mBackgroundImageView;
    private boolean mBackgroundBlurred = false;
    Context context;

    final int numCols = 6;

    private int finalRowInput = 0;
    private int finalColInput = 0;
    private int finalPosition = 0;

    SharedPreferences gridSharedpreferences;

    boolean[] gridMap;

    Gson gson;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.grid);

        gson = new Gson();

        gridSharedpreferences = getSharedPreferences("grid", Context.MODE_PRIVATE);
        gridMap = gson.fromJson(gridSharedpreferences.getString("map", ""), boolean[].class);

        if (gridMap == null) {
            gridMap = new boolean[48];
        }

        printMap();

        GridView gridview = (GridView) findViewById(R.id.grid_view);
        gridview.setAdapter(new ImageAdapter(this));

        returnIntent = new Intent(this, MainActivity.class);
        //mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                if (gridMap[position] == true) {

                } else {
                    finalPosition = position;

                    AlertDialog.Builder builder = new AlertDialog.Builder(Grid.this);
                    builder.setTitle("Choose Size");

                    View viewInflated = LayoutInflater.from(Grid.this).inflate(R.layout.grid_input, (ViewGroup) findViewById(R.id.root_grid_view), false);

                    final EditText rowInput = (EditText) viewInflated.findViewById(R.id.row_input);
                    final EditText colInput = (EditText) viewInflated.findViewById(R.id.col_input);

                    builder.setView(viewInflated);


                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finalRowInput = Integer.parseInt(rowInput.getText().toString());
                            finalColInput = Integer.parseInt(colInput.getText().toString());

                            returnIntent.putExtra("position", finalPosition);
                            returnIntent.putExtra("rowSize", finalRowInput);
                            returnIntent.putExtra("colSize", finalColInput);


                            Log.d("DEBUG22", "Grid: rowSize: " + finalRowInput);
                            Log.d("DEBUG22", "Grid: colSize: " + finalColInput);

                            updateGridMap(finalRowInput, finalColInput, finalPosition);
                            saveGridMap();

                            setResult(RESULT_OK, returnIntent);
                            finish();
                        }
                    });
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                }


//                SharedPreferences sharedpreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedpreferences.edit();
//                editor.putInt("NewIntent", 1);
//                editor.commit();



            }
        });

        findViews();

        printMap();
    }

    public void updateGridMap(int width, int height, int pos) {
        for (int i = pos/numCols; i < pos/numCols + height; i++) {
            for (int j = pos%numCols; j < pos%numCols + width; j++) {
                Log.d("DEBUG22", "i: " + i);
                Log.d("DEBUG22", "j: " + j);
                gridMap[6*i+j] = true;
            }
        }
    }

    private void saveGridMap() {
        SharedPreferences.Editor editor = gridSharedpreferences.edit();
        editor.clear().commit();

        String key = "map";
        String val = gson.toJson(gridMap);
        editor.putString(key, val);

        editor.commit();

        printMap();
    }

    private void findViews() {

        mBackgroundImageView = findViewById(R.id.activity_grid_background_image_view);

        if (mBackgroundBlurred) {
            Log.d(Constants.TAG, "Background is already blurred...");
        } else {
            blurBackground();
        }
    }

    private void blurBackground() {
        mBackgroundImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mBackgroundBlurred) {
                    Log.d(Constants.TAG, "Background already blurred...");
                } else {
                    Log.d(Constants.TAG, "Blurring background...");
                    Blurry.with(context)
                            .radius(44)
                            .animate(500)
                            .capture(mBackgroundImageView)
                            .into(mBackgroundImageView);
                    mBackgroundBlurred = true;
                }
            }
        });
    }

    private void printMap() {
        for (int i = 0; i < gridMap.length; i++) {
            Log.d("GridMap", i + ": " + gridMap[i]);
        }
    }
}
