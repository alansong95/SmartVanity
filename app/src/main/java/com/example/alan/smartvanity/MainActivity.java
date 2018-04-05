package com.example.alan.smartvanity;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    FragmentManager fragmentManager = getFragmentManager();
    TextView mEmailTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        fragmentManager.beginTransaction().replace(R.id.content_frame, new MainFragment()).commit();
        navigationView.setCheckedItem(R.id.nav_home);

        updateDrawerMenuProfile();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new MainFragment()).commit();
        } else if (id == R.id.nav_controller) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new controllerFragment()).commit();
        } else if (id == R.id.nav_bluetooth) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new BluetoothFragment()).commit();
        } else if (id == R.id.nav_account) {

        } else if (id == R.id.nav_about_us) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new AboutUsFragment()).commit();
        } else if (id == R.id.nav_logout) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new LogoutFragment()).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private void updateDrawerMenuProfile() {
        DatabaseReference mDatabase;
        NavigationView mNavView = findViewById(R.id.nav_view);
        mEmailTextView = mNavView.findViewById(R.id.drawer_email_text_view);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            Log.w(Constants.TAG, "Email: " + email);
            mEmailTextView.setText(email);
        }
    }
}