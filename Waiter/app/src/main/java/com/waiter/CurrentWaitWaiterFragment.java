package com.waiter;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.waiter.models.ErrorResponse;
import com.waiter.models.ResponseWait;
import com.waiter.models.Wait;
import com.waiter.network.ServiceGenerator;
import com.waiter.network.WaiterClient;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CurrentWaitWaiterFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_CURRENT_WAIT = "CURRENT_WAIT";
    private static final String TAG = "CurrentWaitWaiterFragme";

    private Wait mWait;

    private OnFragmentInteractionListenerCurrentWaitWaiter mListener;

    private WaiterClient waiterClient;
    private ErrorResponse errorResponse;

    // UI Elements
    private View mView;
    private TextView mWaitTitle, mWaitDescription, mEventAddress, mWaitUpdate, mWaitersState;
    private Button mButtonWaitCanStart, mButtonWaitFinished, mCancelButton;

    public CurrentWaitWaiterFragment() {
        // Required empty public constructor
    }

    public static CurrentWaitWaiterFragment newInstance(Wait wait) {
        CurrentWaitWaiterFragment fragment = new CurrentWaitWaiterFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CURRENT_WAIT, wait);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mWait = getArguments().getParcelable(ARG_CURRENT_WAIT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_current_wait_waiter, container, false);
        setupUI();
        refreshUI();
        waiterClient = ServiceGenerator.createService(WaiterClient.class);
        return mView;
    }

    private void setupUI() {
        mWaitTitle = (TextView) mView.findViewById(R.id.wait_title);
        mWaitDescription = (TextView) mView.findViewById(R.id.wait_description);
        mEventAddress = (TextView) mView.findViewById(R.id.event_address);
        mWaitUpdate = (TextView) mView.findViewById(R.id.wait_update);
        mWaitersState = (TextView) mView.findViewById(R.id.waiters_state);
        mButtonWaitCanStart = (Button) mView.findViewById(R.id.btn_wait_can_start);
        mButtonWaitCanStart.setOnClickListener(this);
        mButtonWaitFinished = (Button) mView.findViewById(R.id.btn_wait_finished);
        mButtonWaitFinished.setOnClickListener(this);
        mCancelButton = (Button) mView.findViewById(R.id.btn_cancel_this_wait);
        mCancelButton.setOnClickListener(this);
    }

    private void refreshUI() {
        mWaitTitle.setText(getString(R.string.wait_id, mWait.getId().substring(0, 6)));
//        mWaitDescription.setText();
        mEventAddress.setText(mWait.getEventLocation().toString());

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        Log.d(TAG, "onCreateView: mWait.getCreatedAt() = " + mWait.getCreatedAt());
        try {
            Date convertedDate;
            if (mWait.getState().equals("created")) {
                convertedDate = df.parse(mWait.getCreatedAt());
                mWaitUpdate.setText(getString(R.string.requested_on, new PrettyTime().format(convertedDate)));
            } else if (mWait.getState().equals("queue-start")) {
                convertedDate = df.parse(mWait.getQueueStart());
                mWaitUpdate.setText(getString(R.string.started_on, new PrettyTime().format(convertedDate)));
            } else {
                convertedDate = df.parse(mWait.getQueueEnd());
                mWaitUpdate.setText(getString(R.string.finished_on, new PrettyTime().format(convertedDate)));
            }
            Log.d(TAG, "onCreateView: convertedDate = " + convertedDate);
        } catch (ParseException e) {
            mWaitUpdate.setText(getString(R.string.unknown_error));
            e.printStackTrace();
        }
        mWaitersState.setText(getString(R.string.waiters_requested, mWait.getWaitersIds().size()));
        if (mWait.getState().equals("created")) {
            mButtonWaitCanStart.setVisibility(View.VISIBLE);
            mButtonWaitFinished.setVisibility(View.GONE);
        } else if (mWait.getState().equals("queue-start")) {
            mButtonWaitCanStart.setVisibility(View.GONE);
            mButtonWaitFinished.setVisibility(View.VISIBLE);
        } else if (mWait.getState().equals("queue-done")) {
            mButtonWaitCanStart.setVisibility(View.GONE);
            mButtonWaitFinished.setVisibility(View.GONE);
            mCancelButton.setVisibility(View.GONE);
        }
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.refreshCurrentWait(mWait);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListenerCurrentWaitWaiter) {
            mListener = (OnFragmentInteractionListenerCurrentWaitWaiter) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_wait_can_start:
                updateWait(true);
                break;
            case R.id.btn_wait_finished:
                updateWait(false);
                break;
            case R.id.btn_cancel_this_wait:
                Toast.makeText(getContext(), "Cancel wait clicked.", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void updateWait(boolean startWait) {
        Call<ResponseWait> call;
        if (startWait) {
            call = waiterClient.queueStart(mWait.getId(), MainActivity.getUserId());;
        } else {
            call = waiterClient.queueDone(mWait.getId(), MainActivity.getUserId());;
        }
        call.enqueue(new Callback<ResponseWait>() {
            @Override
            public void onResponse(@NonNull Call<ResponseWait> call, @NonNull Response<ResponseWait> response) {
                if (response.isSuccessful()) {
                    ResponseWait body = response.body();
                    if (body != null) {
                        mWait = body.getData().getWait();
                        refreshUI();
                        mListener.refreshCurrentWait(mWait);
                    } else {
                        Snackbar.make(mView, getString(R.string.response_body_null), Snackbar.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseWait> call, @NonNull Throwable t) {
                Snackbar.make(mView, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListenerCurrentWaitWaiter {
        void refreshCurrentWait(Wait wait);
    }
}
