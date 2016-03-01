package com.waiter.waiterpoc;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.waiter.waiterpoc.fragments.WaitersFragment;
import com.waiter.waiterpoc.models.Event;
import com.waiter.waiterpoc.models.GenericResponse;
import com.waiter.waiterpoc.models.GenericResponseArray;
import com.waiter.waiterpoc.models.User;
import com.waiter.waiterpoc.network.CheckNetwork;
import com.waiter.waiterpoc.network.ServiceGenerator;
import com.waiter.waiterpoc.network.WaiterService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FullEventActivity extends AppCompatActivity implements WaitersFragment.OnFragmentInteractionListener {

    private static final String LOG_TAG = "EventsFragment";
    private static Context sContext;

    // UI References
    private TextView mNameView;
    private TextView mDescriptionView;
    private TextView mDateView;
    private ImageView mErrorImage;
    private TextView mErrorText;
    private ProgressBar mProgressBar;
    private CardView mCardView;

    // Initial variables
    private String eventId;
    private Event event;
    private static List<User> waitersList;

    public static List<User> getWaitersList() {
        return waitersList;
    }

    public static Context getsContext() {
        return sContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_event);

        // Initial instantiation
        sContext = getApplicationContext();

        // Set back arrow button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Set up UI elements
        mNameView = (TextView) findViewById(R.id.eventName);
        mDescriptionView = (TextView) findViewById(R.id.eventDescription);
        mDateView = (TextView) findViewById(R.id.eventDate);
        mErrorImage = (ImageView) findViewById(R.id.errorImage);
        mErrorText = (TextView) findViewById(R.id.errorText);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mCardView = (CardView) findViewById(R.id.cvFullEvent);

        // Check logout from MainActivity
        Intent myIntent = getIntent();
        eventId = myIntent.getStringExtra("eventId");
        if (!eventId.isEmpty()) {
            refreshEvent();
        }
    }

    private void refreshEvent() {
        mProgressBar.setVisibility(View.VISIBLE);
        mCardView.setVisibility(View.GONE);

        // Check internet connection
        if (!CheckNetwork.isInternetAvailable(MainActivity.getsContext())) {
            //Toast.makeText(LoginActivity.this, "No internet connection", Toast.LENGTH_LONG).show();
            //Snackbar.make((View) findViewById(R.id.root_view), "No internet connection", Snackbar.LENGTH_LONG).show();
            onEventFailed(getString(R.string.no_internet));
            return;
        }

        // API connection and get events from server
        WaiterService service = ServiceGenerator.createService(WaiterService.class);

        Call<GenericResponse<Event>> call = service.getEvent(eventId);

        call.enqueue(new Callback<GenericResponse<Event>>() {
            @Override
            public void onResponse(Call<GenericResponse<Event>> call, Response<GenericResponse<Event>> response) {
                if (response.isSuccess()) {

                    Log.d(LOG_TAG, "Success! Return: " + response.message() + " - Raw: " + response.raw().toString());

                    event = response.body().getData();
                    // Check event
                    if (event != null) {

                        String name = event.getName();
                        if (name != null) {
                            mNameView.setText(name);
                        }
                        String description = event.getDescription();
                        if (description != null) {
                            mDescriptionView.setText(description);
                        }
                        String date = event.getDate();
                        if (date != null) {
                            date = date.substring(0, 10);
                            mDateView.setText(date);
                        }

                        waitersList = response.body().getData().getWaiters();

                        onEventSuccess();
                    } else {
                        onEventFailed(getString(R.string.error_event));
                    }

                } else {
                    Log.d(LOG_TAG, "Failure! Return: " + response.message() + " - Raw: " + response.raw().toString());
                    onEventFailed(getString(R.string.unknown_error_short));
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<Event>> call, Throwable t) {
                Log.d(LOG_TAG, "Error: " + t.getMessage());
                onEventFailed(getString(R.string.unknown_error_short));
            }
        });
    }

    private void onEventSuccess() {

        WaitersFragment fragment = new WaitersFragment();
        FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();

        mCardView.setVisibility(View.VISIBLE);
        mErrorText.setVisibility(View.GONE);
        mErrorImage.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);

    }

    private void onEventFailed(String errorMessage) {
        mCardView.setVisibility(View.GONE);
        mErrorText.setText(errorMessage);
        mErrorText.setVisibility(View.VISIBLE);
        mErrorImage.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.full_event, menu);
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
        if (id == R.id.menu_refresh) {
            Log.i(LOG_TAG, "Refresh menu item selected");
            refreshEvent();
            return true;
        } else if (id == android.R.id.home) {
            //NavUtils.navigateUpFromSameTask(this);
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteractionWaiters(Uri ui) {

    }
}