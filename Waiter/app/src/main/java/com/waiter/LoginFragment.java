package com.waiter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.heinrichreimersoftware.materialintro.app.SlideFragment;
import com.waiter.databinding.FragmentLoginBinding;

public class LoginFragment extends SlideFragment implements View.OnClickListener {

    private static final String TAG = "LoginFragment";
    private FragmentLoginBinding binding;

    private ProgressDialog mProgressDialog;
    private boolean mFirstAttempt = true;

    private boolean loggedIn = false;
    private Handler loginHandler = new Handler();
    private Runnable loginRunnable = new Runnable() {
        @Override
        public void run() {
            mProgressDialog.dismiss();
            binding.btnLogin.setText(R.string.logged_in);

            loggedIn = true;

            binding.welcomeUser.setText(getString(R.string.welcome_back_user, binding.inputEmail.getText()));

            binding.linkForgotPassword.setVisibility(View.GONE);
            binding.scrollView.setVisibility(View.GONE);
            binding.welcomeUser.setVisibility(View.VISIBLE);

            updateNavigation();
        }
    };

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);

        mProgressDialog = new ProgressDialog(getContext(), R.style.AppTheme_Dark_Dialog);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.signing_in));

        binding.inputEmail.setEnabled(!loggedIn);
        binding.inputLayoutPassword.setEnabled(!loggedIn);
        binding.btnLogin.setEnabled(!loggedIn);
        binding.btnLogin.setOnClickListener(this);

        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        loginHandler.removeCallbacks(loginRunnable);
        super.onDestroy();
    }

    @Override
    public boolean canGoBackward() {
        return false;
    }

    @Override
    public boolean canGoForward() {
        return loggedIn;
    }

    private boolean submitForm() {
        boolean validPassword = true;
        boolean validEmail = true;

        validPassword = validatePassword();
        validEmail = validateEmail();

        return validEmail && validPassword;
    }

    public void login() {
        mProgressDialog.show();

        binding.inputEmail.setEnabled(false);
        binding.inputPassword.setEnabled(false);
        binding.btnLogin.setEnabled(false);
        binding.btnLogin.setText(R.string.signing_in);
    }

    @Override
    public void onClick(View v) {
        if (mFirstAttempt) {
            binding.inputEmail.addTextChangedListener(new MyTextWatcher(binding.inputEmail));
            binding.inputPassword.addTextChangedListener(new MyTextWatcher(binding.inputPassword));
            mFirstAttempt = false;
        }

        boolean validForm = submitForm();
        if (validForm) {
            binding.inputEmail.clearFocus();
            binding.inputPassword.clearFocus();
            login();
            loginHandler.postDelayed(loginRunnable, 2000);
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

    private boolean validatePassword() {
        if (binding.inputPassword.getText().toString().trim().isEmpty()) {
            binding.inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(binding.inputPassword);
            return false;
        } else {
            binding.inputLayoutPassword.setErrorEnabled(false);
        }
        return true;
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
