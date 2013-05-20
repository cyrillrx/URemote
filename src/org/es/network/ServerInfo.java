package org.es.network;

import org.es.uremote.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;

/**
 * Class that holds server connection informations.
 * 
 * @author Cyril Leroux
 *
 */
public class ServerInfo {

	/**
	 * 
	 */
	public static final int CONNECTION_TYPE_LOCAL	= 0;
	/**
	 * 
	 */
	public static final int CONNECTION_TYPE_REMOTE	= 0;

	private final String mLabel;
	private final String mServerName;
	private final String mHost;
	private final int mPort;
	private final int mConnectionTimeout;
	private final int mReadTimeout;
	private final int mConnectionType;

	/**
	 * Default constructor
	 * @param host
	 * @param port
	 * @param connectionTimeout
	 * @param readTimeout
	 */
	public ServerInfo(final String host, final int port, final int connectionTimeout, final int readTimeout) {

		mLabel		= "";
		mServerName	= "";
		mHost		= host;
		mPort		= port;
		mConnectionTimeout	= connectionTimeout;
		mReadTimeout		= readTimeout;
		mConnectionType		= CONNECTION_TYPE_LOCAL;

	}

	/**
	 * @param context The application context.
	 * @return The Server connection informations stored in User Preferences
	 */
	public static ServerInfo loadFromPreferences(Context context) {

		final WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		final boolean wifi = wifiMgr.isWifiEnabled();

		// Get Host and Port key
		final int resKeyHost = wifi ? R.string.key_local_host : R.string.key_remote_host;
		final int resKeyPort = wifi ? R.string.key_local_port : R.string.key_remote_port;
		final String keyHost	= context.getString(resKeyHost);
		final String keyPort	= context.getString(resKeyPort);

		// Get key for other properties
		final String keyConnectionTimeout	= context.getString(R.string.key_connection_timeout);
		final String keyReadTimeout			= context.getString(R.string.key_read_timeout);

		// Get default values for Host and Port
		final int resDefHost = wifi ? R.string.default_local_host : R.string.default_remote_host;
		final int resDefPort = wifi ? R.string.default_local_port : R.string.default_remote_port;
		final String defaultHost	= context.getString(resDefHost);
		final String defaultPort	= context.getString(resDefPort);

		// Get default values for other properties
		final String defaultConnectionTimeout	= context.getString(R.string.default_connection_timeout);
		final String defaultReadTimeout			= context.getString(R.string.default_read_timeout);

		// Get the properties values
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
		final String host	= pref.getString(keyHost, defaultHost);
		final int port		= Integer.parseInt(pref.getString(keyPort, defaultPort));
		final int connectionTimeout	= Integer.parseInt(pref.getString(keyConnectionTimeout, defaultConnectionTimeout));
		final int readTimeout		= Integer.parseInt(pref.getString(keyReadTimeout, defaultReadTimeout));

		return new ServerInfo(host, port, connectionTimeout, readTimeout);
	}

	/**
	 * @return Ip address of the server
	 */
	public String getHost() {
		return mHost;
	}

	/**
	 * @return Open port to connect to.
	 */
	public int getPort() {
		return mPort;
	}

	/**
	 * @return Timeout connection in milliseconds.
	 * If the connection with the remote server is not established within this timeout, it is dismiss.
	 */
	public int getConnectionTimeout() {
		return mConnectionTimeout;
	}

	/**
	 * @return Read timeout in milliseconds.
	 */
	public int getReadTimeout() {
		return mReadTimeout;
	}
}