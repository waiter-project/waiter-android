package com.waiter;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import com.waiter.custom.CustomTextInputLayout;

import agency.tango.materialintroscreen.SlideFragment;

public class SignupPasswordFragment extends SlideFragment {

    private static final String TAG = "SignupPasswordFragment";
    private static final int MIN_PASSWORD_LENGTH = 8;

    private EditText mInputPassword, mInputPasswordConfirmation;
    private CustomTextInputLayout mInputLayoutPassword;
    private TextInputLayout mInputLayoutPasswordConfirmation;

    private View view;

    private String mPassword = "";
    private String errorPasswordMessage = "";

    private IntroActivity introActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_signup_password, container, false);

        mInputPassword = (EditText) view.findViewById(R.id.input_password);
        mInputPasswordConfirmation = (EditText) view.findViewById(R.id.input_password_confirmation);
        mInputLayoutPassword = (CustomTextInputLayout) view.findViewById(R.id.input_layout_password);
        mInputLayoutPasswordConfirmation = (TextInputLayout) view.findViewById(R.id.input_layout_password_confirmation);

        mInputPassword.addTextChangedListener(new MyTextWatcher(mInputPassword));
        mInputPasswordConfirmation.addTextChangedListener(new MyTextWatcher(mInputPasswordConfirmation));

        errorPasswordMessage = getString(R.string.is_invalid, "Password");

        introActivity = (IntroActivity)getActivity();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mInputPassword.requestFocus();
    }

    public String getPassword() {
        return mPassword;
    }

    public void disableInput() {
        mInputPassword.setEnabled(false);
        mInputPasswordConfirmation.setEnabled(false);
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
        if (introActivity.isSignedUp()) {
            return true;
        }
        String password = mInputPassword.getText().toString().trim();
        String passwordConfirmation = mInputPasswordConfirmation.getText().toString().trim();

        mPassword = "";
        if (isValidPassword(password)) {
            if (password.equals(passwordConfirmation)) {
                Utils.hideKeyboard(getActivity());
                mPassword = password;
                return true;
            } else {
                errorPasswordMessage = getString(R.string.err_msg_password_confirmation);
                return false;
            }
        } else {
            errorPasswordMessage = getString(R.string.is_invalid, "Password");
            return false;
        }
    }

    @Override
    public String cantMoveFurtherErrorMessage() {
        return errorPasswordMessage;
    }

//    private boolean validatePassword() {
//        String password = mInputPassword.getText().toString().trim();
//
//        if (password.isEmpty() || !isValidPassword(password)) {
//            mInputLayoutPassword.setError(getString(R.string.err_msg_password));
//            requestFocus(mInputPassword);
//            return false;
//        } else {
//            mInputLayoutPassword.setErrorEnabled(false);
//        }
//
//        return true;
//    }

    private boolean validatePassword(boolean showError) {
        String password = mInputPassword.getText().toString();

        if (!isValidPassword(password)) {
            if (!mInputLayoutPassword.isErrorEnabled()) {
                mInputLayoutPassword.setHelperTextEnabled(true);
            }
            mInputLayoutPassword.setError(getString(R.string.password_instructions));
            return false;
        }
        mInputLayoutPassword.setErrorEnabled(false);
        mInputLayoutPassword.setHelperTextEnabled(false);

        return true;
    }

    private boolean validatePasswordConfirmation() {
        String passwordConfirmation = mInputPasswordConfirmation.getText().toString().trim();
        String password = mInputPassword.getText().toString().trim();

        if (passwordConfirmation.equals(password)) {
            mInputLayoutPasswordConfirmation.setErrorEnabled(false);
        } else {
            mInputLayoutPasswordConfirmation.setError(getString(R.string.err_msg_password_confirmation));
//            requestFocus(mInputPasswordConfirmation);
            return false;
        }

        return true;
    }

    private static boolean isValidPassword(String password) {
        return password.length() >= MIN_PASSWORD_LENGTH && !TextUtils.isEmpty(password);
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            ((Activity) getContext()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void showErrorSnackbar(String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
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
                case R.id.input_password:
                    validatePassword(false);
                    break;
                case R.id.input_layout_password_confirmation:
                    validatePasswordConfirmation();
                    break;
            }
        }
    }
}