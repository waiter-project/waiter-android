package com.waiter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.waiter.models.ErrorResponse;
import com.waiter.models.RequestRating;
import com.waiter.models.ResponseRating;
import com.waiter.network.ServiceGenerator;
import com.waiter.network.WaiterClient;
import com.squareup.picasso.Picasso;
import com.waiter.models.User;
import com.waiter.utils.ErrorUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.waiter.HistoryActivity.mHistoryList;

public class HistoryDetailActivity extends AppCompatActivity implements View.OnClickListener, RatingBar.OnRatingBarChangeListener {

    private static final String TAG = "HistoryDetailActivity";

    private int historyPosition;

    private TextView eventName;
    private TextView eventAddress;
    private ImageView staticMaps;
    private TextView waitPrice;
    private TextView waitDate;
    private TextView waitersState;
    private RatingBar ratingBar;
    private Button btnDownloadInvoice;

    private WaiterClient waiterClient;
    private ErrorResponse errorResponse;
    private RequestRating requestRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        historyPosition = intent.getIntExtra("HISTORY_POSITION", -1);

        toolbar.setTitle(mHistoryList.get(historyPosition).getEvent().getName());

        eventName = (TextView)findViewById( R.id.event_name );
        eventAddress = (TextView)findViewById( R.id.event_address );
        staticMaps = (ImageView)findViewById( R.id.static_maps );
        waitPrice = (TextView)findViewById( R.id.wait_price );
        waitDate = (TextView)findViewById( R.id.wait_date );
        waitersState = (TextView)findViewById( R.id.waiters_state );
        ratingBar = (RatingBar)findViewById( R.id.ratingBar );
        btnDownloadInvoice = (Button)findViewById( R.id.btn_download_invoice );
        btnDownloadInvoice.setOnClickListener(this);

        eventName.setText(mHistoryList.get(historyPosition).getEvent().getName());
        eventAddress.setText(mHistoryList.get(historyPosition).getEvent().getAddress());

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = (int) Utils.convertDpToPixel(150, this);
        String staticMapUrl = "http://maps.google.com/maps/api/staticmap"
                + "?size="
                + width
                + "x"
                + height
                + "&zoom=15&markers=size:mid|"
                + mHistoryList.get(historyPosition).getEvent().getLocation().get(1)
                + ","
                + mHistoryList.get(historyPosition).getEvent().getLocation().get(0);

        Picasso.with(this).load(staticMapUrl).fit().centerCrop().into(staticMaps);

        waitPrice.setText(mHistoryList.get(historyPosition).getPrice().getTotal().toString());
        waitDate.setText(mHistoryList.get(historyPosition).getWait().getQueueEnd());

        StringBuilder waitersNames = new StringBuilder();
        for (User waiter: mHistoryList.get(historyPosition).getWaiters()) {
            waitersNames.append(waiter.getFirstName()).append(" ");
        }
        waitersState.setText(waitersNames);
        ratingBar.setRating(mHistoryList.get(historyPosition).getNotation().getNotation());
        ratingBar.setOnRatingBarChangeListener(this);

        requestRating = new RequestRating();

        waiterClient = ServiceGenerator.createService(WaiterClient.class);

    }

    @Override
    public void onClick(View v) {
        if (v == btnDownloadInvoice) {
            Toast.makeText(this, "Download invoice clicked", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, final float rating, boolean fromUser) {
        requestRating.setNotation((int) rating);
        Call<ResponseRating> call = waiterClient.rateHistory(mHistoryList.get(historyPosition).getId(), requestRating);


        call.enqueue(new Callback<ResponseRating>() {
            @Override
            public void onResponse(@NonNull Call<ResponseRating> call, @NonNull Response<ResponseRating> response) {
                if (response.isSuccessful()) {
                    ResponseRating body = response.body();
                    if (body != null) {
                        String confirmation = rating + " stars rating sent";
                        Toast.makeText(HistoryDetailActivity.this, confirmation, Toast.LENGTH_SHORT).show();
                        mHistoryList.get(historyPosition).getNotation().setNotation((int) rating);
                    } else {
                        Toast.makeText(HistoryDetailActivity.this, getString(R.string.response_body_null), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    errorResponse = ErrorUtils.parseError(response);
                    if (errorResponse != null && errorResponse.getData() != null) {
                        if (errorResponse.getData().getCauses() == null || errorResponse.getData().getCauses().isEmpty()) {
                            Toast.makeText(HistoryDetailActivity.this, errorResponse.getData().getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(HistoryDetailActivity.this, errorResponse.getData().getCauses().get(0), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(HistoryDetailActivity.this, getString(R.string.internal_error), Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<ResponseRating> call, Throwable t) {

            }
        });
    }
}
