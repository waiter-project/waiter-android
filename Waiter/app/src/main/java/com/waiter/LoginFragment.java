package com.waiter;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import com.github.paolorotolo.appintro.ISlidePolicy;

public class LoginFragment extends Fragment implements ISlidePolicy {

    private View mRootView;
    private EditText mInputEmail, mInputPassword;
    private TextInputLayout mInputLayoutEmail, mInputLayoutPassword;

    public static boolean validLogin = false;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_login, container, false);
        validLogin = false;

        mInputLayoutEmail = (TextInputLayout) mRootView.findViewById(R.id.input_layout_email);
        mInputLayoutPassword = (TextInputLayout) mRootView.findViewById(R.id.input_layout_password);
        mInputEmail = (EditText) mRootView.findViewById(R.id.input_email);
        mInputPassword = (EditText) mRootView.findViewById(R.id.input_password);

        mInputEmail.addTextChangedListener(new MyTextWatcher(mInputEmail));
        mInputPassword.addTextChangedListener(new MyTextWatcher(mInputPassword));

        return mRootView;
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

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

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

    @Override
    public boolean isPolicyRespected() {
//        Utils.hideKeyboard(getActivity());
        boolean validForm = submitForm();

        if (validForm) {
            mInputEmail.clearFocus();
            mInputPassword.clearFocus();
        }

        return validForm;
    }

    @Override
    public void onUserIllegallyRequestedNextPage() {
        Snackbar.make(mRootView, "Invalid login", Snackbar.LENGTH_LONG).show();
    }
}
