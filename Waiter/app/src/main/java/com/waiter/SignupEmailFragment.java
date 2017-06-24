package com.waiter;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.waiter.network.ServiceGenerator;
import com.waiter.network.WaiterClient;
import com.waiter.utils.CustomTextWatcher;

import agency.tango.materialintroscreen.SlideFragment;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupEmailFragment extends SlideFragment {

    private static final String TAG = "SignupEmailFragment";

    private EditText mInputEmail;
    private TextInputLayout mInputLayoutEmail;
    private ProgressBar mProgressBar;

    private View view;
    private boolean isAvailable;
    private String mEmailAddress = "";
    private String errorEmailMessage = "";

    private IntroActivity introActivity;

    private WaiterClient waiterClient;
    private Call<ResponseBody> call = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_signup_email, container, false);

        mInputEmail = (EditText) view.findViewById(R.id.input_email);
        mInputLayoutEmail = (TextInputLayout) view.findViewById(R.id.input_layout_email);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

        mInputEmail.addTextChangedListener(new CustomTextWatcher(mInputEmail, mInputLayoutEmail, getString(R.string.err_msg_email)));

        errorEmailMessage = getString(R.string.please_enter_email);

        introActivity = (IntroActivity)getActivity();

        waiterClient = ServiceGenerator.createService(WaiterClient.class);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        isAvailable = false;
        mInputEmail.requestFocus();
    }

    public String getEmailAddress() {
        return mEmailAddress;
    }

    public void disableInput() {
        mInputEmail.setEnabled(false);
    }

    @Override
    public int backgroundColor() {
        return R.color.colorPrimary;
    }

    @Override
    public int buttonsColor() {
        return R.color.colorPrimaryDark;
    }

    @Override
    public boolean canMoveFurther() {
        Log.d(TAG, "canMoveFurther");
        return true;
    }

    @Override
    public void onBack() {
        super.onBack();
        Log.d(TAG, "onBack");
        if (call != null) {
            call.cancel();
        }
    }

    @Override
    public void onNext() {
        super.onNext();
        Log.d(TAG, "onNext called");

        if (introActivity.isSignedUp()) {
            return ;
        }

        if (mProgressBar.getVisibility() == View.VISIBLE) {
            return ;
        }

        mEmailAddress = "";
        isAvailable = false;

        final String email = mInputEmail.getText().toString().trim();
        if (CustomTextWatcher.isValidEmail(email)) {
            Log.d(TAG, "onNext: Email is valid");

            mInputLayoutEmail.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.9f));
            mProgressBar.setVisibility(View.VISIBLE);

            call = waiterClient.checkEmailAvailable(email);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        ResponseBody body = response.body();
                        if (body != null) {
                            isAvailable = true;
                            mEmailAddress = email;
                            moveToNextPage();
                        } else {
                            introActivity.showMessage(getString(R.string.response_body_null));
                        }
                    } else {
                        if (response.code() == 409) {
                            ResponseBody errorBody = response.errorBody();
                            if (errorBody != null) {
                                introActivity.showMessage(getString(R.string.email_already_used));
                            } else {
                                introActivity.showMessage(getString(R.string.response_error_body_null));
                            }
                        } else {
                            introActivity.showMessage(getString(R.string.unknown_error));
                        }
                    }
                    mProgressBar.setVisibility(View.GONE);
                    mInputLayoutEmail.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                    Log.d(TAG, "onFailure");
                    if (call.isCanceled()) {
                        Log.d(TAG, "request was cancelled");
                    }
                    else {
                        Log.d(TAG, "other larger issue, i.e. no network connection?");
                    }
                    introActivity.showMessage(t.getLocalizedMessage());
                    mProgressBar.setVisibility(View.GONE);
                    mInputLayoutEmail.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                }
            });

        } else {
            Log.d(TAG, "onNext: Email is invalid");
            introActivity.showMessage(getString(R.string.please_enter_email));
        }
    }

    @Override
    public boolean asyncTaskDone() {
        Log.d(TAG, "asyncTaskDone isAvailable = " + isAvailable);
        return introActivity.isSignedUp() || isAvailable;
    }

    @Override
    public String cantMoveFurtherErrorMessage() {
        return errorEmailMessage;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            ((Activity) getContext()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}