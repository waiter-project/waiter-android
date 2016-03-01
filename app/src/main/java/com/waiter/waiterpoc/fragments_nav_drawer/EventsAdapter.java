package com.waiter.waiterpoc.fragments_nav_drawer;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.waiter.waiterpoc.R;
import com.waiter.waiterpoc.models.Event;

import java.util.List;

public class EventsAdapter extends ArrayAdapter<Event> {

    public EventsAdapter(Context context, List<Event> events) {
        super(context, 0, events);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_layout_event, parent, false);
        }

        EventsViewHolder viewHolder = (EventsViewHolder) convertView.getTag();
        if (viewHolder == null){
            viewHolder = new EventsViewHolder();
            viewHolder.picture = (ImageView) convertView.findViewById(R.id.eventPicture);
            viewHolder.name = (TextView) convertView.findViewById(R.id.eventName);
            viewHolder.address = (TextView) convertView.findViewById(R.id.eventAddress);
            viewHolder.date = (TextView) convertView.findViewById(R.id.eventDate);
            convertView.setTag(viewHolder);
        }

        //getItem(position) récupère l'item [position] de la List<Event>
        Event event = getItem(position);

        String name = event.getName();
        String address = event.getAddress();
        String date = event.getDate();

        if (name == null) {
            name = "null";
        }
        if (address == null) {
            address = "null";
        }
        if (date == null) {
            date = "null";
        } else {
            date = date.substring(0, 10);
        }

        viewHolder.name.setText(name);
        viewHolder.address.setText(address);
        viewHolder.date.setText(date);

        //viewHolder.comment.setText(Html.fromHtml(event.getComment()));

        return convertView;
    }

    private class EventsViewHolder {
        public ImageView picture;
        public TextView name;
        public TextView address;
        public TextView date;
    }
}
