package com.waiter.waiterpoc;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.waiter.waiterpoc.models.GenericResponse;
import com.waiter.waiterpoc.models.RegisterAttempt;
import com.waiter.waiterpoc.models.RegisterResponse;
import com.waiter.waiterpoc.network.CheckNetwork;
import com.waiter.waiterpoc.network.ServiceGenerator;
import com.waiter.waiterpoc.network.WaiterService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    // UI References
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mEmailText;
    private EditText mPasswordText;
    private EditText mRepeatPasswordText;
    private TextView mSignInLink;
    private Button mRegisterButton;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private Intent myIntent;

    // Initial variables
    //private SharedPreferences sp;
    private static boolean connected = false;

    public static boolean isConnected() {
        return connected;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Set initial variables
        //sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.getsContext());

        // Set up the register form
        mFirstName = (EditText) findViewById(R.id.input_first_name);
        mFirstName.requestFocus();
        mLastName = (EditText) findViewById(R.id.input_last_name);
        mEmailText = (EditText) findViewById(R.id.input_email);
        mPasswordText = (EditText) findViewById(R.id.input_password);
        mRepeatPasswordText = (EditText) findViewById(R.id.input_repeat_password);

        // Dismiss virtual keyboard at start
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // Check email_prefilled from LoginActivity
        myIntent = getIntent();
        String email_prefilled = myIntent.getStringExtra("email_prefilled");
        if (email_prefilled != null){
            mEmailText.setText(email_prefilled);
        }

        // Set up various buttons/link
        mRegisterButton = (Button) findViewById(R.id.btn_signup);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        mSignInLink = (TextView) findViewById(R.id.link_sign_in);
        mSignInLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });

        // Set up the AlertDialog
        builder = new AlertDialog.Builder(SignupActivity.this);
        builder.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setTitle(R.string.signup_failed_title);
        dialog = builder.create();

        // Check internet connection
        if (!CheckNetwork.isInternetAvailable(this)) {
            Toast.makeText(SignupActivity.this, "No internet connection", Toast.LENGTH_LONG).show();
        }
    }

    public void signup() {
        Log.d("SignupActivity", "Signup");

        if (!CheckNetwork.isInternetAvailable(this)) {
            builder.setMessage(R.string.no_internet);
            dialog = builder.create();
            dialog.show();
            return ;
        }

        if (!validate()) {
            //onSignupFailed();
            return ;
        }

        mRegisterButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating account...");
        progressDialog.show();

        final String firstName = mFirstName.getText().toString();
        final String lastName = mLastName.getText().toString();
        final String email = mEmailText.getText().toString();
        final String password = mPasswordText.getText().toString();
        final String repeatPassword = mRepeatPasswordText.getText().toString();

        WaiterService service = ServiceGenerator.createService(WaiterService.class);

        RegisterAttempt registerAttempt = new RegisterAttempt(firstName, lastName, email, password, repeatPassword);

        Call<GenericResponse<RegisterResponse>> call = service.basicRegister(registerAttempt);

        call.enqueue(new Callback<GenericResponse<RegisterResponse>>() {
            @Override
            public void onResponse(Call<GenericResponse<RegisterResponse>> call, Response<GenericResponse<RegisterResponse>> response) {
                if (response.isSuccess()) {
                    Log.d("Success", "Return: " + response.message() + " - Raw: " + response.raw().toString());
                    /*
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("email", email);
                    editor.putString("password", password);
                    editor.commit();
                    */
                    progressDialog.dismiss();

                    RegisterResponse registerResponse = response.body().getData();

                    onSignupSuccess(email, password, registerResponse.getFirstname(), registerResponse.getLastname());
                } else {
                    Log.d("Failure", "Return: " + response.message() + " - Raw: " + response.raw().toString());
                    progressDialog.dismiss();
                    builder.setMessage(R.string.signup_failed_email);
                    dialog = builder.create();
                    onSignupFailed();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<RegisterResponse>> call, Throwable t) {
                Log.d("Error", t.getMessage());
                progressDialog.dismiss();
                builder.setMessage(R.string.unknown_error);
                dialog = builder.create();
                onSignupFailed();
            }
        });
    }

    public void onSignupSuccess(String email, String password, String firstname, String lastname) {
        Log.d("onSignupSuccess", "Function onSignupSuccess");
        mRegisterButton.setEnabled(true);
        getIntent().putExtra("email", email);
        getIntent().putExtra("password", password);
        getIntent().putExtra("firstname", firstname);
        getIntent().putExtra("lastname", lastname);
        setResult(RESULT_OK, getIntent());
        finish();
    }

    public void onSignupFailed() {
        Log.d("onSignupFailed", "Function onSignupFailed");
        //Toast.makeText(getBaseContext(), "Registration failed", Toast.LENGTH_LONG).show();
        mRegisterButton.setEnabled(true);
        dialog.show();
    }

    public boolean validate() {
        boolean valid = true;

        String firstName = mFirstName.getText().toString();
        String lastName = mLastName.getText().toString();
        String email = mEmailText.getText().toString();
        String password = mPasswordText.getText().toString();
        String repeatPassword = mRepeatPasswordText.getText().toString();

        if (firstName.isEmpty()) {
            mFirstName.setError(getString(R.string.error_field_required));
            valid = false;
        } else {
            mFirstName.setError(null);
        }

        if (lastName.isEmpty()) {
            mLastName.setError(getString(R.string.error_field_required));
            valid = false;
        } else {
            mLastName.setError(null);
        }

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
                mPasswordText.setError(getString(R.string.error_invalid_password));
                valid = false;
            } else {
                mPasswordText.setError(null);
            }
        }

        if (repeatPassword.isEmpty()) {
            mRepeatPasswordText.setError(getString(R.string.error_field_required));
            valid = false;
        } else {
            if (!password.equals(repeatPassword)) {
                mRepeatPasswordText.setError(getString(R.string.no_match));
                valid = false;
            } else {
                mRepeatPasswordText.setError(null);
            }
        }

        return valid;
    }
}