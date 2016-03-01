package com.waiter.waiterpoc.fragments;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.waiter.waiterpoc.FullEventActivity;
import com.waiter.waiterpoc.LoadingPopup;
import com.waiter.waiterpoc.R;
import com.waiter.waiterpoc.models.User;

import java.util.List;

public class WaitersAdapter extends ArrayAdapter<User> {

    public WaitersAdapter(Context context, List<User> events) {
        super(context, 0, events);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_layout_waiter, parent, false);
        }

        WaitersViewHolder viewHolder = (WaitersViewHolder) convertView.getTag();
        if (viewHolder == null){
            viewHolder = new WaitersViewHolder();
            viewHolder.picture = (ImageView) convertView.findViewById(R.id.waiterPicture);
            viewHolder.name = (TextView) convertView.findViewById(R.id.waiterName);
            viewHolder.rating = (RatingBar) convertView.findViewById(R.id.waiterRating);
            viewHolder.button = (Button) convertView.findViewById(R.id.waiterButton);
            convertView.setTag(viewHolder);
        }

        //getItem(position) récupère l'item [position] de la List<User>
        User waiter = getItem(position);

        String name = waiter.getFirstname();
        int rating = waiter.getNbMark();

        if (name == null) {
            name = "null";
        }

        viewHolder.name.setText(name);
        viewHolder.rating.setRating(rating);
        viewHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getContext(), LoadingPopup.class);
                getContext().startActivity(myIntent);
            }
        });

        return convertView;

    }

    private class WaitersViewHolder {
        public ImageView picture;
        public TextView name;
        public RatingBar rating;
        public Button button;
    }
}
