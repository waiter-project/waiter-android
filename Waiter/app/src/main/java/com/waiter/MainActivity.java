package com.waiter;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Toast;

import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MapsFragment.OnFragmentInteractionListener, MaterialSearchBar.OnSearchActionListener {

    private static final int NUM_PAGES = 2;

    private DrawerLayout mDrawerLayout;

    private List<String> lastSearches;
    private MaterialSearchBar mSearchBar;

    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        ** Begin NavigationDrawer
         */
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // End NavigationDrawer

        /*
        ** Start SearchBar
         */
        mSearchBar = (MaterialSearchBar) findViewById(R.id.searchBar);
        mSearchBar.setNavButtonEnabled(true);
        mSearchBar.setOnSearchActionListener(this);
//        lastSearches = loadSearchSuggestionFromDisk();
//        mSearchBar.setLastSuggestions(lastSearches);
        mSearchBar.inflateMenu(R.menu.main);
        mSearchBar.getMenu().setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_settings:
                        Toast.makeText(MainActivity.this, "Settings clicked.", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });
        // End SearchBar

        /*
        ** Begin SwipeTabs
         */
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        // End SwipeTabs
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //save last queries to disk
        saveSearchSuggestionToDisk(mSearchBar.getLastSuggestions());
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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
//        String s = enabled ? "enabled" : "disabled";
//        Toast.makeText(MainActivity.this, "Search " + s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        startSearch(text.toString(), true, null, true);
    }

    @Override
    public void onButtonClicked(int buttonCode) {
        switch (buttonCode){
            case MaterialSearchBar.BUTTON_NAVIGATION:
                mDrawerLayout.openDrawer(Gravity.LEFT);
                break;
//            case MaterialSearchBar.BUTTON_SPEECH:
//                openVoiceRecognizer();
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new MapsFragment();
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "MAPS";
                case 1:
                    return "LIST";
            }
            return null;
        }
    }

    private List<String> loadSearchSuggestionFromDisk() {
        // To implement.
        return null;
    }

    private void saveSearchSuggestionToDisk(List lastSuggestions) {
        // To implement.
    }

    @Override
    public void onFragmentInteractionMaps(Uri uri) {
        Log.d("onFragmentInteractionMa", String.valueOf(uri));
    }
}
