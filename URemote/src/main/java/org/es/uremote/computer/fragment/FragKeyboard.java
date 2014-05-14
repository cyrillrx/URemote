package org.es.uremote.computer.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import org.es.uremote.R;
import org.es.uremote.device.ServerSetting;
import org.es.uremote.exchange.Message.Request.Code;
import org.es.uremote.exchange.Message.Request.Type;
import org.es.uremote.exchange.MessageUtils;
import org.es.uremote.exchange.RequestSender;

import static android.view.HapticFeedbackConstants.VIRTUAL_KEY;
import static org.es.uremote.exchange.Message.Request.Type.KEYBOARD;

/**
 * Class to connect and send commands to a remote server through AsyncTask.
 *
 * @author Cyril Leroux
 *         Created on 22/04/12.
 */
public class FragKeyboard extends Fragment implements OnClickListener {

    private static final String TAG = "FragKeyboard";

    private RequestSender mRequestSender;

    private ToggleButton mTbControl;
    private ToggleButton mTbAlt;
    private ToggleButton mTbShift;
    private ToggleButton mTbWindows;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mRequestSender = (RequestSender) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);
    }

    /**
     * Set the callback to null so we don't accidentally leak the
     * Activity instance.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mRequestSender = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * Called after {@link #onActivityCreated(Bundle)}
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.server_frag_keyboard, container, false);

        mTbControl = ((ToggleButton) view.findViewById(R.id.kbControl));
        mTbAlt = ((ToggleButton) view.findViewById(R.id.kbAlt));
        mTbShift = ((ToggleButton) view.findViewById(R.id.kbShift));
        mTbWindows = ((ToggleButton) view.findViewById(R.id.kbWindows));

        mTbControl.setOnClickListener(this);
        mTbAlt.setOnClickListener(this);
        mTbShift.setOnClickListener(this);
        mTbWindows.setOnClickListener(this);

        view.findViewById(R.id.kbEnter).setOnClickListener(this);
        view.findViewById(R.id.kbSpace).setOnClickListener(this);
        view.findViewById(R.id.kbBackspace).setOnClickListener(this);
        view.findViewById(R.id.kbEscape).setOnClickListener(this);
        view.findViewById(R.id.kbAltF4).setOnClickListener(this);

        view.findViewById(R.id.kb0).setOnClickListener(this);
        view.findViewById(R.id.kb1).setOnClickListener(this);
        view.findViewById(R.id.kb2).setOnClickListener(this);
        view.findViewById(R.id.kb3).setOnClickListener(this);
        view.findViewById(R.id.kb4).setOnClickListener(this);
        view.findViewById(R.id.kb5).setOnClickListener(this);
        view.findViewById(R.id.kb6).setOnClickListener(this);
        view.findViewById(R.id.kb7).setOnClickListener(this);
        view.findViewById(R.id.kb8).setOnClickListener(this);
        view.findViewById(R.id.kb9).setOnClickListener(this);

        view.findViewById(R.id.kbA).setOnClickListener(this);
        view.findViewById(R.id.kbB).setOnClickListener(this);
        view.findViewById(R.id.kbC).setOnClickListener(this);
        view.findViewById(R.id.kbD).setOnClickListener(this);
        view.findViewById(R.id.kbE).setOnClickListener(this);
        view.findViewById(R.id.kbF).setOnClickListener(this);
        view.findViewById(R.id.kbG).setOnClickListener(this);
        view.findViewById(R.id.kbH).setOnClickListener(this);
        view.findViewById(R.id.kbI).setOnClickListener(this);
        view.findViewById(R.id.kbJ).setOnClickListener(this);
        view.findViewById(R.id.kbK).setOnClickListener(this);
        view.findViewById(R.id.kbL).setOnClickListener(this);
        view.findViewById(R.id.kbM).setOnClickListener(this);
        view.findViewById(R.id.kbN).setOnClickListener(this);
        view.findViewById(R.id.kbO).setOnClickListener(this);
        view.findViewById(R.id.kbP).setOnClickListener(this);
        view.findViewById(R.id.kbQ).setOnClickListener(this);
        view.findViewById(R.id.kbR).setOnClickListener(this);
        view.findViewById(R.id.kbS).setOnClickListener(this);
        view.findViewById(R.id.kbT).setOnClickListener(this);
        view.findViewById(R.id.kbU).setOnClickListener(this);
        view.findViewById(R.id.kbV).setOnClickListener(this);
        view.findViewById(R.id.kbW).setOnClickListener(this);
        view.findViewById(R.id.kbX).setOnClickListener(this);
        view.findViewById(R.id.kbY).setOnClickListener(this);
        view.findViewById(R.id.kbZ).setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        view.performHapticFeedback(VIRTUAL_KEY);

        final Code extraCode = getExtraCode(view);

        switch (view.getId()) {

            case R.id.kbControl:
                if (mTbControl.isChecked()) {
                    mTbAlt.setChecked(false);
                    mTbShift.setChecked(false);
                    mTbWindows.setChecked(false);
                }
                break;

            case R.id.kbAlt:
                if (mTbAlt.isChecked()) {
                    mTbControl.setChecked(false);
                    mTbShift.setChecked(false);
                    mTbWindows.setChecked(false);
                }
                break;

            case R.id.kbShift:
                if (mTbShift.isChecked()) {
                    mTbControl.setChecked(false);
                    mTbAlt.setChecked(false);
                    mTbWindows.setChecked(false);
                }
                break;

            case R.id.kbWindows:
                if (mTbWindows.isChecked()) {
                    mTbControl.setChecked(false);
                    mTbAlt.setChecked(false);
                    mTbShift.setChecked(false);
                }
                break;

            case R.id.kbEnter:
                sendRequest(KEYBOARD, Code.KEYCODE_ENTER, extraCode);
                break;

            case R.id.kbSpace:
                sendRequest(KEYBOARD, Code.KEYCODE_SPACE, extraCode);
                break;

            case R.id.kbBackspace:
                sendRequest(KEYBOARD, Code.KEYCODE_BACKSPACE, extraCode);
                break;

            case R.id.kbEscape:
                sendRequest(KEYBOARD, Code.KEYCODE_ESCAPE, extraCode);
                break;

            case R.id.kbAltF4:
                sendRequest(KEYBOARD, Code.KEYCODE_F4, Code.KEYCODE_ALT_LEFT);
                break;

            case R.id.kb0:
                sendRequest(KEYBOARD, Code.DEFINE, extraCode, "0");
                break;

            case R.id.kb1:
                sendRequest(KEYBOARD, Code.DEFINE, extraCode, "1");
                break;

            case R.id.kb2:
                sendRequest(KEYBOARD, Code.DEFINE, extraCode, "2");
                break;
            case R.id.kb3:
                sendRequest(KEYBOARD, Code.DEFINE, extraCode, "3");
                break;
            case R.id.kb4:
                sendRequest(KEYBOARD, Code.DEFINE, extraCode, "4");
                break;
            case R.id.kb5:
                sendRequest(KEYBOARD, Code.DEFINE, extraCode, "5");
                break;
            case R.id.kb6:
                sendRequest(KEYBOARD, Code.DEFINE, extraCode, "6");
                break;
            case R.id.kb7:
                sendRequest(KEYBOARD, Code.DEFINE, extraCode, "7");
                break;
            case R.id.kb8:
                sendRequest(KEYBOARD, Code.DEFINE, extraCode, "8");
                break;
            case R.id.kb9:
                sendRequest(KEYBOARD, Code.DEFINE, extraCode, "9");
                break;

            case R.id.kbA:
                sendRequest(KEYBOARD, Code.DEFINE, extraCode, "A");
                break;
            case R.id.kbB:
                sendRequest(KEYBOARD, Code.DEFINE, extraCode, "B");
                break;
            case R.id.kbC:
                sendRequest(KEYBOARD, Code.DEFINE, extraCode, "C");
                break;
            case R.id.kbD:
                sendRequest(KEYBOARD, Code.DEFINE, extraCode, "D");
                break;
            case R.id.kbE:
                sendRequest(KEYBOARD, Code.DEFINE, extraCode, "E");
                break;
            case R.id.kbF:
                sendRequest(KEYBOARD, Code.DEFINE, extraCode, "F");
                break;
            case R.id.kbG:
                sendRequest(KEYBOARD, Code.DEFINE, extraCode, "G");
                break;
            case R.id.kbH:
                sendRequest(KEYBOARD, Code.DEFINE, extraCode, "H");
                break;
            case R.id.kbI:
                sendRequest(KEYBOARD, Code.DEFINE, extraCode, "I");
                break;
            case R.id.kbJ:
                sendRequest(KEYBOARD, Code.DEFINE, extraCode, "J");
                break;
            case R.id.kbK:
                sendRequest(KEYBOARD, Code.DEFINE, extraCode, "K");
                break;
            case R.id.kbL:
                sendRequest(KEYBOARD, Code.DEFINE, extraCode, "L");
                break;
            case R.id.kbM:
                sendRequest(KEYBOARD, Code.DEFINE, extraCode, "M");
                break;
            case R.id.kbN:
                sendRequest(KEYBOARD, Code.DEFINE, extraCode, "N");
                break;
            case R.id.kbO:
                sendRequest(KEYBOARD, Code.DEFINE, extraCode, "O");
                break;
            case R.id.kbP:
                sendRequest(KEYBOARD, Code.DEFINE, extraCode, "P");
                break;
            case R.id.kbQ:
                sendRequest(KEYBOARD, Code.DEFINE, extraCode, "Q");
                break;
            case R.id.kbR:
                sendRequest(KEYBOARD, Code.DEFINE, extraCode, "R");
                break;
            case R.id.kbS:
                sendRequest(KEYBOARD, Code.DEFINE, extraCode, "S");
                break;
            case R.id.kbT:
                sendRequest(KEYBOARD, Code.DEFINE, extraCode, "T");
                break;
            case R.id.kbU:
                sendRequest(KEYBOARD, Code.DEFINE, extraCode, "U");
                break;
            case R.id.kbV:
                sendRequest(KEYBOARD, Code.DEFINE, extraCode, "V");
                break;
            case R.id.kbW:
                sendRequest(KEYBOARD, Code.DEFINE, extraCode, "W");
                break;
            case R.id.kbX:
                sendRequest(KEYBOARD, Code.DEFINE, extraCode, "X");
                break;
            case R.id.kbY:
                sendRequest(KEYBOARD, Code.DEFINE, extraCode, "Y");
                break;
            case R.id.kbZ:
                sendRequest(KEYBOARD, Code.DEFINE, extraCode, "Z");
                break;

            default:
                break;
        }
    }

    private Code getExtraCode(View view) {
        if (mTbControl.isChecked()) {
            return Code.KEYCODE_CTRL;
        } else if (mTbAlt.isChecked()) {
            return Code.KEYCODE_ALT_LEFT;
        } else if (mTbShift.isChecked()) {
            return Code.KEYCODE_SHIFT;
        } else if (mTbWindows.isChecked()) {
            return Code.KEYCODE_WINDOWS;
        }
        return Code.NONE;
    }

    ////////////////////////////////////////////////////////////////////
    // *********************** Message Sender *********************** //
    ////////////////////////////////////////////////////////////////////

    /**
     * Initializes the message handler then send the request.
     *
     * @param type      The request type.
     * @param code      The request code.
     * @param extraCode The request extra code.
     */
    public void sendRequest(final Type type, final Code code, final Code extraCode) {
        mRequestSender.sendRequest(MessageUtils.buildRequest(getSecurityToken(), type, code, extraCode));
    }

    public String getSecurityToken() {
        ServerSetting settings = mRequestSender.getDevice();
        if (settings != null) {
            return settings.getSecurityToken();
        }
        return null;
    }

    /**
     * Initializes the message handler then send the request.
     *
     * @param type        The request type.
     * @param code        The request code.
     * @param extraCode   The request extra code.
     * @param stringParam A string parameter.
     */
    public void sendRequest(final Type type, final Code code, final Code extraCode, final String stringParam) {
        mRequestSender.sendRequest(MessageUtils.buildRequest(getSecurityToken(), type, code, extraCode, stringParam));
    }
}
