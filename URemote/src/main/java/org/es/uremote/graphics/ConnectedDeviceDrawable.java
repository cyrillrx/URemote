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

    private final static int TEXT_PADDING           = 3;
    private final static int ROUNDED_RECT_RADIUS    = 5;
    private final static int TEXT_HEIGHT            = 80;

    private final String mText;
    private final Paint mTextPaint;
    private final Rect mTextBounds;
    private final Paint mBgPaint;
    private final RectF mBgBounds;

    public ConnectedDeviceDrawable(final ConnectedDevice device) {

        mText = device.getName().substring(0, 1);
//        final String backgroundColor = "";

        // Text
        mTextPaint = new Paint();
        mTextBounds = new Rect();
        //mTextPaint.setColor(Color.WHITE);
        mTextPaint.setARGB(0, 70, 200, 200);
//        mTextPaint.setAntiAlias(true);
//        mTextPaint.setSubpixelText(true);
//        mTextPaint.setTextAlign(Paint.Align.CENTER); // Important to centre horizontally in the background RectF
        mTextPaint.setTextSize(TEXT_HEIGHT);
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);

        // Map textPaint to a Rect in order to get its true height
        // ... a bit long-winded I know but unfortunately getTextSize does not seem to give a true height!
        mTextPaint.getTextBounds(mText, 0, 1, mTextBounds);

        // Background
        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
        //mBgPaint.setARGB(0, 0, 0, 0);
        mBgPaint.setColor(Color.parseColor("blue"));
        float rectHeight  = TEXT_PADDING * 2 + TEXT_HEIGHT;
        float rectWidth   = TEXT_PADDING * 2 + mTextPaint.measureText(mText);
        //float rectWidth   = TEXT_PADDING * 2 + textHeight;  // Square (alternative)
        // Create the background - use negative start x/y coordinates to centre align the icon
        mBgBounds = new RectF(rectWidth / -2, rectHeight / -2, rectWidth / 2, rectHeight / 2);
    }

    @Override
    public void draw(Canvas canvas) {

        final float centerX = canvas.getWidth()  / 2;
        final float centerY = canvas.getHeight() / 2;

        //canvas.drawCircle(centerX, centerY, 40, new Paint());
        //canvas.drawRoundRect(mBgBounds, ROUNDED_RECT_RADIUS, ROUNDED_RECT_RADIUS, mBgPaint);
        //canvas.drawRect(mBgBounds, ROUNDED_RECT_RADIUS, ROUNDED_RECT_RADIUS, mBgPaint);
        // Position the text in the horizontal/vertical centre of the background RectF

        float rectWidth   = TEXT_PADDING * 2 + mTextPaint.measureText(mText);
        float rectHeight  = TEXT_PADDING * 2 + TEXT_HEIGHT;
        final float textOriginX = centerX - rectWidth  / 2;
        final float textOriginY = centerY + rectHeight / 2;

//        final float textOriginX = centerX - mBgBounds.width()  / 2;
//        final float textOriginY = centerY + mBgBounds.height() / 2;
        canvas.drawText(mText, textOriginX, textOriginY, mTextPaint);

        PointF[] coordinates = Hexagon.getCoordinates(rectHeight, new PointF(centerX, centerY));
        final int count = coordinates.length;
        for (int p = 0; p < count; p++) {
            PointF start = coordinates[p];
            PointF stop  = coordinates[(p+1)%count];
            canvas.drawLine(start.x, start.y, stop.x, stop.y, mTextPaint);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        mBgPaint.setAlpha(alpha);
        mTextPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mBgPaint.setColorFilter(cf);
        mTextPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}