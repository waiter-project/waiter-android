package com.waiter;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.securepreferences.SecurePreferences;
import com.waiter.data.EventSuggestion;
import com.waiter.data.SuggestionHelper;
import com.waiter.models.Event;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MapsFragment.OnFragmentInteractionListener, EventFragment.OnListFragmentInteractionListener, FloatingSearchView.OnMenuItemClickListener, FloatingSearchView.OnFocusChangeListener, FloatingSearchView.OnQueryChangeListener, FloatingSearchView.OnSearchListener, AppBarLayout.OnOffsetChangedListener {

    private final String TAG = "MainActivity";
    private static final int REQUEST_CODE_PROFILE = 1;

    private static final int NUM_PAGES = 2;

    private View navHeaderLayout;

    private FloatingSearchView mSearchView;

    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TabLayout mTabLayout;
    private View mView;

    private MapsFragment mMapsFragment;
    private EventFragment mEventFragment;

    public static ArrayList<Event> mEventList;

    private String mLastQuery = "";
    private AppBarLayout mAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        ** Begin NavigationDrawer
         */
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navHeaderLayout = navigationView.getHeaderView(0);
        setNavDrawerData();
        // End NavigationDrawer

        /*
        ** Begin SearchView
         */
        mSearchView = (FloatingSearchView) findViewById(R.id.floating_search_view);
        mSearchView.attachNavigationDrawerToMenuButton(drawer);
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

        /*
        ** Start Load Events from API
         */
        mEventList = new ArrayList<>();
        List<String> listOfWaiters = new ArrayList<>();
        listOfWaiters.add("58fc51f131087c0011378ebe");
//        mEventList.add(new Event("58fc51e531087c0011378ebc",
//                "Eiffel Tower",
//                "A big piece of iron",
//                "5 Avenue Anatole Paris France",
//                48.8584,
//                2.2945,
//                "Everyday",
//                1,
//                listOfWaiters));
        mEventList.add(new Event("1",
                "Hilltop Park",
                "Beautiful view of Long Beach",
                "2351 Dawson Ave, Signal Hill, CA 90755",
                33.799368,
                -118.1656432,
                "Everyday",
                1,
                listOfWaiters));
        mEventList.add(new Event("2",
                "Bookstore Spring Sales",
                "-20% on every items at the bookstore!",
                "6049 E 7th St, Long Beach, CA 90840",
                33.7799968,
                -118.1143435,
                "Mar. 3rd, 2017",
                1,
                listOfWaiters));
        mEventList.add(new Event("3",
                "iPhone 8 Launch",
                "The new iPhone launches today at your nearest Apple Store!",
                "242 Los Cerritos Center, Cerritos, CA 90703",
                33.862665,
                -118.094118,
                "Sept. 9, 2017",
                1,
                listOfWaiters));
        mEventList.add(new Event("4",
                "Taco Tuesday",
                "Tired after your final week? Free tacos for everyone!",
                "Every Taco restaurant ever",
                33.7882741,
                -118.1237334,
                "May 19, 2017",
                1,
                listOfWaiters));
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
//        prefs.edit().remove("is_logged_in").apply();
//        prefs.edit().remove("user_id").apply();
//        prefs.edit().remove("first_name").apply();
//        prefs.edit().remove("last_name").apply();
//        prefs.edit().remove("auth_token").apply();
        prefs.edit().clear().apply();

        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);

        finish();
    }

    private void setNavDrawerData() {
        TextView userEmail = (TextView) navHeaderLayout.findViewById(R.id.user_email);
        TextView userName = (TextView) navHeaderLayout.findViewById(R.id.user_name);

        SharedPreferences prefs = new SecurePreferences(this);
        String email = prefs.getString("user_email", getString(R.string.placeholder_email));
        String firstName = prefs.getString("first_name", getString(R.string.placeholder_fname));
        String lastName = prefs.getString("last_name", getString(R.string.placeholder_lname));

        userEmail.setText(email);
        userName.setText(firstName + " " + lastName);
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

        Toast.makeText(this, "'" + mLastQuery + "' clicked.", Toast.LENGTH_SHORT).show();
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
    public void onFragmentInteractionMaps(Uri uri) {
        Log.d("MainActivity", "onFragmentInteractionMaps, uri: " + String.valueOf(uri));
    }

    @Override
    public void onListFragmentInteractionEvent(Event item) {
        Log.d("MainActivity", "onListFragmentInteractionEvent, DummyItem: " + String.valueOf(item));
    }
}
