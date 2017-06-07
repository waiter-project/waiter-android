package com.waiter.data;

import android.content.Context;
import android.widget.Filter;

import com.waiter.MainActivity;
import com.waiter.models.Event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SuggestionHelper {

    private static List<Event> sEventWrappers = new ArrayList<>();

    private static List<EventSuggestion> sEventSuggestions = new ArrayList<>();

    public interface OnFindEventsListener {
        void onResults(List<Event> results);
    }

    public interface OnFindSuggestionsListener {
        void onResults(List<EventSuggestion> results);
    }

    public static List<EventSuggestion> getHistory(Context context, int count) {

        List<EventSuggestion> suggestionList = new ArrayList<>();
        EventSuggestion eventSuggestion;
        for (int i = 0; i < sEventSuggestions.size(); i++) {
            eventSuggestion = sEventSuggestions.get(i);
            eventSuggestion.setIsHistory(true);
            suggestionList.add(eventSuggestion);
            if (suggestionList.size() == count) {
                break;
            }
        }
        return suggestionList;
    }

    public static void resetSuggestionsHistory() {
        for (EventSuggestion colorSuggestion : sEventSuggestions) {
            colorSuggestion.setIsHistory(false);
        }
    }

    public static void findSuggestions(Context context, String query, final int limit, final long simulatedDelay,
                                       final OnFindSuggestionsListener listener) {
        initEventWrapperList(context);

        new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                try {
                    Thread.sleep(simulatedDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                SuggestionHelper.resetSuggestionsHistory();
                List<EventSuggestion> suggestionList = new ArrayList<>();
                if (!(constraint == null || constraint.length() == 0)) {

                    for (EventSuggestion suggestion : sEventSuggestions) {
                        if (suggestion.getBody().toUpperCase()
                                .startsWith(constraint.toString().toUpperCase())) {

                            suggestionList.add(suggestion);
                            if (limit != -1 && suggestionList.size() == limit) {
                                break;
                            }
                        }
                    }
                }

                FilterResults results = new FilterResults();
                Collections.sort(suggestionList, new Comparator<EventSuggestion>() {
                    @Override
                    public int compare(EventSuggestion lhs, EventSuggestion rhs) {
                        return lhs.getIsHistory() ? -1 : 0;
                    }
                });
                results.values = suggestionList;
                results.count = suggestionList.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (listener != null) {
                    listener.onResults((List<EventSuggestion>) results.values);
                }
            }
        }.filter(query);

    }

    public static void findEvents(Context context, String query, final OnFindEventsListener listener) {
        initEventWrapperList(context);

        new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {


                List<Event> suggestionList = new ArrayList<>();

                if (!(constraint == null || constraint.length() == 0)) {

                    for (Event color : sEventWrappers) {
                        if (color.getName().toUpperCase()
                                .startsWith(constraint.toString().toUpperCase())) {

                            suggestionList.add(color);
                        }
                    }

                }

                FilterResults results = new FilterResults();
                results.values = suggestionList;
                results.count = suggestionList.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (listener != null) {
                    listener.onResults((List<Event>) results.values);
                }
            }
        }.filter(query);

    }

    private static void initEventWrapperList(Context context) {

        sEventSuggestions = new ArrayList<>();
        for (Event event : MainActivity.mEventList) {
            sEventSuggestions.add(new EventSuggestion(event.getName()));

//        if (sEventSuggestions.isEmpty()) {
//            sEventSuggestions = new ArrayList<>();
//            for (Event event : MainActivity.mEventList) {
//                sEventSuggestions.add(new EventSuggestion(event.getName()));
//            }
//            sEventSuggestions.add(new EventSuggestion("toto"));
//            sEventSuggestions.add(new EventSuggestion("tata"));
//            sEventSuggestions.add(new EventSuggestion("titi"));
        }
    }

    public static void refreshSuggestions() {
        sEventSuggestions.clear();
        for (Event event : MainActivity.mEventList) {
            sEventSuggestions.add(new EventSuggestion(event.getName()));
        }
    }

}
