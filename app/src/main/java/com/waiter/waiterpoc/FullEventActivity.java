package com.waiter.waiterpoc;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toolbar;

import com.waiter.waiterpoc.dummy.DummyContent;

public class FullEventActivity extends AppCompatActivity implements EventFragment.OnListFragmentInteractionListener, WaiterFragment.OnListFragmentInteractionListener {


    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_event);

        WaiterFragment fragment = new WaiterFragment();
        FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();


    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }
}
