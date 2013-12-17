package org.es.graphics;

import android.graphics.PointF;

/**
 * Created by Cyril Leroux on 16/12/13.
 */
public abstract class Polygon {

    protected abstract PointF[] getCoordinates();

    protected abstract float getLeft();

    protected abstract float getTop();

    protected abstract float getRight();

    protected abstract float getBottom();

    public boolean pointInHexagon(final float pointX, final float pointY) {

        // Check rect bounds
        if (pointX < getLeft() || pointX > getRight() || pointY < getTop() || pointY > getBottom()) {
            return false;
        }

        PointF[] coordinates = getCoordinates();
        final int verticesCount = coordinates.length;

        // nvert: Number of vertices in the polygon. Whether to repeat the first vertex at the end.
        // vertx, verty: Arrays containing the x- and y-coordinates of the polygon's vertices.
        // testx, testy: X- and y-coordinate of the test point.

//        int i, j, c = 0;
        boolean pointIsInPolygon = false;
        for (int i = 0, j = verticesCount - 1; i < verticesCount; j = i++) {
            if (((coordinates[i].y > pointY) != (coordinates[j].y > pointY)) &&
                    (pointX < (coordinates[j].x - coordinates[i].x) * (pointY - coordinates[i].y) / (coordinates[j].y - coordinates[i].y) + coordinates[i].x)) {
                pointIsInPolygon = !pointIsInPolygon;
            }
        }
        //            return c;

        return pointIsInPolygon;
    }
}
