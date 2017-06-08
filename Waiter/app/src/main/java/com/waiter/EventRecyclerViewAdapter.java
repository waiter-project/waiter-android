package com.waiter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.waiter.EventFragment.OnListFragmentInteractionListener;
import com.waiter.models.Event;

import java.util.List;

public class EventRecyclerViewAdapter extends RecyclerView.Adapter<EventRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "EventRecyclerViewAdapte";

    private final List<Event> mValues;
    private final OnListFragmentInteractionListener mListener;

    public EventRecyclerViewAdapter(List<Event> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mTitleView.setText(mValues.get(position).getName());
//        holder.mPriceView.setText(mValues.get(position).getPrice());
        holder.mPriceView.setText(R.string.placeholder_price);
        holder.mDescriptionView.setText(mValues.get(position).getDescription());
        holder.mDateView.setText(mValues.get(position).getDate());
        holder.mStarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageView = (ImageView) v;
                if (holder.savedEvent) {
                    imageView.setImageResource(R.drawable.ic_star_border_grey_500_24dp);
                    holder.savedEvent = false;
                } else {
                    imageView.setImageResource(R.drawable.ic_star_amber_500_24dp);
                    holder.savedEvent = true;
                }
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteractionEvent(holder.getAdapterPosition());
                }
            }
        });
    }

    public void clear() {
        mValues.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Event> list) {
        mValues.addAll(list);
        notifyDataSetChanged();
    }

    public void refreshList() {
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;
        public final TextView mPriceView;
        public final TextView mDescriptionView;
        public final TextView mDateView;
        public final ImageView mStarView;
        public Event mItem;

        public boolean savedEvent = false;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.event_title);
            mPriceView = (TextView) view.findViewById(R.id.event_price);
            mDescriptionView = (TextView) view.findViewById(R.id.event_description);
            mDateView = (TextView) view.findViewById(R.id.event_date);
            mStarView = (ImageView) view.findViewById(R.id.event_star);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }
}
