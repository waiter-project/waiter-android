package com.waiter.waiterpoc;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.waiter.waiterpoc.models.LoginAttempt;
import com.waiter.waiterpoc.network.ServiceGenerator;
import com.waiter.waiterpoc.network.WaiterService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    // UI References
    private EditText mEmailText;
    private EditText mPasswordText;
    private TextView mSignUpLink;
    private Button mLoginButton;
    private AlertDialog dialog;

    // Initial variables
    private SharedPreferences sp;
    private static boolean connected = false;
    private static final int REQUEST_SIGNUP = 0;

    public static boolean isConnected() {
        return connected;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set initial variables
        sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.getsContext());

        // Set up the login form
        mEmailText = (EditText) findViewById(R.id.input_email);
        mEmailText.requestFocus();
        mPasswordText = (EditText) findViewById(R.id.input_password);

        mLoginButton = (Button) findViewById(R.id.btn_login);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        mSignUpLink = (TextView) findViewById(R.id.link_sign_up);
        mSignUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

        // Set up the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setMessage(R.string.signin_failed_message).setTitle(R.string.signin_failed_title);
        dialog = builder.create();
    }

    public void login() {
        Log.d("LoginActivity", "Login");

        if (!validate()) {
            //onLoginFailed();
            return ;
        }

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Signing In...");
        progressDialog.show();

        final String email = mEmailText.getText().toString();
        final String password = mPasswordText.getText().toString();

        WaiterService service = ServiceGenerator.createService(WaiterService.class);

        LoginAttempt loginAttempt = new LoginAttempt(email, password);

        Call<LoginAttempt> call = service.basicLogin(loginAttempt);

        call.enqueue(new Callback<LoginAttempt>() {
            @Override
            public void onResponse(Call<LoginAttempt> call, Response<LoginAttempt> response) {
                if (response.isSuccess()) {
                    Log.d("Success", "Return: " + response.message() + " - Raw: " + response.raw().toString());

                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("email", email);
                    editor.putString("password", password);
                    editor.commit();

                    progressDialog.dismiss();

                    onLoginSuccess();
                } else {
                    Log.d("Failure", "Return: " + response.message() + " - Raw: " + response.raw().toString());
                    progressDialog.dismiss();
                    onLoginFailed();
                }

            }

            @Override
            public void onFailure(Call<LoginAttempt> call, Throwable t) {
                Log.d("Error", t.getMessage());
                progressDialog.dismiss();
                onLoginFailed();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        Log.d("onLoginSuccess", "Function onLoginSuccess");
        mLoginButton.setEnabled(true);
        connected = true;
        finish();
    }

    public void onLoginFailed() {
        Log.d("onLoginFailed", "Function onLoginFailed");
        //Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        mLoginButton.setEnabled(true);
        dialog.show();
    }

    public boolean validate() {
        boolean valid = true;

        String email = mEmailText.getText().toString();
        String password = mPasswordText.getText().toString();

        if (email.isEmpty()) {
            mEmailText.setError(getString(R.string.error_field_required));
            valid = false;
        } else {
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                mEmailText.setError(getString(R.string.error_invalid_email));
                valid = false;
            } else {
                mEmailText.setError(null);
            }
        }

        if (password.isEmpty()) {
            mPasswordText.setError(getString(R.string.error_field_required));
            valid = false;
        } else {
            if (password.length() < 6 || password.length() > 20) {
                mPasswordText.setError(getString(R.string.error_incorrect_password));
                valid = false;
            } else {
                mPasswordText.setError(null);
            }
        }

        return valid;
    }
}
