package com.cyrillrx.uremote;

import android.app.Application;
import android.graphics.Color;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * @author Cyril Leroux
 *         Created on 23/07/2014.
 */
public class URemoteApp extends Application {

    //    private static final String TAG = URemoteApp.class.getSimpleName();
    public static final int COLOR_DEFAULT = Color.argb(255, 70, 200, 200);

//    private BlockingQueue mRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
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
