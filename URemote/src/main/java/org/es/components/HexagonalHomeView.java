package org.es.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

import org.es.graphics.Hexagon;

import java.util.Random;

/**
 * Created by Cyril Leroux on 05/12/13.
 */
public class HexagonalHomeView extends View {

    /** Coefficient to compute the hexagon side. */
    private static final float HEXAGON_SIDE_COEF = 0.2f;
    /** The paint's stroke width, used whenever the paint's style is Stroke or StrokeAndFill. */
    private static final int STROKE_WIDTH = 3;

    private final Paint mPaint;
    private final Paint mPaintOnTouch;

    private Hexagon mHexagon = null;
    private boolean mIsTouched = false;

    public HexagonalHomeView(Context context) {
        super(context);

        // Paint
        mPaint = new Paint();
        mPaint.setARGB(255, 70, 200, 200);
        mPaint.setAntiAlias(true);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mPaint.setStrokeWidth(STROKE_WIDTH);

        // Paint
        mPaintOnTouch = new Paint(mPaint);
        mPaintOnTouch.setARGB(255, 255, 255, 255);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        final float centerX = canvas.getWidth()  / 2;
        final float centerY = canvas.getHeight() / 2;

        // Draw the hexagons
        final float hexagonSide = canvas.getWidth() * HEXAGON_SIDE_COEF;
        mHexagon = new Hexagon(hexagonSide);

        mHexagon.moveTo(centerX, centerY - mHexagon.getHeight() - hexagonSide / 2);
        mHexagon.draw(canvas, mPaint);

        mHexagon.moveCenterTo(centerX - mHexagon.getWidth() / 2, centerY);
        mHexagon.draw(canvas, mPaint);

        mHexagon.moveCenterTo(centerX + mHexagon.getWidth() / 2, centerY);
        mHexagon.draw(canvas, mPaint);

        mHexagon.moveTo(centerX, centerY + hexagonSide / 2);
        mHexagon.draw(canvas, mIsTouched ? mPaintOnTouch : mPaint);
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent event) {
//
//        if (mHexagon == null) {
//            return super.onTouchEvent(event);
//        }
//
//        final boolean isTouched = mHexagon.pointInHexagon(event.getX(), event.getY());
//        if (isTouched != mIsTouched) {
//            mIsTouched = isTouched;
//            invalidate();
//        }
//        return super.dispatchTouchEvent(event);
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mHexagon == null) {
            return super.onTouchEvent(event);
        }

        final boolean isTouched = mHexagon.pointInHexagon(event.getX(), event.getY());
        if (isTouched != mIsTouched) {
            mIsTouched = isTouched;
            invalidate();
        }
        return super.onTouchEvent(event);
    }
}
