package com.example.alan.smartvanity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.AbsoluteLayout;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;

public class MainFragment extends Fragment {
    private final int numRows = 8;
    private final int numCols = 6;

    // from monitor
    int height;
    int width;
    int sbc_height = 1848;
    int sbc_width = 1080;

    Context context = getActivity();

    ViewGroup mainLayout;

    AppWidgetManager mAppWidgetManager;
    AppWidgetHost mAppWidgetHost;

    SharedPreferences sharedpreferences;
    SharedPreferences id_sharedpreferences;

    int widgetCount;

    ArrayList<String> providerList;
    ArrayList<Integer> posListL;
    ArrayList<Integer> posListT;
    ArrayList<Integer> appWidgetIdList;

    ArrayList<Integer> sbc_posListL;
    ArrayList<Integer> sbc_posListT;

    ArrayList<Integer> rowSizeList;
    ArrayList<Integer> colSizeList;


    Gson gson;

    AbsoluteLayout.LayoutParams params;

    AppWidgetProviderInfo newInfo;
    int newAppWidgetId;

    private ActionBarDrawerToggle mToggle;

    private DrawerLayout mDrawerLayout;

    SharedPreferences gridSharedpreferences;

    boolean[] gridMap;
    ArrayList<Integer> posList;


    public void initialize() {
        gson = new Gson();

        id_sharedpreferences = this.getActivity().getSharedPreferences("id", Context.MODE_PRIVATE);
        sharedpreferences = this.getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        gridSharedpreferences = this.getActivity().getSharedPreferences("grid", Context.MODE_PRIVATE);

        gridMap = gson.fromJson(gridSharedpreferences.getString("map", ""), boolean[].class);

        if (gridMap == null) {
            gridMap = new boolean[48];
        }

        widgetCount = sharedpreferences.getInt("WidgetCount", 0);

        providerList = new ArrayList<>();
        posListL = new ArrayList<>();
        posListT = new ArrayList<>();
        appWidgetIdList = new ArrayList<>();


        sbc_posListL = new ArrayList<>();
        sbc_posListT = new ArrayList<>();

        rowSizeList = new ArrayList<>();
        colSizeList = new ArrayList<>();

        posList = new ArrayList<>();

        // get screen width and height
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        mAppWidgetHost = new AppWidgetHost(this.getActivity(), R.id.APPWIDGET_HOST_ID);
        mAppWidgetManager = AppWidgetManager.getInstance(this.getActivity());

        mainLayout = getView().findViewById(R.id.main_layout);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        //mAppWidgetHost.startListening();

        Log.d("DEBUG123", "LoL");

        initialize();

        Button addButton = getView().findViewById(R.id.add_button);
        Button deleteButton = getView().findViewById(R.id.delete_button);
        registerForContextMenu(deleteButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                selectWidget();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
                //builderSingle.setIcon(R.drawable.ic_launcher);
                builderSingle.setTitle("Choose widget");

                final ListAdapter listAdapter = new ListAdapter(getActivity(), appWidgetIdList);
                builderSingle.setAdapter(listAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteWidget(listAdapter.getIndex(which));
                    }
                });
                builderSingle.show();
            }
        });

        Button syncButton = getView().findViewById(R.id.sync_button);
        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncData();
            }
        });

        populateUI();

        handleRestoreWidgets();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.REQUEST_PICK_APPWIDGET) {
                //getPosition();
                configureWidget(data);
            } else if (requestCode == Constants.REQUEST_CREATE_APPWIDGET) {
                createWidget(data);
            } else if (requestCode == Constants.REQUEST_PICK_GRID) {
                setPosition(data);
            }
        } else if (resultCode == Activity.RESULT_CANCELED && data != null) {
        }
    }

    void selectWidget() {
        int appWidgetId = this.mAppWidgetHost.allocateAppWidgetId();
        Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
        pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        addEmptyData(pickIntent);

        startActivityForResult(pickIntent, Constants.REQUEST_PICK_APPWIDGET);

    }

    void addEmptyData(Intent pickIntent) {
        ArrayList<AppWidgetProviderInfo> customInfo = new ArrayList<>();
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_INFO, customInfo);
        ArrayList<Bundle> customExtras = new ArrayList<>();
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_EXTRAS, customExtras);
    }


    private void configureWidget(Intent data) {
        Bundle extras = data.getExtras();
        newAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        newInfo = mAppWidgetManager.getAppWidgetInfo(newAppWidgetId);
        if (newInfo.configure != null) {
            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.setComponent(newInfo.configure);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, newAppWidgetId);
            startActivityForResult(intent, Constants.REQUEST_CREATE_APPWIDGET);
        } else {
            createWidget(data);
        }
    }

    private void handleAddNewAppWidget(int newAppWidgetId, AppWidgetProviderInfo newInfo) {
        AppWidgetHostView hostView = mAppWidgetHost.createView(this.getActivity().getApplicationContext(), newAppWidgetId, newInfo);
        hostView.setAppWidget(newAppWidgetId, newInfo);

        hostView.setId(newAppWidgetId);
        mainLayout.addView(hostView, widgetCount, params);

        widgetCount++;

        addDataToList(newAppWidgetId);
        saveData();
    }

    public void addDataToList(int newAppWidgetId) {
        appWidgetIdList.add(newAppWidgetId);
    }

    public void saveData() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear().commit();
        editor.putInt("WidgetCount", widgetCount);

        for (int i = 0; i < widgetCount; i++) {
            String key_id = "id" + i;
            String key_positionL = "positionL" + i;
            String key_positionT = "positionT" + i;
            String key_sbc_positionL = "sbc_positionL" + i;
            String key_sbc_positionT = "sbc_positionT" + i;
            String key_rowSize = "rowSize" + i;
            String key_colSize = "colSize" + i;
            String key_pos = "pos" + i;

            editor.putInt(key_id, appWidgetIdList.get(i));
            editor.putInt(key_positionL, posListL.get(i));
            editor.putInt(key_positionT, posListT.get(i));
            editor.putInt(key_sbc_positionL, sbc_posListL.get(i));
            editor.putInt(key_sbc_positionT, sbc_posListT.get(i));
            editor.putInt(key_rowSize, rowSizeList.get(i));
            editor.putInt(key_colSize, colSizeList.get(i));
            editor.putInt(key_pos, posList.get(i));
        }
        editor.commit();
    }

    public void refresh() {
        mainLayout.removeAllViews();
        putWidget();
    }

    public void putWidget() {
        int appWidgetId;
        AppWidgetProviderInfo info;
        AbsoluteLayout.LayoutParams params;
        AppWidgetHostView hostView;

        mainLayout.removeAllViews();


        for (int i = 0; i < widgetCount; i++) {
            appWidgetId = appWidgetIdList.get(i);
            info = AppWidgetManager.getInstance(this.getActivity().getApplicationContext()).getAppWidgetInfo(appWidgetId);

            hostView = mAppWidgetHost.createView(this.getActivity().getApplicationContext(), appWidgetId, info);
            hostView.setAppWidget(appWidgetId, info);

            params = new AbsoluteLayout.LayoutParams(rowSizeList.get(i) * width / numCols, colSizeList.get(i) * height / numRows, posListL.get(i), posListT.get(i));

            Log.d("DEBUG22", "kk: " +  rowSizeList.get(i) * width / numCols);
            Log.d("DEBUG22", "kk: " + colSizeList.get(i) * height / numRows);

//            params.leftMargin = posListL.get(i);
//            params.topMargin = posListT.get(i);

            hostView.setId(appWidgetId);

            mainLayout.addView(hostView, i, params);
        }
    }

    private void handleRestoreWidgets() {
        loadData();
        putWidget();
    }

    public void createWidget(Intent data) {
        newInfo = mAppWidgetManager.getAppWidgetInfo(newAppWidgetId);
        getPosition();
    }

    public void getPosition() {
        Intent gridIntent = new Intent(getActivity(), Grid.class);
        startActivityForResult(gridIntent, Constants.REQUEST_PICK_GRID);
    }

    public void syncData() {
        AppWidgetProviderInfo info;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String uid = id_sharedpreferences.getString("uid", "");

        DatabaseReference myRef = database.getReference("users");

        myRef= myRef.child(uid).child("widgets");
        myRef.setValue(null);

        myRef= myRef.child("widget_count").child("val");
        myRef.setValue(Integer.toString(widgetCount));

        for (int i = 0; i < widgetCount; i++) {
            info = AppWidgetManager.getInstance(this.getActivity().getApplicationContext()).getAppWidgetInfo(appWidgetIdList.get(i));

            myRef = myRef.getParent().getParent().child("positionL").child("val" + i);
            myRef.setValue(sbc_posListL.get(i));

            myRef = myRef.getParent().getParent().child("positionT").child("val" + i);
            myRef.setValue(sbc_posListT.get(i));

            myRef = myRef.getParent().getParent().child("provider").child("val" + i);
            myRef.setValue(gson.toJson(info.provider));

            myRef = myRef.getParent().getParent().child("id").child("val" + i);
            myRef.setValue(appWidgetIdList.get(i));

            myRef = myRef.getParent().getParent().child("rowSize").child("val" + i);
            myRef.setValue(rowSizeList.get(i) * sbc_width / numCols);

            myRef = myRef.getParent().getParent().child("colSize").child("val" + i);
            myRef.setValue(colSizeList.get(i) * sbc_height / numRows);
        }
        myRef = myRef.getParent().getParent().child("updated");
        myRef.setValue(true);
    }

    private void populateUI() {
        //getActivity().getSupportActionBar().setTitle("Your Mirror");
    }


//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        setIntent(intent);
//    }

    @Override
    public void onStop() {
        super.onStop();
        mAppWidgetHost.stopListening();
    }

    private void setPosition(Intent data) {
        int position = data.getExtras().getInt("position");
        int rowSize = data.getExtras().getInt("rowSize");
        int colSize = data.getExtras().getInt("colSize");

        Log.d("DEBUG123", "rowSize: " + rowSize);
        Log.d("DEBUG123", "colSize: " + colSize);

        int left;
        int top;
        left = (position % numCols) * width / numCols;
        top = (position / numCols) * height / numRows;

        int sbc_left;
        int sbc_top;
        sbc_left = (position % numCols) * sbc_width / numCols;
        sbc_top = (position / numCols) * sbc_height / numRows;

        posListL.add(left);
        posListT.add(top);

        sbc_posListL.add(sbc_left);
        sbc_posListT.add(sbc_top);

        rowSizeList.add(rowSize);
        colSizeList.add(colSize);
        posList.add(position);

//        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        params = new AbsoluteLayout.LayoutParams(rowSize * width / numCols, colSize * height / numRows, left, top);

        Log.d("DEBUG123", "kk: " +  rowSize * width / numCols);
        Log.d("DEBUG123", "kk: " + colSize * height / numRows);
//        params.leftMargin = left;
//        params.topMargin = top;

        handleAddNewAppWidget(newAppWidgetId, newInfo);
    }

    public void loadData() {
        if (sharedpreferences.contains("id0")) {
            for (int i = 0; i < widgetCount; i++) {
                appWidgetIdList.add(sharedpreferences.getInt("id" + i, -1));
                posListL.add(sharedpreferences.getInt("positionL" + i, -1));
                posListT.add(sharedpreferences.getInt("positionT" + i, -1));
                sbc_posListL.add(sharedpreferences.getInt("sbc_positionL" + i, -1));
                sbc_posListT.add(sharedpreferences.getInt("sbc_positionT" + i, -1));
                rowSizeList.add(sharedpreferences.getInt("rowSize" + i, -1));
                colSizeList.add(sharedpreferences.getInt("colSize" + i, -1));
                posList.add(sharedpreferences.getInt("pos" + i, -1));
            }
        }
    }

    public void deleteWidget(int index) {
        mainLayout.getChildAt(index).setVisibility(View.GONE);

        updateGridMap(rowSizeList.get(index),colSizeList.get(index), posList.get(index));
        saveGridMap();

        posListL.remove(index);
        posListT.remove(index);
        sbc_posListL.remove(index);
        sbc_posListT.remove(index);
        appWidgetIdList.remove(index);
        rowSizeList.remove(index);
        colSizeList.remove(index);
        posList.remove(index);

        widgetCount--;

        //Toast.makeText(MainFragment.this, "hello", Toast.LENGTH_SHORT).show();
        saveData();
        refresh();
    }

    private void updateGridMap(int width, int height, int pos) {
        for (int i = pos/numCols; i < pos/numCols + height; i++) {
            for (int j = pos%numCols; j < pos%numCols + width; j++) {
                Log.d("DEBUG22", "i: " + i);
                Log.d("DEBUG22", "j: " + j);
                gridMap[6*i+j] = false;
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
    }

}