package com.waiter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.waiter.HistoryActivity.OnListFragmentInteractionListener;
import com.waiter.models.History;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<HistoryRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "HistoryRecyclerViewAdapter";

    private final List<History> mValues;
    private final OnListFragmentInteractionListener mListener;

    public HistoryRecyclerViewAdapter(List<History> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
//        holder.mImageView.setImageResource();
        holder.mTitleView.setText(mValues.get(position).getEvent().getName());

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date convertedDate = dateFormat.parse(mValues.get(position).getWait().getQueueEnd());
            holder.mDurationView.setText(convertedDate.toString());
        } catch (ParseException e) {
            e.printStackTrace();
            holder.mDurationView.setText(R.string.unknown_error);
        }

        double totalPrice = mValues.get(position).getPrice().getTotal();
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String totalPriceFormated = "$" + decimalFormat.format(totalPrice);
        holder.mPriceView.setText(totalPriceFormated);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteractionHistory(holder.mItem);
                }
            }
        });
    }

    public void clear() {
        mValues.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<History> list) {
        mValues.addAll(list);
        notifyDataSetChanged();
    }

    public void refreshList(List<History> list) {
        mValues.clear();
        mValues.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImageView;
        public final TextView mTitleView;
        public final TextView mDurationView;
        public final TextView mPriceView;
        public History mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.waiter_picture);
            mTitleView = (TextView) view.findViewById(R.id.event_title);
            mDurationView = (TextView) view.findViewById(R.id.wait_duration);
            mPriceView = (TextView) view.findViewById(R.id.wait_price);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }
}
