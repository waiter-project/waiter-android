package com.waiter;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
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
import android.widget.Toast;

import com.securepreferences.SecurePreferences;
import com.waiter.models.ErrorResponse;
import com.waiter.models.Event;
import com.waiter.models.Wait;
import com.waiter.network.ServiceGenerator;
import com.waiter.network.WaiterClient;
import com.waiter.utils.ErrorUtils;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private AlertDialog mConfirmDialog;
    RequestDialogFragment requestDialogFragment;
    private ProgressDialog mProgressDialog;

    private boolean joinedEvent;

    private int eventPosition;
    private WaiterClient waiterClient;
    private ErrorResponse errorResponse;
    private String authToken;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_event);

        setResult(RESULT_CANCELED);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        eventPosition = intent.getIntExtra("EVENT_POSITION", -1);
        Log.d("FullEventActivity", "eventPosition = " + eventPosition);

        SharedPreferences prefs = new SecurePreferences(this);

        authToken = prefs.getString("auth_token", "");
        userId = prefs.getString("user_id", "");

        waiterClient = ServiceGenerator.createService(WaiterClient.class);

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
        if (MainActivity.waiterMode) {
            checkIfJoinedEvent();
        }
        mRequestButton.setVisibility(View.VISIBLE);
        mRequestButton.setOnClickListener(this);
        requestDialogFragment = new RequestDialogFragment();
        mProgressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.requesting_waiters));
        requestDialogFragment.setProgressDialog(mProgressDialog);

        refreshEvent(eventPosition);
    }

    private void checkIfJoinedEvent() {
        joinedEvent = MainActivity.mEventList.get(eventPosition).getListOfWaiters().contains(userId);
        if (joinedEvent) {
            mRequestButton.setText(getString(R.string.leave_this_event));
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.confirm_leave_event_title, MainActivity.mEventList.get(eventPosition).getName()))
                    .setMessage(getString(R.string.confirm_leave_event_message, MainActivity.mEventList.get(eventPosition).getName()))
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            leaveEvent();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            mConfirmDialog = builder.create();
        } else {
            mRequestButton.setText(getString(R.string.join_this_event));
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.confirm_join_event_title, MainActivity.mEventList.get(eventPosition).getName()))
                    .setMessage(R.string.confirm_join_event_message)
                    .setPositiveButton(R.string.agree, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            joinEvent();
                        }
                    })
                    .setNegativeButton(R.string.disagree, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            mConfirmDialog = builder.create();
        }
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
                if (MainActivity.waiterMode) {
                    mConfirmDialog.show();
                } else {
                    showRequestDialog();
                }
                break;
        }
    }

    public void showRequestDialog() {
        requestDialogFragment.show(getSupportFragmentManager(), "RequestDialogFragment");
    }

    private void joinEvent() {
        Call<ResponseBody> call = waiterClient.joinEvent(authToken, MainActivity.mEventList.get(eventPosition).getId(), userId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                mProgressDialog.dismiss();
                if (response.isSuccessful()) {
                    ResponseBody body = response.body();
                    if (body != null) {
                        Snackbar.make(mRootView, getString(R.string.join_event_success, MainActivity.mEventList.get(eventPosition).getName()), Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(mRootView, getString(R.string.response_body_null), Snackbar.LENGTH_LONG).show();
                    }
                    MainActivity.mEventList.get(eventPosition).getListOfWaiters().add(userId);
                    checkIfJoinedEvent();
                    setResult(RESULT_OK);
                } else {
                    errorResponse = ErrorUtils.parseError(response);
                    if (errorResponse != null && errorResponse.getData() != null) {
                        if (errorResponse.getData().getCauses() == null || errorResponse.getData().getCauses().isEmpty()) {
                            Snackbar.make(mRootView, errorResponse.getData().getMessage(), Snackbar.LENGTH_LONG).show();
                        } else {
                            Snackbar.make(mRootView, errorResponse.getData().getCauses().get(0), Snackbar.LENGTH_LONG).show();
                        }
                    } else {
                        Snackbar.make(mRootView, getString(R.string.internal_error), Snackbar.LENGTH_LONG).show();
                    }
                    setResult(RESULT_CANCELED);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                mProgressDialog.dismiss();
                Snackbar.make(mRootView, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                setResult(RESULT_CANCELED);
            }
        });
    }

    private void leaveEvent() {
        Call<ResponseBody> call = waiterClient.leaveEvent(authToken, MainActivity.mEventList.get(eventPosition).getId(), userId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    ResponseBody body = response.body();
                    if (body != null) {
                        Snackbar.make(mRootView, getString(R.string.leave_event_success, MainActivity.mEventList.get(eventPosition).getName()), Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(mRootView, getString(R.string.response_body_null), Snackbar.LENGTH_LONG).show();
                    }
                    MainActivity.mEventList.get(eventPosition).getListOfWaiters().remove(userId);
                    checkIfJoinedEvent();
                    setResult(RESULT_OK);
                } else {
                    errorResponse = ErrorUtils.parseError(response);
                    if (errorResponse != null && errorResponse.getData() != null) {
                        if (errorResponse.getData().getCauses() == null || errorResponse.getData().getCauses().isEmpty()) {
                            Snackbar.make(mRootView, errorResponse.getData().getMessage(), Snackbar.LENGTH_LONG).show();
                        } else {
                            Snackbar.make(mRootView, errorResponse.getData().getCauses().get(0), Snackbar.LENGTH_LONG).show();
                        }
                    } else {
                        Snackbar.make(mRootView, getString(R.string.internal_error), Snackbar.LENGTH_LONG).show();
                    }
                    setResult(RESULT_CANCELED);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Snackbar.make(mRootView, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                setResult(RESULT_CANCELED);
            }
        });
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
