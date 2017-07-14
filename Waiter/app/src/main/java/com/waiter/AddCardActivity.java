package com.waiter;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.securepreferences.SecurePreferences;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;
import com.waiter.models.ErrorResponse;
import com.waiter.network.ServiceGenerator;
import com.waiter.network.WaiterClient;
import com.waiter.utils.ErrorUtils;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCardActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AddCardActivity";

    // UI elements
    private CardInputWidget mCardInputWidget;
    private Button mButtonAddCard;
    private ProgressDialog mProgressDialog;

    private String authToken;

    private WaiterClient waiterClient;
    private ErrorResponse errorResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        waiterClient = ServiceGenerator.createService(WaiterClient.class);

        mCardInputWidget = (CardInputWidget) findViewById(R.id.card_input_widget);
        mButtonAddCard = (Button) findViewById(R.id.btn_add_card);
        mButtonAddCard.setOnClickListener(this);
        mProgressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.saving_card));

        SharedPreferences prefs = new SecurePreferences(this);
        authToken = prefs.getString("auth_token", "");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_card:
                addCard();
                break;
        }
    }

    private void addCard() {
        Card cardToSave = mCardInputWidget.getCard();
        Log.d(TAG, "addCard: card = " + cardToSave);
        if (cardToSave == null) {
            Snackbar.make(findViewById(android.R.id.content), getString(R.string.invalid_card_data), Snackbar.LENGTH_SHORT).show();
        } else {
            mProgressDialog.show();
            Stripe stripe = new Stripe(this, "pk_test_hSipBuKxXUGrnuu2XpqrNpm4");
            stripe.createToken(
                    cardToSave,
                    new TokenCallback() {
                        public void onSuccess(Token token) {
                            // Send token to your server
                            sendCardToken(token);
                        }
                        public void onError(Exception error) {
                            // Show localized error message
                            mProgressDialog.dismiss();
                            Snackbar.make(findViewById(android.R.id.content), error.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    }
            );
        }
    }

    private void sendCardToken(Token token) {
        Call<ResponseBody> call = waiterClient.addNewCard(authToken, MainActivity.getUserId(), token.getId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                mProgressDialog.dismiss();
                if (response.isSuccessful()) {
                    ResponseBody body = response.body();
                    if (body != null) {
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        setResult(RESULT_CANCELED);
                        Snackbar.make(findViewById(android.R.id.content), getString(R.string.response_body_null), Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    setResult(RESULT_CANCELED);
                    errorResponse = ErrorUtils.parseError(response);
                    if (errorResponse != null && errorResponse.getData() != null) {
                        if (errorResponse.getData().getCauses() == null || errorResponse.getData().getCauses().isEmpty()) {
                            Snackbar.make(findViewById(android.R.id.content), errorResponse.getData().getMessage(), Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(findViewById(android.R.id.content), errorResponse.getData().getCauses().get(0), Snackbar.LENGTH_SHORT).show();
                        }
                    } else {
                        Snackbar.make(findViewById(android.R.id.content), getString(R.string.internal_error), Snackbar.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                mProgressDialog.dismiss();
                setResult(RESULT_CANCELED);
                Snackbar.make(findViewById(android.R.id.content), t.getLocalizedMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}
