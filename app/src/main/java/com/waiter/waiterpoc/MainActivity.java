package com.waiter.waiterpoc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.waiter.waiterpoc.dummy.DummyContent;
import com.waiter.waiterpoc.fragments_nav_drawer.EventsFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, EventsFragment.OnFragmentInteractionListener, EventFragment.OnListFragmentInteractionListener, DebuggingFragment.OnFragmentInteractionListener {

    private static final String LOG_TAG = "MainActivity";

    private static Context sContext;

    private static boolean connected = false;

    private SharedPreferences sp; //crash completely

    public static Context getsContext() {
        return sContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        sContext = getApplicationContext();
        connected = LoginActivity.isConnected();
        if (!connected) {
            Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
            //myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(myIntent);
            //MainActivity.this.startActivity(myIntent);
        } else {
            // Check internet connection
            /*
            if (!CheckNetwork.isInternetAvailable(this)) {
                Snackbar.make(findViewById(R.id.root_view), "No internet connection", Snackbar.LENGTH_LONG);
            }
            */
        }

        //sp = PreferenceManager.getDefaultSharedPreferences(this); //crash completely
        //sp = getSharedPreferences(getString(R.string.preference_settings), 0);
        /*
        TextView headerName = (TextView) navigationView.findViewById(R.id.header_name);
        TextView headerEmail = (TextView) navigationView.findViewById(R.id.header_email);

        headerEmail.setText("EMAIL TEST");
        headerName.setText("NAME TEST");


        String email = sp.getString("email", "");
        if (!email.isEmpty()) {
            headerEmail.setText(email);
        }

        String firstname = sp.getString("firstname", "");
        String lastname = sp.getString("lastname", "");
        if (!firstname.isEmpty() && !lastname.isEmpty()) {
            headerName.setText(firstname + " " + lastname);
        }
        */

        EventsFragment fragment = new EventsFragment();
        FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();
        setTitle("Events");

        /*
        EventFragment fragment = new EventFragment();
        FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();
        setTitle("Events");
        */
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

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Log.i(LOG_TAG, "onOptionsItemSelected (menu item)");

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.menu_refresh) {
            Log.i(LOG_TAG, "Refresh menu item selected");
            return false;
        }

        return super.onOptionsItemSelected(item);
    }
    */

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            // Handle the camera action
        } else if (id == R.id.nav_event) {
            EventsFragment fragment = new EventsFragment();
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment);
            fragmentTransaction.commit();
            setTitle("Events");
        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_logout) {
            Intent myIntent = new Intent(this, LoginActivity.class);
            myIntent.putExtra("logout", true);
            startActivity(myIntent);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_debugging) {
            DebuggingFragment fragment = new DebuggingFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment);
            fragmentTransaction.commit();
            setTitle(getString(R.string.navigation_drawer_debugging));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteractionEvents(Uri uri) {
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
    }

    @Override
    public void onFragmentInteractionDebugging(Uri uri) {
    }
}
