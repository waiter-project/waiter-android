package com.waiter.waiterpoc.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.waiter.waiterpoc.FullEventActivity;
import com.waiter.waiterpoc.R;
import com.waiter.waiterpoc.fragments_nav_drawer.EventsAdapter;
import com.waiter.waiterpoc.models.Event;
import com.waiter.waiterpoc.models.User;
import com.waiter.waiterpoc.network.CheckNetwork;
import com.waiter.waiterpoc.network.WaiterService;

import java.util.List;

public class WaitersFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private static final String LOG_TAG = "WaitersFragment";

    // UI references.
    private ImageView mErrorImage;
    private TextView mErrorText;
    private ProgressBar mProgressBar;
    private EventsAdapter adapter;
    private ListView mListView;
    private SwipeRefreshLayout mSwipeRefresh;
    private WaiterService service;

    // Initial variables
    private List<Event> eventsList;

    public WaitersFragment() {
        // Required empty public constructor
    }

    public static WaitersFragment newInstance() {
        WaitersFragment fragment = new WaitersFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_waiters, container, false);

        // UI instantiation
        mErrorText = (TextView) view.findViewById(R.id.errorText);
        mErrorImage = (ImageView) view.findViewById(R.id.errorImage);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mListView = (ListView) view.findViewById(R.id.listView);
        mSwipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);

        mProgressBar.setVisibility(View.VISIBLE);

        /*
         * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
         * performs a swipe-to-refresh gesture.
         */
        mSwipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");

                // This method performs the actual data-refresh operation.
                // The method calls setRefreshing(false) when it's finished.
                refreshList();
            }
        });

        refreshList();

        return view;
    }

    private void refreshList() {
        // Check internet connection
        if (!CheckNetwork.isInternetAvailable(FullEventActivity.getsContext())) {
            //Toast.makeText(LoginActivity.this, "No internet connection", Toast.LENGTH_LONG).show();
            //Snackbar.make((View) findViewById(R.id.root_view), "No internet connection", Snackbar.LENGTH_LONG).show();
            onWaitersFailed(getString(R.string.no_internet));
            return;
        }

        List<User> waitersList = FullEventActivity.getWaitersList();

        if (waitersList != null) {
            if (waitersList.size() > 0) {
                WaitersAdapter adapter = new WaitersAdapter(getActivity(), waitersList); // check FullEventActivity.getsContext() ou getActivity() ?
                mListView.setAdapter(adapter);
                onWaitersSuccess();
            } else {
                onWaitersFailed(getString(R.string.no_waiters));
            }
        } else {
            onWaitersFailed(getString(R.string.no_waiters));
        }

    }

    private void onWaitersSuccess() {
        mSwipeRefresh.setRefreshing(false);
        if (adapter != null) {
            mListView.setVisibility(View.VISIBLE);
        }
        mErrorText.setVisibility(View.INVISIBLE);
        mErrorImage.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    private void onWaitersFailed(String errorMessage) {
        mSwipeRefresh.setRefreshing(false);
        if (adapter != null) {
            mListView.setVisibility(View.INVISIBLE);
        }
        mErrorText.setText(errorMessage);
        mErrorText.setVisibility(View.VISIBLE);
        mErrorImage.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            mListener = (OnFragmentInteractionListener) getActivity();
            mListener.onFragmentInteractionWaiters(Uri.parse("doWhatYouWant"));
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + "must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteractionWaiters(Uri uri);
    }
}
