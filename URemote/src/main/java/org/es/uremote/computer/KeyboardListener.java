package org.es.uremote.computer;

import android.inputmethodservice.KeyboardView;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.View;

import org.es.uremote.exchange.MessageUtils;
import org.es.uremote.exchange.RequestSender;
import org.es.uremote.utils.ToastSender;
import org.es.uremote.exchange.Message;
import org.es.utils.Log;

/**
 * @author Cyril Leroux
 *         Created 01/05/2014.
 */
public class KeyboardListener implements KeyboardView.OnKeyboardActionListener {

    private static final int NO_FLAG = 0;
    private static final String TAG = "org.es.uremote.computer.KeyboardListener";

    private final RequestSender mRequestSender;
    private View mHapticFeedbackView;
    private ToastSender mToastSender;

    public KeyboardListener(final RequestSender requestSender) {
        mRequestSender = requestSender;
    }

    /**
     * Sets the view that will be use to perform haptic feedback.
     * @param view The view that will be use to perform haptic feedback.
     */
    public void setHapticFeedbackView(final View view) {
        mHapticFeedbackView = view;
    }

    /**
     * Performs haptic feedback using the view specified through {@link #setHapticFeedbackView(android.view.View)}.
     */
    private void performHapticFeedback() {
        if (mHapticFeedbackView == null) {
            Log.warning(TAG, "#performHapticFeedback - mHapticFeedbackView is null. Can't perform haptic feedback.");
            return;
        }
        mHapticFeedbackView.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
    }

    public void setToastSender(final ToastSender toastSender) { mToastSender = toastSender; }

    private void sendToast(final String message) {
        if (mToastSender == null) {
            Log.warning(TAG, "#sendToast - mToastSender is null. Can't send toast.");
            return;
        }
        mToastSender.sendToast(message);
    }

    public boolean handleKey(int keyCode, int extraCodes) {
        performHapticFeedback();

        switch (keyCode) {

            // Special Keys

            case KeyEvent.KEYCODE_CTRL_LEFT:
            case KeyEvent.KEYCODE_CTRL_RIGHT:
                sendKey(Message.Request.Code.KEYCODE_CTRL, extraCodes);
                return true;

            case KeyEvent.KEYCODE_ALT_LEFT:
                sendKey(Message.Request.Code.KEYCODE_ALT_LEFT, extraCodes);
                return true;

            case KeyEvent.KEYCODE_ALT_RIGHT:
                sendKey(Message.Request.Code.KEYCODE_ALT_RIGHT, extraCodes);
                return true;

            case KeyEvent.KEYCODE_SHIFT_LEFT:
            case KeyEvent.KEYCODE_SHIFT_RIGHT:
                sendKey(Message.Request.Code.KEYCODE_SHIFT, extraCodes);
                return true;

            case KeyEvent.KEYCODE_HOME:
                sendKey(Message.Request.Code.KEYCODE_WINDOWS, extraCodes);
                return true;

            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                sendKey(Message.Request.Code.KEYCODE_ENTER, extraCodes);
                return true;

            case KeyEvent.KEYCODE_SPACE:
                sendKey(Message.Request.Code.KEYCODE_SPACE, extraCodes);
                return true;

            case KeyEvent.KEYCODE_DEL:
                sendKey(Message.Request.Code.KEYCODE_BACKSPACE, extraCodes);
                return true;

            case KeyEvent.KEYCODE_FORWARD_DEL:
                sendKey(Message.Request.Code.KEYCODE_DELETE, extraCodes);
                return true;

            case KeyEvent.KEYCODE_ESCAPE:
                sendKey(Message.Request.Code.KEYCODE_ESCAPE, extraCodes);
                return true;

            // D-pad keys

            case KeyEvent.KEYCODE_DPAD_LEFT:
                sendKey(Message.Request.Code.DPAD_LEFT, extraCodes);
                return true;

            case KeyEvent.KEYCODE_DPAD_UP:
                sendKey(Message.Request.Code.DPAD_UP, extraCodes);
                return true;

            case KeyEvent.KEYCODE_DPAD_RIGHT:
                sendKey(Message.Request.Code.DPAD_RIGHT, extraCodes);
                return true;

            case KeyEvent.KEYCODE_DPAD_DOWN:
                sendKey(Message.Request.Code.DPAD_DOWN, extraCodes);
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
                sendKey(Message.Request.Code.KEYCODE_0, extraCodes);
                return true;

            case KeyEvent.KEYCODE_1:
                sendKey(Message.Request.Code.KEYCODE_1, extraCodes);
                return true;

            case KeyEvent.KEYCODE_2:
                sendKey(Message.Request.Code.KEYCODE_2, extraCodes);
                return true;

            case KeyEvent.KEYCODE_3:
                sendKey(Message.Request.Code.KEYCODE_3, extraCodes);
                return true;

            case KeyEvent.KEYCODE_4:
                sendKey(Message.Request.Code.KEYCODE_4, extraCodes);
                return true;

            case KeyEvent.KEYCODE_5:
                sendKey(Message.Request.Code.KEYCODE_5, extraCodes);
                return true;

            case KeyEvent.KEYCODE_6:
                sendKey(Message.Request.Code.KEYCODE_6, extraCodes);
                return true;

            case KeyEvent.KEYCODE_7:
                sendKey(Message.Request.Code.KEYCODE_7, extraCodes);
                return true;

            case KeyEvent.KEYCODE_8:
                sendKey(Message.Request.Code.KEYCODE_8, extraCodes);
                return true;

            case KeyEvent.KEYCODE_9:
                sendKey(Message.Request.Code.KEYCODE_9, extraCodes);
                return true;

            //
            // Letter keys (A-Z)
            //

            case KeyEvent.KEYCODE_A:
                sendKey(Message.Request.Code.KEYCODE_A, extraCodes);
                return true;

            case KeyEvent.KEYCODE_B:
                sendKey(Message.Request.Code.KEYCODE_B, extraCodes);
                return true;

            case KeyEvent.KEYCODE_C:
                sendKey(Message.Request.Code.KEYCODE_C, extraCodes);
                return true;

            case KeyEvent.KEYCODE_D:
                sendKey(Message.Request.Code.KEYCODE_D, extraCodes);
                return true;

            case KeyEvent.KEYCODE_E:
                sendKey(Message.Request.Code.KEYCODE_E, extraCodes);
                return true;

            case KeyEvent.KEYCODE_F:
                sendKey(Message.Request.Code.KEYCODE_F, extraCodes);
                return true;

            case KeyEvent.KEYCODE_G:
                sendKey(Message.Request.Code.KEYCODE_G, extraCodes);
                return true;

            case KeyEvent.KEYCODE_H:
                sendKey(Message.Request.Code.KEYCODE_H, extraCodes);
                return true;

            case KeyEvent.KEYCODE_I:
                sendKey(Message.Request.Code.KEYCODE_I, extraCodes);
                return true;

            case KeyEvent.KEYCODE_J:
                sendKey(Message.Request.Code.KEYCODE_J, extraCodes);
                return true;

            case KeyEvent.KEYCODE_K:
                sendKey(Message.Request.Code.KEYCODE_K, extraCodes);
                return true;

            case KeyEvent.KEYCODE_L:
                sendKey(Message.Request.Code.KEYCODE_L, extraCodes);
                return true;

            case KeyEvent.KEYCODE_M:
                sendKey(Message.Request.Code.KEYCODE_M, extraCodes);
                return true;

            case KeyEvent.KEYCODE_N:
                sendKey(Message.Request.Code.KEYCODE_N, extraCodes);
                return true;

            case KeyEvent.KEYCODE_O:
                sendKey(Message.Request.Code.KEYCODE_O, extraCodes);
                return true;

            case KeyEvent.KEYCODE_P:
                sendKey(Message.Request.Code.KEYCODE_P, extraCodes);
                return true;

            case KeyEvent.KEYCODE_Q:
                sendKey(Message.Request.Code.KEYCODE_Q, extraCodes);
                return true;

            case KeyEvent.KEYCODE_R:
                sendKey(Message.Request.Code.KEYCODE_R, extraCodes);
                return true;

            case KeyEvent.KEYCODE_S:
                sendKey(Message.Request.Code.KEYCODE_S, extraCodes);
                return true;

            case KeyEvent.KEYCODE_T:
                sendKey(Message.Request.Code.KEYCODE_T, extraCodes);
                return true;

            case KeyEvent.KEYCODE_U:
                sendKey(Message.Request.Code.KEYCODE_U, extraCodes);
                return true;

            case KeyEvent.KEYCODE_V:
                sendKey(Message.Request.Code.KEYCODE_V, extraCodes);
                return true;

            case KeyEvent.KEYCODE_W:
                sendKey(Message.Request.Code.KEYCODE_W, extraCodes);
                return true;

            case KeyEvent.KEYCODE_X:
                sendKey(Message.Request.Code.KEYCODE_X, extraCodes);
                return true;

            case KeyEvent.KEYCODE_Y:
                sendKey(Message.Request.Code.KEYCODE_Y, extraCodes);
                return true;

            case KeyEvent.KEYCODE_Z:
                sendKey(Message.Request.Code.KEYCODE_Z, extraCodes);
                return true;

            //
            // Special F1 - F12
            //

            case KeyEvent.KEYCODE_F1:
                sendKey(Message.Request.Code.KEYCODE_F1, extraCodes);
                return true;

            case KeyEvent.KEYCODE_F2:
                sendKey(Message.Request.Code.KEYCODE_F2, extraCodes);
                return true;

            case KeyEvent.KEYCODE_F3:
                sendKey(Message.Request.Code.KEYCODE_F3, extraCodes);
                return true;

            case KeyEvent.KEYCODE_F4:
                sendKey(Message.Request.Code.KEYCODE_F4, extraCodes);
                return true;

            case KeyEvent.KEYCODE_F5:
                sendKey(Message.Request.Code.KEYCODE_F5, extraCodes);
                return true;

            case KeyEvent.KEYCODE_F6:
                sendKey(Message.Request.Code.KEYCODE_F6, extraCodes);
                return true;

            case KeyEvent.KEYCODE_F7:
                sendKey(Message.Request.Code.KEYCODE_F7, extraCodes);
                return true;

            case KeyEvent.KEYCODE_F8:
                sendKey(Message.Request.Code.KEYCODE_F8, extraCodes);
                return true;

            case KeyEvent.KEYCODE_F9:
                sendKey(Message.Request.Code.KEYCODE_F9, extraCodes);
                return true;

            case KeyEvent.KEYCODE_F10:
                sendKey(Message.Request.Code.KEYCODE_F10, extraCodes);
                return true;

            case KeyEvent.KEYCODE_F11:
                sendKey(Message.Request.Code.KEYCODE_F11, extraCodes);
                return true;

            case KeyEvent.KEYCODE_F12:
                sendKey(Message.Request.Code.KEYCODE_F12, extraCodes);
                return true;

            //
            // Special char keys
            // '=', '-', '+', '*',
            // '/', '\', '_', '|'
            // ',', '.', ':', ';', '@', '''
            //

            case KeyEvent.KEYCODE_EQUALS:
                sendKey(Message.Request.Code.KEYCODE_EQUALS, extraCodes);
                return true;

            case KeyEvent.KEYCODE_MINUS:
                sendKey(Message.Request.Code.KEYCODE_MINUS, extraCodes);
                return true;

            case KeyEvent.KEYCODE_PLUS:
                sendKey(Message.Request.Code.KEYCODE_PLUS, extraCodes);
                return true;

            case KeyEvent.KEYCODE_STAR:
                sendKey(Message.Request.Code.KEYCODE_STAR, extraCodes);
                return true;

            case KeyEvent.KEYCODE_SLASH:
                sendKey(Message.Request.Code.KEYCODE_SLASH, extraCodes);
                return true;

            case KeyEvent.KEYCODE_BACKSLASH:
                sendKey(Message.Request.Code.KEYCODE_BACKSLASH, extraCodes);
                return true;

            // TODO : map underscore '_'
//            case KeyEvent.KEYCODE_UNDERSCORE:
//                sendKey(Message.Request.Code.KEYCODE_UNDERSCORE, extraCodes);
//                return true;

            // TODO : map pipe '|'
//            case KeyEvent.KEYCODE_PIPE:
//                sendKey(Message.Request.Code.KEYCODE_PIPE, extraCodes);
//                return true;

            case KeyEvent.KEYCODE_COMMA:
                sendKey(Message.Request.Code.KEYCODE_COMMA, extraCodes);
                return true;

            case KeyEvent.KEYCODE_PERIOD:
                sendKey(Message.Request.Code.KEYCODE_PERIODE, extraCodes);
                return true;

            // TODO : map colon ':'
//            case KeyEvent.KEYCODE_COLON:
//                sendKey(Message.Request.Code.KEYCODE_COLON, extraCodes);
//                return true;

            case KeyEvent.KEYCODE_SEMICOLON:
                sendKey(Message.Request.Code.KEYCODE_SEMICOLON, extraCodes);
                return true;

            case KeyEvent.KEYCODE_AT:
                sendKey(Message.Request.Code.KEYCODE_AT, extraCodes);
                return true;

            case KeyEvent.KEYCODE_APOSTROPHE:
                sendKey(Message.Request.Code.KEYCODE_APOSTROPHE, extraCodes);
                return true;


            //
            // Parent and bracket keys
            // '(', '[', '{', '<'
            //

            case KeyEvent.KEYCODE_NUMPAD_LEFT_PAREN:
                sendKey(Message.Request.Code.KEYCODE_LEFT_PAREN, extraCodes);
                return true;

            case KeyEvent.KEYCODE_NUMPAD_RIGHT_PAREN:
                sendKey(Message.Request.Code.KEYCODE_RIGHT_PARENT, extraCodes);
                return true;

            case KeyEvent.KEYCODE_LEFT_BRACKET:
                sendKey(Message.Request.Code.KEYCODE_LEFT_BRACKET, extraCodes);
                return true;

            case KeyEvent.KEYCODE_RIGHT_BRACKET:
                sendKey(Message.Request.Code.KEYCODE_RIGHT_BRACKET, extraCodes);
                return true;

            // TODO : map curly brackets '{' and '}'
//            case KeyEvent.KEYCODE_LEFT_CURLY_BRACKET:
//                sendKey(Message.Request.Code.KEYCODE_LEFT_CURLY_BRACKET, extraCodes);
//                return true;
//
//            case KeyEvent.KEYCODE_RIGHT_CURLY_BRACKET:
//                sendKey(Message.Request.Code.KEYCODE_RIGHT_CURLY_BRACKET, extraCodes);
//                return true;

            // TODO : map curly brackets '<' and '>'
//            case KeyEvent.KEYCODE_LEFT_ANGLE_BRACKET:
//                sendKey(Message.Request.Code.KEYCODE_LEFT_ANGLE_BRACKET, extraCodes);
//                return true;
//
//            case KeyEvent.KEYCODE_RIGHT_ANGLE_BRACKET:
//                sendKey(Message.Request.Code.KEYCODE_RIGHT_ANGLE_BRACKET, extraCodes);
//                return true;

            default:
                sendToast("Key not handled : " + keyCode);
                return false;
        }
    }

    /**
     * Modifier
     * ----------
     * True if the key code is one of
     * - KEYCODE_SHIFT_LEFT,
     * - KEYCODE_SHIFT_RIGHT,
     * - KEYCODE_ALT_LEFT,
     * - KEYCODE_ALT_RIGHT,
     * - KEYCODE_CTRL_LEFT,
     * - KEYCODE_CTRL_RIGHT,
     * - KEYCODE_META_LEFT,
     * - KEYCODE_META_RIGHT,
     * - KEYCODE_SYM,
     * - KEYCODE_NUM,
     * - KEYCODE_FUNCTION.
     */
    private int getExtraCodes(KeyEvent keyEvent) {
        // TODO get the extra codes from keyEvent parameter
        return NO_FLAG;
    }

    private void sendKey(final Message.Request.Code primaryCode, final int extraCodes) {
        sendRequest(primaryCode, extraCodes);
    }

    private void sendRequest(Message.Request.Code primaryCode, int extraCodes) {
        // TODO pass the extra codes instead of Code.NONE
        mRequestSender.sendRequest(MessageUtils.buildRequest(mRequestSender.getSecurityToken(), Message.Request.Type.KEYBOARD, primaryCode, Message.Request.Code.NONE));
    }

    //
    // OnKeyboardActionListener methods
    //

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        handleKey(primaryCode, getExtraCodes(null));
    }

    @Override public void onPress(int primaryCode) { }
    @Override public void onRelease(int primaryCode) { }
    @Override public void onText(CharSequence text) { }
    @Override public void swipeLeft() { }
    @Override public void swipeRight() { }
    @Override public void swipeDown() { }
    @Override public void swipeUp() { }
}