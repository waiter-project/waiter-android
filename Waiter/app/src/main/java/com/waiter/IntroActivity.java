package com.waiter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.securepreferences.SecurePreferences;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
    }

    public void onClick_SignIn(View view) {
        Toast.makeText(this, "onClick_SignIn", Toast.LENGTH_SHORT).show();

//        SharedPreferences prefs = new SecurePreferences(this);
//        prefs.edit().putBoolean("is_logged_in", true).apply(); // to remove later

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        finish();
    }

    public void onClick_SignUp(View view) {
        Toast.makeText(this, "onClick_SignUp", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        finish();
    }
}
