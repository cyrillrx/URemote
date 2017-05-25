package com.cyrillrx.uremote.common.model;

/**
 * Simple object that hold an action.
 * Used in Home Activity
 *
 * @author Cyril Leroux
 *         Created on 05/11/12.
 */
public class ActionItem {

    public static final int ACTION_COMPUTER = 0;
    public static final int ACTION_LIGHTS = 1;
    public static final int ACTION_TV = 2;
    public static final int ACTION_ROBOTS = 3;
    public static final int ACTION_HIFI = 4;
    public static final int ACTION_EXPLORER = 5;

    public static final int ACTION_APP = 10;
    public static final int ACTION_3D_CUBE = 11;

    private final int id;
    private final String title;
    private final int imageRes;

    /**
     * @param title    The action title.
     * @param imageRes The resource id of the thumbnail image.
     */
    public ActionItem(int id, final String title, final int imageRes) {
        this.id = id;
        this.title = title;
        this.imageRes = imageRes;
    }

    public int getId() { return id; }

    /** @return the title */
    public String getTitle() { return title; }

    /** @return the image resource */
    public int getImageResource() { return imageRes; }
}
