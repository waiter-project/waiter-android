package com.waiter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class WelcomeActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_INTRO = 1;

    private RelativeLayout mWelcomeLayout;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mWelcomeLayout = (RelativeLayout) findViewById(R.id.welcome_layout);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
    }

    public void onClick_SignIn(View view) {
        Intent intent = new Intent(this, IntroActivity.class);
        startActivityForResult(intent, REQUEST_CODE_INTRO);
    }

    public void onClick_SignUp(View view) {
        Intent intent = new Intent(this, IntroActivity.class);
        intent.putExtra("sign_up", true);
        startActivityForResult(intent, REQUEST_CODE_INTRO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_INTRO) {
            if (resultCode == RESULT_OK) {
                mWelcomeLayout.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }
}
