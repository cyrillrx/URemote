package org.es.uremote.objects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Simple parcelable object that represents an application.
 *
 * @author Cyril Leroux
 * Created before first commit (08/04/12).
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

    private String mLabel;
    private String mPath;
    private int mImageResource;

    /**
     * Constructor with parameters
     *
     * @param label
     * @param path
     * @param resId
     */
    public AppItem(final String label, final String path, final int resId) {
        mLabel = label;
        mPath = path;
        mImageResource = resId;
    }

    /** @param src */
    public AppItem(final Parcel src) {
        mLabel = src.readString();
        mPath = src.readString();
        mImageResource = src.readInt();
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel destination, int flags) {
        destination.writeString(mLabel);
        destination.writeString(mPath);
        destination.writeInt(mImageResource);
    }

    public int getImageResource() { return mImageResource; }

    public void setImageResource(int imageResource) { mImageResource = imageResource; }

    public String getLabel() { return mLabel; }

    public void setLabel(String label) { mLabel = label; }

    public String getPath() { return mPath; }

    public void setPath(final String path) { mPath = path; }
}
