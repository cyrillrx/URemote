package org.es.graphics;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

/**
 * Created by Cyril on 01/12/13.
 */
public class Hexagon {

    public static class Type {
        public static final int POINTY  = 0;
        public static final int FLAT    = 1;
    }

    private float mSide;
    private int mType = Type.POINTY;

    private float h;
    private float r;

    public Hexagon(final float side) {
        mSide = side;
        r = calculateR(side);
        h = calculateH(side);
    }

    public Hexagon(final float side, final int type) {
        this(side);
        mType = type;
    }

    public float getHeight() {
        return mSide + 2 * h;
    }

    public float getWidth() {
        return mSide + 2 * h;
    }

    private static float calculateH(final float side) {
        return (float) Math.sin(Math.toRadians(30)) * side;
    }

    private static float calculateR(final float side) {
        return (float) (Math.cos(Math.toRadians(30)) * side);
    }

    private static float nextCoordinates(final float side) {
        return (float) (Math.cos(Math.toRadians(30)) * side);
    }

    private PointF[] getCoordinates(final PointF center) {

        // Calculate the origin point from the center.
        final PointF origin = new PointF(center.x, center.y - h - mSide / 2);

        PointF[] coordinates = new PointF[6];

        coordinates[0] = new PointF(origin.x, origin.y);
        coordinates[1] = new PointF(origin.x + r, origin.y + h);
        coordinates[2] = new PointF(origin.x + r, origin.y + h + mSide);
        coordinates[3] = new PointF(origin.x, origin.y + h + mSide + h);
        coordinates[4] = new PointF(origin.x - r, origin.y + h + mSide);
        coordinates[5] = new PointF(origin.x - r, origin.y + h);

        return coordinates;
    }

    /**
     * Draw the hexagon at the given position.
     * @param canvas The canvas in which to draw.
     * @param paint The paint that holds the style and color to draw.
     * @param center The point at the center of the hexagon.
     */
    public void draw(Canvas canvas, Paint paint, PointF center) {

        PointF[] coordinates = getCoordinates(center);

        final int count = coordinates.length;
        for (int p = 0; p < count; p++) {
            PointF start = coordinates[p];
            PointF stop  = coordinates[(p+1) % count];
            canvas.drawLine(start.x, start.y, stop.x, stop.y, paint);
        }
    }

    /**
     * Draw only specific sides of the hexagon at the given position.
     * @param canvas The canvas in which to draw.
     * @param paint The paint that holds the style and color to draw.
     * @param center The point at the center of the hexagon.
     * @param ids Ids of the sides to draw (value range is 0 to 5).
     */
    public void drawSides(Canvas canvas, Paint paint, PointF center, int[] ids) {

        PointF[] coordinates = getCoordinates(center);
        final int count = coordinates.length;

        for (int id : ids) {
            PointF start = coordinates[id];
            PointF stop  = coordinates[(id+1) % count];
            canvas.drawLine(start.x, start.y, stop.x, stop.y, paint);
        }
    }
}
