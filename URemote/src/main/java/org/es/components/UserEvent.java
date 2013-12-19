package org.es.components;

/**
 * Class that defines possible user inputs.
 *
 * Created by Cyril on 25/09/13.
 */
public class UserEvent {

    public static final int ACTION_DOWN     = 0;
    public static final int ACTION_UP       = 1;

    public static final int KEYCODE_LEFT    = 0;
    public static final int KEYCODE_UP      = 1;
    public static final int KEYCODE_RIGHT   = 2;
    public static final int KEYCODE_DOWN    = 3;

    private int mKeyCode;
    private int mAction;

    public UserEvent(int keyCode, int action) {
        mKeyCode = keyCode;
        mAction = action;
    }

    public int getKeyCode() {
        return mKeyCode;
    }

    public int getAction() {
        return mAction;
    }
}
