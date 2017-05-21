package com.waiter;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.heinrichreimersoftware.materialintro.app.SlideFragment;
import com.waiter.databinding.FragmentSignupNameBinding;

public class SignupNameFragment extends SlideFragment {

    private static final String TAG = "SignupNameFragment";
    private FragmentSignupNameBinding binding;

    public SignupNameFragment() {
        // Required empty public constructor
    }

    public static SignupNameFragment newInstance() {
        return new SignupNameFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup_name, container, false);

        binding.inputFname.addTextChangedListener(new MyTextWatcher(binding.inputFname));
        binding.inputLname.addTextChangedListener(new MyTextWatcher(binding.inputLname));

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
        return !binding.inputFname.getText().toString().trim().isEmpty() && !binding.inputLname.getText().toString().trim().isEmpty();
    }

    public String getInputFirstName() {
        return binding.inputFname.getText().toString().trim();
    }

    public String getInputLastName() {
        return binding.inputLname.getText().toString().trim();
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
                case R.id.input_fname:
                    validateFirstName();
                    break;
                case R.id.input_lname:
                    validateLastName();
                    break;
            }
        }
    }

    private boolean validateFirstName() {
        String firstName = binding.inputFname.getText().toString().trim();
        if (firstName.isEmpty()) {
            binding.inputLayoutFname.setError(getString(R.string.is_required, "First name"));
            requestFocus(binding.inputFname);
            return false;
        } else {
            binding.inputLayoutFname.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateLastName() {
        String lastName = binding.inputLname.getText().toString().trim();
        if (lastName.isEmpty()) {
            binding.inputLayoutLname.setError(getString(R.string.is_required, "Last name"));
            requestFocus(binding.inputLname);
            return false;
        } else {
            binding.inputLayoutLname.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            ((Activity) getContext()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public void firstFocus() {
        Utils.showKeyboard(getActivity(), binding.inputFname);
    }
}