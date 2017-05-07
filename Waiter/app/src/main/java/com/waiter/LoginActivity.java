package com.waiter;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class LoginActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(LoginFragment.newInstance());
        addSlide(AppIntroFragment.newInstance("Permission Request", "In order to access your location, we need your permission.", R.drawable.custom_marker, getResources().getColor(R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance("Thank you!", "Thank you for using Waiter!", R.drawable.waiter_logo, getResources().getColor(R.color.colorPrimary)));

        askForPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);

        showSkipButton(false);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);

        Utils.hideKeyboard(this);
    }
}
