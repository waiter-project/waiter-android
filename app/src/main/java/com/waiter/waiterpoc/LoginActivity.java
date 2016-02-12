package com.waiter.waiterpoc;

import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Sign In fragment
     */
    public static class SignInFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        // UI references.
        private EditText mEmailView;
        private EditText mPasswordView;
        private View mProgressView;
        private View mSignInFormView;

        public SignInFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static SignInFragment newInstance(int sectionNumber) {
            SignInFragment fragment = new SignInFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_signin, container, false);
            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

            // Set up the sign in form
            mEmailView = (EditText) rootView.findViewById(R.id.emailText);

            mPasswordView = (EditText) rootView.findViewById(R.id.passwordText);
            mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == R.id.login || id == EditorInfo.IME_NULL) {
                        //attemptLogin();
                        return true;
                    }
                    return false;
                }
            });

            Button mEmailSignInButton = (Button) rootView.findViewById(R.id.email_sign_in_button);
            mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //attemptLogin();
                }
            });

            mSignInFormView = rootView.findViewById(R.id.signin_form);
            mProgressView = rootView.findViewById(R.id.signin_progress);

            return rootView;
        }
    }

    /**
     * Sign Up fragment
     */
    public static class SignUpFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        // UI references.
        private EditText mFirstNameView;
        private EditText mLastNameView;
        private EditText mEmailView;
        private EditText mPasswordView;
        private EditText mRepeatPasswordView;
        private View mProgressView;
        private View mSignUpFormView;

        public SignUpFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static SignUpFragment newInstance(int sectionNumber) {
            SignUpFragment fragment = new SignUpFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_signup, container, false);
            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

            // Set up the sign in form
            mFirstNameView= (EditText) rootView.findViewById(R.id.firstNameText);
            mLastNameView = (EditText) rootView.findViewById(R.id.lastNameText);
            mEmailView = (EditText) rootView.findViewById(R.id.emailText);

            mPasswordView = (EditText) rootView.findViewById(R.id.passwordText);
            mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == R.id.login || id == EditorInfo.IME_NULL) {
                        //attemptLogin();
                        return true;
                    }
                    return false;
                }
            });
            mRepeatPasswordView = (EditText) rootView.findViewById(R.id.repeatPasswordText);

            Button mEmailSignUpButton = (Button) rootView.findViewById(R.id.email_sign_up_button);
            mEmailSignUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //attemptLogin();
                }
            });

            mSignUpFormView = rootView.findViewById(R.id.signup_form);
            mProgressView = rootView.findViewById(R.id.signup_progress);

            return rootView;
        }
    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkbox_license:
                if (checked) {
                    // Put some meat on the sandwich
                }
                else {
                    // Remove the meat
                }
                break;
            // TODO: Veggie sandwich
        }
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return SignInFragment.newInstance(position + 1);
                case 1:
                    return SignUpFragment.newInstance(position + 1);
            }
            return null;
            //return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SIGN IN";
                case 1:
                    return "SIGN UP";
            }
            return null;
        }
    }
}
