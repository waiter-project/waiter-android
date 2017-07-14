package com.waiter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

public class AddPaymentActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD_CARD = 84;

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
        Intent intent = new Intent(this, AddCardActivity.class);
        startActivityForResult(intent, REQUEST_CODE_ADD_CARD);
    }

    public void onClickAddAndroidPay(View view) {
        Toast.makeText(this, "Add Android Pay", Toast.LENGTH_SHORT).show();
    }

    public void onClickAddPaypal(View view) {
        Toast.makeText(this, "Add PayPal", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_CARD) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK);
                finish();
            }
        }
    }
}
