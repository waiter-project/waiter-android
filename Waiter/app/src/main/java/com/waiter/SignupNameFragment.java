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

import com.waiter.utils.CustomTextWatcher;

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

        mInputFirstName.addTextChangedListener(new CustomTextWatcher(mInputFirstName, mInputLayoutFirstName, getString(R.string.is_required, "First name")));
        mInputLastName.addTextChangedListener(new CustomTextWatcher(mInputLastName, mInputLayoutLastName, getString(R.string.is_required, "Last name")));

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

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            ((Activity) getContext()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}