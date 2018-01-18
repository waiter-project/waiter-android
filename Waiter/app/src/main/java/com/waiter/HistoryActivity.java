package com.waiter;

import android.content.Intent;
import android.support.annotation.NonNull;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.waiter.models.ErrorResponse;
import com.waiter.models.History;
import com.waiter.models.ResponseHistory;
import com.waiter.network.ServiceGenerator;
import com.waiter.network.WaiterClient;
import com.waiter.utils.ErrorUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryActivity extends AppCompatActivity {

    private static final String TAG = "HistoryActivity";

    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    private HistoryRecyclerViewAdapter mAdapter;

    private LinearLayout mRootView;
    private SwipeRefreshLayout mSwipeContainer;

    public static List<History> mHistoryList = new ArrayList<>();

    private WaiterClient waiterClient;
    private ErrorResponse errorResponse;

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

        mRootView = (LinearLayout) findViewById(R.id.root_view);

        waiterClient = ServiceGenerator.createService(WaiterClient.class);

        loadHistory();
//        mHistoryList.add(new History("3h 45min", "$33.45", MainActivity.mEventList.get(0), new User(), new ArrayList<User>()));
//        mHistoryList.add(new History("3h 45min", "$33.45", MainActivity.mEventList.get(0), new User(), new ArrayList<User>()));
//        mHistoryList.add(new History("3h 45min", "$33.45", MainActivity.mEventList.get(0), new User(), new ArrayList<User>()));
//        mHistoryList.add(new History("3h 45min", "$33.45", MainActivity.mEventList.get(0), new User(), new ArrayList<User>()));

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

        mSwipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadHistory();
            }
        });

    }

    private ArrayList<History> loadHistory() {
        String userType = MainActivity.waiterMode ? "waiter" : "client";
        Call<ResponseHistory> call = waiterClient.getHistory(userType, MainActivity.getUserId());

        call.enqueue(new Callback<ResponseHistory>() {
            @Override
            public void onResponse(@NonNull Call<ResponseHistory> call, @NonNull Response<ResponseHistory> response) {
                if (response.isSuccessful()) {
                    ResponseHistory body = response.body();
                    if (body != null) {
                        mHistoryList = body.getData().getHistories();
                        if (mHistoryList.size() == 0) {
                            Toast.makeText(HistoryActivity.this, "You haven't done any wait yet", Toast.LENGTH_SHORT).show();
                        }
                        mAdapter.refreshList(mHistoryList);
                    } else {
                        Toast.makeText(HistoryActivity.this, getString(R.string.response_body_null), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    errorResponse = ErrorUtils.parseError(response);
                    if (errorResponse != null && errorResponse.getData() != null) {
                        if (errorResponse.getData().getCauses() == null || errorResponse.getData().getCauses().isEmpty()) {
                            Toast.makeText(HistoryActivity.this, errorResponse.getData().getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(HistoryActivity.this, errorResponse.getData().getCauses().get(0), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(HistoryActivity.this, getString(R.string.internal_error), Toast.LENGTH_SHORT).show();
                    }
                }
                mSwipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseHistory> call, @NonNull Throwable t) {
                Toast.makeText(HistoryActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                mSwipeContainer.setRefreshing(false);
            }
        });

        return new ArrayList<>();
    }

    public class OnListFragmentInteractionListener {

        public void onListFragmentInteractionHistory(int position) {
            Intent intent = new Intent(HistoryActivity.this, HistoryDetailActivity.class);
//            intent.putExtra("EVENT_POSITION", position);
            intent.putExtra("HISTORY_POSITION", position);
            startActivity(intent);
//            Toast.makeText(HistoryActivity.this, history.getEvent().getName() + " clicked.", Toast.LENGTH_SHORT).show();
        }

    }
}
