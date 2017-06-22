package com.waiter;

import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.onesignal.OneSignal;
import com.securepreferences.SecurePreferences;
import com.waiter.data.EventSuggestion;
import com.waiter.data.SuggestionHelper;
import com.waiter.models.Event;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MapsFragment.OnFragmentInteractionListener,
        EventFragment.OnListFragmentInteractionListener, FloatingSearchView.OnMenuItemClickListener,
        FloatingSearchView.OnFocusChangeListener, FloatingSearchView.OnQueryChangeListener,
        FloatingSearchView.OnSearchListener, AppBarLayout.OnOffsetChangedListener,
        RequestDialogFragment.RequestDialogListener, View.OnClickListener {

    private final String TAG = "MainActivity";
    private static final int REQUEST_CODE_PROFILE = 1;

    private static final int NUM_PAGES = 2;

    private DrawerLayout mDrawerLayout;
    private View navHeaderLayout;
    private LinearLayout footerNavDrawer;

    private FloatingSearchView mSearchView;

    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TabLayout mTabLayout;
    private View mView;

    private MapsFragment mMapsFragment;
    private EventFragment mEventFragment;

    RequestDialogFragment requestDialogFragment;
    private ProgressDialog mProgressDialog;

    public static List<Event> mEventList;

    private String mLastQuery = "";
    private AppBarLayout mAppBar;

    private static String userId;
    private static String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean waiterMode = new SecurePreferences(this).getBoolean("waiter_mode", false);
        setTheme(waiterMode ? R.style.AppThemeWaiter_NoActionBar : R.style.AppTheme_NoActionBar);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        overridePendingTransition(R.anim.flip_in, R.anim.flip_out);

        /*
        ** Begin NavigationDrawer
         */
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navHeaderLayout = navigationView.getHeaderView(0);
        setNavDrawerData();

        footerNavDrawer = (LinearLayout) findViewById(R.id.footer_nav_drawer);
        footerNavDrawer.setOnClickListener(this);
        // End NavigationDrawer

        /*
        ** Begin SearchView
         */
        mSearchView = (FloatingSearchView) findViewById(R.id.floating_search_view);
        mSearchView.attachNavigationDrawerToMenuButton(mDrawerLayout);
        mSearchView.setOnMenuItemClickListener(this);
        mSearchView.setOnQueryChangeListener(this);
        mSearchView.setOnSearchListener(this);
        mSearchView.setOnFocusChangeListener(this);
        //End Searchview

        mAppBar = (AppBarLayout) findViewById(R.id.appbar);

        mAppBar.addOnOffsetChangedListener(this);

        /*
        ** Begin SwipeTabs
         */
        mMapsFragment = new MapsFragment();
        mEventFragment = new EventFragment();
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setIcon(R.drawable.ic_map_white_48dp);
        mTabLayout.getTabAt(1).setIcon(R.drawable.ic_view_list_white_48dp);
        mView = findViewById(R.id.view);
        mTabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                int numTab = tab.getPosition();
                Log.d(TAG, "numTab = " + numTab);
                if (numTab == 0) {
                    AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) mView.getLayoutParams();
                    params.setScrollFlags(0);
                    mView.setLayoutParams(params);
                } else {
                    AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) mView.getLayoutParams();
                    params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS | AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
                    mView.setLayoutParams(params);
                }
            }
        });
        // End SwipeTabs

        requestDialogFragment = new RequestDialogFragment();
        mProgressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.requesting_waiters));
        requestDialogFragment.setProgressDialog(mProgressDialog);

        /*
        ** Start Load Events from API
         */
        mEventList = new ArrayList<>();
        // End Load Events from API
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            List fragmentList = getSupportFragmentManager().getFragments();

            boolean handled = false;
            for (Object f : fragmentList) {
                if (f instanceof MapsFragment) {
                    handled = ((MapsFragment) f).onBackPressed();
                    if (handled) {
                        break;
                    }
                }
            }
            if (!handled) {
                super.onBackPressed();
            }
        }
    }

    @Override
    public void onActionMenuItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_location) {
            if (mViewPager.getCurrentItem() != 0) {
                mViewPager.setCurrentItem(0);
            }
            mMapsFragment.setLastKnownLocation(true);
        } else if (id == R.id.action_refresh) {
            Toast.makeText(this, "Refresh clicked.", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.action_view) {
            int currentTab = mViewPager.getCurrentItem();
            if (currentTab == 0) {
                mViewPager.setCurrentItem(1);
                item.setTitle(getString(R.string.action_maps));
            } else if (currentTab == 1) {
                mViewPager.setCurrentItem(0);
                item.setTitle(getString(R.string.action_list));
            }
        }
    }

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            Toast.makeText(this, "Refresh clicked.", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_view) {
            Toast.makeText(this, "Action View clicked.", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        SharedPreferences prefs = new SecurePreferences(this);
        prefs.edit().remove("is_logged_in").apply();
        prefs.edit().remove("waiter_mode").apply();
        prefs.edit().remove("user_id").apply();
        prefs.edit().remove("first_name").apply();
        prefs.edit().remove("last_name").apply();
        prefs.edit().remove("auth_token").apply();
//        prefs.edit().clear().apply();

        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);

        finish();
    }

    public static String getUserId() {
        return userId;
    }

    public static String getUserEmail() {
        return userEmail;
    }

    private void setNavDrawerData() {
        TextView userEmailView = (TextView) navHeaderLayout.findViewById(R.id.user_email);
        TextView userNameView = (TextView) navHeaderLayout.findViewById(R.id.user_name);

        SharedPreferences prefs = new SecurePreferences(this);
        String email = prefs.getString("user_email", getString(R.string.placeholder_email));
        String firstName = prefs.getString("first_name", getString(R.string.placeholder_fname));
        String lastName = prefs.getString("last_name", getString(R.string.placeholder_lname));
        String id = prefs.getString("user_id", "empty");

        userEmailView.setText(email);
        userNameView.setText(firstName + " " + lastName);

        userEmail = email;
        userId = id;

        OneSignal.syncHashedEmail(email);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PROFILE) {
            if (resultCode == RESULT_OK) {
                setNavDrawerData();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivityForResult(intent, REQUEST_CODE_PROFILE);
        } else if (id == R.id.nav_payment) {

        } else if (id == R.id.nav_history) {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            logout();
        } else if (id == R.id.nav_about) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onSearchTextChanged(String oldQuery, String newQuery) {
        //get suggestions based on newQuery

        //pass them on to the search view
//        mSearchView.swapSuggestions(newSuggestions);

        if (!oldQuery.equals("") && newQuery.equals("")) {
            mSearchView.clearSuggestions();
        } else {
            mSearchView.showProgress();

            SuggestionHelper.findSuggestions(this, newQuery, 5, 250, new SuggestionHelper.OnFindSuggestionsListener() {
                @Override
                public void onResults(List<EventSuggestion> results) {
                    mSearchView.swapSuggestions(results);
                    mSearchView.hideProgress();
                }
            });
        }
    }

    @Override
    public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
        EventSuggestion eventSuggestion = (EventSuggestion) searchSuggestion;
        SuggestionHelper.findEvents(this, eventSuggestion.getBody(),
                new SuggestionHelper.OnFindEventsListener() {
                    @Override
                    public void onResults(List<Event> results) {
//                        mSearchResultsAdapter.swapData(results);
                    }
                });

        Log.d(TAG, "onSuggestionClicked()");

        mLastQuery = searchSuggestion.getBody();
        int mLastQueryPosition = ((EventSuggestion) searchSuggestion).getEventPosition();

        Intent intent = new Intent(this, FullEventActivity.class);
        intent.putExtra("EVENT_POSITION", mLastQueryPosition);
        startActivity(intent);
    }

    @Override
    public void onSearchAction(String currentQuery) {
        mLastQuery = currentQuery;

        SuggestionHelper.findEvents(this, currentQuery, new SuggestionHelper.OnFindEventsListener() {
            @Override
            public void onResults(List<Event> results) {
//                mSearchResultsAdapter.swapData(results);
            }
        });

        Toast.makeText(this, "onSearchAction()", Toast.LENGTH_SHORT).show();

        Log.d(TAG, "onSearchAction()");
    }

    @Override
    public void onFocus() {
        fadeInBackground(0, 150);
        //show suggestions when search bar gains focus (typically history suggestions)
        if (mLastQuery != null && !mLastQuery.trim().isEmpty()) {
            mSearchView.setSearchText(mLastQuery);
            mSearchView.swapSuggestions(SuggestionHelper.getHistory(this, 3));
        }
    }

    @Override
    public void onFocusCleared() {
        fadeInBackground(150, 0);
        mSearchView.setSearchBarTitle(mLastQuery);
    }

    private void fadeInBackground(int alphaFocused, int alphaNotFocused) {
        ValueAnimator anim = ValueAnimator.ofInt(alphaFocused, alphaNotFocused);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                int value = (Integer) animation.getAnimatedValue();
                mTabLayout.setBackgroundColor(getResources().getColor(R.color.black));
                mTabLayout.getBackground().setAlpha(value);
            }
        });
        anim.setDuration(250);
        anim.start();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        mSearchView.setTranslationY(verticalOffset);
    }

    @Override
    public void onDialogPositiveClick(AppCompatDialogFragment dialog, int value) {
        dialog.dismiss();
//        Toast.makeText(this, "onDialogPositiveClick (request " + value + " waiters)", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDialogNegativeClick(AppCompatDialogFragment dialog) {
//        Toast.makeText(this, "onDialogNegativeClick", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSnackbarMessage(String message) {
        Snackbar.make(findViewById(R.id.parent_view), message, Snackbar.LENGTH_LONG).show();
    }

    public ViewPager getViewPager() {
        return this.mViewPager;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.footer_nav_drawer:
//                mDrawerLayout.closeDrawers();
                SharedPreferences prefs = new SecurePreferences(this);
                boolean waiterMode = prefs.getBoolean("waiter_mode", false);
                if (!waiterMode) {
                    prefs.edit().putBoolean("waiter_mode", true).apply();
                } else {
                    prefs.edit().putBoolean("waiter_mode", false).apply();
                }

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return mMapsFragment;
            } else if (position == 1) {
                return mEventFragment;
            }
            return null;
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

    @Override
    public void onFragmentInteractionMaps() {
        Log.d("MainActivity", "onFragmentInteractionMaps");
        mEventFragment.refreshEventsList();
        SuggestionHelper.refreshSuggestions();
    }

    @Override
    public void onMapsEventClicked(int eventPosition) {
        requestDialogFragment.setEventId(mEventList.get(eventPosition).getId());
        requestDialogFragment.show(getSupportFragmentManager(), "RequestDialogFragment");
    }

    @Override
    public void showErrorSnackbar(String message) {
        Snackbar.make(mViewPager, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onListFragmentInteractionEvent(int position) {
        Intent intent = new Intent(this, FullEventActivity.class);
        intent.putExtra("EVENT_POSITION", position);
        startActivity(intent);
    }
}
