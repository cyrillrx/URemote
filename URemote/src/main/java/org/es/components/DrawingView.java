package org.es.components;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Parent class for drawing views
 * <p/>
 * Created by Cyril on 22/09/13.
 */
public abstract class DrawingView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "DrawingView";

    protected DrawingThread mThread = null;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        mThread = createDrawingThread(holder, context);

        setFocusable(true);
    }

    protected abstract DrawingThread createDrawingThread(SurfaceHolder holder, Context context);

    public void setFrameRate(int framePerSecond) {
        if (mThread != null) {
            mThread.setFrameRate(framePerSecond);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mThread.setRunning(true);
        mThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mThread.setSurfaceSize(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        // Stop the drawing thread for it not to touch the Surface/Canvas again.
        mThread.setRunning(false);
        try {
            mThread.join();
        } catch (InterruptedException e) { /* swallow */ }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (!hasWindowFocus) {
            if (mThread != null) {
                Log.d(TAG, "onWindowFocusChanged");
                //TODO
                //                thread.pause();
            }
        }
    }

    protected float getCenterX() { return (float) getWidth() / 2f; }

    protected float getCenterY() { return (float) getHeight() / 2f; }
}
