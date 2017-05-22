package com.waiter;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.heinrichreimersoftware.materialintro.app.OnNavigationBlockedListener;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;
import com.heinrichreimersoftware.materialintro.slide.Slide;
import com.waiter.models.RequestSignup;

public class IntroActivity extends com.heinrichreimersoftware.materialintro.app.IntroActivity {

    private static final String TAG = "IntroActivity";
    private static final int REQUEST_CODE_INTRO = 1;

    private LoginFragment loginFragment;
    private SignupNameFragment nameFragment;
    private SignupEmailFragment emailFragment;
    public static String errorEmailMessage = "";
    private SignupPasswordFragment passwordFragment;
    public static String errorPasswordMessage = "";

    private RequestSignup requestSignup = new RequestSignup();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        final boolean signUp = intent.getBooleanExtra("sign_up", false);

        setButtonBackVisible(false);

        final Slide fakeSlide;
        fakeSlide = new SimpleSlide.Builder()
                .title(R.string.welcome)
                .image(R.drawable.waiter_logo)
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.colorPrimaryDark)
                .scrollable(false)
                .build();
        addSlide(fakeSlide);

        final Slide loginSlide;
        final Slide nameSlide;
        final Slide emailSlide;
        final Slide passwordSlide;
        if (signUp) {
            loginSlide = null;
            nameFragment = SignupNameFragment.newInstance();
            nameSlide = new FragmentSlide.Builder()
                    .background(R.color.colorPrimary)
                    .backgroundDark(R.color.colorPrimaryDark)
                    .fragment(nameFragment)
                    .build();
            addSlide(nameSlide);
            emailFragment = SignupEmailFragment.newInstance();
            emailSlide = new FragmentSlide.Builder()
                    .background(R.color.colorPrimary)
                    .backgroundDark(R.color.colorPrimaryDark)
                    .fragment(emailFragment)
                    .build();
            addSlide(emailSlide);
            passwordFragment = SignupPasswordFragment.newInstance();
            passwordSlide = new FragmentSlide.Builder()
                    .background(R.color.colorPrimary)
                    .backgroundDark(R.color.colorPrimaryDark)
                    .fragment(passwordFragment)
                    .build();
            addSlide(passwordSlide);
        } else {
            nameSlide = null;
            emailSlide = null;
            passwordSlide = null;
            loginFragment = LoginFragment.newInstance();
            loginSlide = new FragmentSlide.Builder()
                    .background(R.color.colorPrimary)
                    .backgroundDark(R.color.colorPrimaryDark)
                    .fragment(loginFragment)
                    .build();
            addSlide(loginSlide);
        }

        final Slide permissionsSlide;
        permissionsSlide = new SimpleSlide.Builder()
                .title("We need your permission")
                .description("In order to show you local events")
                .image(R.drawable.custom_marker_resized)
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.colorPrimaryDark)
                .scrollable(false)
                .canGoBackward(false)
                .permission(Manifest.permission.ACCESS_FINE_LOCATION)
                .build();
        addSlide(permissionsSlide);

        addOnNavigationBlockedListener(new OnNavigationBlockedListener() {
            @Override
            public void onNavigationBlocked(int position, int direction) {
                View contentView = findViewById(android.R.id.content);
                if (contentView != null) {
                    Slide slide = getSlide(position);

                    if (slide == permissionsSlide) {
                        Snackbar.make(contentView, "Please grant the permissions before proceeding.", Snackbar.LENGTH_LONG).show();
                    }

                    if (signUp) {
                        if (slide == nameSlide) {
                            Snackbar.make(contentView, "Please enter your name before proceeding.", Snackbar.LENGTH_LONG).show();
                        } else if (slide == emailSlide) {
                            Snackbar.make(contentView, errorEmailMessage, Snackbar.LENGTH_LONG).show();
                        } else if (slide == passwordSlide) {
                            Snackbar.make(contentView, errorPasswordMessage, Snackbar.LENGTH_SHORT).show();
                        }
                    } else {
                        if (slide == loginSlide) {
                            Snackbar.make(contentView, "Please sign in before proceeding.", Snackbar.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });

        addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                View contentView = findViewById(android.R.id.content);
                if (contentView != null) {
                    Slide slide = getSlide(position);

                    if (signUp) {
                        if (slide == nameSlide) {
                            nameFragment.firstFocus();
                        } if (slide == emailSlide) { // After nameSlide
                            emailFragment.firstFocus();
                            requestSignup.setFirstname(nameFragment.getInputFirstName());
                            requestSignup.setLastname(nameFragment.getInputLastName());
                        } else if (slide == passwordSlide) { // After emailSlide
                            passwordFragment.firstFocus();
                            requestSignup.setEmail(emailFragment.getInputEmail());
                        } else if (slide == permissionsSlide) { // After passwordSlide
                            requestSignup.setPassword(passwordFragment.getInputPassword());
                        }
                    } else {
                        if (slide == loginSlide) {
                            loginFragment.firstFocus();
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public Intent onSendActivityResult(int result) {
        super.onSendActivityResult(result);
        requestSignup.setType(0);
        Log.d(TAG, "requestSignup = " + requestSignup.toString());
        return null;
    }
}
