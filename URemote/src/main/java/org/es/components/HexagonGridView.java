package org.es.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import org.es.graphics.Hexagon;

import java.util.Random;

/**
 * Created by Cyril Leroux on 05/12/13.
 */
public class HexagonGridView extends DrawingView {

    public HexagonGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected DrawingThread createDrawingThread(SurfaceHolder holder, Context context) {
        return new HexagonGridThread(holder, context);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        final int action = event.getActionMasked();
        switch (action) {

            case MotionEvent.ACTION_MOVE:
                mThread.addUserEvent(new UserEvent(UserEvent.ACTION_MOVE, event.getX(), event.getY()));
                return true;

            case MotionEvent.ACTION_DOWN:
                mThread.addUserEvent(new UserEvent(UserEvent.ACTION_DOWN, event.getX(), event.getY()));
                return true;

            case MotionEvent.ACTION_UP:
                mThread.addUserEvent(new UserEvent(UserEvent.ACTION_UP, event.getX(), event.getY()));
                return true;
        }

        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        final int action = event.getAction();
        final int keyCode = event.getKeyCode();

        if (action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            mThread.addUserEvent(new UserEvent(UserEvent.KEYCODE_LEFT, UserEvent.ACTION_DOWN));
            return true;

        } else if (action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            mThread.addUserEvent(new UserEvent(UserEvent.KEYCODE_LEFT, UserEvent.ACTION_UP));
            return true;

        } else if (action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            mThread.addUserEvent(new UserEvent(UserEvent.KEYCODE_RIGHT, UserEvent.ACTION_DOWN));
            return true;

        } else if (action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            mThread.addUserEvent(new UserEvent(UserEvent.KEYCODE_RIGHT, UserEvent.ACTION_UP));
            return true;

        } else if (action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            mThread.addUserEvent(new UserEvent(UserEvent.KEYCODE_RIGHT, UserEvent.ACTION_DOWN));
            return true;

        } else if (action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            mThread.addUserEvent(new UserEvent(UserEvent.KEYCODE_RIGHT, UserEvent.ACTION_UP));
            return true;
        }

        return super.dispatchKeyEvent(event);
    }
}
