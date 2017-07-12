package com.waiter;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.waiter.models.Wait;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class CurrentWaitClientFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_CURRENT_WAIT = "CURRENT_WAIT";
    private static final String TAG = "CurrentWaitClientFragme";

    private Wait mWait;

    private OnFragmentInteractionListener mListener;

    // UI Elements
    private TextView mWaitTitle, mWaitDescription, mEventAddress, mWaitUpdate, mWaitersState;
    private Button mCancelButton;

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
        View view = inflater.inflate(R.layout.fragment_current_wait_client, container, false);
        setupUI(view);
        refreshUI();
        return view;
    }

    private void setupUI(View view) {
        mWaitTitle = (TextView) view.findViewById(R.id.wait_title);
        mWaitDescription = (TextView) view.findViewById(R.id.wait_description);
        mEventAddress = (TextView) view.findViewById(R.id.event_address);
        mWaitUpdate = (TextView) view.findViewById(R.id.wait_update);
        mWaitersState = (TextView) view.findViewById(R.id.waiters_state);
        mCancelButton = (Button) view.findViewById(R.id.btn_cancel_this_wait);
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
        if (mWait.getState().equals("queue-done")) {
            mCancelButton.setVisibility(View.GONE);
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
        }
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
