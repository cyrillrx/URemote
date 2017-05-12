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
    public static final int ACTION_STORE = 11;
    public static final int ACTION_SHOW_NAO = 12;

    private final int id;
    private final String title;
    private final String summary;
    private final int imageRes;

    /**
     * Default constructor
     *
     * @param title         The action title.
     * @param summary       The action summary.
     * @param imageResource The resource id of the thumbnail image.
     */
    public ActionItem(int id, final String title, final String summary, final int imageResource) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        imageRes = imageResource;
    }

    /**
     * Constructor without summary
     *
     * @param title         The action title.
     * @param imageResource The resource id of the thumbnail image.
     */
    public ActionItem(int id, final String title, final int imageResource) {
        this.id = id;
        this.title = title;
        summary = "";
        imageRes = imageResource;
    }

    public int getId() { return id; }

    /** @return the title */
    public String getTitle() { return title; }

    /** @return the summary */
    public String getSummary() { return summary; }

    /** @return the mImageResource */
    public int getImageResource() { return imageRes; }
}
