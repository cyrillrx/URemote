package com.cyrillrx.uremote.ui.computer.fragment;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.cyrillrx.logger.Logger;
import com.cyrillrx.uremote.R;
import com.cyrillrx.uremote.common.device.NetworkDevice;
import com.cyrillrx.uremote.common.model.AppItem;
import com.cyrillrx.uremote.network.AsyncMessageMgr;
import com.cyrillrx.uremote.request.RequestSender;
import com.cyrillrx.uremote.request.protobuf.RemoteCommand.Request;
import com.cyrillrx.uremote.request.protobuf.RemoteCommand.Request.Code;
import com.cyrillrx.uremote.request.protobuf.RemoteCommand.Request.Type;
import com.cyrillrx.uremote.request.protobuf.RemoteCommand.Response;
import com.cyrillrx.uremote.ui.AppLauncherActivity;
import com.cyrillrx.uremote.utils.IntentKeys;
import com.cyrillrx.uremote.utils.TaskCallbacks;

import java.util.ArrayList;

/**
 * Class to connect and send commands to a remote device through AsyncTask.
 *
 * @author Cyril Leroux
 *         Created on 21/04/12.
 */
public class FragDashboard extends Fragment implements OnClickListener, OnSeekBarChangeListener, RequestSender {

    private static final String TAG = FragDashboard.class.getSimpleName();

    /**
     * ActivityForResults request codes
     */
    private static final int RC_APP_LAUNCHER = 0;

    private static final int DELAY = 500;
    private static final int DURATION = 500;

    private TaskCallbacks callbacks;
    private RequestSender requestSender;

    private ObjectAnimator fadeIn;
    private ObjectAnimator fadeOut;

    private TextView tvVolume;
    private ImageButton ibMute;
    private SeekBar sbVolume;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callbacks = (TaskCallbacks) activity;
        requestSender = (RequestSender) activity;
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
        callbacks = null;
        requestSender = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * Called when the application is created.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.server_frag_dashboard, container, false);

        view.findViewById(R.id.kbLeft).setOnClickListener(this);
        view.findViewById(R.id.kbRight).setOnClickListener(this);
        view.findViewById(R.id.kbUp).setOnClickListener(this);
        view.findViewById(R.id.kbDown).setOnClickListener(this);
        view.findViewById(R.id.kbOk).setOnClickListener(this);

        view.findViewById(R.id.cmdTest).setOnClickListener(this);
        view.findViewById(R.id.cmdSwitch).setOnClickListener(this);
        view.findViewById(R.id.cmdGomStretch).setOnClickListener(this);
        view.findViewById(R.id.btnAppLauncher).setOnClickListener(this);

        view.findViewById(R.id.cmdPrevious).setOnClickListener(this);
        view.findViewById(R.id.cmdPlayPause).setOnClickListener(this);
        view.findViewById(R.id.cmdStop).setOnClickListener(this);
        view.findViewById(R.id.cmdNext).setOnClickListener(this);

        tvVolume = view.findViewById(R.id.toastText);

        ibMute = view.findViewById(R.id.cmdMute);
        ibMute.setOnClickListener(this);

        sbVolume = view.findViewById(R.id.sbVolume);
        sbVolume.setOnSeekBarChangeListener(this);

        return view;
    }

    @Override
    public void onClick(View _view) {
        _view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

        switch (_view.getId()) {

            case R.id.kbLeft:
                sendRequest(Type.KEYBOARD, Code.DPAD_LEFT);
                break;

            case R.id.kbRight:
                sendRequest(Type.KEYBOARD, Code.DPAD_RIGHT);
                break;

            case R.id.kbUp:
                sendRequest(Type.KEYBOARD, Code.DPAD_UP);
                break;

            case R.id.kbDown:
                sendRequest(Type.KEYBOARD, Code.DPAD_DOWN);
                break;

            case R.id.kbOk:
                sendRequest(Type.KEYBOARD, Code.KEYCODE_ENTER);
                break;

            case R.id.cmdTest:
                sendRequest(Type.SIMPLE, Code.TEST);
                break;

            case R.id.cmdSwitch:
                sendRequest(Type.SIMPLE, Code.SWITCH_WINDOW);
                break;

            case R.id.btnAppLauncher:
                final Intent appLauncherIntent = new Intent(getActivity().getApplicationContext(), AppLauncherActivity.class);
                ArrayList<AppItem> applicationList = new ArrayList<>();
                applicationList.add(new AppItem(getString(R.string.cmd_gom_start), "", R.drawable.app_gom_player, Code.ON.getNumber()));
                applicationList.add(new AppItem(getString(R.string.cmd_gom_kill), "", R.drawable.app_gom_player, Code.OFF.getNumber()));
                appLauncherIntent.putParcelableArrayListExtra(IntentKeys.EXTRA_APPLICATION_LIST, applicationList);
                startActivityForResult(appLauncherIntent, RC_APP_LAUNCHER);
                break;

            case R.id.cmdGomStretch:
                sendRequest(Type.APP, Code.KEYCODE_0);
                break;

            case R.id.cmdPrevious:
                sendRequest(Type.KEYBOARD, Code.MEDIA_PREVIOUS);
                break;

            case R.id.cmdPlayPause:
                sendRequest(Type.KEYBOARD, Code.MEDIA_PLAY_PAUSE);
                break;

            case R.id.cmdStop:
                sendRequest(Type.KEYBOARD, Code.MEDIA_STOP);
                break;

            case R.id.cmdNext:
                sendRequest(Type.KEYBOARD, Code.MEDIA_NEXT);
                break;

            case R.id.cmdMute:
                sendRequest(Type.VOLUME, Code.MUTE);
                break;

            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_APP_LAUNCHER && resultCode == Activity.RESULT_OK) {
            final Type type = Type.valueOf(data.getIntExtra(IntentKeys.REQUEST_TYPE, -1));
            final Code code = Code.valueOf(data.getIntExtra(IntentKeys.REQUEST_CODE, -1));

            if (type != null && code != null) {
                sendRequest(type, code);
            }
        }
    }

    /**
     * Show a static toast message.
     * The next message replace the previous one.
     *
     * @param message The message to display.
     * @param x       The x position to display it.
     * @param y       The y position to display it.
     */
    private void showVolumeToast(final String message, final int x, final int y) {
        tvVolume.setText(message);
        fadeIn(tvVolume);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!fromUser) {
            return;
        }
        final int volume = seekBar.getProgress();
        sendRequest(Type.VOLUME, Code.DEFINE, volume);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    //
    // Message Sender
    //

    /**
     * Initializes the message handler then send the request.
     *
     * @param requestType The request type.
     * @param requestCode The request code.
     * @param intExtra    An integer parameter.
     */
    private void sendRequest(Type requestType, Code requestCode, int intExtra) {
        sendRequest(Request.newBuilder()
                .setSecurityToken(getSecurityToken())
                .setType(requestType)
                .setCode(requestCode)
                .setIntExtra(intExtra)
                .build());
    }

    /**
     * Initializes the message handler then send the request.
     *
     * @param requestType The request type.
     * @param requestCode The request code.
     */
    private void sendRequest(Type requestType, Code requestCode) {
        sendRequest(Request.newBuilder()
                .setSecurityToken(getSecurityToken())
                .setType(requestType)
                .setCode(requestCode)
                .build());
    }

    /**
     * Initializes the message handler then send the request.
     *
     * @param request The request to send.
     */
    @Override
    public void sendRequest(Request request) {

        if (DashboardMessageMgr.availablePermits() > 0) {
            new DashboardMessageMgr().execute(request);
        } else {
            final boolean defineVolume = Type.VOLUME.equals(request.getType()) && Code.DEFINE.equals(request.getCode());
            if (!defineVolume) {
                final String message = "#sendRequest - " + getString(R.string.msg_no_more_permit) + "\n" + request.toString();
                Logger.warning(TAG, message);
            }
        }
    }

    @Override
    public NetworkDevice getDevice() { return requestSender.getDevice(); }

    @Override
    public String getSecurityToken() { return requestSender.getSecurityToken(); }

    /**
     * Class that handle asynchronous requests sent to a remote device.
     * Specialize for Dashboard.
     *
     * @author Cyril Leroux
     */
    private class DashboardMessageMgr extends AsyncMessageMgr {

        public DashboardMessageMgr() { super(getDevice(), callbacks); }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            callbacks.onPostExecute(response);

            final String message = response.getMessage();
            Logger.debug(TAG, "#onPostExecute - Sending response : " + message);

            final Type type = response.getRequestType();
            final Code code = response.getRequestCode();

            boolean usingVolumeSeekBar = Type.VOLUME.equals(type) && Code.DEFINE.equals(code);
            if (!usingVolumeSeekBar) {
                // TODO specify when to display a message => Wrap Request
                //sendToastToUI(message);
            } else {
                final int volume = response.getIntValue();

                Rect hitRect = new Rect();
                sbVolume.getDrawingRect(hitRect);
                final int x = sbVolume.getLeft() + (int) ((float) volume / (float) sbVolume.getMax() * sbVolume.getWidth());
                final int y = sbVolume.getTop();
                showVolumeToast(volume + "%", x, y);
            }

            // Handle UI mute icon
            if (Type.VOLUME.equals(type) && Code.MUTE.equals(code)) {
                if (response.getIntValue() == 0) { // Mute
                    ibMute.setImageResource(R.drawable.volume_muted);
                } else if (response.getIntValue() == 1) { // Volume On
                    ibMute.setImageResource(R.drawable.volume_on);
                }
            }
        }
    }

    /**
     * Launch a fade in animation.
     */
    private void fadeIn(View view) {
        initFadeIn(view);
        initFadeOut(view);

        if (fadeOut.isStarted()) {
            fadeOut.cancel();
        }

        if (fadeIn.isStarted()) {
            return;
        }

        if (view.getAlpha() != 1.0 || view.getVisibility() != View.VISIBLE) {
            fadeIn.start();
            return;
        }
        fadeOut(view);
    }

    /**
     * Launch a fade out animation.
     */
    private void fadeOut(View view) {
        if (fadeOut.isStarted() && !fadeOut.isRunning()) {
            fadeOut.setStartDelay(DELAY);
        } else {
            initFadeOut(view);
            fadeOut.start();
        }
    }

    private void initFadeIn(View view) {
        if (fadeIn != null) {
            return;
        }
        fadeIn = ObjectAnimator.ofFloat(view, "alpha", 1f).setDuration(DURATION);
        fadeIn.setStartDelay(0);

        fadeIn.addListener(new AnimatorListener() {

            private final View view = tvVolume;

            @Override
            public void onAnimationStart(Animator animation) {
                // TODO Replace view by the view in parameter
                if (view.getVisibility() != View.VISIBLE) {
                    view.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // TODO Replace view by the view in parameter
                final float alpha = view.getAlpha();
                if (alpha == 1.0) {
                    fadeOut(view);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });
    }

    private void initFadeOut(View view) {
        if (fadeOut != null) {
            return;
        }
        fadeOut = ObjectAnimator.ofFloat(view, "alpha", 0f).setDuration(DURATION);
        fadeOut.setStartDelay(DELAY);

        fadeOut.addListener(new AnimatorListener() {

            private final View view = tvVolume;

            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // TODO Replace view by the view in parameter
                final float alpha = view.getAlpha();

                if (alpha == 0.0) {
                    // TODO Replace view by the view in parameter
                    if (view.getVisibility() == View.VISIBLE) {
                        view.setVisibility(View.GONE);
                        fadeIn = null;
                        fadeOut = null;
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });
    }
}
