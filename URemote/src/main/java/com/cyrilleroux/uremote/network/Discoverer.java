package com.cyrilleroux.uremote.network;

import com.cyrilleroux.android.toolbox.Logger;
import com.cyrilleroux.uremote.common.device.NetworkDevice;
import com.cyrilleroux.uremote.request.MessageUtils;
import com.cyrilleroux.uremote.request.protobuf.RemoteCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Cyril Leroux
 *         Created on 08/03/2015.
 */
public class Discoverer {

    private static final String TAG = Discoverer.class.getSimpleName();

    public static class DiscoverTask implements Callable<NetworkDevice> {

        final RemoteCommand.Request mRequest;
        final NetworkDevice mDevice;

        public DiscoverTask(RemoteCommand.Request request, final NetworkDevice device) {
            mRequest = request;
            mDevice = device;
        }

        @Override
        public NetworkDevice call() throws Exception {

            final RemoteCommand.Response response = MessageUtils.sendRequest(mRequest, mDevice);
            if (RemoteCommand.Response.ReturnCode.RC_ERROR.equals(response.getReturnCode())) {
                Logger.error(TAG, "Not responding. " + mDevice);
                return null;
            }

            Logger.info(TAG, "Device found : " + mDevice);
            return mDevice;
        }
    }

    public static void discoverDevices() {
        // TODO replace hardcoded subnet and port
        discover("192.168.1.", 9002);
    }

    /**
     * Scans network for devices.
     *
     * @return The list of fond devices.
     */
    private static List<NetworkDevice> discover(String subnet, int port) {

        Logger.info(TAG, "Send ping to subnet: " + subnet);

        final NetworkDevice device;
        try {
            device = NetworkDevice.newBuilder()
                    .setName("unknown")
                    .setLocalHost(subnet)
                    .setLocalPort(port)
                    .setSecurityToken("1234")
                    .setConnectionType(NetworkDevice.ConnectionType.LOCAL)
                    .build();
        } catch (Exception e) {
            Logger.error(TAG, "Unable to start the search.", e);
            return new ArrayList<>();
        }

        final RemoteCommand.Request request = RemoteCommand.Request.newBuilder()
                .setSecurityToken("1234")
                .setType(RemoteCommand.Request.Type.SIMPLE)
                .setCode(RemoteCommand.Request.Code.PING)
                .build();

        final List<Future<NetworkDevice>> futures = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(4);

        // TODO clone the device for each task.
        // TODO define a range of possible ip addresses
        for (int i = 1; i < 256; i++) {
            String host = subnet + i;
            device.setLocalHost(host);
            DiscoverTask task = new DiscoverTask(request, device);
            futures.add(executor.submit(task));
        }
        // This will make the executor accept no new threads
        // and finish all existing threads in the queue
        executor.shutdown();

        final List<NetworkDevice> foundDevices = new ArrayList<>();
        // now retrieve the result
        for (Future<NetworkDevice> future : futures) {
            try {
                final NetworkDevice foundDevice = future.get();
                Logger.info(TAG, "Device found !" + foundDevice);
                foundDevices.add(foundDevice);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        return foundDevices;
    }
}
