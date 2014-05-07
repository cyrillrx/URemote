package org.es.uremote.computer;

import android.inputmethodservice.KeyboardView;
import android.view.HapticFeedbackConstants;
import android.view.View;

import org.es.uremote.exchange.Message;

/**
 * @author Cyril Leroux
 *         Created 01/05/2014.
 */
public class KeyboardListener implements KeyboardView.OnKeyboardActionListener {

    private View mHapticFeedbackView;

    public void setHapticFeedbackView(final View view) {
        mHapticFeedbackView = view;
    }

    private void performHapticFeedback() {
        if (mHapticFeedbackView != null) {
            mHapticFeedbackView.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
        }
    }

    @Override
    public void onPress(int primaryCode) {

        performHapticFeedback();

        switch (primaryCode) {

            // Special Keys

            case Message.Request.Code.KB_CTRL_VALUE:
                break;

            case Message.Request.Code.KB_ALT_VALUE:
                break;

            case Message.Request.Code.KB_SHIFT_VALUE:
                break;

            case Message.Request.Code.KB_WINDOWS_VALUE:
                break;

            case Message.Request.Code.KB_RETURN_VALUE:
                break;

            case Message.Request.Code.KB_SPACE_VALUE:
                break;

            case Message.Request.Code.KB_BACKSPACE_VALUE:
                break;

            case Message.Request.Code.KB_ESCAPE_VALUE:
                break;

            // D-pad keys

            case Message.Request.Code.LEFT_VALUE:
                break;

            case Message.Request.Code.UP_VALUE:
                break;

            case Message.Request.Code.RIGHT_VALUE:
                break;

            case Message.Request.Code.DOWN_VALUE:
                break;

            // Combination
//
//            case ExchangeMessages.Request.Code.ALTLEFT_VALUE:
//                break;

//            <!-- Combination -->
//            <string translatable="false" name="code_alt_f4">1</string>
//            <string translatable="false" name="code_ctrl_enter">1</string>
//
//            <!-- Other keys -->

            case Message.Request.Code.KB_LOWERCASE_A_VALUE:
                break;

            case Message.Request.Code.KB_LOWERCASE_B_VALUE:
                break;

            case Message.Request.Code.KB_LOWERCASE_C_VALUE:
                break;

            case Message.Request.Code.KB_LOWERCASE_D_VALUE:
                break;

            case Message.Request.Code.KB_LOWERCASE_E_VALUE:
                break;

            case Message.Request.Code.KB_LOWERCASE_F_VALUE:
                break;

            case Message.Request.Code.KB_LOWERCASE_G_VALUE:
                break;

            case Message.Request.Code.KB_LOWERCASE_H_VALUE:
                break;

            case Message.Request.Code.KB_LOWERCASE_I_VALUE:
                break;

            case Message.Request.Code.KB_LOWERCASE_J_VALUE:
                break;

            case Message.Request.Code.KB_LOWERCASE_K_VALUE:
                break;

            case Message.Request.Code.KB_LOWERCASE_L_VALUE:
                break;

            case Message.Request.Code.KB_LOWERCASE_M_VALUE:
                break;

            case Message.Request.Code.KB_LOWERCASE_N_VALUE:
                break;

            case Message.Request.Code.KB_LOWERCASE_O_VALUE:
                break;

            case Message.Request.Code.KB_LOWERCASE_P_VALUE:
                break;

            case Message.Request.Code.KB_LOWERCASE_Q_VALUE:
                break;

            case Message.Request.Code.KB_LOWERCASE_R_VALUE:
                break;

            case Message.Request.Code.KB_LOWERCASE_S_VALUE:
                break;

            case Message.Request.Code.KB_LOWERCASE_T_VALUE:
                break;

            case Message.Request.Code.KB_LOWERCASE_U_VALUE:
                break;

            case Message.Request.Code.KB_LOWERCASE_V_VALUE:
                break;

            case Message.Request.Code.KB_LOWERCASE_W_VALUE:
                break;

            case Message.Request.Code.KB_LOWERCASE_X_VALUE:
                break;

            case Message.Request.Code.KB_LOWERCASE_Y_VALUE:
                break;

            case Message.Request.Code.KB_LOWERCASE_Z_VALUE:
                break;
        }
    }

    @Override
    public void onRelease(int primaryCode) {

    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {

    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }
}