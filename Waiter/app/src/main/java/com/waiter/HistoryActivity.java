package com.waiter;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.waiter.models.History;
import com.waiter.models.User;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    private static final String TAG = "HistoryActivity";

    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    private HistoryRecyclerViewAdapter mAdapter;

    public static ArrayList<History> mHistoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mHistoryList = new ArrayList<>();
        mHistoryList.add(new History("3h 45min", "$33.45", MainActivity.mEventList.get(0), new User(), new ArrayList<User>()));
        mHistoryList.add(new History("3h 45min", "$33.45", MainActivity.mEventList.get(0), new User(), new ArrayList<User>()));
        mHistoryList.add(new History("3h 45min", "$33.45", MainActivity.mEventList.get(0), new User(), new ArrayList<User>()));
        mHistoryList.add(new History("3h 45min", "$33.45", MainActivity.mEventList.get(0), new User(), new ArrayList<User>()));

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), mLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        // Set the adapter
        if (recyclerView != null) {
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(this, mColumnCount));
            }

            mListener = new OnListFragmentInteractionListener();
            mAdapter = new HistoryRecyclerViewAdapter(mHistoryList, mListener);

            recyclerView.setAdapter(mAdapter);
        }

        final SwipeRefreshLayout swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.refreshList();
                swipeContainer.setRefreshing(false);
            }
        });

    }

    public class OnListFragmentInteractionListener {

        public void onListFragmentInteractionHistory(History history) {
            Log.d(TAG, "onListFragmentInteractionEvent: history = " + history.toString());
            Toast.makeText(HistoryActivity.this, history.getEvent().getName() + " clicked.", Toast.LENGTH_SHORT).show();
        }

    }
}
