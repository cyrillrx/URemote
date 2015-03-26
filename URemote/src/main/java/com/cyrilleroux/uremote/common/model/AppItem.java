package com.cyrilleroux.uremote.common.model;

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
        public AppItem createFromParcel(Parcel src) {
            return new AppItem(src);
        }

        @Override
        public AppItem[] newArray(int size) {
            return new AppItem[size];
        }
    };

    private final String mLabel;
    private final String mPath;
    private final int mImageResource;
    private final int mAction;

    /**
     * Constructor with parameters
     *
     * @param label
     * @param path
     * @param resId
     */
    public AppItem(final String label, final String path, final int resId, int action) {
        mLabel = label;
        mPath = path;
        mImageResource = resId;
        mAction = action;
    }

    /** @param src  */
    public AppItem(final Parcel src) {
        mLabel = src.readString();
        mPath = src.readString();
        mImageResource = src.readInt();
        mAction = src.readInt();
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel destination, int flags) {
        destination.writeString(mLabel);
        destination.writeString(mPath);
        destination.writeInt(mImageResource);
        destination.writeInt(mAction);
    }

    public int getImageResource() { return mImageResource; }

    public String getLabel() { return mLabel; }

    public String getPath() { return mPath; }

    public int getAction() { return mAction; }
}
