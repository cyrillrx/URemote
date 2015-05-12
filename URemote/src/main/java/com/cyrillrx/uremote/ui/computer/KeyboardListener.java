package com.cyrillrx.uremote.ui.computer;

import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;

import com.cyrillrx.android.toolbox.Logger;
import com.cyrillrx.uremote.BuildConfig;
import com.cyrillrx.uremote.request.RequestSender;
import com.cyrillrx.uremote.request.protobuf.RemoteCommand;
import com.cyrillrx.uremote.utils.ToastSender;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Cyril Leroux
 *         Created on 01/05/2014.
 */
public class KeyboardListener implements KeyboardView.OnKeyboardActionListener {

    private static final String TAG = KeyboardListener.class.getSimpleName();

    private static final int FLAG_NONE = 0b00000;
    private static final int FLAG_CTRL = 0b00001;
    private static final int FLAG_SHIFT = 0b00010;
    private static final int FLAG_ALT_LEFT = 0b00100;
    private static final int FLAG_ALT_RIGHT = 0b01000;
    private static final int FLAG_WINDOWS = 0b10000;

    private final RequestSender mRequestSender;
    private KeyboardView mKeyboardView;
    private ToastSender mToastSender;
    private Set<Keyboard.Key> mModifierKeys;
    private int mModifierFlag;

    public KeyboardListener(final RequestSender requestSender) {
        mRequestSender = requestSender;
        mModifierKeys = new HashSet<>();
        mModifierFlag = FLAG_NONE;
    }

    public void setToastSender(final ToastSender toastSender) { mToastSender = toastSender; }

    private void sendToast(final String message) {
        if (mToastSender == null) {
            Logger.warning(TAG, "#sendToast - mToastSender is null. Can't send toast.");
            return;
        }
        mToastSender.sendToast(message);
    }

    /**
     * Sets the keyboard view and caches the modifier keys of the attached keyboard.
     * The attached keyboard must not be null.
     * The view will be use to check modifier key states and perform haptic feedback.
     *
     * @param view The keyboard view.
     */
    public void setKeyboardView(final KeyboardView view) {
        mKeyboardView = view;
        mModifierKeys.clear();

        final List<Keyboard.Key> keys = view.getKeyboard().getKeys();
        for (Keyboard.Key key : keys) {
            if (key.modifier) {
                mModifierKeys.add(key);
            }
        }
    }

    /**
     * Performs haptic feedback using the view specified through {@link #setKeyboardView(android.inputmethodservice.KeyboardView)}.
     */
    private void performHapticFeedback() {
        if (mKeyboardView == null) {
            Logger.warning(TAG, "#performHapticFeedback - mKeyboardView is null. Can't perform haptic feedback.");
            return;
        }
        mKeyboardView.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
    }

    private void updateModifierKeys() {

        mModifierFlag = FLAG_NONE;

        for (Keyboard.Key key : mModifierKeys) {

            final int code = key.codes[0];

            if (code == KeyEvent.KEYCODE_CTRL_LEFT || code == KeyEvent.KEYCODE_CTRL_RIGHT) {
                mModifierFlag += key.on ? FLAG_CTRL : FLAG_NONE;

            } else if (code == KeyEvent.KEYCODE_SHIFT_LEFT || code == KeyEvent.KEYCODE_SHIFT_RIGHT) {
                mModifierFlag += key.on ? FLAG_SHIFT : FLAG_NONE;

            } else if (code == KeyEvent.KEYCODE_ALT_LEFT) {
                mModifierFlag += key.on ? FLAG_ALT_LEFT : FLAG_NONE;

            } else if (code == KeyEvent.KEYCODE_ALT_RIGHT) {
                mModifierFlag += key.on ? FLAG_ALT_RIGHT : FLAG_NONE;

            } else if (code == KeyEvent.KEYCODE_WINDOW) {
                mModifierFlag += key.on ? FLAG_WINDOWS : FLAG_NONE;
            }

            if (BuildConfig.DEBUG) {
                sendToast(Integer.toBinaryString(mModifierFlag));
            }
        }
    }

    private void sendRequest(final RemoteCommand.Request.Code primaryCode) {
        mRequestSender.sendRequest(RemoteCommand.Request.newBuilder()
                .setSecurityToken(mRequestSender.getSecurityToken())
                .setType(RemoteCommand.Request.Type.KEYBOARD)
                .setCode(primaryCode)
                .setIntExtra(mModifierFlag)
                .build());
    }

    //
    // OnKeyboardActionListener methods
    //

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        handleKey(primaryCode);
    }

    @Override
    public void onPress(int primaryCode) { }

    @Override
    public void onRelease(int primaryCode) { }

    @Override
    public void onText(CharSequence text) { }

    @Override
    public void swipeLeft() { }

    @Override
    public void swipeRight() { }

    @Override
    public void swipeDown() { }

    @Override
    public void swipeUp() { }

    public boolean handleKey(int keyCode) {

        performHapticFeedback();

        switch (keyCode) {

            // Modifier keys

            case KeyEvent.KEYCODE_CTRL_LEFT:
            case KeyEvent.KEYCODE_CTRL_RIGHT:
            case KeyEvent.KEYCODE_SHIFT_LEFT:
            case KeyEvent.KEYCODE_SHIFT_RIGHT:
            case KeyEvent.KEYCODE_ALT_LEFT:
            case KeyEvent.KEYCODE_ALT_RIGHT:
            case KeyEvent.KEYCODE_WINDOW:
                updateModifierKeys();
                return true;

            // Special Keys

            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_ENTER);
                return true;

            case KeyEvent.KEYCODE_TAB:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_TAB);
                return true;

            case KeyEvent.KEYCODE_SPACE:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_SPACE);
                return true;

            case KeyEvent.KEYCODE_DEL:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_BACKSPACE);
                return true;

            case KeyEvent.KEYCODE_FORWARD_DEL:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_DELETE);
                return true;

            case KeyEvent.KEYCODE_ESCAPE:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_ESCAPE);
                return true;

            // D-pad keys

            case KeyEvent.KEYCODE_DPAD_LEFT:
                sendRequest(RemoteCommand.Request.Code.DPAD_LEFT);
                return true;

            case KeyEvent.KEYCODE_DPAD_UP:
                sendRequest(RemoteCommand.Request.Code.DPAD_UP);
                return true;

            case KeyEvent.KEYCODE_DPAD_RIGHT:
                sendRequest(RemoteCommand.Request.Code.DPAD_RIGHT);
                return true;

            case KeyEvent.KEYCODE_DPAD_DOWN:
                sendRequest(RemoteCommand.Request.Code.DPAD_DOWN);
                return true;

            // Combination
            //
            //            case ExchangeMessages.Request.Code.ALTLEFT_VALUE:
            //                return true;

            //            <!-- Combination -->
            //            <string translatable="false" name="code_alt_f4">1</string>
            //            <string translatable="false" name="code_ctrl_enter">1</string>
            //
            // Number keys

            case KeyEvent.KEYCODE_0:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_0);
                return true;

            case KeyEvent.KEYCODE_1:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_1);
                return true;

            case KeyEvent.KEYCODE_2:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_2);
                return true;

            case KeyEvent.KEYCODE_3:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_3);
                return true;

            case KeyEvent.KEYCODE_4:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_4);
                return true;

            case KeyEvent.KEYCODE_5:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_5);
                return true;

            case KeyEvent.KEYCODE_6:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_6);
                return true;

            case KeyEvent.KEYCODE_7:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_7);
                return true;

            case KeyEvent.KEYCODE_8:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_8);
                return true;

            case KeyEvent.KEYCODE_9:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_9);
                return true;

            //
            // Letter keys (A-Z)
            //

            case KeyEvent.KEYCODE_A:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_A);
                return true;

            case KeyEvent.KEYCODE_B:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_B);
                return true;

            case KeyEvent.KEYCODE_C:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_C);
                return true;

            case KeyEvent.KEYCODE_D:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_D);
                return true;

            case KeyEvent.KEYCODE_E:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_E);
                return true;

            case KeyEvent.KEYCODE_F:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_F);
                return true;

            case KeyEvent.KEYCODE_G:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_G);
                return true;

            case KeyEvent.KEYCODE_H:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_H);
                return true;

            case KeyEvent.KEYCODE_I:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_I);
                return true;

            case KeyEvent.KEYCODE_J:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_J);
                return true;

            case KeyEvent.KEYCODE_K:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_K);
                return true;

            case KeyEvent.KEYCODE_L:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_L);
                return true;

            case KeyEvent.KEYCODE_M:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_M);
                return true;

            case KeyEvent.KEYCODE_N:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_N);
                return true;

            case KeyEvent.KEYCODE_O:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_O);
                return true;

            case KeyEvent.KEYCODE_P:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_P);
                return true;

            case KeyEvent.KEYCODE_Q:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_Q);
                return true;

            case KeyEvent.KEYCODE_R:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_R);
                return true;

            case KeyEvent.KEYCODE_S:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_S);
                return true;

            case KeyEvent.KEYCODE_T:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_T);
                return true;

            case KeyEvent.KEYCODE_U:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_U);
                return true;

            case KeyEvent.KEYCODE_V:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_V);
                return true;

            case KeyEvent.KEYCODE_W:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_W);
                return true;

            case KeyEvent.KEYCODE_X:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_X);
                return true;

            case KeyEvent.KEYCODE_Y:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_Y);
                return true;

            case KeyEvent.KEYCODE_Z:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_Z);
                return true;

            //
            // Special F1 - F12
            //

            case KeyEvent.KEYCODE_F1:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_F1);
                return true;

            case KeyEvent.KEYCODE_F2:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_F2);
                return true;

            case KeyEvent.KEYCODE_F3:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_F3);
                return true;

            case KeyEvent.KEYCODE_F4:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_F4);
                return true;

            case KeyEvent.KEYCODE_F5:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_F5);
                return true;

            case KeyEvent.KEYCODE_F6:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_F6);
                return true;

            case KeyEvent.KEYCODE_F7:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_F7);
                return true;

            case KeyEvent.KEYCODE_F8:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_F8);
                return true;

            case KeyEvent.KEYCODE_F9:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_F9);
                return true;

            case KeyEvent.KEYCODE_F10:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_F10);
                return true;

            case KeyEvent.KEYCODE_F11:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_F11);
                return true;

            case KeyEvent.KEYCODE_F12:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_F12);
                return true;

            //
            // Special char keys
            // '=', '-', '+', '*',
            // '/', '\', '_', '|'
            // ',', '.', ':', ';', '@', '''
            //

            case KeyEvent.KEYCODE_EQUALS:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_EQUALS);
                return true;

            case KeyEvent.KEYCODE_MINUS:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_MINUS);
                return true;

            case KeyEvent.KEYCODE_PLUS:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_PLUS);
                return true;

            case KeyEvent.KEYCODE_STAR:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_STAR);
                return true;

            case KeyEvent.KEYCODE_SLASH:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_SLASH);
                return true;

            case KeyEvent.KEYCODE_BACKSLASH:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_BACKSLASH);
                return true;

            // TODO : map underscore '_'
            //            case KeyEvent.KEYCODE_UNDERSCORE:
            //                sendRequest(RemoteCommand.Request.Code.KEYCODE_UNDERSCORE);
            //                return true;

            // TODO : map pipe '|'
            //            case KeyEvent.KEYCODE_PIPE:
            //                sendRequest(RemoteCommand.Request.Code.KEYCODE_PIPE);
            //                return true;

            case KeyEvent.KEYCODE_COMMA:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_COMMA);
                return true;

            case KeyEvent.KEYCODE_PERIOD:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_PERIODE);
                return true;

            // TODO : map colon ':'
            //            case KeyEvent.KEYCODE_COLON:
            //                sendRequest(RemoteCommand.Request.Code.KEYCODE_COLON);
            //                return true;

            case KeyEvent.KEYCODE_SEMICOLON:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_SEMICOLON);
                return true;

            case KeyEvent.KEYCODE_AT:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_AT);
                return true;

            case KeyEvent.KEYCODE_APOSTROPHE:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_APOSTROPHE);
                return true;


            //
            // Parent and bracket keys
            // '(', '[', '{', '<'
            //

            case KeyEvent.KEYCODE_NUMPAD_LEFT_PAREN:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_LEFT_PAREN);
                return true;

            case KeyEvent.KEYCODE_NUMPAD_RIGHT_PAREN:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_RIGHT_PARENT);
                return true;

            case KeyEvent.KEYCODE_LEFT_BRACKET:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_LEFT_BRACKET);
                return true;

            case KeyEvent.KEYCODE_RIGHT_BRACKET:
                sendRequest(RemoteCommand.Request.Code.KEYCODE_RIGHT_BRACKET);
                return true;

            // TODO : map curly brackets '{' and '}'
            //            case KeyEvent.KEYCODE_LEFT_CURLY_BRACKET:
            //                sendRequest(RemoteCommand.Request.Code.KEYCODE_LEFT_CURLY_BRACKET);
            //                return true;
            //
            //            case KeyEvent.KEYCODE_RIGHT_CURLY_BRACKET:
            //                sendRequest(RemoteCommand.Request.Code.KEYCODE_RIGHT_CURLY_BRACKET);
            //                return true;

            // TODO : map curly brackets '<' and '>'
            //            case KeyEvent.KEYCODE_LEFT_ANGLE_BRACKET:
            //                sendRequest(RemoteCommand.Request.Code.KEYCODE_LEFT_ANGLE_BRACKET);
            //                return true;
            //
            //            case KeyEvent.KEYCODE_RIGHT_ANGLE_BRACKET:
            //                sendRequest(RemoteCommand.Request.Code.KEYCODE_RIGHT_ANGLE_BRACKET);
            //                return true;

            default:
                sendToast("Key not handled : " + keyCode);
                return false;
        }
    }
}