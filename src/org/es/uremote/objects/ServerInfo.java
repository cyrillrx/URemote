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

	private String mName;
	private String mLocalHost;
	private int mLocalPort;
	private String mRemoteHost;
	private int mRemotePort;
	private String mMacAddress;
	private int mConnectionType;

	/**
	 * If the connection with the remote server is not established within this timeout, it is dismiss.
	 */
	private int mConnectionTimeout;
	private int mReadTimeout;

	/**
	 * Constructor with parameters
	 * @param localHost
	 * @param localPort
	 * @param remoteHost
	 * @param remotePort
	 * @param connectionTimeout
	 * @param readTimeout
	 */
	public ServerInfo(final String localHost, final int localPort, 
			final String remoteHost, final int remotePort, 
			final String macAddress,
			final int connectionTimeout, final int readTimeout) {

		mName		= "";
		mLocalHost	= localHost;
		mLocalPort	= localPort;
		mRemoteHost	= remoteHost;
		mRemotePort	= remotePort;
		mMacAddress	= macAddress;
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
		final String keyMacAddress	= context.getString(R.string.key_mac_address);

		// Get key for other properties
		final String keyConnectionTimeout	= context.getString(R.string.key_connection_timeout);
		final String keyReadTimeout			= context.getString(R.string.key_read_timeout);

		// Get default values for Host and Port
		final String defaultLocalHost	= context.getString(R.string.default_local_host);
		final String defaultLocalPort	= context.getString(R.string.default_local_port);
		final String defaultRemoteHost	= context.getString(R.string.default_remote_host);
		final String defaultRemotePort	= context.getString(R.string.default_remote_port);
		final String defaultMacAddress	= context.getString(R.string.default_mac_address);

		// Get default values for other properties
		final String defaultConnectionTimeout	= context.getString(R.string.default_connection_timeout);
		final String defaultReadTimeout			= context.getString(R.string.default_read_timeout);

		// Get the properties values
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
		final String localHost		= pref.getString(keyLocalHost, defaultLocalHost);
		final int localPort			= Integer.parseInt(pref.getString(keyLocalPort, defaultLocalPort));
		final String remoteHost		= pref.getString(keyRemoteHost, defaultRemoteHost);
		final int remotePort		= Integer.parseInt(pref.getString(keyRemotePort, defaultRemotePort));
		final String macAddress		= pref.getString(keyMacAddress, defaultMacAddress);
		final int connectionTimeout	= Integer.parseInt(pref.getString(keyConnectionTimeout, defaultConnectionTimeout));
		final int readTimeout		= Integer.parseInt(pref.getString(keyReadTimeout, defaultReadTimeout));

		return new ServerInfo(localHost, localPort, remoteHost, remotePort, macAddress, connectionTimeout, readTimeout);
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
	 * @return Concatenation of host and port of the local server.
	 */
	public String getFullLocal() {
		return mLocalHost + ":" + mLocalPort;

	}
	/**
	 * @return Concatenation of host and port of the remote server.
	 */
	public String getFullRemote() {
		return mRemoteHost + ":" + mRemotePort;
	}

	public String getName() {
		return mName;
	}

	public void setName(final String name) {
		mName = name;
	}

	/**
	 * @return The local ip address.
	 */
	public String getLocalHost() {
		return mLocalHost;
	}

	/**
	 * Set the local ip address.
	 * @param ipAddress The ip address of the local server.
	 */
	public void setLocalHost(final String ipAddress) {
		mLocalHost = ipAddress;
	}

	public int getLocalPort() {
		return mLocalPort;
	}
	
	/**
	 * Set the local port.
	 * @param port The port of the local server.
	 */
	public void setLocalPort(final int port) {
		mLocalPort = port;
	}

	public String getRemoteHost() {
		return mRemoteHost;
	}
	
	/**
	 * Set the remote ip address.
	 * @param ipAddress The ip address of the remote server.
	 */
	public void setRemoteHost(final String ipAddress) {
		mRemoteHost = ipAddress;
	}

	public int getRemotePort() {
		return mRemotePort;
	}

	/**
	 * Set the remote port.
	 * @param port The remote port.
	 */
	public void setRemotePort(final int port) {
		mRemotePort = port;
	}
	
	public String getMacAddress() {
		return mMacAddress;
	}
	
	public void setMacAddress(final String macAddress) {
		mMacAddress = macAddress;
	}
	
	/**
	 * @return Timeout connection in milliseconds.
	 */
	public int getConnectionTimeout() {
		return mConnectionTimeout;
	}

	public void setConnectionTimeout(final int timeout) {
		mConnectionTimeout = timeout;
	}
	
	/**
	 * @return Read timeout in milliseconds.
	 */
	public int getReadTimeout() {
		return mReadTimeout;
	}
	
	public void setReadTimout(final int timeout) {
		mReadTimeout = timeout;
	}
}