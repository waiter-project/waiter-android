package com.waiter;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.securepreferences.SecurePreferences;
import com.waiter.models.ErrorResponse;
import com.waiter.models.RequestUpdateProfile;
import com.waiter.network.ServiceGenerator;
import com.waiter.network.WaiterClient;
import com.waiter.utils.CustomTextWatcher;
import com.waiter.utils.ErrorUtils;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PASSWORD = 2;

    // View Mode
    private ImageView mPictureView;
    private TextView mNameView;
    private TextView mEmailView;
    private TextView mPhoneView;
    private TextView mAddressView;
    private Button mPasswordButton;

    // Edit Mode
    private LinearLayout mEditLayoutName;
    private EditText mInputFirstName, mInputLastName;
    private TextInputLayout mInputLayoutFirstName, mInputLayoutLastName;
    private EditText mInputEmail;
    private TextInputLayout mInputLayoutEmail;
    private EditText mInputPhone;
    private TextInputLayout mInputLayoutPhone;
    private EditText mInputAddress;
    private TextInputLayout mInputLayoutAddress;
    private Button mSaveButton;
    private ProgressDialog mProgressDialog;

    private LinearLayout mRootView;
    private AlertDialog mDialog;

    private boolean editMode = false;

    private WaiterClient waiterClient;
    private RequestUpdateProfile requestUpdateProfile;
    private ErrorResponse errorResponse;
    private String authToken;
    private String userId;

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prefs = new SecurePreferences(this);
        setupUI();

        authToken = prefs.getString("auth_token", "");
        userId = prefs.getString("user_id", "");

        waiterClient = ServiceGenerator.createService(WaiterClient.class);
        requestUpdateProfile = new RequestUpdateProfile();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void refreshData() {
        String userEmail = prefs.getString("user_email", "No email address");
        String firstName = prefs.getString("first_name", "No first name");
        String lastName = prefs.getString("last_name", "No last name");
        String phoneNumber = prefs.getString("phone_number", "No phone number");
        String streetAddress = prefs.getString("street_address", "No street address");

        // View Mode
        mNameView.setText(firstName + " " + lastName);
        mEmailView.setText(userEmail);
        mPhoneView.setText(phoneNumber);
        mAddressView.setText(streetAddress);

        // Edit Mode
        if (!firstName.equals("No first name"))
            mInputFirstName.setText(firstName);
        if (!lastName.equals("No last name"))
            mInputLastName.setText(lastName);
        if (!userEmail.equals("No email address"))
            mInputEmail.setText(userEmail);
        if (!phoneNumber.equals("No phone number"))
            mInputPhone.setText(phoneNumber);
        if (!streetAddress.equals("No street address"))
            mInputAddress.setText(streetAddress);
    }

    private void setupUI() {
        // View Mode
        mPictureView = (ImageView) findViewById(R.id.profile_picture);
        mNameView = (TextView) findViewById(R.id.profile_name);
        mEmailView = (TextView) findViewById(R.id.profile_email);
        mPhoneView = (TextView) findViewById(R.id.profile_phone);
        mAddressView = (TextView) findViewById(R.id.profile_address);
        mPasswordButton = (Button) findViewById(R.id.password_btn);

        // Edit Mode
        mEditLayoutName = (LinearLayout) findViewById(R.id.edit_layout_name);
        mInputFirstName = (EditText) findViewById(R.id.input_fname);
        mInputLastName = (EditText) findViewById(R.id.input_lname);
        mInputLayoutFirstName = (TextInputLayout) findViewById(R.id.input_layout_fname);
        mInputLayoutLastName = (TextInputLayout) findViewById(R.id.input_layout_lname);
        mInputEmail = (EditText) findViewById(R.id.input_email);
        mInputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        mInputPhone = (EditText) findViewById(R.id.input_phone);
        mInputLayoutPhone = (TextInputLayout) findViewById(R.id.input_layout_phone);
        mInputAddress = (EditText) findViewById(R.id.input_address);
        mInputLayoutAddress = (TextInputLayout) findViewById(R.id.input_layout_address);
        mSaveButton = (Button) findViewById(R.id.save_btn);

        refreshData();

        mInputFirstName.addTextChangedListener(new CustomTextWatcher(mInputFirstName, mInputLayoutFirstName, getString(R.string.is_required, "First name")));
        mInputLastName.addTextChangedListener(new CustomTextWatcher(mInputLastName, mInputLayoutLastName, getString(R.string.is_required, "Last name")));
        mInputEmail.addTextChangedListener(new CustomTextWatcher(mInputEmail, mInputLayoutEmail, getString(R.string.err_msg_email)));
        mInputPhone.addTextChangedListener(new CustomTextWatcher(mInputPhone, mInputLayoutPhone, getString(R.string.is_invalid, "Phone number")));
        mInputAddress.addTextChangedListener(new CustomTextWatcher(mInputAddress, mInputLayoutAddress, getString(R.string.is_invalid, "Street address")));

        mRootView = (LinearLayout) findViewById(R.id.root_view);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_discard_changes)
                .setPositiveButton(R.string.discard, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        hideEditMode();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        mDialog = builder.create();

        mProgressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.updating_profile));
    }

    @Override
    public void onBackPressed() {
        if (editMode) {
            mDialog.show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit) {
            if (editMode) {
                mDialog.show();
            } else {
                editMode();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick_Change_Password(View view) {
        Intent intent = new Intent(this, UpdatePasswordActivity.class);
        startActivityForResult(intent, REQUEST_CODE_PASSWORD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PASSWORD) {
            if (resultCode == RESULT_OK) {
                Snackbar.make(mRootView, getString(R.string.update_success, "Password"), Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private void editMode() {
        showEditMode();
    }

    public void onClick_Save_Changes(View view) {
        boolean validForm = submitForm();
        if (validForm) {
            mInputFirstName.clearFocus();
            mInputLastName.clearFocus();
            mInputEmail.clearFocus();
            mInputPhone.clearFocus();
            mInputAddress.clearFocus();
            updateProfile();
        } else {
            Snackbar.make(mRootView, "A field is invalid", Snackbar.LENGTH_SHORT).show();
        }
    }

    private boolean submitForm() {
        return !mInputLayoutFirstName.isErrorEnabled()
                && !mInputLayoutLastName.isErrorEnabled()
                && !mInputLayoutEmail.isErrorEnabled()
                && !mInputLayoutPhone.isErrorEnabled()
                && !mInputLayoutAddress.isErrorEnabled();
    }

    private void updateProfile() {
        mProgressDialog.show();

        requestUpdateProfile.setFirstName(mInputFirstName.getText().toString());
        requestUpdateProfile.setLastName(mInputLastName.getText().toString());
        requestUpdateProfile.setEmail(mInputEmail.getText().toString());
//        requestUpdateProfile.setPhone(mInputPhone.getText().toString());
//        requestUpdateProfile.setAddress(mInputAddress.getText().toString());

        Call<ResponseBody> call = waiterClient.updateProfile(authToken, userId, requestUpdateProfile);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                mProgressDialog.dismiss();
                if (response.isSuccessful()) {
                    saveChanges();
                    refreshData();
                    hideEditMode();
                    ResponseBody body = response.body();
                    if (body != null) {
                        Snackbar.make(mRootView, getString(R.string.update_success, "Profile"), Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(mRootView, getString(R.string.response_body_null), Snackbar.LENGTH_LONG).show();
                    }
                    setResult(RESULT_OK);
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

    private void saveChanges() {
        prefs.edit().putString("user_email", mInputEmail.getText().toString())
                .putString("first_name", mInputFirstName.getText().toString())
                .putString("last_name", mInputLastName.getText().toString())
                .apply();
    }

    private void showEditMode() {
        editMode = true;

        mNameView.setVisibility(View.GONE);
        mEditLayoutName.setVisibility(View.VISIBLE);

        mEmailView.setVisibility(View.GONE);
        mInputLayoutEmail.setVisibility(View.VISIBLE);

        mPhoneView.setVisibility(View.GONE);
        mInputLayoutPhone.setVisibility(View.VISIBLE);

        mAddressView.setVisibility(View.GONE);
        mInputLayoutAddress.setVisibility(View.VISIBLE);

        mPasswordButton.setVisibility(View.GONE);
        mSaveButton.setVisibility(View.VISIBLE);
    }

    private void hideEditMode() {
        editMode = false;

        mNameView.setVisibility(View.VISIBLE);
        mEditLayoutName.setVisibility(View.GONE);

        mEmailView.setVisibility(View.VISIBLE);
        mInputLayoutEmail.setVisibility(View.GONE);

        mPhoneView.setVisibility(View.VISIBLE);
        mInputLayoutPhone.setVisibility(View.GONE);

        mAddressView.setVisibility(View.VISIBLE);
        mInputLayoutAddress.setVisibility(View.GONE);

        mPasswordButton.setVisibility(View.VISIBLE);
        mSaveButton.setVisibility(View.GONE);
    }
}
