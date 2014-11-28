package org.es.utils;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

/**
 * Created by Cyril on 13/09/13.
 */
public class EditIntPreference extends EditTextPreference {

    private int mInteger;

    public EditIntPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public EditIntPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditIntPreference(Context context) {
        super(context);
    }

    /**
     * Gets the integer from the {@link android.content.SharedPreferences}.
     *
     * @return The current preference value.
     */
    public int getInt() {
        return mInteger;
    }

    /**
     * Saves the integer to the {@link android.content.SharedPreferences}.
     *
     * @param integer The integer to save.
     */
    public void setInt(int integer) {

        final boolean wasBlocking = shouldDisableDependents();

        mInteger = integer;

        persistInt(integer);

        final boolean isBlocking = shouldDisableDependents();
        if (isBlocking != wasBlocking) {
            notifyDependencyChange(isBlocking);
        }
    }

    /**
     * Gets the text from the {@link android.content.SharedPreferences}.
     *
     * @return The current preference value.
     */
    public String getText() {
        return String.valueOf(getInt());
    }

    /**
     * Saves the text to the {@link android.content.SharedPreferences}.
     *
     * @param text The text to save.
     */
    public void setText(String text) {
        setInt(Integer.valueOf(text));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setInt(restoreValue ? getPersistedInt(mInteger) : Integer.valueOf((String) defaultValue));
    }

    @Override
    public boolean shouldDisableDependents() {
        return mInteger == Integer.MIN_VALUE || super.shouldDisableDependents();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            // No need to save instance state since it's persistent
            return superState;
        }

        final SavedState myState = new SavedState(superState);
        myState.integer = getInt();
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            // Didn't save state for us in onSaveInstanceState
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        setInt(myState.integer);
    }

    private static class SavedState extends BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
        int integer;

        public SavedState(Parcel source) {
            super(source);
            integer = source.readInt();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel destination, int flags) {
            super.writeToParcel(destination, flags);
            destination.writeInt(integer);
        }
    }
}
