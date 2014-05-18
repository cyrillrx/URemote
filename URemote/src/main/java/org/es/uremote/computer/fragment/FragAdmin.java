package org.es.uremote.computer.fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import org.es.uremote.R;
import org.es.uremote.device.ServerSetting;
import org.es.uremote.exchange.Message;
import org.es.uremote.exchange.Message.Request;
import org.es.uremote.exchange.MessageUtils;
import org.es.uremote.exchange.RequestSender;
import org.es.uremote.network.WakeOnLan;
import org.es.uremote.utils.TaskCallbacks;
import org.es.uremote.utils.ToastSender;

import static android.view.HapticFeedbackConstants.VIRTUAL_KEY;
import static org.es.uremote.exchange.Message.Request.Code.KILL_SERVER;
import static org.es.uremote.exchange.Message.Request.Code.LOCK;
import static org.es.uremote.exchange.Message.Request.Code.MUTE;
import static org.es.uremote.exchange.Message.Request.Code.SHUTDOWN;
import static org.es.uremote.exchange.Message.Request.Type.AI;
import static org.es.uremote.exchange.Message.Request.Type.SIMPLE;

/**
 * Class to connect and send commands to a remote server through AsyncTask.
 *
 * @author Cyril Leroux
 *         Created on 22/05/12.
 */
public class FragAdmin extends Fragment implements OnClickListener {

    private TaskCallbacks mCallbacks;
    private RequestSender mRequestSender;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (TaskCallbacks) activity;
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
        mCallbacks = null;
        mRequestSender = null;
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
        View view = inflater.inflate(R.layout.server_frag_admin, container, false);

        view.findViewById(R.id.cmdWakeOnLan).setOnClickListener(this);
        view.findViewById(R.id.cmdShutdown).setOnClickListener(this);
        view.findViewById(R.id.cmdAiMute).setOnClickListener(this);
        view.findViewById(R.id.cmdKillServer).setOnClickListener(this);
        view.findViewById(R.id.cmdLock).setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        view.performHapticFeedback(VIRTUAL_KEY);

        switch (view.getId()) {

            case R.id.cmdWakeOnLan:
                wakeOnLan();
                break;

            case R.id.cmdShutdown:
                confirmRequest(buildRequest(SIMPLE, SHUTDOWN));
                break;

            case R.id.cmdAiMute:
                mRequestSender.sendRequest(buildRequest(AI, MUTE));
                break;

            case R.id.cmdKillServer:
                confirmRequest(buildRequest(SIMPLE, KILL_SERVER));
                break;

            case R.id.cmdLock:
                confirmRequest(SIMPLE, LOCK);
                break;

            default:
                break;
        }
    }

    private void wakeOnLan() {

        final WifiManager wifiMgr = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        final boolean wifi = wifiMgr.isWifiEnabled();

        final int defaultHostResId = wifi ? R.string.default_broadcast : R.string.default_remote_host;
        final String defaultHost = getString(defaultHostResId);
        final String defaultMacAddress = getString(R.string.default_mac_address);

        final ServerSetting settings = mRequestSender.getDevice();
        String host = defaultHost;
        String macAddress = defaultMacAddress;
        if (settings != null) {
            host = wifi ? settings.getBroadcast() : settings.getRemoteHost();
            macAddress = settings.getMacAddress();
        }
        new WakeOnLan((ToastSender) mCallbacks).execute(host, macAddress);

        // TODO : if server, run. else error toast
    }

    ////////////////////////////////////////////////////////////////////
    // *********************** Message Sender *********************** //
    ////////////////////////////////////////////////////////////////////

    private Request buildRequest(final Request.Type requestType, final Request.Code requestCode) {
         return Request.newBuilder()
                .setSecurityToken(mRequestSender.getSecurityToken())
                .setType(requestType)
                .setCode(requestCode)
                .build();
    }

    private void confirmRequest(final Request.Type requestType, final Request.Code requestCode) {
        confirmRequest(Request.newBuilder()
                .setSecurityToken(mRequestSender.getSecurityToken())
                .setType(requestType)
                .setCode(requestCode)
                .build());
    }

    /**
     * Ask for the user to confirm before sending a request to the server.
     *
     * @param request The request to send.
     */
    private  void confirmRequest(final Request request) {
        int resId = (KILL_SERVER.equals(request.getCode())) ? R.string.confirm_kill_server : R.string.confirm_command;

        new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_menu_more)
                .setMessage(resId)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the request if the user confirms it
                        mRequestSender.sendRequest(request);
                    }
                })

                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }
}
