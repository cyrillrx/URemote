package com.cyrillrx.uremote.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.cyrillrx.common.graphic.Hexagon;

/**
 * @author Cyril Leroux
 *         Created on 22/09/13.
 */
public class HexagonGridThread extends DrawingThread {

    /** Coefficient to compute the hexagon side. */
    private static final float HEXAGON_SIDE_COEF = 0.2f;
    /** The paint's stroke width, used whenever the paint's style is Stroke or StrokeAndFill. */
    private static final int STROKE_WIDTH = 3;

    private final Paint paint;
    private final Paint paintOnTouch;

    private Hexagon[] hexagon = new Hexagon[4];
    private boolean isTouched = false;

    public HexagonGridThread(SurfaceHolder surfaceHolder, Context context) {
        super(surfaceHolder, context);

        // Paint
        paint = new Paint();
        paint.setARGB(255, 70, 200, 200);
        paint.setAntiAlias(true);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setStrokeWidth(STROKE_WIDTH);

        // Paint
        paintOnTouch = new Paint(paint);
        paintOnTouch.setARGB(255, 255, 255, 255);
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

        isTouched = (action == MotionEvent.ACTION_DOWN) && hexagon[0].pointInHexagon(event.getX(), event.getY()) ||
                (action == MotionEvent.ACTION_MOVE) && isTouched && hexagon[0].pointInHexagon(event.getX(), event.getY());
    }

    @Override
    protected void processEvent(KeyEvent event) { }

    @Override
    protected void doDraw(Canvas canvas) {

        final float centerX = canvas.getWidth() / 2;
        final float centerY = canvas.getHeight() / 2;

        // Draw the hexagons
        final float hexagonSide = canvas.getWidth() * HEXAGON_SIDE_COEF;

        if (hexagon[0] == null) {
            hexagon[0] = new Hexagon(hexagonSide);
        }
        hexagon[0].moveTo(centerX, centerY - hexagon[0].getHeight() - hexagonSide / 2);
        hexagon[0].draw(canvas, paint);

        hexagon[0].moveCenterTo(centerX - hexagon[0].getWidth() / 2, centerY);
        hexagon[0].draw(canvas, paint);

        hexagon[0].moveCenterTo(centerX + hexagon[0].getWidth() / 2, centerY);
        hexagon[0].draw(canvas, paint);

        hexagon[0].moveTo(centerX, centerY + hexagonSide / 2);
        hexagon[0].draw(canvas, isTouched ? paintOnTouch : paint);
    }
}
