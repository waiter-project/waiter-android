package com.waiter;

import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

import com.securepreferences.SecurePreferences;

public class ProfileActivity extends AppCompatActivity {

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

    private AlertDialog mDialog;

    private boolean editMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupUI();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setupUI() {
        SharedPreferences prefs = new SecurePreferences(this);
        String userEmail = prefs.getString("user_email", "No email address");
        String firstName = prefs.getString("first_name", "No first name");
        String lastName = prefs.getString("last_name", "No last name");
        String phoneNumber = prefs.getString("phone_number", "No phone number");
        String streetAddress = prefs.getString("street_address", "No street address");

        // View Mode
        mPictureView = (ImageView) findViewById(R.id.profile_picture);
        mNameView = (TextView) findViewById(R.id.profile_name);
        mEmailView = (TextView) findViewById(R.id.profile_email);
        mPhoneView = (TextView) findViewById(R.id.profile_phone);
        mAddressView = (TextView) findViewById(R.id.profile_address);
        mPasswordButton = (Button) findViewById(R.id.password_btn);
        mNameView.setText(firstName + " " + lastName);
        mEmailView.setText(userEmail);
        mPhoneView.setText(phoneNumber);
        mAddressView.setText(streetAddress);

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
        Toast.makeText(this, "Change password clicked", Toast.LENGTH_SHORT).show();
    }

    private void editMode() {
        showEditMode();
    }

    public void onClick_Save_Changes(View view) {
        hideEditMode();
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
