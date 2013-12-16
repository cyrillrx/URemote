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

        // TODO more checks here

        return true;
    }
}
