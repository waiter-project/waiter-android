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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.waiter.models.ErrorResponse;
import com.waiter.models.ResponseGenerateCode;
import com.waiter.models.Wait;
import com.waiter.network.ServiceGenerator;
import com.waiter.network.WaiterClient;
import com.waiter.utils.ErrorUtils;

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


public class CurrentWaitClientFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_CURRENT_WAIT = "CURRENT_WAIT";
    private static final String TAG = "CurrentWaitClientFragme";

    private Wait mWait;
    private String mCode;

    private OnFragmentInteractionListener mListener;

    private WaiterClient waiterClient;
    private ErrorResponse errorResponse;

    // UI Elements
    private View mView;
    private TextView mWaitTitle, mWaitDescription, mEventAddress, mWaitUpdate, mWaitersState, mValidationCode;
    private LinearLayout mValidationLayout;
    private Button mCancelButton, mGenerateCodeButton;
    private ProgressBar mProgressBar;

    public CurrentWaitClientFragment() {
        // Required empty public constructor
    }

    public static CurrentWaitClientFragment newInstance(Wait wait) {
        CurrentWaitClientFragment fragment = new CurrentWaitClientFragment();
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
        mView = inflater.inflate(R.layout.fragment_current_wait_client, container, false);
        waiterClient = ServiceGenerator.createService(WaiterClient.class);
        setupUI();
        refreshUI();
        return mView;
    }

    private void setupUI() {
        mWaitTitle = (TextView) mView.findViewById(R.id.wait_title);
        mWaitDescription = (TextView) mView.findViewById(R.id.wait_description);
        mEventAddress = (TextView) mView.findViewById(R.id.event_address);
        mWaitUpdate = (TextView) mView.findViewById(R.id.wait_update);
        mWaitersState = (TextView) mView.findViewById(R.id.waiters_state);
        mCancelButton = (Button) mView.findViewById(R.id.btn_cancel_this_wait);
        mCancelButton.setOnClickListener(this);
        mGenerateCodeButton = (Button) mView.findViewById(R.id.btn_generate_code);
        mGenerateCodeButton.setOnClickListener(this);
        mValidationCode = (TextView) mView.findViewById(R.id.validation_code);
        mProgressBar = (ProgressBar) mView.findViewById(R.id.progress_bar);
        mValidationLayout = (LinearLayout) mView.findViewById(R.id.validation_layout);
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

        switch (mWait.getState()) {
            case "created":
                mCancelButton.setVisibility(View.VISIBLE);
                mValidationLayout.setVisibility(View.GONE);
                break;
            case "queue-start":
                mCancelButton.setVisibility(View.VISIBLE);
                mValidationLayout.setVisibility(View.GONE);
                break;
            case "queue-done":
                mCancelButton.setVisibility(View.GONE);
                mValidationLayout.setVisibility(View.VISIBLE);
                if (mCode != null) {
                    mValidationCode.setText(mCode);
                    mProgressBar.setVisibility(View.GONE);
                    mValidationCode.setVisibility(View.VISIBLE);
                }
                break;
            case "paid":
                mCancelButton.setVisibility(View.GONE);
                mValidationLayout.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel_this_wait:
                Toast.makeText(getContext(), "Cancel button clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_generate_code:
                generateCode();
                break;
        }
    }

    private void generateCode() {
        mValidationCode.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        Call<ResponseGenerateCode> call = waiterClient.generateCode(mWait.getId(), MainActivity.getUserId());;
        call.enqueue(new Callback<ResponseGenerateCode>() {
            @Override
            public void onResponse(@NonNull Call<ResponseGenerateCode> call, @NonNull Response<ResponseGenerateCode> response) {
                if (response.isSuccessful()) {
                    ResponseGenerateCode body = response.body();
                    if (body != null) {
                        mCode = body.getData().getCode();
                        Log.d(TAG, "onResponse: mCode = " + mCode);
                        refreshUI();
//                        mListener.refreshCurrentWait(mWait);
                    } else {
                        mProgressBar.setVisibility(View.GONE);
                        Snackbar.make(mView, getString(R.string.response_body_null), Snackbar.LENGTH_LONG).show();
                    }
                }  else {
                    errorResponse = ErrorUtils.parseError(response);
                    if (errorResponse != null && errorResponse.getData() != null) {
                        if (errorResponse.getData().getCauses() == null || errorResponse.getData().getCauses().isEmpty()) {
                            Snackbar.make(mView, errorResponse.getData().getMessage(), Snackbar.LENGTH_LONG).show();
                        } else {
                            Snackbar.make(mView, errorResponse.getData().getCauses().get(0), Snackbar.LENGTH_LONG).show();
                        }
                    } else {
                        Snackbar.make(mView, getString(R.string.internal_error), Snackbar.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseGenerateCode> call, @NonNull Throwable t) {
                mProgressBar.setVisibility(View.GONE);
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
