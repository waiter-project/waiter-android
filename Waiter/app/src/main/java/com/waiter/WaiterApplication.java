package com.waiter;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

import com.onesignal.OneSignal;

public class WaiterApplication extends Application {

    private static WaiterApplication singleton;

    public static WaiterApplication getInstance(){
        return singleton;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .setNotificationOpenedHandler(new WaiterNotificationOpenedHandler())
                .setNotificationReceivedHandler(new WaiterNotificationReceivedHandler())
                .init();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

}
