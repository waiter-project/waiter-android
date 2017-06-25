package com.waiter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.waiter.models.Event;
import com.waiter.models.Wait;

public class FullEventActivity extends AppCompatActivity implements View.OnClickListener, RequestDialogFragment.RequestDialogListener {

    // UI References
    private LinearLayout mRootView;
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
    RequestDialogFragment requestDialogFragment;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_event);

        setResult(RESULT_CANCELED);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up UI elements
        mRootView = (LinearLayout) findViewById(R.id.root_view);
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
        requestDialogFragment = new RequestDialogFragment();
        mProgressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.requesting_waiters));
        requestDialogFragment.setProgressDialog(mProgressDialog);

        Intent intent = getIntent();
        int eventPosition = intent.getIntExtra("EVENT_POSITION", -1);
        Log.d("FullEventActivity", "eventPosition = " + eventPosition);

        refreshEvent(eventPosition);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void refreshEvent(int eventPosition) {
        mProgressBar.setVisibility(View.VISIBLE);
        mFullEventLayout.setVisibility(View.GONE);

        requestDialogFragment.setEventId(MainActivity.mEventList.get(eventPosition).getId());

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
                showRequestDialog();
                break;
        }
    }

    public void showRequestDialog() {
        requestDialogFragment.show(getSupportFragmentManager(), "RequestDialogFragment");
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
        Snackbar.make(mRootView, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showCurrentWaitLayout(Wait wait) {
        setResult(RESULT_OK);
    }
}
