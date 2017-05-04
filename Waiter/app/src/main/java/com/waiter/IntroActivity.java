package com.waiter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
    }

    public void onClick_SignIn(View view) {
        Toast.makeText(this, "onClick_SignIn", Toast.LENGTH_SHORT).show();
    }

    public void onClick_SignUp(View view) {
        Toast.makeText(this, "onClick_SignUp", Toast.LENGTH_SHORT).show();
    }
}
