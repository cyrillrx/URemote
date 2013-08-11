package org.es.utils;

import android.content.Context;
import android.net.ConnectivityManager;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * This class manage connections to WIFI, mobile data, Bluetooth, etc.
 *
 * @author Cyril Leroux
 */
public class ConnectionUtils {
	private static final String TAG = "WebUtils";

	/**
	 * @param _context Activity or Application context.
	 * @return The ConnectivityManager object binded to the context.
	 */
	public static ConnectivityManager getConnectivityManager(Context _context) {
		return (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	/**
	 * @param _connectivityMgr Object that contains informations on connections.
	 * @return true if the device is connected to the Internet through WIFI. false otherwise.
	 */
	public static boolean isConnectedThroughWifi(ConnectivityManager _connectivityMgr) {
		boolean isConnectedToWifi = _connectivityMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
		return isConnectedToWifi;
	}

	/**
	 * Tests the Internet connection.
	 * @param _connectivityMgr Object that contains informations on connections.
	 * @return true if the device is connected to the Internet (through WIFI or mobile data). false otherwise.
	 */
	public static boolean isConnectedToInternet(ConnectivityManager _connectivityMgr) {

		boolean isMobileConnected	= _connectivityMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
		boolean isWifiConnected		= isConnectedThroughWifi(_connectivityMgr);

		if (isMobileConnected || isWifiConnected) {
			return true;
		}

		Log.error(TAG, "No acces to the world wide web");
		return false;
	}

	/**
	 * @param _connectivityMgr Object that contains informations on connections.
	 * @param _ipAddress The IP address of the host we want to connect to.
	 * @return true if the device can access the specified host. false otherwise.
	 */
	public static boolean canAccessHost(ConnectivityManager _connectivityMgr, String _ipAddress) {
		return _connectivityMgr.requestRouteToHost(ConnectivityManager.TYPE_WIFI, lookupHost(_ipAddress));
	}

	/**
	 * Convert the IP address into an integer.
	 * @param _ipAddress The IP address to convert.
	 * @return The IP address converted as an integer.
	 */
	private static int lookupHost(String _ipAddress) {
		InetAddress inetAddress;
		try {
			inetAddress = InetAddress.getByName(_ipAddress);
		} catch (UnknownHostException e) {
			return -1;
		}

		byte[] addressBytes;
		int address;
		addressBytes = inetAddress.getAddress();
		address = ((addressBytes[3] & 0xff) << 24)
		| ((addressBytes[2] & 0xff) << 16)
		| ((addressBytes[1] & 0xff) << 8)
		|  (addressBytes[0] & 0xff);
		return address;
	}
}
