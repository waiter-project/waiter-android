package com.waiter;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.securepreferences.SecurePreferences;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.SlideFragmentBuilder;
import agency.tango.materialintroscreen.animations.IViewTranslation;

public class IntroActivity extends MaterialIntroActivity {

    private static final String TAG = "IntroActivity";

    private LoginFragment loginFragment;
    private SignupNameFragment signupNameFragment;
    private SignupEmailFragment signupEmailFragment;
    private SignupPasswordFragment signupPasswordFragment;
    private SignupBirthdayFragment signupBirthdayFragment;

    private boolean signUp;
    private boolean signedUp = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getBackButtonTranslationWrapper()
                .setEnterTranslation(new IViewTranslation() {
                    @Override
                    public void translate(View view, @FloatRange(from = 0, to = 1.0) float percentage) {
                        view.setAlpha(percentage);
                    }
                });

        Intent intent = getIntent();
        signUp = intent.getBooleanExtra("sign_up", false);

        if (!signUp) {
            loginFragment = new LoginFragment();
            addSlide(loginFragment);
        } else {
            signupNameFragment = new SignupNameFragment();
            addSlide(signupNameFragment);
            signupEmailFragment = new SignupEmailFragment();
            addSlide(signupEmailFragment);
            signupPasswordFragment = new SignupPasswordFragment();
            addSlide(signupPasswordFragment);
            signupBirthdayFragment = new SignupBirthdayFragment();
            addSlide(signupBirthdayFragment);
        }

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.colorPrimary)
                .buttonsColor(R.color.colorPrimaryDark)
                .neededPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION})
                .title(getString(R.string.we_need_your_permission))
                .description(getString(R.string.permission_location_description))
                .image(R.drawable.folded_map)
                .build());
    }

    public String getFirstName() {
        return signupNameFragment.getFirstName();
    }

    public String getLastName() {
        return signupNameFragment.getLastName();
    }

    public String getEmailAddress() {
        return signupEmailFragment.getEmailAddress();
    }

    public String getPassword() {
        return signupPasswordFragment.getPassword();
    }

    public void disableInput() {
        signupNameFragment.disableInput();
        signupEmailFragment.disableInput();
        signupPasswordFragment.disableInput();
    }

    @Override
    public void onFinish() {
        super.onFinish();
        Log.d(TAG, "onFinish");
        SharedPreferences prefs = new SecurePreferences(this);
        if (!signUp) {
            prefs.edit().putString("user_id", loginFragment.getUserId())
                    .putString("first_name", loginFragment.getFirstName())
                    .putString("last_name", loginFragment.getLastName())
                    .putString("auth_token", loginFragment.getAuthToken())
                    .apply();
        } else {
            prefs.edit().putString("user_id", signupBirthdayFragment.getUserId())
                    .putString("first_name", signupNameFragment.getFirstName())
                    .putString("last_name", signupNameFragment.getLastName())
                    .putString("auth_token", signupBirthdayFragment.getAuthToken())
                    .apply();
        }
        prefs.edit().putBoolean("is_logged_in", true).apply();

        checkSecurePreferences();

        setResult(RESULT_OK);
    }

    private void checkSecurePreferences() {
        SharedPreferences prefs = new SecurePreferences(this);
        boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);
        String userId = prefs.getString("user_id", "empty");
        String firstName = prefs.getString("first_name", "empty");
        String lastName = prefs.getString("last_name", "empty");
        String authToken = prefs.getString("auth_token", "empty");

        Log.d(TAG, "checkSecurePreferences: isLoggedIn = " + isLoggedIn);
        Log.d(TAG, "checkSecurePreferences: userId = " + userId);
        Log.d(TAG, "checkSecurePreferences: firstName = " + firstName);
        Log.d(TAG, "checkSecurePreferences: lastName = " + lastName);
        Log.d(TAG, "checkSecurePreferences: authToken = " + authToken);
    }

    public boolean isSignedUp() {
        return signedUp;
    }

    public void setSignedUp(boolean signedUp) {
        this.signedUp = signedUp;
    }
}
