package org.es.uremote.objects;

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
	 * The server is in the same network.
	 * We can use a local IP address.
	 */
	public static final int CONNECTION_TYPE_LOCAL	= 0;
	/**
	 * The server is not in the same network.
	 * We can only reach it by a remote IP address.
	 */
	public static final int CONNECTION_TYPE_REMOTE	= 1;

	private final String mLabel;
	private final String mServerName;
	private String mLocalHost;
	private int mLocalPort;
	private String mRemoteHost;
	private int mRemotePort;
	private int mConnectionType;

	/** If the connection with the remote server is not established within this timeout, it is dismiss. */
	private final int mConnectionTimeout;
	private final int mReadTimeout;

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

		// Get Host and Port key
		final String keyLocalHost	= context.getString(R.string.key_local_host);
		final String keyLocalPort	= context.getString(R.string.key_local_port);
		final String keyRemoteHost	= context.getString(R.string.key_remote_host);
		final String keyRemotePort	= context.getString(R.string.key_remote_port);

		// Get key for other properties
		final String keyConnectionTimeout	= context.getString(R.string.key_connection_timeout);
		final String keyReadTimeout			= context.getString(R.string.key_read_timeout);

		// Get default values for Host and Port
		final String defaultLocalHost	= context.getString(R.string.default_local_host);
		final String defaultLocalPort	= context.getString(R.string.default_local_port);
		final String defaultRemoteHost	= context.getString(R.string.default_remote_host);
		final String defaultRemotePort	= context.getString(R.string.default_remote_port);

		// Get default values for other properties
		final String defaultConnectionTimeout	= context.getString(R.string.default_connection_timeout);
		final String defaultReadTimeout			= context.getString(R.string.default_read_timeout);

		// Get the properties values
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
		final String localHost	= pref.getString(keyLocalHost, defaultLocalHost);
		final int localPort		= Integer.parseInt(pref.getString(keyLocalPort, defaultLocalPort));
		final String remoteHost	= pref.getString(keyRemoteHost, defaultRemoteHost);
		final int remotePort	= Integer.parseInt(pref.getString(keyRemotePort, defaultRemotePort));
		final int connectionTimeout	= Integer.parseInt(pref.getString(keyConnectionTimeout, defaultConnectionTimeout));
		final int readTimeout		= Integer.parseInt(pref.getString(keyReadTimeout, defaultReadTimeout));

		return new ServerInfo(localHost, localPort, remoteHost, remotePort, connectionTimeout, readTimeout);
	}

	public boolean isLocal(Context context) {
		// TODO define when local and remote
		final WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		return wifiMgr.isWifiEnabled();
	}

	/**
	 * Save the object attributes into a XML file.
	 * @return true if save succeeded false otherwise.
	 */
	public boolean saveToXmlFile() {
		// TODO implement
		return false;
	}

	public void setLocal() {
		mConnectionType = CONNECTION_TYPE_LOCAL;
	}

	public void setRemote() {
		mConnectionType = CONNECTION_TYPE_REMOTE;
	}

	/**
	 * @param context
	 * @return Concatenation of host and port of the remote server.
	 */
	public String getFullAddress(Context context) {
		if (isLocal(context)) {
			return mLocalHost + ":" + mLocalPort;
		}

		return mRemoteHost + ":" + mRemotePort;
	}

	/**
	 * @return Local or remote ip address of the server.
	 */
	public String getHost() {
		if (mConnectionType == CONNECTION_TYPE_REMOTE) {
			return mRemoteHost;
		}
		return mLocalHost;
	}

	/**
	 * @return Local or remote port on which we want to establish a connection with the server.
	 */
	public int getPort() {
		if (mConnectionType == CONNECTION_TYPE_REMOTE) {
			return mRemotePort;
		}
		return mLocalPort;
	}

	/**
	 * Set the local ipAddress.
	 * @param ipAddress
	 */
	public void setLocalHost(final String ipAddress) {
		mLocalHost = ipAddress;
	}

	/**
	 * Set the local port.
	 * @param port
	 */
	public void setLocalPort(final int port) {
		mLocalPort = port;
	}

	/**
	 * Set the remote ipAddress.
	 * @param ipAddress
	 */
	public void setRemoteHost(final String ipAddress) {
		mRemoteHost = ipAddress;
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