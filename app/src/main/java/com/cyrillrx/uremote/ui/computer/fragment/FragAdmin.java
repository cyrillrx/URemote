package com.cyrillrx.uremote.ui.computer.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.cyrillrx.android.component.Console;
import com.cyrillrx.uremote.R;
import com.cyrillrx.uremote.common.device.NetworkDevice;
import com.cyrillrx.uremote.network.WakeOnLan;
import com.cyrillrx.uremote.request.RequestSender;
import com.cyrillrx.uremote.request.protobuf.RemoteCommand.Request;

import static android.view.HapticFeedbackConstants.VIRTUAL_KEY;
import static com.cyrillrx.uremote.request.protobuf.RemoteCommand.Request.Code;
import static com.cyrillrx.uremote.request.protobuf.RemoteCommand.Request.Type;

/**
 * Class to connect and send commands to a remote device through AsyncTask.
 *
 * @author Cyril Leroux
 *         Created on 22/05/12.
 */
public class FragAdmin extends Fragment implements OnClickListener {

    private RequestSender requestSender;
    private Console console;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
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
        View view = inflater.inflate(R.layout.server_frag_admin, container, false);

        view.findViewById(R.id.cmdWakeOnLan).setOnClickListener(this);
        view.findViewById(R.id.cmdShutdown).setOnClickListener(this);
        view.findViewById(R.id.cmdAiMute).setOnClickListener(this);
        view.findViewById(R.id.cmdKillServer).setOnClickListener(this);
        view.findViewById(R.id.cmdLock).setOnClickListener(this);

        console = view.findViewById(R.id.console);

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
                confirmRequest(buildRequest(Type.SIMPLE, Code.SHUTDOWN));
                break;

            case R.id.cmdAiMute:
                requestSender.sendRequest(buildRequest(Type.AI, Code.MUTE));
                // TODO factorize log code
                console.addLine("Sending request - Type:" + Type.AI.name() + " Code: " + Code.MUTE.name());
                break;

            case R.id.cmdKillServer:
                confirmRequest(buildRequest(Type.SIMPLE, Code.KILL_SERVER));
                break;

            case R.id.cmdLock:
                confirmRequest(Type.SIMPLE, Code.LOCK);
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

        final NetworkDevice device = requestSender.getDevice();
        String host = defaultHost;
        String macAddress = defaultMacAddress;
        if (device != null) {
            host = wifi ? device.getBroadcast() : device.getRemoteHost();
            macAddress = device.getMacAddress();
        }
        new WakeOnLan().execute(host, macAddress);

        // TODO factorize log code
        final String message = "ip : " + host + ", mac : " + macAddress;
        console.addLine("Sending WOL - " + message);

        // TODO : if device, run. else error toast
    }

    //
    // Message Sender
    //

    private Request buildRequest(final Request.Type requestType, final Request.Code requestCode) {
        return Request.newBuilder()
                .setSecurityToken(requestSender.getSecurityToken())
                .setType(requestType)
                .setCode(requestCode)
                .build();
    }

    private void confirmRequest(final Request.Type requestType, final Request.Code requestCode) {
        confirmRequest(Request.newBuilder()
                .setSecurityToken(requestSender.getSecurityToken())
                .setType(requestType)
                .setCode(requestCode)
                .build());
    }

    /**
     * Ask for the user to confirm before sending a request to the device.
     *
     * @param request The request to send.
     */
    private void confirmRequest(final Request request) {
        // TODO add int param for message resource.
        int resId = (Code.KILL_SERVER.equals(request.getCode())) ? R.string.confirm_kill_server : R.string.confirm_command;

        new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_menu_more)
                .setMessage(resId)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the request if the user confirms it
                        requestSender.sendRequest(request);
                        // TODO factorize log code
                        console.addLine("Sending request - Type:" + request.getType().name() + " Code: " + request.getCode().name());
                    }
                })

                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }
}
