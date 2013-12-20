package org.es.components;

/**
 * Class that defines possible user inputs.
 *
 * Created by Cyril on 25/09/13.
 */
public class UserEvent {

    public static final int ACTION_DOWN     = 0;
    public static final int ACTION_UP       = 1;
    public static final int ACTION_MOVE     = 2;

    public static final int KEYCODE_TOUCH   = 0;
    public static final int KEYCODE_LEFT    = 1;
    public static final int KEYCODE_UP      = 2;
    public static final int KEYCODE_RIGHT   = 3;
    public static final int KEYCODE_DOWN    = 4;

    private int mKeyCode;
    private int mAction;
    private float mX;
    private float mY;

    private UserEvent(int keyCode, int action, float x, float y) {
        mKeyCode = keyCode;
        mAction = action;
        mX = x;
        mY = y;
    }

    /**
     * Create a UserEvent for physic and tactile key actions.
     * @param keyCode
     * @param action
     */
    public UserEvent(int keyCode, int action) {
        this(keyCode, action, 0f, 0f);
    }

    /**
     * Create a UserEvent for move actions.
     * @param action
     * @param x
     * @param y
     */
    public UserEvent(int action, float x, float y) {
        this(KEYCODE_TOUCH, action, x, y);
    }

    public int getKeyCode() { return mKeyCode; }

    public int getAction() { return mAction; }

    public float getX() { return mX; }

    public float getY() { return mY; }
}
