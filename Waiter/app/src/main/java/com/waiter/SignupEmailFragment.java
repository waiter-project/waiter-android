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

import com.waiter.network.ClientGenerator;
import com.waiter.network.WaiterClient;

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

        mInputEmail.addTextChangedListener(new MyTextWatcher(mInputEmail));

        errorEmailMessage = getString(R.string.please_enter_email);

        introActivity = (IntroActivity)getActivity();

        waiterClient = ClientGenerator.createClient(WaiterClient.class);

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

//    private class CheckEmailTask extends AsyncTask<String, Void, ResponseBody> {
//        protected ResponseBody doInBackground(String... urls) {
//            String email = urls[0];
//            Call<ResponseBody> call = waiterClient.checkEmailAvailable(email);
//            try {
//                ResponseBody response = call.execute().body();
//                Log.d(TAG, "doInBackground: try success");
//                waitResponse = false;
//                return response;
//            } catch (IOException e) {
//                e.printStackTrace();
//                Log.d(TAG, "doInBackground: try exception = " + e.getMessage());
//            }
//            waitResponse = false;
//            return null;
//        }
//
//        protected void onProgressUpdate() {
//            Log.d(TAG, "onProgressUpdate");
//        }
//
//        protected void onPostExecute(ResponseBody responseBody) {
//            Log.d(TAG, "onPostExecute");
//        }
//    }

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
    public void onNext(final LinearLayout navigationView) {
        super.onNext(navigationView);
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
        if (isValidEmail(email)) {
            Log.d(TAG, "onNext: Email is valid");

            mInputLayoutEmail.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.9f));
            mProgressBar.setVisibility(View.VISIBLE);

            call = waiterClient.checkEmailAvailable(email);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    Log.d(TAG, "onResponse");
                    if (response.isSuccessful()) {
                        Log.d(TAG, "onResponse: response is successful");
                        ResponseBody responseCheckEmailAvailable = response.body();
                        if (responseCheckEmailAvailable != null) {
                            isAvailable = true;
                            mEmailAddress = email;
                            moveToNextPage();
                        } else {
                            showErrorSnackbar(navigationView, "Response is null");
                        }
                    } else {
                        Log.d(TAG, "onResponse: response is not successful");
                        showErrorSnackbar(navigationView, getString(R.string.email_already_used));
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
                    showErrorSnackbar(navigationView, t.getLocalizedMessage());
                    mProgressBar.setVisibility(View.GONE);
                    mInputLayoutEmail.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                }
            });

        } else {
            Log.d(TAG, "onNext: Email is invalid");
            showErrorSnackbar(navigationView, getString(R.string.please_enter_email));
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

    private void showErrorSnackbar(final LinearLayout navigationView, String message) {
        if (view != null) {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).setCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    navigationView.setTranslationY(0f);
                    super.onDismissed(snackbar, event);
                }
            }).show();
//            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
        }
    }

    private boolean validateEmail() {
        String email = mInputEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            mInputLayoutEmail.setError(getString(R.string.err_msg_email));
//            requestFocus(mInputEmail);
            return false;
        } else {
            mInputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_email:
                    validateEmail();
                    break;
            }
        }
    }
}