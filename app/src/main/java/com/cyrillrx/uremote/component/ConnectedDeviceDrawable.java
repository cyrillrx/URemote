package com.cyrillrx.uremote.component;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;

import com.cyrillrx.common.graphic.Hexagon;
import com.cyrillrx.uremote.URemoteApp;
import com.cyrillrx.uremote.common.device.ConnectedDevice;
import com.cyrillrx.uremote.common.device.NetworkDevice;

import java.util.Random;

/**
 * @author Cyril Leroux
 *         Created on 29/11/13.
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

    private final Paint paint;

    private final String text;
    private final Rect textBounds;

    private final String subscript;
    private final Rect subscriptBounds;

    private final String superscript;
    private final Rect superscriptBounds;

    public ConnectedDeviceDrawable(final ConnectedDevice device) {
        this(device, Integer.MIN_VALUE);
    }

    public ConnectedDeviceDrawable(final ConnectedDevice device, final int color) {

        // Paint
        paint = new Paint();
        paint.setColor((color != Integer.MIN_VALUE ? color : URemoteApp.COLOR_DEFAULT));
        paint.setAntiAlias(true);
        paint.setStrokeWidth(STROKE_WIDTH);

        // Text : first letter of the device name
        text = getText(device);
        subscript = getSubscript(device);
        superscript = getSuperscript(device);

        // Text bounds
        textBounds = new Rect();

        // Subscript bounds
        subscriptBounds = new Rect();

        // Superscript bounds
        superscriptBounds = new Rect();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {

        final float centerX = canvas.getWidth() / 2f;
        final float centerY = canvas.getHeight() / 2f;

        final float textSize = canvas.getHeight() * TEXT_SIZE_COEF;
        final float subscriptSize = canvas.getHeight() * SUBSCRIPT_SIZE_COEF;

        // Draw the text
        paint.setTextSize(textSize);
        paint.getTextBounds(text, 0, text.length(), textBounds);
        final float textOriginX = centerX - textBounds.width() / 2f - Math.max(subscriptBounds.width(), superscriptBounds.width()) / 2f;
        final float textOriginY = centerY + textBounds.height() / 2f;
        canvas.drawText(text, textOriginX, textOriginY, paint);

        // Draw subscript
        if (!subscript.isEmpty()) {
            paint.setTextSize(subscriptSize);
            paint.getTextBounds(subscript, 0, subscript.length(), subscriptBounds);
            final float subscriptOriginX = textOriginX + textBounds.width() + SUBSCRIPT_PADDING;
            final float subscriptOriginY = textOriginY + subscriptSize / 2f;
            canvas.drawText(subscript, subscriptOriginX, subscriptOriginY, paint);
        }

        // Draw superscript
        if (!superscript.isEmpty()) {
            paint.setTextSize(subscriptSize);
            paint.getTextBounds(superscript, 0, superscript.length(), superscriptBounds);
            final float superscriptOriginX = textOriginX + textBounds.width() + SUBSCRIPT_PADDING;
            final float superscriptOriginY = textOriginY - textBounds.height() / 2f;
            canvas.drawText(superscript, superscriptOriginX, superscriptOriginY, paint);
        }

        // Draw the outer hexagon
        final float outerHexagonSide = canvas.getHeight() * OUTER_HEXAGON_SIDE_COEF;
        Hexagon outerHex = new Hexagon(outerHexagonSide);
        outerHex.moveCenterTo(centerX, centerY);
        outerHex.draw(canvas, paint);

        // Draw two random sides of the inner hexagon
        final float innerHexagonSide = canvas.getHeight() * INNER_HEXAGON_SIDE_COEF;
        Hexagon innerHex = new Hexagon(innerHexagonSide);
        innerHex.moveCenterTo(centerX, centerY);
        final Random rand = new Random();
        innerHex.drawSides(canvas, paint, rand.nextInt(5), rand.nextInt(5));
    }

    @Override
    public void setAlpha(int alpha) { paint.setAlpha(alpha); }

    @Override
    public void setColorFilter(ColorFilter cf) { paint.setColorFilter(cf); }

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