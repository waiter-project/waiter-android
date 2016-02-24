package com.waiter.waiterpoc;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.waiter.waiterpoc.dummy.DummyContent;

public class LoadingPopup extends AppCompatActivity implements WaiterFragment.OnListFragmentInteractionListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_popup);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .6));
    }

    /*
    public View getView(int postion, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(this).inflate(R.layout.activity_loading_popup,parent, false);
        }

        final TextView mTextView = (TextView) findViewById(R.id.command);

        // A implementer plus tard
        if (wait accepte) {
            mTextView.setText("Waiter is coming");
        }
        while (waiter attend) {
            mTextView.setText("Waiter is doing the wait");
        }
        if (waiter a fini) {
            mTextView.setText("Wait over, venez remplacer votre Waiter avec le code");
        }
        return convertView;
    }*/

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }
}
