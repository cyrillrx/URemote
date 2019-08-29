package com.cyrillrx.uremote;

import android.app.Application;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatDelegate;

import com.crashlytics.android.Crashlytics;
import com.cyrillrx.logger.Logger;
import com.cyrillrx.logger.Severity;
import com.cyrillrx.logger.extension.CrashlyticsLogger;
import com.cyrillrx.logger.extension.LogCat;
import com.cyrillrx.notifier.Toaster;

import io.fabric.sdk.android.Fabric;

/**
 * @author Cyril Leroux
 *         Created on 23/07/2014.
 */
public class App extends Application {

    static {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }

    //    private static final String TAG = URemoteApp.class.getSimpleName();
    public static final int COLOR_DEFAULT = Color.argb(255, 70, 200, 200);

//    private BlockingQueue mRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();

        Fabric.with(this, new Crashlytics());
        initLogger();
    }

    private void initLogger() {

        boolean isDebug = BuildConfig.DEBUG;

        Toaster.initialize(this, isDebug);

        Logger.initialize();
        Logger.addChild(new LogCat(isDebug ? Severity.VERBOSE : Severity.WARN));
        Logger.addChild(new CrashlyticsLogger(Severity.WARN));
    }

//    public BlockingQueue getRequestQueue() {
//        if (mRequestQueue == null) {
//            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
//        }
//        return mRequestQueue;
//    }
//
//    public <Response> void addToRequestQueue(Request<Response> req, String tag) {
//        // set the default tag if tag is empty
//        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
//        getRequestQueue().add(req);
//    }
//
//    public <T> void addToRequestQueue(Request<T> req) {
//        req.setTag(TAG);
//        getRequestQueue().add(req);
//    }
//
//    public void cancelPendingRequests(Object tag) {
//        if (mRequestQueue != null) {
//            mRequestQueue.cancelAll(tag);
//        }
//    }
}
