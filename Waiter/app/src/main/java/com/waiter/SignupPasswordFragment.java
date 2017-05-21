package com.waiter;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.heinrichreimersoftware.materialintro.app.SlideFragment;
import com.waiter.databinding.FragmentSignupPasswordBinding;

public class SignupPasswordFragment extends SlideFragment {

    private static final String TAG = "SignupPasswordFragment";
    private FragmentSignupPasswordBinding binding;

    private static final int MIN_PASSWORD_LENGTH = 8;

    public SignupPasswordFragment() {
        // Required empty public constructor
    }

    public static SignupPasswordFragment newInstance() {
        return new SignupPasswordFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup_password, container, false);

        binding.inputPassword.addTextChangedListener(new MyTextWatcher(binding.inputPassword));
        binding.inputPasswordConfirmation.addTextChangedListener(new MyTextWatcher(binding.inputPasswordConfirmation));

        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean canGoBackward() {
        return false;
    }

    @Override
    public boolean canGoForward() {
        String password = binding.inputPassword.getText().toString().trim();
        String passwordConfirmation = binding.inputPasswordConfirmation.getText().toString().trim();

        if (isValidPassword(password)) {
            if (password.equals(passwordConfirmation)) {
                Utils.hideKeyboard(getActivity());
                return true;
            } else {
                IntroActivity.errorPasswordMessage = getString(R.string.err_msg_password_confirmation);
                return false;
            }
        } else {
            IntroActivity.errorPasswordMessage = getString(R.string.is_invalid, "Password");
            return false;
        }
    }

    public String getInputPassword() {
        return binding.inputPassword.getText().toString().trim();
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
                case R.id.input_password:
                    validatePassword(false);
                    break;
                case R.id.input_password_confirmation:
                    validatePasswordConfirmation();
                    break;
            }
        }
    }

//    private boolean validatePassword() {
//        String password = binding.inputPassword.getText().toString().trim();
//
//        if (password.isEmpty() || !isValidPassword(password)) {
//            binding.inputLayoutPassword.setError(getString(R.string.err_msg_password));
//            requestFocus(binding.inputPassword);
//            return false;
//        } else {
//            binding.inputLayoutPassword.setErrorEnabled(false);
//        }
//
//        return true;
//    }

    private boolean validatePassword(boolean showError) {
        String password = binding.inputPassword.getText().toString();

        if (!isValidPassword(password)) {
            if (!binding.inputLayoutPassword.isErrorEnabled()) {
                binding.inputLayoutPassword.setHelperTextEnabled(true);
            }
            binding.inputLayoutPassword.setError(getString(R.string.password_instructions));
            return false;
        }
        binding.inputLayoutPassword.setErrorEnabled(false);
        binding.inputLayoutPassword.setHelperTextEnabled(false);

        return true;
    }

    private boolean validatePasswordConfirmation() {
        String passwordConfirmation = binding.inputPasswordConfirmation.getText().toString().trim();
        String password = binding.inputPassword.getText().toString().trim();

        if (passwordConfirmation.equals(password)) {
            binding.inputLayoutPasswordConfirmation.setErrorEnabled(false);
        } else {
            binding.inputLayoutPasswordConfirmation.setError(getString(R.string.err_msg_password_confirmation));
            requestFocus(binding.inputPasswordConfirmation);
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

    public void firstFocus() {
        Utils.showKeyboard(getActivity(), binding.inputPassword);
    }

}