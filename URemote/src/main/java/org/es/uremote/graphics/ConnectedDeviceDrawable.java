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

/**
 * Created by Cyril Leroux on 29/11/13.
 */
public class ConnectedDeviceDrawable extends Drawable {

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
        mPaint.setTextSize(80);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mPaint.setStrokeWidth(3);

        // Text bounds
        mTextBounds = new Rect();
        mPaint.getTextBounds(mText, 0, mText.length(), mTextBounds);
    }

    @Override
    public void draw(Canvas canvas) {

        // Hexagon side is 40% of canvas height (50% fills all the space)
        final float hexagonSide = canvas.getHeight() * 0.4f;
        final float centerX = canvas.getWidth()  / 2;
        final float centerY = canvas.getHeight() / 2;

        // Draw the text
        final float textOriginX = centerX - mTextBounds.width()  / 2;
        final float textOriginY = centerY + mTextBounds.height() / 2;
        canvas.drawText(mText, textOriginX, textOriginY, mPaint);

        // Draw the hexagon
        Hexagon hex = new Hexagon(hexagonSide);
        hex.draw(canvas, mPaint, new PointF(centerX, centerY));
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
    public int getOpacity() { return PixelFormat.TRANSLUCENT; }
}