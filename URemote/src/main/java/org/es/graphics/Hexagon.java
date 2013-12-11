package org.es.graphics;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

/**
 *
 *   0 __ 1
 * 5 /    \ 2
 *   \ __ /
 *   4    3
 *
 *      0
 *  5  ^ 1
 *   |    |
 *  4  ^  2
 *      3
 * Created by Cyril on 01/12/13.
 */
public class Hexagon {

    public static class Orientation {
        public static final int VERTICAL = 0;
        public static final int HORIZONTAL = 1;
    }

    private final float mSide;
    private final PointF mPosition;
    private final int mOrientation;

    private final float h;
    private final float r;


    public Hexagon(final float side, final int orientation, final PointF position) {

        mSide = side;
        mOrientation = orientation;
        mPosition = position;

        r = calculateR(side);
        h = calculateH(side);
    }

    /**
     * Constructor that creates a vertical oriented hexagon.
     * @param side
     */
    public Hexagon(final float side) {
        this(side, Orientation.VERTICAL, new PointF());
    }

    private static float calculateH(final float side) {
        return (float) Math.sin(Math.toRadians(30)) * side;
    }

    private static float calculateR(final float side) {
        return (float) (Math.cos(Math.toRadians(30)) * side);
    }

    public float getHeight() {
        if (mOrientation == Orientation.HORIZONTAL) {
            return 2 * r;
        }
        return mSide + 2 * h;
    }

    public float getWidth() {
        if (mOrientation == Orientation.HORIZONTAL) {
            return mSide + 2 * h;
        }
        return 2 * r;
    }

    public PointF getCenter() {
        if (mOrientation == Orientation.HORIZONTAL) {
            // TODO
            throw new RuntimeException("getCenter not available for horizontal orientation.");
        }
        return new PointF(mPosition.x, mPosition.y + getHeight() / 2.0f);
    }

    /**
     * Move the origin point of the hexagon to the given position.
     * The origin depends on the mOrientation attribute of the object.
     * @param posX The abscissa of the new position.
     * @param posY The ordinate of the new position.
     */
    public void moveTo(final float posX, final float posY) {
        mPosition.set(posX, posY);
    }

    /**
     * Move the center point of the hexagon to the given position.
     * @param centerX The abscissa of the new center.
     * @param centerY The ordinate of the new center.
     */
    public void moveCenterTo(final float centerX, final float centerY) {
        if (mOrientation == Orientation.HORIZONTAL) {
            // TODO
            throw new RuntimeException("moveCenterTo not available for horizontal orientation.");
        }
        mPosition.set(centerX, centerY - getHeight() / 2.0f);
    }

    private PointF[] getCoordinates() {

        PointF[] coordinates = new PointF[6];

        if (mOrientation == Orientation.HORIZONTAL) {
            // TODO
            throw new RuntimeException("getCoordinates not available for horizontal orientation.");
//            coordinates[0] = new PointF(origin.x, origin.y);
//            coordinates[1] = new PointF(origin.x + r, origin.y + h);
//            coordinates[2] = new PointF(origin.x + r, origin.y + h + mSide);
//            coordinates[3] = new PointF(origin.x, origin.y + h + mSide + h);
//            coordinates[4] = new PointF(origin.x - r, origin.y + h + mSide);
//            coordinates[5] = new PointF(origin.x - r, origin.y + h);
//
//            return coordinates;
        } else {
            coordinates[0] = mPosition;
            coordinates[1] = new PointF(mPosition.x + r, mPosition.y + h);
            coordinates[2] = new PointF(mPosition.x + r, mPosition.y + h + mSide);
            coordinates[3] = new PointF(mPosition.x,     mPosition.y + h + mSide + h);
            coordinates[4] = new PointF(mPosition.x - r, mPosition.y + h + mSide);
            coordinates[5] = new PointF(mPosition.x - r, mPosition.y + h);
        }

        return coordinates;
    }

    /**
     * Draw the hexagon at its current position.
     *
     * @param canvas The canvas in which to draw.
     * @param paint The paint that holds the style and color to draw.
     */
    public void draw(Canvas canvas, Paint paint) {

        PointF[] coordinates = getCoordinates();

        final int count = coordinates.length;
        for (int p = 0; p < count; p++) {
            PointF start = coordinates[p];
            PointF stop = coordinates[(p + 1) % count];
            canvas.drawLine(start.x, start.y, stop.x, stop.y, paint);
        }
    }

    /**
     * Draw only specific sides of the hexagon at the given position.
     *
     * @param canvas The canvas in which to draw.
     * @param paint The paint that holds the style and color to draw.
     * @param ids Ids of the sides to draw (value range is 0 to 5).
     */
    public void drawSides(Canvas canvas, Paint paint, int[] ids) {

        PointF[] coordinates = getCoordinates();
        final int count = coordinates.length;

        for (int id : ids) {
            PointF start = coordinates[id];
            PointF stop = coordinates[(id + 1) % count];
            canvas.drawLine(start.x, start.y, stop.x, stop.y, paint);
        }
    }

    public boolean pointInHexagon(final float posX, final float posY) {

        PointF[] coordinates = getCoordinates();

        // check rect
        if (posX < coordinates[5].x || posX > coordinates[2].x ||
                posY < coordinates[0].y || posY > coordinates[3].y) {
            return false;
        }

        // TODO more checks here
        return true;
    }
}
