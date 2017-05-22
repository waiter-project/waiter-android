package com.waiter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.SlideFragmentBuilder;
import agency.tango.materialintroscreen.animations.IViewTranslation;

public class IntroActivity extends MaterialIntroActivity {

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
        final boolean signUp = intent.getBooleanExtra("sign_up", false);

        if (!signUp) {
            addSlide(new LoginFragment());
        } else {

        }

        addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.colorPrimary)
                        .buttonsColor(R.color.colorPrimaryDark)
                        .neededPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION})
                        .title(getString(R.string.we_need_your_permission))
                        .description(getString(R.string.permission_location_description))
                        .build());
    }

    @Override
    public void onFinish() {
        super.onFinish();
        setResult(RESULT_OK);
    }
}
