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

    private Type mType;

    public Hexagon() { }

    public Hexagon(Type type) {
        mType = type;
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

//    private static Pointf[] getCoordinates(PointF firstCoordinates) {
//        PointF[6] cooridinates = new PointF()
//        return
//    }
}
