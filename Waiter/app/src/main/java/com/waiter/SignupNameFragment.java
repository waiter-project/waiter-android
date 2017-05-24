package com.waiter;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import agency.tango.materialintroscreen.SlideFragment;

public class SignupNameFragment extends SlideFragment {

    private static final String TAG = "SignupNameFragment";
    private EditText mInputFirstName, mInputLastName;
    private TextInputLayout mInputLayoutFirstName, mInputLayoutLastName;

    private String mFirstName;
    private String mLastName;

    private IntroActivity introActivity;

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_signup_name, container, false);

        mInputFirstName = (EditText) view.findViewById(R.id.input_fname);
        mInputLastName = (EditText) view.findViewById(R.id.input_lname);
        mInputLayoutFirstName = (TextInputLayout) view.findViewById(R.id.input_layout_fname);
        mInputLayoutLastName = (TextInputLayout) view.findViewById(R.id.input_layout_lname);

        mInputFirstName.addTextChangedListener(new MyTextWatcher(mInputFirstName));
        mInputLastName.addTextChangedListener(new MyTextWatcher(mInputLastName));

        introActivity = (IntroActivity)getActivity();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mInputFirstName.requestFocus();
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void disableInput() {
        Log.d(TAG, "disableInput");
        mInputFirstName.setEnabled(false);
        mInputLastName.setEnabled(false);
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
        String firstName = mInputFirstName.getText().toString().trim();
        String lastName = mInputLastName.getText().toString().trim();
        if (!firstName.isEmpty() && !lastName.isEmpty()) {
            mFirstName = firstName;
            mLastName = lastName;
            return true;
        } else {
            mFirstName = "";
            mLastName = "";
        }
        return false;
    }

    @Override
    public String cantMoveFurtherErrorMessage() {
        return getString(R.string.please_enter_name);
    }

    private boolean validateFirstName() {
        String firstName = mInputFirstName.getText().toString().trim();
        if (firstName.isEmpty()) {
            mInputLayoutFirstName.setError(getString(R.string.is_required, "First name"));
//            requestFocus(mInputFirstName);
            return false;
        } else {
            mInputLayoutFirstName.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateLastName() {
        String lastName = mInputLastName.getText().toString().trim();
        if (lastName.isEmpty()) {
            mInputLayoutLastName.setError(getString(R.string.is_required, "Last name"));
//            requestFocus(mInputLastName);
            return false;
        } else {
            mInputLayoutLastName.setErrorEnabled(false);
        }
        return true;
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
                case R.id.input_fname:
                    validateFirstName();
                    break;
                case R.id.input_lname:
                    validateLastName();
                    break;
            }
        }
    }
}