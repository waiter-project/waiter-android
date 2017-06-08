package com.waiter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.waiter.models.Event;
import com.waiter.network.ServiceGenerator;
import com.waiter.network.WaiterClient;

public class FullEventActivity extends AppCompatActivity implements View.OnClickListener {

    // UI References
    private TextView mTitleView;
    private TextView mDescriptionView;
    private TextView mDateView;
    private TextView mAddressView;
    private TextView mWaitersAvailableView;
    private ImageView mErrorImage;
    private TextView mErrorText;
    private ProgressBar mProgressBar;
    private LinearLayout mFullEventLayout;
    private Button mRequestButton;

    private WaiterClient waiterClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_event);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up UI elements
        mTitleView = (TextView) findViewById(R.id.event_title);
        mDescriptionView = (TextView) findViewById(R.id.event_description);
        mAddressView = (TextView) findViewById(R.id.event_address);
        mDateView = (TextView) findViewById(R.id.event_date);
        mWaitersAvailableView = (TextView) findViewById(R.id.event_waiters_available);
        mErrorImage = (ImageView) findViewById(R.id.error_image);
        mErrorText = (TextView) findViewById(R.id.error_text);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mFullEventLayout = (LinearLayout) findViewById(R.id.content_event_layout);
        mRequestButton = (Button) findViewById(R.id.request_btn);
        mRequestButton.setVisibility(View.VISIBLE);
        mRequestButton.setOnClickListener(this);

        Intent intent = getIntent();
        int eventPosition = intent.getIntExtra("EVENT_POSITION", -1);
        Log.d("FullEventActivity", "eventPosition = " + eventPosition);

        refreshEvent(eventPosition);

        waiterClient = ServiceGenerator.createService(WaiterClient.class);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void refreshEvent(int eventPosition) {
        mProgressBar.setVisibility(View.VISIBLE);
        mFullEventLayout.setVisibility(View.GONE);

        if (eventPosition == -1) {
            onEventFailed("Event not found");
        } else {
            Event event = MainActivity.mEventList.get(eventPosition);

            String name = event.getName();
            if (name != null) {
                mTitleView.setText(name);
            }
            String description = event.getDescription();
            if (description != null) {
                mDescriptionView.setText(description);
            }
            String address = event.getAddress();
            if (address != null) {
                mAddressView.setText(address);
            }
            String date = event.getDate();
            if (date != null) {
                mDateView.setText(date);
            }
            mWaitersAvailableView.setText(getString(R.string.waiters_available, event.getListOfWaiters().size()));

            onEventSuccess();
        }
    }

    private void onEventSuccess() {
        mFullEventLayout.setVisibility(View.VISIBLE);
        mErrorText.setVisibility(View.GONE);
        mErrorImage.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        mRequestButton.setVisibility(View.VISIBLE);
    }

    private void onEventFailed(String errorMessage) {
        mFullEventLayout.setVisibility(View.GONE);
        mErrorText.setText(errorMessage);
        mErrorText.setVisibility(View.VISIBLE);
        mErrorImage.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mRequestButton.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.request_btn:
                Toast.makeText(this, "Request Button clicked", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
