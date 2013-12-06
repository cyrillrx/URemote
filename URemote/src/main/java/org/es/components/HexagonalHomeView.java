package org.es.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;
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

    public HexagonalHomeView(Context context) {
        super(context);

        // Paint
        mPaint = new Paint();
        mPaint.setARGB(255, 70, 200, 200);
        mPaint.setAntiAlias(true);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mPaint.setStrokeWidth(STROKE_WIDTH);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);

        final float centerX = canvas.getWidth()  / 2;
        final float centerY = canvas.getHeight() / 2;

        // Draw the hexagons
        final float hexagonSide = canvas.getWidth() * HEXAGON_SIDE_COEF;
        Hexagon hexagon = new Hexagon(hexagonSide);

        hexagon.moveTo(centerX, centerY - hexagon.getHeight() - hexagonSide / 2);
        hexagon.draw(canvas, mPaint);

        hexagon.moveCenterTo(centerX - hexagon.getWidth() / 2, centerY);
        hexagon.draw(canvas, mPaint);

        hexagon.moveCenterTo(centerX + hexagon.getWidth() / 2, centerY);
        hexagon.draw(canvas, mPaint);

        hexagon.moveTo(centerX, centerY + hexagonSide / 2);
        hexagon.draw(canvas, mPaint);
    }
}
