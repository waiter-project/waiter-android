package com.waiter;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.heinrichreimersoftware.materialintro.app.NavigationPolicy;
import com.heinrichreimersoftware.materialintro.app.OnNavigationBlockedListener;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;
import com.heinrichreimersoftware.materialintro.slide.Slide;

public class IntroActivity extends com.heinrichreimersoftware.materialintro.app.IntroActivity {

    private static final String TAG = "IntroActivity";
    private static final int REQUEST_CODE_INTRO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        loginSlide = new FragmentSlide.Builder()
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.colorPrimaryDark)
                .fragment(LoginFragment.newInstance())
                .canGoBackward(false)
                .build();
        addSlide(loginSlide);

        final Slide permissionsSlide;
        permissionsSlide = new SimpleSlide.Builder()
                .title("We need your permission")
                .description("In order to show you local events")
                .image(R.drawable.custom_marker_resized)
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.colorPrimaryDark)
                .scrollable(false)
                .permission(Manifest.permission.ACCESS_FINE_LOCATION)
                .canGoBackward(false)
                .build();
        addSlide(permissionsSlide);

        addOnNavigationBlockedListener(new OnNavigationBlockedListener() {
            @Override
            public void onNavigationBlocked(int position, int direction) {
                View contentView = findViewById(android.R.id.content);
                if (contentView != null) {
                    Slide slide = getSlide(position);

                    if (slide == permissionsSlide) {
                        Snackbar.make(contentView, "Please grant the permissions before proceeding.", Snackbar.LENGTH_SHORT).show();
                    } else if (slide == loginSlide) {
                        Snackbar.make(contentView, "Please sign in before proceeding.", Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
}
