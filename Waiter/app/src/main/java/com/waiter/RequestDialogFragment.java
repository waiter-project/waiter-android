package com.waiter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.travijuu.numberpicker.library.Enums.ActionEnum;
import com.travijuu.numberpicker.library.Interface.ValueChangedListener;
import com.travijuu.numberpicker.library.NumberPicker;
import com.waiter.models.ErrorResponse;
import com.waiter.models.RequestCreateWait;
import com.waiter.models.Wait;
import com.waiter.network.ServiceGenerator;
import com.waiter.network.WaiterClient;
import com.waiter.utils.ErrorUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestDialogFragment extends AppCompatDialogFragment implements View.OnClickListener, ValueChangedListener {

    public interface RequestDialogListener {
        void onDialogPositiveClick(AppCompatDialogFragment dialog, int value);
        void onDialogNegativeClick(AppCompatDialogFragment dialog);
//        void onCreateWaitResponse(Response<Wait> response);
//        void onCreateWaitFailure(Throwable t);
        void showSnackbarMessage(String message);
    }

    RequestDialogListener mListener;

    String eventId;
    private WaiterClient waiterClient;
    private RequestCreateWait requestCreateWait;
    private ErrorResponse errorResponse;

    // UI References
    private LinearLayout mRequestFormLayout;
    private LinearLayout mRequestingLayout;
    private NumberPicker mNumberPicker;
    private Button mRequestButton;
    private TextView mRequestingTextView;
    private ProgressDialog mProgressDialog;

    private Context sContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (RequestDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement RequestDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogRootView = inflater.inflate(R.layout.dialog_request, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogRootView);

        mRequestFormLayout = (LinearLayout) dialogRootView.findViewById(R.id.request_form_layout);
        mRequestingLayout = (LinearLayout) dialogRootView.findViewById(R.id.requesting_layout);

        mNumberPicker = (NumberPicker) dialogRootView.findViewById(R.id.number_picker);
        mNumberPicker.setValueChangedListener(this);
        mRequestButton = (Button) dialogRootView.findViewById(R.id.request_btn);
        mRequestButton.setOnClickListener(this);

        mRequestingTextView = (TextView) dialogRootView.findViewById(R.id.requesting_text_view);

        waiterClient = ServiceGenerator.createService(WaiterClient.class);
        requestCreateWait = new RequestCreateWait();

        sContext = getContext();

        return builder.create();
    }

    @Override
    public void valueChanged(int value, ActionEnum action) {
        if (value > 1) {
            mRequestButton.setText(getString(R.string.request_waiters, mNumberPicker.getValue()));
        } else {
            mRequestButton.setText(getString(R.string.request_one_waiter));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.request_btn:
                requestWaiters();
                break;
        }
    }

    private void requestWaiters() {
        showProgressLayout();

        requestCreateWait.setEventId(eventId);
        requestCreateWait.setUserId(MainActivity.getUserId());
        requestCreateWait.setNumberOfWaiters(mNumberPicker.getValue());

        Call<Wait> call = waiterClient.createWait(requestCreateWait);
        call.enqueue(new Callback<Wait>() {
            @Override
            public void onResponse(@NonNull Call<Wait> call, @NonNull Response<Wait> response) {
                hideProgressLayout();
                mListener.onDialogPositiveClick(RequestDialogFragment.this, mNumberPicker.getValue());
                if (response.isSuccessful()) {
                    Wait body = response.body();
                    if (body != null) {
                        mListener.showSnackbarMessage(sContext.getString(R.string.request_sent));
                    } else {
                        mListener.showSnackbarMessage(sContext.getString(R.string.response_body_null));
                    }
                } else {
                    errorResponse = ErrorUtils.parseError(response);
                    if (errorResponse != null && errorResponse.getData() != null) {
                        if (errorResponse.getData().getCauses() == null || errorResponse.getData().getCauses().isEmpty()) {
                            mListener.showSnackbarMessage(errorResponse.getData().getMessage());
                        } else {
                            mListener.showSnackbarMessage(errorResponse.getData().getCauses().get(0));
                        }
                    } else {
                        mListener.showSnackbarMessage(sContext.getString(R.string.internal_error));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Wait> call, @NonNull Throwable t) {
                hideProgressLayout();
//                mListener.onDialogPositiveClick(RequestDialogFragment.this, mNumberPicker.getValue());
                mListener.showSnackbarMessage(t.getLocalizedMessage());
            }
        });

    }

    private void showProgressLayout() {
        setCancelable(false);
        mRequestingTextView.setText(getString(R.string.requesting_waiters, mNumberPicker.getValue()));
        mRequestFormLayout.setVisibility(View.GONE);
        mRequestingLayout.setVisibility(View.VISIBLE);
    }

    private void hideProgressLayout() {
        setCancelable(true);
        mRequestFormLayout.setVisibility(View.VISIBLE);
        mRequestingLayout.setVisibility(View.GONE);
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setProgressDialog(ProgressDialog mProgressDialog) {
        this.mProgressDialog = mProgressDialog;
    }
}
