package com.waiter.utils;

import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.waiter.R;
import com.waiter.custom.CustomTextInputLayout;

public class CustomTextWatcher implements TextWatcher {

    private static final int MIN_PASSWORD_LENGTH = 8;

    private View view;
    private TextInputLayout textInputLayout;
    private String errorMessage;

    public CustomTextWatcher(View view, TextInputLayout textInputLayout, String errorMessage) {
        this.view = view;
        this.textInputLayout = textInputLayout;
        this.errorMessage = errorMessage;
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

    public void afterTextChanged(Editable editable) {
        switch (view.getId()) {
            case R.id.input_email:
                validateEmail();
                break;
            case R.id.input_fname:
                validateFirstName();
                break;
            case R.id.input_lname:
                validateLastName();
                break;
            case R.id.input_password:
                validatePassword(false);
                break;
//            case R.id.input_layout_password_confirmation:
//                validatePasswordConfirmation();
//                break;
            case R.id.input_current_password:
                validateCurrentPassword(false);
                break;
            case R.id.input_birthday:
                validateBirthday();
                break;
            case R.id.input_phone:
                validatePhone();
                break;
            case R.id.input_address:
                validateAddress();
                break;
        }
    }

    private boolean validateEmail() {
        String email = ((EditText) view).getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            textInputLayout.setError(errorMessage);
//            requestFocus(mInputEmail);
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean validateFirstName() {
        String firstName = ((EditText) view).getText().toString().trim();
        if (firstName.isEmpty()) {
            textInputLayout.setError(errorMessage);
//            requestFocus(mInputFirstName);
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateLastName() {
        String lastName = ((EditText) view).getText().toString().trim();
        if (lastName.isEmpty()) {
            textInputLayout.setError(errorMessage);
//            requestFocus(mInputLastName);
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePassword(boolean showError) {
        String password = ((EditText) view).getText().toString();

        if (!isValidPassword(password)) {
            if (!textInputLayout.isErrorEnabled()) {
                ((CustomTextInputLayout) textInputLayout).setHelperTextEnabled(true);
            }
//            getString(R.string.password_instructions)
            textInputLayout.setError(errorMessage);
            return false;
        }
        textInputLayout.setErrorEnabled(false);
        ((CustomTextInputLayout) textInputLayout).setHelperTextEnabled(false);

        return true;
    }

    private boolean validateCurrentPassword(boolean showError) {
        String password = ((EditText) view).getText().toString();

        if (!isValidPassword(password)) {
            textInputLayout.setError(errorMessage);
//            requestFocus(mInputPassword);
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }
    private static boolean isValidPassword(String password) {
        return password.length() >= MIN_PASSWORD_LENGTH && !TextUtils.isEmpty(password);
    }

    private boolean validateBirthday() {
        String birthday = ((EditText) view).getText().toString();

        if (!isValidBirthday(birthday)) {
            if (!textInputLayout.isErrorEnabled()) {
                ((CustomTextInputLayout) textInputLayout).setHelperTextEnabled(true);
            }
//            getString(R.string.birthday_instructions)
            textInputLayout.setError(errorMessage);
            return false;
        }
        textInputLayout.setErrorEnabled(false);
        ((CustomTextInputLayout) textInputLayout).setHelperTextEnabled(false);

        return true;
    }

    private boolean isValidBirthday(String birthday) {
        return !TextUtils.isEmpty(birthday);
    }

    private boolean validatePhone() {
        String phone = ((EditText) view).getText().toString().trim();

        if (!phone.isEmpty() && !isValidPhone(phone)) {
            textInputLayout.setError(errorMessage);
//            requestFocus(mInputEmail);
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidPhone(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    private boolean validateAddress() {
        return true;
    }

}
