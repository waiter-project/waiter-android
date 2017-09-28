package com.waiter;

import android.util.Log;

import com.onesignal.OSNotification;
import com.onesignal.OneSignal;

import org.json.JSONObject;

class WaiterNotificationReceivedHandler implements OneSignal.NotificationReceivedHandler {

    private static final String TAG = "NotifReceivedHandler";

    @Override
    public void notificationReceived(OSNotification notification) {
        Log.d(TAG, "notificationReceived: Notification received");

        JSONObject data = notification.payload.additionalData;
        String customKey;

        if (data != null) {
            customKey = data.optString("customkey", null);
            if (customKey != null)
                Log.i("OneSignalExample", "customkey set with value: " + customKey);
        }
    }
}
