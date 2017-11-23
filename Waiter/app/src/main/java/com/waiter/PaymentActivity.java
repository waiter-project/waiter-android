package com.waiter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.securepreferences.SecurePreferences;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.waiter.models.ErrorResponse;
import com.waiter.models.ResponseCards;
import com.waiter.network.ServiceGenerator;
import com.waiter.network.WaiterClient;
import com.waiter.utils.ErrorUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PaymentActivity";

    private int mColumnCount = 1;
    private static final int REQUEST_CODE_ADD_PAYMENT = 21;

    private OnListFragmentInteractionListener mListener;

    private PaymentRecyclerViewAdapter mAdapter;

    public List<Card> mPaymentList = new ArrayList<>();

    private String authToken;

    private WaiterClient waiterClient;
    private ErrorResponse errorResponse;

    // UI elements
    private TextView mNoPaymentMethods;
    private Button mButtonAddPaymentMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        SharedPreferences prefs = new SecurePreferences(this);
        authToken = prefs.getString("auth_token", "");

        mNoPaymentMethods = (TextView) findViewById(R.id.no_payment_methods);
        mButtonAddPaymentMethod = (Button) findViewById(R.id.btn_add_payment_method);
        mButtonAddPaymentMethod.setOnClickListener(this);

        waiterClient = ServiceGenerator.createService(WaiterClient.class);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);

        // Set the adapter
        if (recyclerView != null) {
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(this, mColumnCount));
            }

            mListener = new OnListFragmentInteractionListener();
            mAdapter = new PaymentRecyclerViewAdapter(mPaymentList, mListener);

            recyclerView.setAdapter(mAdapter);
        }

        loadCards();
    }

    private void loadCards() {
        Call<ResponseCards> call = waiterClient.getCards(authToken, MainActivity.getUserId());

        call.enqueue(new Callback<ResponseCards>() {
            @Override
            public void onResponse(@NonNull Call<ResponseCards> call, @NonNull Response<ResponseCards> response) {
                if (response.isSuccessful()) {
                    ResponseCards body = response.body();
                    if (body != null) {
                        makeCardsFromToken(body.getData().getCards());
                    } else {
                        Toast.makeText(PaymentActivity.this, getString(R.string.response_body_null), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    errorResponse = ErrorUtils.parseError(response);
                    if (errorResponse != null && errorResponse.getData() != null) {
                        if (errorResponse.getData().getCauses() == null || errorResponse.getData().getCauses().isEmpty()) {
                            Toast.makeText(PaymentActivity.this, errorResponse.getData().getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PaymentActivity.this, errorResponse.getData().getCauses().get(0), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(PaymentActivity.this, getString(R.string.internal_error), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseCards> call, @NonNull Throwable t) {
                Toast.makeText(PaymentActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void makeCardsFromToken(List<String> cards) {
        mPaymentList.clear();
        for (String token: cards) {
            mPaymentList.add(new Card("4242 4242 4242 4242", 12, 2014, "543", "John Doe", "63 St Name", "", "Berkeley", "CA", "94270", "US", "VISA", "4242", "fingerprint", "funding", "US", "US Dollars", "ID"));
            Log.d(TAG, "makeCardsFromToken: lol");
        }
        if (mPaymentList.size() == 0) {
            mNoPaymentMethods.setVisibility(View.VISIBLE);
        } else {
            mNoPaymentMethods.setVisibility(View.GONE);
        }
//        mAdapter.refreshList(mPaymentList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_payment_method:
                Intent intent = new Intent(this, AddPaymentActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD_PAYMENT);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_PAYMENT) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK);
                loadCards();
                Snackbar.make(findViewById(android.R.id.content), getString(R.string.card_successfully_added), Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    public class OnListFragmentInteractionListener {

        public void onListFragmentInteractionPayment(Card mItem) {
            Toast.makeText(PaymentActivity.this, mItem.getLast4() + " clicked", Toast.LENGTH_SHORT).show();
        }

    }
}
