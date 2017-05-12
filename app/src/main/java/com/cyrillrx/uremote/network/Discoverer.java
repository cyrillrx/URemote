package com.cyrillrx.uremote.network;

import com.cyrillrx.logger.Logger;
import com.cyrillrx.uremote.common.device.NetworkDevice;
import com.cyrillrx.uremote.request.MessageUtils;
import com.cyrillrx.uremote.request.protobuf.RemoteCommand;

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

        final RemoteCommand.Request request;
        final NetworkDevice device;

        public DiscoverTask(RemoteCommand.Request request, final NetworkDevice device) {
            this.request = request;
            this.device = device;
        }

        @Override
        public NetworkDevice call() throws Exception {

            final RemoteCommand.Response response = MessageUtils.sendRequest(request, device);
            if (RemoteCommand.Response.ReturnCode.RC_ERROR.equals(response.getReturnCode())) {
                Logger.error(TAG, "Not responding. " + device);
                return null;
            }

            Logger.info(TAG, "Device found : " + device);
            return device;
        }
    }

    public static void discoverDevices() {

        ExecutorService executor = null;
        try {
            executor = Executors.newSingleThreadExecutor();
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    NetUtils.getLocalIpAddress();
                    // TODO replace hardcoded subnet and port
                    discover("192.168.1.", 9002);
                }
            });
        } finally {
            if (executor != null) {
                executor.shutdown();
            }
        }
    }

    /**
     * Scans network for devices.
     *
     * @return The list of fond devices.
     */
    public static List<NetworkDevice> discover(String subnet, int port) {

        Logger.info(TAG, "Send ping to subnet: " + subnet);

        // TODO remove hard coded security token
        final RemoteCommand.Request request = RemoteCommand.Request.newBuilder()
                .setSecurityToken("1234")
                .setType(RemoteCommand.Request.Type.SIMPLE)
                .setCode(RemoteCommand.Request.Code.PING)
                .build();

        final List<Future<NetworkDevice>> futures = new ArrayList<>();

        ExecutorService executor = null;
        try {
            executor = Executors.newFixedThreadPool(4);

            // TODO clone the device for each task.
            // TODO define a range of possible ip addresses
            for (int i = 1; i < 256; i++) {
                String host = subnet + i;
                DiscoverTask task = new DiscoverTask(request, buildDevice(host, port));
                futures.add(executor.submit(task));
            }

        } finally {
            // This will make the executor accept no new threads
            // and finish all existing threads in the queue
            assert executor != null;
            executor.shutdown();
        }

        final List<NetworkDevice> foundDevices = new ArrayList<>();
        // now retrieve the result
        for (Future<NetworkDevice> future : futures) {
            try {
                final NetworkDevice foundDevice = future.get();
                Logger.info(TAG, "Device found ! " + foundDevice);
                foundDevices.add(foundDevice);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        return foundDevices;
    }

    public static NetworkDevice buildDevice(String host, int port) {

        // TODO remove hard coded security token
        try {
            return NetworkDevice.newBuilder()
                    .setName("unknown")
                    .setLocalHost(host)
                    .setLocalPort(port)
                    .setSecurityToken("1234")
                    .setConnectionType(NetworkDevice.ConnectionType.LOCAL)
                    .build();

        } catch (Exception e) {
            Logger.error(TAG, "Unable to build the device.", e);
            return null;
        }
    }
}
