package com.waiter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.securepreferences.SecurePreferences;
import com.waiter.models.ErrorResponse;
import com.waiter.models.RequestLogin;
import com.waiter.models.ResponseLogin;
import com.waiter.network.ClientGenerator;
import com.waiter.network.WaiterClient;
import com.waiter.utils.ErrorUtils;

import agency.tango.materialintroscreen.SlideFragment;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends SlideFragment implements View.OnClickListener {

    private static final String TAG = "LoginFragment";

    private ScrollView mScrollView;
    private TextView mWelcomeUser, mLinkForgotPassword;
    private EditText mInputEmail, mInputPassword;
    private TextInputLayout mInputLayoutEmail, mInputLayoutPassword;
    private Button mLoginButton;
    private ProgressDialog mProgressDialog;

    private boolean loggedIn = false;
    private boolean firstAttempt = true;

    private IntroActivity introActivity;

    private WaiterClient waiterClient;
    private RequestLogin requestLogin;
    private ErrorResponse errorResponse;

    private String userId;
    private String userEmail;
    private String authToken;
    private String firstName;
    private String lastName;

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);

        mProgressDialog = new ProgressDialog(getContext(), R.style.AppTheme_Dark_Dialog);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.signing_in));

        mInputEmail = (EditText) view.findViewById(R.id.input_email);
        mInputPassword = (EditText) view.findViewById(R.id.input_password);
        mInputLayoutEmail = (TextInputLayout) view.findViewById(R.id.input_layout_email);
        mInputLayoutPassword = (TextInputLayout) view.findViewById(R.id.input_layout_password);
        mLoginButton = (Button) view.findViewById(R.id.login_btn);
        mLoginButton.setOnClickListener(this);
        mInputPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mLoginButton.performClick();
                    return true;
                }
                return false;
            }
        });

        mScrollView = (ScrollView) view.findViewById(R.id.scroll_view);
        mWelcomeUser = (TextView) view.findViewById(R.id.welcome_user);
        mLinkForgotPassword = (TextView) view.findViewById(R.id.link_forgot_password);

        introActivity = ((IntroActivity)getActivity());

        waiterClient = ClientGenerator.createClient(WaiterClient.class);
        requestLogin = new RequestLogin();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mInputEmail.requestFocus();
    }

    @Override
    public int backgroundColor() {
        return R.color.colorPrimary;
    }

    @Override
    public int buttonsColor() {
        return R.color.colorPrimaryDark;
    }

    public void disableInput() {
        mInputEmail.setEnabled(false);
        mInputPassword.setEnabled(false);
        mLoginButton.setEnabled(false);
//        introActivity.disableInput();
    }

    public String getAuthToken() { return this.authToken; }

    public String getUserId() { return this.userId; }

    public String getUserEmail() { return this.userEmail; }

    public String getFirstName() { return this.firstName; }

    public String getLastName() { return this.lastName; }

    @Override
    public boolean canMoveFurther() {
        return loggedIn;
    }

    @Override
    public String cantMoveFurtherErrorMessage() {
        return getString(R.string.please_sign_in);
    }

    @Override
    public void onClick(View v) {
        Utils.hideKeyboard(getActivity());

        if (firstAttempt) {
            mInputEmail.addTextChangedListener(new MyTextWatcher(mInputEmail));
            mInputPassword.addTextChangedListener(new MyTextWatcher(mInputPassword));
            firstAttempt = false;
        }

        boolean validForm = submitForm();
        if (validForm) {
            mInputEmail.clearFocus();
            mInputPassword.clearFocus();
            login();
        }
    }

    private void login() {
        mProgressDialog.show();

        requestLogin.setEmail(mInputEmail.getText().toString());
        requestLogin.setPassword(mInputPassword.getText().toString());
        Call<ResponseLogin> call = waiterClient.login(requestLogin);

        call.enqueue(new Callback<ResponseLogin>() {
            @Override
            public void onResponse(@NonNull Call<ResponseLogin> call, @NonNull Response<ResponseLogin> response) {
                mProgressDialog.dismiss();
                if (response.isSuccessful()) {
                    ResponseLogin body = response.body();
                    if (body != null) {
                        loggedIn = true;
                        mWelcomeUser.setText(getString(R.string.welcome_back_user, body.getData().getUser().getFirstName()));
                        mLinkForgotPassword.setVisibility(View.GONE);
                        mScrollView.setVisibility(View.GONE);
                        mWelcomeUser.setVisibility(View.VISIBLE);

                        disableInput();
//                        introActivity.setLoggedIn(true);

                        authToken = body.getData().getToken();
                        userId = body.getData().getUser().getId();
                        userEmail = mInputEmail.getText().toString();
                        firstName = body.getData().getUser().getFirstName();
                        lastName = body.getData().getUser().getLastName();
                    } else {
                        introActivity.showMessage(getString(R.string.response_body_null));
                    }
                } else {
                    errorResponse = ErrorUtils.parseError(response);
                    if (errorResponse != null) {
                        if (errorResponse.getData().getCauses() == null || errorResponse.getData().getCauses().isEmpty()) {
                            introActivity.showMessage(errorResponse.getData().getMessage());
                        } else {
                            introActivity.showMessage(errorResponse.getData().getCauses().get(0));
                        }
                    } else {
                        introActivity.showMessage(getString(R.string.internal_error));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseLogin> call, @NonNull Throwable t) {
                mProgressDialog.dismiss();
                introActivity.showMessage(t.getLocalizedMessage());
            }
        });
    }

    private boolean submitForm() {
        boolean validPassword = true;
        boolean validEmail = true;

        validPassword = validatePassword();
        validEmail = validateEmail();

        return validEmail && validPassword;
    }

    private boolean validateEmail() {
        String email = mInputEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            mInputLayoutEmail.setError(getString(R.string.err_msg_email));
//            Utils.showKeyboard(getActivity(), mInputEmail);
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

    private boolean validatePassword() {
        if (mInputPassword.getText().toString().trim().isEmpty()) {
            mInputLayoutPassword.setError(getString(R.string.err_msg_password));
//            Utils.showKeyboard(getActivity(), mInputPassword);
//            requestFocus(mInputPassword);
            return false;
        } else {
            mInputLayoutPassword.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            ((Activity) getContext()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
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
                case R.id.input_password:
                    validatePassword();
                    break;
            }
        }
    }
}
