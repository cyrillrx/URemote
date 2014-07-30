package org.es.utils;

import android.content.Context;
import android.net.ConnectivityManager;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * This class manage connections to WIFI, mobile data, Bluetooth, etc.
 *
 * @author Cyril Leroux
 *         Created before first commit (08/04/12).
 */
public class ConnectionUtils {

    private static final String TAG = ConnectionUtils.class.getSimpleName();

    /**
     * @param context Activity or Application context.
     * @return The ConnectivityManager object bound to the context.
     */
    public static ConnectivityManager getConnectivityManager(Context context) {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    /**
     * @param connectivityMgr Object that contains connections data.
     * @return true if the device is connected to the Internet through WIFI. false otherwise.
     */
    public static boolean isConnectedThroughWifi(ConnectivityManager connectivityMgr) {
        boolean isConnectedToWifi = connectivityMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
        return isConnectedToWifi;
    }

    /**
     * Tests the Internet connection.
     *
     * @param connectivityMgr Object that contains connections data.
     * @return true if the device is connected to the Internet (through WIFI or mobile data). false otherwise.
     */
    public static boolean isConnectedToInternet(ConnectivityManager connectivityMgr) {

        boolean isMobileConnected = connectivityMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
        boolean isWifiConnected = isConnectedThroughWifi(connectivityMgr);

        if (isMobileConnected || isWifiConnected) {
            return true;
        }

        Log.error(TAG, "No access to the world wide web");
        return false;
    }

    /**
     * @param connectivityMgr Object that contains connections data.
     * @param ipAddress       The IP address of the host we want to connect to.
     * @return true if the device can access the specified host. false otherwise.
     */
    public static boolean canAccessHost(ConnectivityManager connectivityMgr, String ipAddress) {
        return connectivityMgr.requestRouteToHost(ConnectivityManager.TYPE_WIFI, lookupHost(ipAddress));
    }

    /**
     * Convert the IP address into an integer.
     *
     * @param ipAddress The IP address to convert.
     * @return The IP address converted as an integer.
     */
    private static int lookupHost(String ipAddress) {
        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getByName(ipAddress);
        } catch (UnknownHostException e) {
            return -1;
        }

        byte[] addressBytes;
        int address;
        addressBytes = inetAddress.getAddress();
        address = ((addressBytes[3] & 0xff) << 24)
                | ((addressBytes[2] & 0xff) << 16)
                | ((addressBytes[1] & 0xff) << 8)
                | (addressBytes[0] & 0xff);
        return address;
    }
}