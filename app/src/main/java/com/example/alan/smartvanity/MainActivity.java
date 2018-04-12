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
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    FragmentManager fragmentManager = getFragmentManager();
    TextView mEmailTextView;
    TextView mDisplayNameTextView;

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


        fragmentManager.beginTransaction().add(R.id.content_frame, new MainFragment(), "home").commit();
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
            if (fragmentManager.findFragmentByTag("home") == null) {
                fragmentManager.beginTransaction().add(R.id.content_frame, new MainFragment(), "home").commit();
            } else {
                fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("home")).commit();
            }
            if(fragmentManager.findFragmentByTag("controller") != null){
                //if the other fragment is visible, hide it.
                fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("controller")).commit();
            }

        } else if (id == R.id.nav_controller) {
            if (fragmentManager.findFragmentByTag("controller") == null) {
                fragmentManager.beginTransaction().add(R.id.content_frame, new controllerFragment(), "controller").commit();
            } else {
                fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("controller")).commit();
            }
            if(fragmentManager.findFragmentByTag("home") != null){
                //if the other fragment is visible, hide it.
                fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("home")).commit();
            }

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

        NavigationView mNavView = findViewById(R.id.nav_view);
        View headerView = mNavView.getHeaderView(0);
        mEmailTextView = headerView.findViewById(R.id.drawer_email_text_view);
        mDisplayNameTextView = headerView.findViewById(R.id.drawer_display_text_view);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            Log.w(Constants.TAG, "Email: " + email);
            mEmailTextView.setText(email);
            if (user.getDisplayName() != null) {
                String displayName = user.getDisplayName();
                Log.w(Constants.TAG, "Display Name: " + displayName);
                mDisplayNameTextView.setText(displayName);
            } else {
                mDisplayNameTextView.setText("No Display Name");
            }
        }
    }
}