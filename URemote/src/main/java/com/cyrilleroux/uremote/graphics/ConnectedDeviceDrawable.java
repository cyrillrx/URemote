package com.cyrilleroux.uremote.graphics;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import com.cyrilleroux.common.graphic.Hexagon;
import com.cyrilleroux.uremote.URemoteApp;
import com.cyrilleroux.uremote.device.ConnectedDevice;
import com.cyrilleroux.uremote.device.NetworkDevice;

import java.util.Random;

/**
 * Created by Cyril Leroux on 29/11/13.
 */
public class ConnectedDeviceDrawable extends Drawable {

    /** The paint's text coefficient. Used to deduce text size from the canvas. */
    private static final float TEXT_SIZE_COEF = 0.42f;
    /** The paint's subscript and superscript coefficient. Used to deduce text size from the canvas. */
    private static final float SUBSCRIPT_SIZE_COEF = 0.1f;
    /** Coefficient to compute the outer hexagon side from canvas size. */
    private static final float OUTER_HEXAGON_SIDE_COEF = 0.42f;
    /** Coefficient to compute the inner hexagon side from canvas size. */
    private static final float INNER_HEXAGON_SIDE_COEF = 0.37f;

    private static final float SUBSCRIPT_PADDING = 1f;
    /** The paint's stroke width, used whenever the paint's style is Stroke or StrokeAndFill. */
    private static final float STROKE_WIDTH = 2.5f;

    private final Paint mPaint;

    private final String mText;
    private final Rect mTextBounds;

    private final String mSubscript;
    private final Rect mSubscriptBounds;

    private final String mSuperscript;
    private final Rect mSuperscriptBounds;

    public ConnectedDeviceDrawable(final ConnectedDevice device) {
        this(device, Integer.MIN_VALUE);
    }

    public ConnectedDeviceDrawable(final ConnectedDevice device, final int color) {

        // Paint
        mPaint = new Paint();
        mPaint.setColor((color != Integer.MIN_VALUE ? color : URemoteApp.COLOR_DEFAULT));
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(STROKE_WIDTH);

        // Text : first letter of the device name
        mText = getText(device);
        mSubscript = getSubscript(device);
        mSuperscript = getSuperscript(device);

        // Text bounds
        mTextBounds = new Rect();

        // Subscript bounds
        mSubscriptBounds = new Rect();

        // Superscript bounds
        mSuperscriptBounds = new Rect();
    }

    @Override
    public void draw(Canvas canvas) {

        final float centerX = canvas.getWidth() / 2f;
        final float centerY = canvas.getHeight() / 2f;

        final float textSize = canvas.getHeight() * TEXT_SIZE_COEF;
        final float subscriptSize = canvas.getHeight() * SUBSCRIPT_SIZE_COEF;

        // Draw the text
        mPaint.setTextSize(textSize);
        mPaint.getTextBounds(mText, 0, mText.length(), mTextBounds);
        final float textOriginX = centerX - mTextBounds.width() / 2f - Math.max(mSubscriptBounds.width(), mSuperscriptBounds.width()) / 2f;
        final float textOriginY = centerY + mTextBounds.height() / 2f;
        canvas.drawText(mText, textOriginX, textOriginY, mPaint);

        // Draw subscript
        if (!mSubscript.isEmpty()) {
            mPaint.setTextSize(subscriptSize);
            mPaint.getTextBounds(mSubscript, 0, mSubscript.length(), mSubscriptBounds);
            final float subscriptOriginX = textOriginX + mTextBounds.width() + SUBSCRIPT_PADDING;
            final float subscriptOriginY = textOriginY + subscriptSize / 2f;
            canvas.drawText(mSubscript, subscriptOriginX, subscriptOriginY, mPaint);
        }

        // Draw superscript
        if (!mSuperscript.isEmpty()) {
            mPaint.setTextSize(subscriptSize);
            mPaint.getTextBounds(mSuperscript, 0, mSuperscript.length(), mSuperscriptBounds);
            final float superscriptOriginX = textOriginX + mTextBounds.width() + SUBSCRIPT_PADDING;
            final float superscriptOriginY = textOriginY - mTextBounds.height() / 2f;
            canvas.drawText(mSuperscript, superscriptOriginX, superscriptOriginY, mPaint);
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
        final Random rand = new Random();
        innerHex.drawSides(canvas, mPaint, rand.nextInt(5), rand.nextInt(5));
    }

    @Override
    public void setAlpha(int alpha) { mPaint.setAlpha(alpha); }

    @Override
    public void setColorFilter(ColorFilter cf) { mPaint.setColorFilter(cf); }

    @Override
    public int getOpacity() { return PixelFormat.TRANSLUCENT; }


    protected String getText(final ConnectedDevice device) {
        return device.getName().substring(0, 1);
    }

    protected String getSubscript(final ConnectedDevice device) {

        if (device instanceof NetworkDevice) {
            String ip = ((NetworkDevice) device).getLocalHost();
            final int pos = ip.lastIndexOf('.');
            return ip.substring(pos + 1);
        }

        return "";
    }

    protected String getSuperscript(final ConnectedDevice device) { return ""; }
}