package org.es.graphics;

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

    public static PointF[] getCoordinates(final float side, final PointF center) {

        final float r = calculateR(side);
        final float h = calculateH(side);

        final PointF origin = new PointF(center.x, center.y - h - side / 2);
        PointF[] coordinates = new PointF[6];

        coordinates[0].set(origin.x, origin.y);
        coordinates[1].set(origin.x + r, origin.y + h);
        coordinates[2].set(origin.x + r, origin.y + h + side);
        coordinates[3].set(origin.x, origin.y + h + side + h);
        coordinates[4].set(origin.x - r, origin.y + h + side);
        coordinates[5].set(origin.x - r, origin.y + h);

        return coordinates;
    }
}
