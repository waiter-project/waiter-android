package com.waiter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.securepreferences.SecurePreferences;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getBoolean("is_first_launch", true)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Log.d("SplashActivity", "onCreate: " + sharedPreferences.getString(SettingsActivity.KEY_PREF_SERVER, "empty"));
            if (Utils.isEmulator()) {
                editor.putString(SettingsActivity.KEY_PREF_SERVER, "0");
            } else {
                editor.putString(SettingsActivity.KEY_PREF_SERVER, "1");
            }
            editor.putBoolean("is_first_launch", false);
            editor.apply();
            Log.d("SplashActivity", "onCreate: " + sharedPreferences.getString(SettingsActivity.KEY_PREF_SERVER, "empty"));
        }

        SharedPreferences prefs = new SecurePreferences(this);
        boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);
        if (isLoggedIn) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
        }

        finish();
    }
}
