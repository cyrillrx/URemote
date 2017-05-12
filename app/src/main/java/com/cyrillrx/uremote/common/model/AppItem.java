package com.cyrillrx.uremote.common.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Simple parcelable object that represents a remote application.
 *
 * @author Cyril Leroux
 *         Created before first commit (08/04/12).
 */
public class AppItem implements Parcelable {

    /** CREATOR is a required attribute to create an instance of a class that implements Parcelable */
    public static final Parcelable.Creator<AppItem> CREATOR = new Parcelable.Creator<AppItem>() {
        @Override
        public AppItem createFromParcel(Parcel src) { return new AppItem(src); }

        @Override
        public AppItem[] newArray(int size) { return new AppItem[size]; }
    };

    private final String label;
    private final String path;
    private final int imageResource;
    private final int action;

    /**
     * Constructor with parameters
     *
     * @param label
     * @param path
     * @param resId
     */
    public AppItem(final String label, final String path, final int resId, int action) {
        this.label = label;
        this.path = path;
        imageResource = resId;
        this.action = action;
    }

    /** @param src  */
    public AppItem(final Parcel src) {
        label = src.readString();
        path = src.readString();
        imageResource = src.readInt();
        action = src.readInt();
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel destination, int flags) {
        destination.writeString(label);
        destination.writeString(path);
        destination.writeInt(imageResource);
        destination.writeInt(action);
    }

    public int getImageResource() { return imageResource; }

    public String getLabel() { return label; }

    public String getPath() { return path; }

    public int getAction() { return action; }
}
