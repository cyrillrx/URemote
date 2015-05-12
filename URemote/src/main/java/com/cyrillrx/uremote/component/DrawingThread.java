package com.cyrillrx.uremote.component;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.util.Log;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Parent class for drawing threads
 *
 * @author Cyril Leroux
 *         Created on 22/09/13.
 */
public abstract class DrawingThread extends Thread {

    private static final String TAG = DrawingThread.class.getSimpleName();

    private final SurfaceHolder mSurfaceHolder;
    private final Resources mResources;
    private ConcurrentLinkedQueue<InputEvent> mEventQueue = new ConcurrentLinkedQueue<>();

    /** Number of frame we wish to draw per second. */
    private int mFrameRate = 20;
    /** The time a frame is suppose to stay on screen in milliseconds. */
    private int mFrameDuration = 1000 / mFrameRate;
    /** Indicate whether the thread is suppose to draw or not. */
    private boolean mRunning = true;

    public DrawingThread(SurfaceHolder surfaceHolder, Context context) {
        mSurfaceHolder = surfaceHolder;
        mResources = context.getResources();
    }

    @Override
    public void run() {

        while (mRunning) {
            long start = System.currentTimeMillis();

            update();
            draw();

            final long waitingTimeMillis = mFrameDuration - (System.currentTimeMillis() - start);
            if (waitingTimeMillis > 0) {
                try {
                    sleep(waitingTimeMillis);
                } catch (InterruptedException e) {
                    Log.e(TAG, "DrawingThread - Crash on run()", e);
                    mRunning = false;
                }
            } else {
                // We are running late !
                Log.d(TAG, "Running late ! " + waitingTimeMillis);
            }
        }
    }

    public void setFrameRate(int framePerSecond) {
        mFrameRate = framePerSecond;
        mFrameDuration = 1000 / mFrameRate;
    }

    /**
     * Used to signal the thread whether it should be running or not.
     *
     * @param running true to run, false to shut down
     */
    public void setRunning(boolean running) {
        mRunning = running;
    }

    /** Callback invoked when the surface dimensions change. */
    public void setSurfaceSize(int width, int height) {
        // synchronized to make sure these all change atomically
        synchronized (mSurfaceHolder) {
            updateSurfaceSize(width, height);
        }
    }

    /**
     * Update the surface size atomically.<br />
     * Synchronized is performed by the caller ({@link #setSurfaceSize(int, int)}).
     */
    protected abstract void updateSurfaceSize(int surfaceWidth, int surfaceHeight);

    /** Check user inputs and update data. */
    protected abstract boolean update();

    /** Processes the event to update the view. */
    protected final void processEvent(InputEvent event) {

        if (event instanceof MotionEvent) {
            processEvent((MotionEvent) event);

        } else if (event instanceof KeyEvent) {
            processEvent((KeyEvent) event);
        }
    }

    /** Processes the event to update the view. */
    protected abstract void processEvent(MotionEvent event);

    /** Processes the event to update the view. */
    protected abstract void processEvent(KeyEvent event);

    /** Add a motionEvent that will be processed in {@link #update()}. */
    public boolean addInputEvent(InputEvent event) {
        return mEventQueue.add(event);
    }

    /** @return true if the event queue is not empty. */
    protected boolean hasNext() {
        return !mEventQueue.isEmpty();
    }

    /** Poll the next motionEvent to process. */
    protected InputEvent pollInputEvent() {
        return mEventQueue.poll();
    }

    /** Draw the new frame. */
    private void draw() {
        Canvas canvas = null;
        try {
            canvas = mSurfaceHolder.lockCanvas(null);
            if (canvas != null) {
                doDraw(canvas);
            }
        } finally {
            if (canvas != null) {
                mSurfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    /**
     * Draws current state of the canvas.<br />
     * Canvas null check is performed by the caller ({@link #draw()}).
     */
    protected abstract void doDraw(Canvas canvas);

    protected Resources getResources() {
        return mResources;
    }
}