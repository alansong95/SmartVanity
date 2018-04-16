package com.example.alan.smartvanity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import jp.wasabeef.blurry.Blurry;

public class Grid extends AppCompatActivity {
    Intent returnIntent;
    ImageView mBackgroundImageView;
    private boolean mBackgroundBlurred = false;
    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
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

        findViews();
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
}
