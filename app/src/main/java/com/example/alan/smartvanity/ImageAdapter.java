package com.example.alan.smartvanity;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.google.gson.Gson;

/**
 * Created by Alan on 2/9/2018.
 */

public class ImageAdapter extends BaseAdapter {
    private Context mContext;

    final int numGrid = 48;

    SharedPreferences gridSharedpreferences;

    Gson gson;

    boolean[] gridMap;

    private Integer[] mThumbIds = new Integer[numGrid];

    public ImageAdapter(Context c) {
        mContext = c;

        gson = new Gson();

        gridSharedpreferences = mContext.getSharedPreferences("grid", Context.MODE_PRIVATE);
        gridMap = gson.fromJson(gridSharedpreferences.getString("map", ""), boolean[].class);

        if (gridMap == null) {
            gridMap = new boolean[48];
        }

        for (int i = 0; i < numGrid; i++) {
            if (gridMap[i] == true) {
                mThumbIds[i] = R.drawable.notgrid;
            } else {
                mThumbIds[i] = R.drawable.grid;
            }
        }

    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(144, 144));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }

    // references to  images
//    private Integer[] mThumbIds = {
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//            R.drawable.grid,
//    };
}