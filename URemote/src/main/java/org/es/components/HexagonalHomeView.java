package org.es.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.view.SurfaceView;

import org.es.graphics.Hexagon;

import java.util.Random;

/**
 * Created by Cyril Leroux on 05/12/13.
 */
public class HexagonalHomeView extends SurfaceView {

    /** Coefficient to compute the hexagon side. */
    private static final float HEXAGON_SIDE_COEF = 0.15f;
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
    public void draw(Canvas canvas) {
        super.draw(canvas);

        final float centerX = canvas.getWidth()  / 2;
        final float centerY = canvas.getHeight() / 2;

        // Draw the hexagons
        final float hexagonSide = canvas.getWidth() * HEXAGON_SIDE_COEF;
        Hexagon hexagon = new Hexagon(hexagonSide);

        PointF origin1 = new PointF(centerX, centerY - hexagon.getHeight() - (1 / 2 * hexagonSide));
        hexagon.drawFromOrigin(canvas, mPaint, origin1);

        PointF origin2 = new PointF(centerX - hexagon.getWidth() / 2, centerY);
        hexagon.draw(canvas, mPaint, origin2);

        PointF origin3 = new PointF(centerX + hexagon.getWidth() / 2, centerY);
        hexagon.draw(canvas, mPaint, origin3);

        PointF origin4 = new PointF(centerX, centerY + (1 / 2 * hexagonSide));
        hexagon.drawFromOrigin(canvas, mPaint, origin4);
    }
}
