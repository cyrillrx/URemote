package org.es.utils;

import static org.es.uremote.utils.Constants.DEBUG;

import java.net.InetAddress;
import java.net.UnknownHostException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

/**
 * Classe gérant les connexions wifi, 3G, bluetooth, etc.
 * @author Cyril Leroux
 */
public class ConnectionUtils {
	private static final String TAG = "WebUtils";

	/**
	 * @param _context Le context de l'activité ou de l'application.
	 * @return L'objet ConnectivityManager lié au contexte
	 */
	public static ConnectivityManager getConnectivityManager(Context _context) {
		return (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
	}
	
	/**
	 * @param _connectivityMgr Ojet contenant les informations sur les connexions.
	 * @return true si l'on a accès à internet par Wifi
	 */
	public static boolean isConnectedThroughWifi(ConnectivityManager _connectivityMgr) {
		boolean isConnectedToWifi = _connectivityMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
		return isConnectedToWifi;
	}
	
	/**
	 * Teste l'accès à internet.
	 * @param _connectivityMgr Ojet contenant les informations sur les connexions.
	 * @return true si l'on a accès à internet par le wifi ou le 3G.
	 */
	public static boolean isConnectedToInternet(ConnectivityManager _connectivityMgr) {
		
		boolean isMobileConnected	= _connectivityMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected(); // Vérifie l'accès à l'internet mobile
		boolean isWifiConnected		= isConnectedThroughWifi(_connectivityMgr);
		
		if (isMobileConnected || isWifiConnected)
			return true;
		
		if (DEBUG)
			Log.e(TAG, "No acces to the world wide web");
		return false;
	}
	
	/**
	 * @param _connectivityMgr Ojet contenant les informations sur les connexions.
	 * @param _ipAddress L'adresse ip de l'hôte auquel on souhaite se connecter
	 * @return true si l'on a accès à l'hôte dont l'adresse est spécifiée en paramètre. 
	 */
	public static boolean canAccessHost(ConnectivityManager _connectivityMgr, String _ipAddress) {
		return _connectivityMgr.requestRouteToHost(ConnectivityManager.TYPE_WIFI, lookupHost(_ipAddress));
	}
	
	/**
	 * Convertit la chaine de caractère de l'adresse ip et un entier .
	 * @param _ipAddress L'adresse de l'hôte sous forme de chaine de caractère.
	 * @return L'adresse de l'hôte sous forme d'entier.
	 */
	private static int lookupHost(String _ipAddress) {
	    InetAddress inetAddress;
	    try {
	        inetAddress = InetAddress.getByName(_ipAddress);
	    } catch (UnknownHostException e) {
	        return -1;
	    }
	    byte[] addrBytes;
	    int addr;
	    addrBytes = inetAddress.getAddress();
	    addr = ((addrBytes[3] & 0xff) << 24)
	            | ((addrBytes[2] & 0xff) << 16)
	            | ((addrBytes[1] & 0xff) << 8)
	            |  (addrBytes[0] & 0xff);
	    return addr;
	}
}
