package com.example.alan.smartvanity;

import android.app.AlertDialog;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Context context = MainActivity.this;

    int height, width;

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

    Gson gson;

    RelativeLayout.LayoutParams params;

    AppWidgetProviderInfo newInfo;
    int newAppWidgetId;

    private ActionBarDrawerToggle mToggle;

    private DrawerLayout mDrawerLayout;

    public void initialize() {
        gson = new Gson();

        id_sharedpreferences = getSharedPreferences("id", Context.MODE_PRIVATE);
        sharedpreferences = getSharedPreferences("data", Context.MODE_PRIVATE);

        widgetCount = sharedpreferences.getInt("WidgetCount", 0);

        providerList = new ArrayList<>();
        posListL = new ArrayList<>();
        posListT = new ArrayList<>();
        appWidgetIdList = new ArrayList<>();

        // get screen width and height
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        mAppWidgetHost = new AppWidgetHost(this, R.id.APPWIDGET_HOST_ID);
        mAppWidgetManager = AppWidgetManager.getInstance(this);

        mainLayout = findViewById(R.id.main_layout);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();

        Button addButton = findViewById(R.id.add_button);
        Button deleteButton = findViewById(R.id.delete_button);
        registerForContextMenu(deleteButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                selectWidget();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
                //builderSingle.setIcon(R.drawable.ic_launcher);
                builderSingle.setTitle("Choose widget");

                final ListAdapter listAdapter = new ListAdapter(MainActivity.this, appWidgetIdList);
                builderSingle.setAdapter(listAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteWidget(listAdapter.getIndex(which));
                    }
                });
                builderSingle.show();
            }
        });

        Button syncButton = findViewById(R.id.sync_button);
        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncData();
            }
        });

        findLayouts();
        populateUI();

        handleRestoreWidgets();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.REQUEST_PICK_APPWIDGET) {
                //getPosition();
                configureWidget(data);
            } else if (requestCode == Constants.REQUEST_CREATE_APPWIDGET) {
                createWidget(data);
            } else if (requestCode == Constants.REQUEST_PICK_GRID) {
                setPosition(data);
            }
        } else if (resultCode == RESULT_CANCELED && data != null) {
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
        AppWidgetHostView hostView = mAppWidgetHost.createView(context.getApplicationContext(), newAppWidgetId, newInfo);
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

            editor.putInt(key_id, appWidgetIdList.get(i));
            editor.putInt(key_positionL, posListL.get(i));
            editor.putInt(key_positionT, posListT.get(i));
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
        RelativeLayout.LayoutParams params;
        AppWidgetHostView hostView;


        for (int i = 0; i < widgetCount; i++) {
            appWidgetId = appWidgetIdList.get(i);
            info = AppWidgetManager.getInstance(this.getApplicationContext()).getAppWidgetInfo(appWidgetId);

            hostView = mAppWidgetHost.createView(context.getApplicationContext(), appWidgetId, info);
            hostView.setAppWidget(appWidgetId, info);

            params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = posListL.get(i);
            params.topMargin = posListT.get(i);

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
        Intent gridIntent = new Intent(this, Grid.class);
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
            info = AppWidgetManager.getInstance(context.getApplicationContext()).getAppWidgetInfo(appWidgetIdList.get(i));

            myRef = myRef.getParent().getParent().child("positionL").child("val" + i);
            myRef.setValue(posListL.get(i));

            myRef = myRef.getParent().getParent().child("positionT").child("val" + i);
            myRef.setValue(posListT.get(i));

            myRef = myRef.getParent().getParent().child("provider").child("val" + i);
            myRef.setValue(gson.toJson(info.provider));

            myRef = myRef.getParent().getParent().child("id").child("val" + i);
            myRef.setValue(appWidgetIdList.get(i));
        }
        myRef = myRef.getParent().getParent().child("updated");
        myRef.setValue(true);
    }

    private void populateUI() {
        getSupportActionBar().setTitle("Your Mirror");
    }

    private void findLayouts() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAppWidgetHost.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAppWidgetHost.stopListening();
    }

    private void setPosition(Intent data) {
        int position = data.getExtras().getInt("position");

        int left;
        int top;
        left = (position % 6) * width / 6;
        top = position * height / 6 / 6;

        posListL.add(left);
        posListT.add(top);

        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.leftMargin = left;
        params.topMargin = top;

        handleAddNewAppWidget(newAppWidgetId, newInfo);
    }

    public void loadData() {
        if (sharedpreferences.contains("id0")) {
            for (int i = 0; i < widgetCount; i++) {
                appWidgetIdList.add(sharedpreferences.getInt("id" + i, -1));
                posListL.add(sharedpreferences.getInt("positionL" + i, -1));
                posListT.add(sharedpreferences.getInt("positionT" + i, -1));
            }
        }
    }

    public void deleteWidget(int index) {
        mainLayout.getChildAt(index).setVisibility(View.GONE);

        posListL.remove(index);
        posListT.remove(index);
        appWidgetIdList.remove(index);
        widgetCount--;

        Toast.makeText(MainActivity.this, "hello", Toast.LENGTH_SHORT).show();
        saveData();
        refresh();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}