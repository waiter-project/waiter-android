package com.waiter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

public class AddPaymentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void onClickAddCreditDebitCard(View view) {
        Toast.makeText(this, "Add Credit Debit Card", Toast.LENGTH_SHORT).show();
    }

    public void onClickAddAndroidPay(View view) {
        Toast.makeText(this, "Add Android Pay", Toast.LENGTH_SHORT).show();
    }

    public void onClickAddPaypal(View view) {
        Toast.makeText(this, "Add PayPal", Toast.LENGTH_SHORT).show();
    }
}
