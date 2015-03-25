package com.cyrilleroux.uremote.objects;


/**
 * Simple object that hold an action.
 * Used in Home Activity
 *
 * @author Cyril Leroux
 *         Created 05/11/12.
 */
public class ActionItem {

    public static final int ACTION_COMPUTER = 0;
    public static final int ACTION_LIGHTS = 1;
    public static final int ACTION_TV = 2;
    public static final int ACTION_ROBOTS = 3;
    public static final int ACTION_HIFI = 4;

    public static final int ACTION_APP = 10;
    public static final int ACTION_STORE = 11;
    public static final int ACTION_SHOW_NAO = 12;

    private final int mId;
    private final String mTitle;
    private final String mSummary;
    private final int mImageRes;

    /**
     * Default constructor
     *
     * @param title         The action title.
     * @param summary       The action summary.
     * @param imageResource The resource id of the thumbnail image.
     */
    public ActionItem(int id, final String title, final String summary, final int imageResource) {
        mId = id;
        mTitle = title;
        mSummary = summary;
        mImageRes = imageResource;
    }

    /**
     * Constructor without summary
     *
     * @param title         The action title.
     * @param imageResource The resource id of the thumbnail image.
     */
    public ActionItem(int id, final String title, final int imageResource) {
        mId = id;
        mTitle = title;
        mSummary = "";
        mImageRes = imageResource;
    }

    public int getId() { return mId; }

    /** @return the mTitle */
    public String getTitle() {
        return mTitle;
    }

    /** @return the mSummary */
    public String getSummary() {
        return mSummary;
    }

    /** @return the mImageResource */
    public int getImageResource() {
        return mImageRes;
    }
}
