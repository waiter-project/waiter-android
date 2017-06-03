package com.waiter;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.securepreferences.SecurePreferences;
import com.waiter.models.ErrorResponse;
import com.waiter.models.RequestUpdatePassword;
import com.waiter.network.ServiceGenerator;
import com.waiter.network.WaiterClient;
import com.waiter.utils.CustomTextWatcher;
import com.waiter.utils.ErrorUtils;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdatePasswordActivity extends AppCompatActivity {

    private EditText mInputCurrentPassword, mInputPassword, mInputPasswordConfirmation;
    private TextInputLayout mInputLayoutCurrentPassword, mInputLayoutPassword, mInputLayoutPasswordConfirmation;
    private ProgressDialog mProgressDialog;
    private LinearLayout mRootView;

    private WaiterClient waiterClient;
    private RequestUpdatePassword requestUpdatePassword;
    private ErrorResponse errorResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        setupUI();

        waiterClient = ServiceGenerator.createService(WaiterClient.class);
        requestUpdatePassword = new RequestUpdatePassword();
    }

    private void setupUI() {
        mInputCurrentPassword = (EditText) findViewById(R.id.input_current_password);
        mInputPassword = (EditText) findViewById(R.id.input_password);
        mInputPasswordConfirmation = (EditText) findViewById(R.id.input_password_confirmation);

        mInputLayoutCurrentPassword = (TextInputLayout) findViewById(R.id.input_layout_current_password);
        mInputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        mInputLayoutPasswordConfirmation = (TextInputLayout) findViewById(R.id.input_layout_password_confirmation);

        mInputCurrentPassword.addTextChangedListener(new CustomTextWatcher(mInputCurrentPassword, mInputLayoutCurrentPassword, getString(R.string.is_invalid, "Password")));
        mInputPassword.addTextChangedListener(new CustomTextWatcher(mInputPassword, mInputLayoutPassword, getString(R.string.password_instructions)));
//        mInputPasswordConfirmation.addTextChangedListener(new CustomTextWatcher(mInputPasswordConfirmation, mInputLayoutPasswordConfirmation, getString(R.string.err_msg_password_confirmation)));

        mProgressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.updating_password));

        mRootView = (LinearLayout) findViewById(R.id.root_view);
    }

    public void onClick_Update_Password(View view) {
        Utils.hideKeyboard(this);

        boolean validForm = submitForm();
        if (validForm) {
            mInputCurrentPassword.clearFocus();
            mInputPassword.clearFocus();
            mInputPasswordConfirmation.clearFocus();
            updatePassword();
        } else {
            Snackbar.make(mRootView, "A field is invalid", Snackbar.LENGTH_SHORT).show();
        }
    }

    private boolean submitForm() {
        return !mInputLayoutCurrentPassword.isErrorEnabled()
                && !mInputLayoutPassword.isErrorEnabled()
                && !mInputLayoutPasswordConfirmation.isErrorEnabled();
    }

    private void updatePassword() {
        mProgressDialog.show();

        SharedPreferences prefs = new SecurePreferences(this);
        String authToken = prefs.getString("auth_token", "");
        String userId = prefs.getString("user_id", "");

        requestUpdatePassword.setPassword(mInputCurrentPassword.getText().toString());
        requestUpdatePassword.setNewPassword(mInputPassword.getText().toString());

        Call<ResponseBody> call = waiterClient.updatePassword(authToken, userId, requestUpdatePassword);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                mProgressDialog.dismiss();
                if (response.isSuccessful()) {
                    ResponseBody body = response.body();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    errorResponse = ErrorUtils.parseError(response);
                    if (errorResponse != null) {
                        if (errorResponse.getData().getCauses() == null || errorResponse.getData().getCauses().isEmpty()) {
                            Snackbar.make(mRootView, errorResponse.getData().getMessage(), Snackbar.LENGTH_LONG).show();
                        } else {
                            Snackbar.make(mRootView, errorResponse.getData().getCauses().get(0), Snackbar.LENGTH_LONG).show();
                        }
                    } else {
                        Snackbar.make(mRootView, getString(R.string.internal_error), Snackbar.LENGTH_LONG).show();
                    }
                    setResult(RESULT_CANCELED);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                mProgressDialog.dismiss();
                Snackbar.make(mRootView, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                setResult(RESULT_CANCELED);
            }
        });
    }
}
