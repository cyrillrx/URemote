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
	private String mLocalHost;
	private int mLocalPort;
	private String mRemoteHost;
	private int mRemotePort;

	/** If the connection with the remote server is not established within this timeout, it is dismiss. */
	private final int mConnectionTimeout;
	private final int mReadTimeout;
	private final int mConnectionType;

	/**
	 * Default constructor
	 */
	public ServerInfo() {
		mLabel		= "";
		mServerName	= "";
		mLocalHost	= "";
		mLocalPort	= 0000;
		mRemoteHost	= "";
		mRemotePort	= 0000;
		mConnectionTimeout	= 500;
		mReadTimeout		= 500;
		mConnectionType		= CONNECTION_TYPE_LOCAL;
	}
	/**
	 * Constructor with parameters
	 * @param localHost
	 * @param localPort
	 * @param remoteHost
	 * @param remotePort
	 * @param connectionTimeout
	 * @param readTimeout
	 */
	public ServerInfo(final String localHost, final int localPort, final int connectionTimeout, final int readTimeout) {

		mLabel		= "";
		mServerName	= "";
		mLocalHost	= localHost;
		mLocalPort	= localPort;
		mConnectionTimeout	= connectionTimeout;
		mReadTimeout		= readTimeout;
		mConnectionType		= CONNECTION_TYPE_LOCAL;
	}

	/**
	 * Constructor with parameters
	 * @param localHost
	 * @param localPort
	 * @param remoteHost
	 * @param remotePort
	 * @param connectionTimeout
	 * @param readTimeout
	 */
	public ServerInfo(final String localHost, final int localPort, final String remoteHost, final int remotePort, final int connectionTimeout, final int readTimeout) {

		mLabel		= "";
		mServerName	= "";
		mLocalHost	= localHost;
		mLocalPort	= localPort;
		mRemoteHost	= remoteHost;
		mRemotePort	= remotePort;
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
	 * @return Local ip address of the server
	 */
	public String getLocalHost() {
		return mLocalHost;
	}

	/**
	 * Set the local ipAddress.
	 * @param ipAddress
	 */
	public void setLocalHost(final String ipAddress) {
		mLocalHost = ipAddress;
	}

	/**
	 * @return Open port to connect to the local server.
	 */
	public int getLocalPort() {
		return mLocalPort;
	}

	/**
	 * Set the local port.
	 * @param port
	 */
	public void setLocalPort(final int port) {
		mLocalPort = port;
	}

	/**
	 * @return Remote ip address of the server.
	 */
	public String getRemoteHost() {
		return mRemoteHost;
	}

	/**
	 * Set the remote ipAddress.
	 * @param ipAddress
	 */
	public void setRemoteHost(final String ipAddress) {
		mRemoteHost = ipAddress;
	}

	/**
	 * @return Open port to connect to the remote server.
	 */
	public int getRemotePort() {
		return mRemotePort;
	}

	/**
	 * Set the remote port.
	 * @param port
	 */
	public void setRemotePort(final int port) {
		mRemotePort = port;
	}

	/**
	 * @return Timeout connection in milliseconds.
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