package com.waiter;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.view.View;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.SlideFragmentBuilder;
import agency.tango.materialintroscreen.animations.IViewTranslation;

public class IntroActivity extends MaterialIntroActivity {

    private LoginFragment loginFragment;
    private SignupNameFragment signupNameFragment;
    private SignupEmailFragment signupEmailFragment;
    private SignupPasswordFragment signupPasswordFragment;
    private SignupBirthdayFragment signupBirthdayFragment;

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
        boolean signUp = intent.getBooleanExtra("sign_up", false);

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
        setResult(RESULT_OK);
    }

    public boolean isSignedUp() {
        return signedUp;
    }

    public void setSignedUp(boolean signedUp) {
        this.signedUp = signedUp;
    }
}
