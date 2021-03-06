package com.waiter.data;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

public class EventSuggestion implements SearchSuggestion {

    private String mEventName;
    private boolean mIsHistory = false;

    private String mEventId;
    private int mEventPosition;

    public EventSuggestion(String suggestion, String id, int position) {
        this.mEventName = suggestion;
        this.mEventId = id;
        this.mEventPosition = position;
    }

    public EventSuggestion(Parcel source) {
        this.mEventName = source.readString();
        this.mIsHistory = source.readInt() != 0;
    }

    public void setIsHistory(boolean isHistory) {
        this.mIsHistory = isHistory;
    }

    public boolean getIsHistory() {
        return this.mIsHistory;
    }

    @Override
    public String getBody() {
        return mEventName;
    }

    public String getEventId() {
        return mEventId;
    }

    public int getEventPosition() {
        return mEventPosition;
    }

    public static final Creator<EventSuggestion> CREATOR = new Creator<EventSuggestion>() {
        @Override
        public EventSuggestion createFromParcel(Parcel in) {
            return new EventSuggestion(in);
        }

        @Override
        public EventSuggestion[] newArray(int size) {
            return new EventSuggestion[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mEventName);
        dest.writeInt(mIsHistory ? 1 : 0);
    }
}
