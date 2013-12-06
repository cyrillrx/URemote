package org.es.uremote.graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import org.es.graphics.Hexagon;
import org.es.uremote.objects.ConnectedDevice;

import java.util.Random;

/**
 * Created by Cyril Leroux on 29/11/13.
 */
public class ConnectedDeviceDrawable extends Drawable {

    /** The paint's text size. */
    private static final int TEXT_SIZE = 80;
    /** The paint's text size. */
    private static final int SUBSCRIPT_SIZE = 20;
    private static final int SUBSCRIPT_PADDING = 2;

    /** The paint's stroke width, used whenever the paint's style is Stroke or StrokeAndFill. */
    private static final int STROKE_WIDTH = 3;
    /** Coefficient to compute the outer hexagon side. */
    private static final float OUTER_HEXAGON_SIDE_COEF = 0.4f;
    /** Coefficient to compute the inner hexagon side. */
    private static final float INNER_HEXAGON_SIDE_COEF = 0.35f;

    private final String mText;
    private final Paint mPaint;
    private final Rect mTextBounds;

    public ConnectedDeviceDrawable(final ConnectedDevice device) {

        // Text : first letter of the device name
        mText = device.getName().substring(0, 1);

        // Paint
        mPaint = new Paint();
        mPaint.setARGB(255, 70, 200, 200);
        mPaint.setAntiAlias(true);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mPaint.setStrokeWidth(STROKE_WIDTH);

        // Text bounds
        mTextBounds = new Rect();
        mPaint.setTextSize(TEXT_SIZE);
        mPaint.getTextBounds(mText, 0, mText.length(), mTextBounds);
    }

    @Override
    public void draw(Canvas canvas) {

        final float centerX = canvas.getWidth()  / 2;
        final float centerY = canvas.getHeight() / 2;

        // Draw the text
        mPaint.setTextSize(TEXT_SIZE);
        final float textOriginX = centerX - mTextBounds.width()  / 2;
        final float textOriginY = centerY + mTextBounds.height() / 2;
        canvas.drawText(mText, textOriginX, textOriginY, mPaint);

        // Draw subscript
        final String subscript = getSubscript();
        if (subscript != null && !subscript.isEmpty()) {
            mPaint.setTextSize(SUBSCRIPT_SIZE);
            final float subscriptOriginX = centerX + mTextBounds.width()  / 2 + SUBSCRIPT_PADDING;
            final float subscriptOriginY = centerY + mTextBounds.height() / 2 + SUBSCRIPT_SIZE;
            canvas.drawText(subscript, subscriptOriginX, subscriptOriginY, mPaint);
        }

        // Draw superscript
        final String superscript = getSuperscript();
        if (superscript != null && !superscript.isEmpty()) {
            mPaint.setTextSize(SUBSCRIPT_SIZE);
            final float superscriptOriginX = centerX + mTextBounds.width()  / 2 + SUBSCRIPT_PADDING;
            final float superscriptOriginY = centerY - mTextBounds.height() / 2;
            canvas.drawText(superscript, superscriptOriginX, superscriptOriginY, mPaint);
        }

        // Draw the outer hexagon
        final float outerHexagonSide = canvas.getHeight() * OUTER_HEXAGON_SIDE_COEF;
        Hexagon outerHex = new Hexagon(outerHexagonSide);
        outerHex.moveCenterTo(centerX, centerY);
        outerHex.draw(canvas, mPaint);

        // Draw two random sides of the inner hexagon
        final float innerHexagonSide = canvas.getHeight() * INNER_HEXAGON_SIDE_COEF;
        Hexagon innerHex = new Hexagon(innerHexagonSide);
        innerHex.moveCenterTo(centerX, centerY);
        Random rand = new Random();
        innerHex.drawSides(canvas, mPaint, new int[]{rand.nextInt(5), rand.nextInt(5)});
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    protected String getSubscript() {
        return null;
    }

    protected String getSuperscript() {
        return null;
    }
}