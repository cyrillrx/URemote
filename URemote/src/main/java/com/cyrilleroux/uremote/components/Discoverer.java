package com.cyrilleroux.uremote.components;

import android.os.AsyncTask;
import android.util.Log;

import com.cyrilleroux.uremote.device.NetworkDevice;
import com.cyrilleroux.uremote.network.AsyncMessageMgr;
import com.cyrilleroux.uremote.request.protobuf.RemoteCommand;
import com.cyrilleroux.uremote.utils.TaskCallbacks;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Cyril Leroux
 *         Created 08/03/2015.
 */
public class Discoverer {


    public static void discoverDevices() {

        discover("192.168.1.", 9002);
    }

    /**
     * Scans network for devices.
     *
     * @return The list of fond devices.
     */
    private static List<String> discover(String subnet, int port) {

        List<String> foundDevices = new ArrayList<>();

        for (int i = 1; i < 256; i++) {
            String host = subnet + i;
            if (sendPing(host, port)) {
                foundDevices.add(host);
            }
        }

        return foundDevices;
    }

    private static boolean sendPing(final String host, int port) {

        Log.w("HomeActivity", "send ping to " + host);

        NetworkDevice server;
        try {
            server = NetworkDevice.newBuilder()
                    .setName("unknown")
                    .setLocalHost(host)
                    .setLocalPort(port)
                    .setBroadcast(host)
                    .setRemoteHost(host)
                    .setRemotePort(port)
                    .setMacAddress("--")
                    .setConnectionTimeout(500)
                    .setReadTimeout(500)
                    .setSecurityToken("1234")
                    .setConnectionType(NetworkDevice.ConnectionType.LOCAL)
                    .build();
        } catch (Exception e) {
            Log.e("HomeActivity", e.getMessage(), e);
            return false;
        }

        final RemoteCommand.Request request = RemoteCommand.Request.newBuilder()
                .setSecurityToken("1234")
                .setType(RemoteCommand.Request.Type.SIMPLE)
                .setCode(RemoteCommand.Request.Code.PING)
                .build();

        new AsyncMessageMgr(server, new TaskCallbacks() {
            @Override
            public void onPreExecute() {
                Log.w("HomeActivity", "onPreExecute " + host);
            }

            @Override
            public void onProgressUpdate(int percent) {

            }

            @Override
            public void onPostExecute(RemoteCommand.Response response) {
                if (RemoteCommand.Response.ReturnCode.RC_ERROR.equals(response.getReturnCode())) {
                    // todo
//                    Toast.makeText(getApplicationContext(), host + " OK ", LENGTH_SHORT).show();
                    Log.w("HomeActivity", "host " + host + " request OK");
                } else {
                    Log.e("HomeActivity", "host " + host + " request KO");
                }
            }

            @Override
            public void onCancelled(RemoteCommand.Response response) {

            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request);
        return true;
    }
}
