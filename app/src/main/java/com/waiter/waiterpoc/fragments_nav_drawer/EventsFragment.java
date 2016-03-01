package com.waiter.waiterpoc.fragments_nav_drawer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.waiter.waiterpoc.FullEventActivity;
import com.waiter.waiterpoc.OldFullEventActivity;
import com.waiter.waiterpoc.MainActivity;
import com.waiter.waiterpoc.R;
import com.waiter.waiterpoc.models.Event;
import com.waiter.waiterpoc.models.GenericResponseArray;
import com.waiter.waiterpoc.network.CheckNetwork;
import com.waiter.waiterpoc.network.ServiceGenerator;
import com.waiter.waiterpoc.network.WaiterService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private static final String LOG_TAG = "EventsFragment";

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

    public EventsFragment() {
        // Required empty public constructor
    }

    public static EventsFragment newInstance() {
        EventsFragment fragment = new EventsFragment();
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
        View view = inflater.inflate(R.layout.fragment_events, container, false);

        // Set options menu
        setHasOptionsMenu(true);

        // UI instantiation
        mErrorText = (TextView) view.findViewById(R.id.errorText);
        mErrorImage = (ImageView) view.findViewById(R.id.errorImage);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mListView = (ListView) view.findViewById(R.id.listView);
        mSwipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(getActivity(), FullEventActivity.class);
                myIntent.putExtra("eventId", eventsList.get(position).getId());
                getActivity().startActivity(myIntent);
                Log.d(LOG_TAG, "Event cliked! FullEvent launched. Int Position: " + position);
                Log.d(LOG_TAG, "Event cliked! FullEvent launched. Long ID: " + id);
            }
        });

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
        if (!CheckNetwork.isInternetAvailable(MainActivity.getsContext())) {
            //Toast.makeText(LoginActivity.this, "No internet connection", Toast.LENGTH_LONG).show();
            //Snackbar.make((View) findViewById(R.id.root_view), "No internet connection", Snackbar.LENGTH_LONG).show();
            onEventsFailed(getString(R.string.no_internet));
            return;
        }

        // API connection and get events from server
        service = ServiceGenerator.createService(WaiterService.class);

        Call<GenericResponseArray<Event>> call = service.getEventsList();

        call.enqueue(new Callback<GenericResponseArray<Event>>() {
            @Override
            public void onResponse(Call<GenericResponseArray<Event>> call, Response<GenericResponseArray<Event>> response) {
                if (response.isSuccess()) {
                    Log.d(LOG_TAG, "Success! Return: " + response.message() + " - Raw: " + response.raw().toString());

                    eventsList = response.body().getData();

                    // Check eventsList
                    if (eventsList != null) {
                        adapter = new EventsAdapter(MainActivity.getsContext(), eventsList);
                        mListView.setAdapter(adapter);
                        onEventsSuccess();
                    } else {
                        onEventsFailed(getString(R.string.no_events));
                    }

                } else {
                    Log.d(LOG_TAG, "Failure! Return: " + response.message() + " - Raw: " + response.raw().toString());
                    onEventsFailed(getString(R.string.unknown_error_short));
                }
            }

            @Override
            public void onFailure(Call<GenericResponseArray<Event>> call, Throwable t) {
                Log.d(LOG_TAG, "Error: " + t.getMessage());
                onEventsFailed(getString(R.string.unknown_error_short));
            }
        });
    }

    private void onEventsSuccess() {
        mSwipeRefresh.setRefreshing(false);
        if (adapter != null) {
            mListView.setVisibility(View.VISIBLE);
        }
        mErrorText.setVisibility(View.INVISIBLE);
        mErrorImage.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    private void onEventsFailed(String errorMessage) {
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.events, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(LOG_TAG, "onOptionsItemSelected (menu item)");

        switch (item.getItemId()) {

            // Check if user triggered a refresh:
            case R.id.menu_refresh:
                Log.i(LOG_TAG, "Refresh menu item selected");

                // Signal SwipeRefreshLayout to start the progress indicator
                mSwipeRefresh.setRefreshing(true);

                // Start the refresh background task.
                // This method calls setRefreshing(false) when it's finished.
                refreshList();

                return true;
        }

        // User didn't trigger a refresh, let the superclass handle this action
        return super.onOptionsItemSelected(item);
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
            mListener.onFragmentInteractionEvents(Uri.parse("doWhatYouWant"));
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
        void onFragmentInteractionEvents(Uri uri);
    }
}
