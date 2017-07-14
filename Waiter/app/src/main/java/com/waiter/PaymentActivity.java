package com.waiter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.stripe.android.model.Card;

import java.util.ArrayList;
import java.util.List;

public class PaymentActivity extends AppCompatActivity implements View.OnClickListener {

    private int mColumnCount = 1;
    private static final int REQUEST_CODE_ADD_PAYMENT = 21;

    private OnListFragmentInteractionListener mListener;

    private PaymentRecyclerViewAdapter mAdapter;

    public static List<Card> mPaymentList = new ArrayList<>();

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

        mNoPaymentMethods = (TextView) findViewById(R.id.no_payment_methods);
        mButtonAddPaymentMethod = (Button) findViewById(R.id.btn_add_payment_method);
        mButtonAddPaymentMethod.setOnClickListener(this);

        mPaymentList.add(new Card("4242 4242 4242 4242", 12, 2014, "543", "John Doe", "63 St Name", "", "Berkeley", "CA", "94270", "US", "VISA", "4242", "fingerprint", "funding", "US", "US Dollars", "ID"));
        mPaymentList.add(new Card("4242 4242 4242 4242", 12, 2014, "543", "John Doe", "63 St Name", "", "Berkeley", "CA", "94270", "US", "VISA", "4242", "fingerprint", "funding", "US", "US Dollars", "ID"));

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

        if (mPaymentList.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            mNoPaymentMethods.setVisibility(View.VISIBLE);
        }
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
                Toast.makeText(this, "New payment method added", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class OnListFragmentInteractionListener {

        public void onListFragmentInteractionPayment(Card mItem) {
            Toast.makeText(PaymentActivity.this, mItem.getLast4() + " clicked", Toast.LENGTH_SHORT).show();
        }

    }
}
