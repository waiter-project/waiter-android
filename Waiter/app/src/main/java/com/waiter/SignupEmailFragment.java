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
import com.waiter.databinding.FragmentSignupEmailBinding;

public class SignupEmailFragment extends SlideFragment {

    private static final String TAG = "SignupEmailFragment";
    private FragmentSignupEmailBinding binding;

    public SignupEmailFragment() {
        // Required empty public constructor
    }

    public static SignupEmailFragment newInstance() {
        return new SignupEmailFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup_email, container, false);

        binding.inputEmail.addTextChangedListener(new MyTextWatcher(binding.inputEmail));

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
        String email = binding.inputEmail.getText().toString().trim();
        if (isValidEmail(email)) {
            if (email.equals("samuel@waiter.com")) {
                IntroActivity.errorEmailMessage = "This email address is already used.";
                return false;
            } else {
                return true;
            }
        } else {
            IntroActivity.errorEmailMessage = "Please enter a valid email address before proceeding.";
            return false;
        }
    }

    public String getInputEmail() {
        return binding.inputEmail.getText().toString().trim();
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
            }
        }
    }

    private boolean validateEmail() {
        String email = binding.inputEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            binding.inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(binding.inputEmail);
            return false;
        } else {
            binding.inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            ((Activity) getContext()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public void firstFocus() {
        Utils.showKeyboard(getActivity(), binding.inputEmail);
    }

}