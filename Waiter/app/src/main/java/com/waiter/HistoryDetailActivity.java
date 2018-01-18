package com.waiter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.waiter.models.User;

public class HistoryDetailActivity extends AppCompatActivity implements View.OnClickListener {

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
        int historyPosition = intent.getIntExtra("HISTORY_POSITION", -1);

        toolbar.setTitle(HistoryActivity.mHistoryList.get(historyPosition).getEvent().getName());

        eventName = (TextView)findViewById( R.id.event_name );
        eventAddress = (TextView)findViewById( R.id.event_address );
        staticMaps = (ImageView)findViewById( R.id.static_maps );
        waitPrice = (TextView)findViewById( R.id.wait_price );
        waitDate = (TextView)findViewById( R.id.wait_date );
        waitersState = (TextView)findViewById( R.id.waiters_state );
        ratingBar = (RatingBar)findViewById( R.id.ratingBar );
        btnDownloadInvoice = (Button)findViewById( R.id.btn_download_invoice );
        btnDownloadInvoice.setOnClickListener(this);

        eventName.setText(HistoryActivity.mHistoryList.get(historyPosition).getEvent().getName());
        eventAddress.setText(HistoryActivity.mHistoryList.get(historyPosition).getEvent().getAddress());

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
                + HistoryActivity.mHistoryList.get(historyPosition).getEvent().getLocation().get(1)
                + ","
                + HistoryActivity.mHistoryList.get(historyPosition).getEvent().getLocation().get(0);

        Picasso.with(this).load(staticMapUrl).fit().centerCrop().into(staticMaps);

        waitPrice.setText(HistoryActivity.mHistoryList.get(historyPosition).getPrice().getTotal().toString());
        waitDate.setText(HistoryActivity.mHistoryList.get(historyPosition).getWait().getQueueEnd());

        StringBuilder waitersNames = new StringBuilder();
        for (User waiter: HistoryActivity.mHistoryList.get(historyPosition).getWaiters()) {
            waitersNames.append(waiter.getFirstName()).append(" ");
        }
        waitersState.setText(waitersNames);
//        ratingBar.setText(HistoryActivity.mHistoryList.get(historyPosition).getEvent().getName());
    }

    @Override
    public void onClick(View v) {
        if (v == btnDownloadInvoice) {
            Toast.makeText(this, "Download invoice clicked", Toast.LENGTH_SHORT).show();
        }
    }

}
