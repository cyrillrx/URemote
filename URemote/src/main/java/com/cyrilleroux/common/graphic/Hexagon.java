package com.cyrilleroux.common.graphic;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

/**
 * The hexagon can be either FLAT or POINTY
 * <p/>
 * FLAT
 * 0 __ 1
 * 5 /    \ 2
 * \ __ /
 * 4    3
 * <p/>
 * POINTY
 * 0
 * ^
 * 5  /     \ 1
 * |       |
 * 4  \     / 2
 * <p/>
 * 3
 * <p/>
 * Created by Cyril on 01/12/13.
 */
public class Hexagon extends Polygon {

    public static class Orientation {
        public static final int POINTY = 0;
        public static final int FLAT = 1;
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
     * Constructor that creates a pointy hexagon.
     *
     * @param side
     */
    public Hexagon(final float side) {
        this(side, Orientation.POINTY, new PointF());
    }

    private static float calculateH(final float side) {
        return (float) Math.sin(Math.toRadians(30)) * side;
    }

    private static float calculateR(final float side) {
        return (float) (Math.cos(Math.toRadians(30)) * side);
    }

    public float getHeight() {
        if (mOrientation == Orientation.FLAT) {
            return 2 * r;
        }
        return mSide + 2 * h;
    }

    public float getWidth() {
        if (mOrientation == Orientation.FLAT) {
            return mSide + 2 * h;
        }
        return 2 * r;
    }

    public PointF getCenter() {
        if (mOrientation == Orientation.FLAT) {
            return new PointF(mPosition.x + mSide / 2.0f, mPosition.y + getHeight() / 2.0f);
        }
        return new PointF(mPosition.x, mPosition.y + getHeight() / 2.0f);
    }

    /**
     * Move the origin point of the hexagon to the given position.
     * The origin depends on the mOrientation attribute of the object.
     *
     * @param posX The abscissa of the new position.
     * @param posY The ordinate of the new position.
     */
    public void moveTo(final float posX, final float posY) {
        mPosition.set(posX, posY);
    }

    /**
     * Move the center point of the hexagon to the given position.
     *
     * @param centerX The abscissa of the new center.
     * @param centerY The ordinate of the new center.
     */
    public void moveCenterTo(final float centerX, final float centerY) {

        if (mOrientation == Orientation.FLAT) {
            mPosition.set(centerX - mSide / 2.0f, centerY - getHeight() / 2.0f);
        }
        mPosition.set(centerX, centerY - getHeight() / 2.0f);
    }

    @Override
    protected PointF[] getCoordinates() {

        PointF[] coordinates = new PointF[6];

        if (mOrientation == Orientation.FLAT) {
            coordinates[0] = mPosition;
            coordinates[1] = new PointF(mPosition.x + mSide, mPosition.y);
            coordinates[2] = new PointF(mPosition.x + mSide + h, mPosition.y + r);
            coordinates[3] = new PointF(mPosition.x + mSide, mPosition.y + r * 2);
            coordinates[4] = new PointF(mPosition.x, mPosition.y + r * 2);
            coordinates[5] = new PointF(mPosition.x - h, mPosition.y + r);

        } else {
            coordinates[0] = mPosition;
            coordinates[1] = new PointF(mPosition.x + r, mPosition.y + h);
            coordinates[2] = new PointF(mPosition.x + r, mPosition.y + h + mSide);
            coordinates[3] = new PointF(mPosition.x, mPosition.y + h + mSide + h);
            coordinates[4] = new PointF(mPosition.x - r, mPosition.y + h + mSide);
            coordinates[5] = new PointF(mPosition.x - r, mPosition.y + h);
        }

        return coordinates;
    }

    protected float getLeft() { return getCoordinates()[5].x; }

    protected float getTop() { return getCoordinates()[0].y; }

    protected float getRight() { return getCoordinates()[2].x; }

    protected float getBottom() { return getCoordinates()[3].y; }

    /**
     * Draw only specific sides of the hexagon at the given position.
     *
     * @param canvas The canvas in which to draw.
     * @param paint  The paint that holds the style and color to draw.
     * @param ids    Ids of the sides to draw (value range is 0 to 5).
     */
    public void drawSides(Canvas canvas, Paint paint, int... ids) {

        PointF[] coordinates = getCoordinates();
        final int count = coordinates.length;

        for (int id : ids) {
            PointF start = coordinates[id];
            PointF stop = coordinates[(id + 1) % count];
            canvas.drawLine(start.x, start.y, stop.x, stop.y, paint);
        }
    }
}
