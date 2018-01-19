package com.waiter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
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

import com.waiter.models.ErrorResponse;
import com.waiter.models.RequestRating;
import com.waiter.models.ResponseRating;
import com.waiter.network.ServiceGenerator;
import com.waiter.network.WaiterClient;
import com.squareup.picasso.Picasso;
import com.waiter.models.User;
import com.waiter.utils.ErrorUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.waiter.HistoryActivity.mHistoryList;

public class HistoryDetailActivity extends AppCompatActivity implements View.OnClickListener, RatingBar.OnRatingBarChangeListener {

    private static final String TAG = "HistoryDetailActivity";

    private static final int PERMISSIONS_REQUEST_STORAGE = 19;

    private boolean mPermissionDenied = true;

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

    private void checkStoragePermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_STORAGE);
            } else {
                Toast.makeText(this, "Sorry, you need to updated your version of Android :(", Toast.LENGTH_LONG).show();
            }
        } else {
            downloadInvoice();
        }
    }

    private void downloadInvoice() {
        Call<ResponseBody> call = waiterClient.getBilling(mHistoryList.get(historyPosition).getId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    ResponseBody body = response.body();
                    if (body != null) {
                        boolean writtenToDisk = writeResponseBodyToDisk(body);
                        Log.d(TAG, "file download was a success? " + writtenToDisk);
                        if (writtenToDisk) {
                            openInvoicePDF();
                        } else {
                            Snackbar.make(findViewById(android.R.id.content), "Download failed.", Snackbar.LENGTH_LONG).show();
                        }
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
            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                Toast.makeText(HistoryDetailActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openInvoicePDF() {
        String invoiceFilename = "Invoice_Wait_" + mHistoryList.get(historyPosition).getId() + ".pdf";
        File file = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                invoiceFilename);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Log.d(TAG, "openInvoicePDF: " + getApplicationContext().getPackageName());
        Uri invoiceURI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".custom.GenericFileProvider", file);
        intent.setDataAndType(invoiceURI, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkStoragePermission();
                } else {
                    Toast.makeText(this, "Storage permission denied.", Toast.LENGTH_LONG).show();
                    mPermissionDenied = true;
                }
            }
        }
    }


    @Override
    public void onClick(View v) {
        if (v == btnDownloadInvoice) {
            if (!mPermissionDenied) {
                downloadInvoice();
            } else {
                checkStoragePermission();
            }
        }
    }

    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            String invoiceFilename = "Invoice_Wait_" + mHistoryList.get(historyPosition).getId() + ".pdf";
            File file = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    invoiceFilename);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d(TAG, "File download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                Log.d(TAG, "writeResponseBodyToDisk: IOException: " + e.getLocalizedMessage());
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            Log.d(TAG, "writeResponseBodyToDisk: IOException: " + e.getLocalizedMessage());
            return false;
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
                Toast.makeText(HistoryDetailActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
