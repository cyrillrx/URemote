package org.es.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import org.es.graphics.Hexagon;

/**
 * Created by Cyril on 22/09/13.
 */
public class HexagonGridThread extends DrawingThread {

    /** Coefficient to compute the hexagon side. */
    private static final float HEXAGON_SIDE_COEF = 0.2f;
    /** The paint's stroke width, used whenever the paint's style is Stroke or StrokeAndFill. */
    private static final int STROKE_WIDTH = 3;

    private final Paint mPaint;
    private final Paint mPaintOnTouch;

    private Hexagon[] mHexagon = new Hexagon[4];
    private boolean mIsTouched = false;

    public HexagonGridThread(SurfaceHolder surfaceHolder, Context context) {
        super(surfaceHolder, context);

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
    protected void updateSurfaceSize(int surfaceWidth, int surfaceHeight) { }

    @Override
    protected boolean update() {

        boolean updated = false;

        while (hasNext()) {
            processEvent(pollInputEvent());
        }

        return updated;
    }

    @Override
    protected void processEvent(MotionEvent event) {

        final int action = event.getActionMasked();

        mIsTouched = (action == MotionEvent.ACTION_DOWN) && mHexagon[0].pointInHexagon(event.getX(), event.getY()) ||
                (action == MotionEvent.ACTION_MOVE) && mIsTouched && mHexagon[0].pointInHexagon(event.getX(), event.getY());
    }

    @Override
    protected void processEvent(KeyEvent event) { }

    @Override
    protected void doDraw(Canvas canvas) {

        final float centerX = canvas.getWidth() / 2;
        final float centerY = canvas.getHeight() / 2;

        // Draw the hexagons
        final float hexagonSide = canvas.getWidth() * HEXAGON_SIDE_COEF;

        if (mHexagon[0] == null) {
            mHexagon[0] = new Hexagon(hexagonSide);
        }
        mHexagon[0].moveTo(centerX, centerY - mHexagon[0].getHeight() - hexagonSide / 2);
        mHexagon[0].draw(canvas, mPaint);

        mHexagon[0].moveCenterTo(centerX - mHexagon[0].getWidth() / 2, centerY);
        mHexagon[0].draw(canvas, mPaint);

        mHexagon[0].moveCenterTo(centerX + mHexagon[0].getWidth() / 2, centerY);
        mHexagon[0].draw(canvas, mPaint);

        mHexagon[0].moveTo(centerX, centerY + hexagonSide / 2);
        mHexagon[0].draw(canvas, mIsTouched ? mPaintOnTouch : mPaint);
    }
}
