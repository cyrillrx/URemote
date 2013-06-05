package org.es.uremote.objects;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.es.uremote.R;
import org.es.utils.XmlWriter;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;

/**
 * Class that holds server connection informations.
 * 
 * @author Cyril Leroux
 *
 */
public class ServerInfo implements Parcelable {

	public static final String SAVE_FILE		= "serverConfig";
	public static final String TAG_ROOT			= "servers";
	public static final String TAG_SERVER		= "server";
	public static final String TAG_LOCAL_HOST		= "local_ip_address";
	public static final String TAG_LOCAL_PORT	= "local_port";
	public static final String TAG_REMOTE_HOST	= "remote_ip_address";
	public static final String TAG_REMOTE_PORT	= "remote_port";
	public static final String TAG_MAC_ADDRESS	= "mac_address";
	public static final String TAG_CONNECTION_TIMEOUT	= "connection_timeout";
	public static final String TAG_READ_TIMEOUT			= "read_timeout";


	private static String TAG = "ServerInfo";
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

	private final String mName;
	private final String mLocalHost;
	private final int mLocalPort;
	private final String mRemoteHost;
	private final int mRemotePort;
	private final String mMacAddress;
	private final int mConnectionType;

	/**
	 * If the connection with the remote server is not established within this timeout, it is dismiss.
	 */
	private final int mConnectionTimeout;
	private final int mReadTimeout;

	/**
	 * Constructor with parameters
	 * @param localHost
	 * @param localPort
	 * @param remoteHost
	 * @param remotePort
	 * @param macAddress
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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mName);
		dest.writeString(mLocalHost);
		dest.writeInt(mLocalPort);
		dest.writeString(mRemoteHost);
		dest.writeInt(mRemotePort);
		dest.writeString(mMacAddress);
		dest.writeInt(mConnectionTimeout);
		dest.writeInt(mReadTimeout);
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
	 * @param context
	 * @param servers
	 * @return true if save succeeded false otherwise.
	 * @throws IOException
	 */
	public static boolean saveToXmlFile(Context context, List<ServerInfo> servers) throws IOException {

		if (servers == null || servers.isEmpty()) {
			return false;
		}

		FileOutputStream fos = context.openFileOutput(SAVE_FILE, Context.MODE_PRIVATE);
		//		ObjectOutputStream os = new ObjectOutputStream(fos);
		//		os.writeObject(this);
		//		os.close();
		//		return true;

		XmlWriter xmlWriter = new XmlWriter(fos, TAG_ROOT);

		xmlWriter.startTag(null, TAG_SERVER);

		for (ServerInfo server : servers) {
			xmlWriter.addChild(TAG_LOCAL_HOST, server.getLocalHost(), null);
			xmlWriter.addChild(TAG_LOCAL_PORT, server.getLocalPort(), null);
			xmlWriter.addChild(TAG_REMOTE_HOST, server.getRemoteHost(), null);
			xmlWriter.addChild(TAG_REMOTE_PORT, server.getRemotePort(), null);

			xmlWriter.addChild(TAG_MAC_ADDRESS, server.getMacAddress(), null);
			xmlWriter.addChild(TAG_CONNECTION_TIMEOUT, server.getConnectionTimeout(), null);
			xmlWriter.addChild(TAG_READ_TIMEOUT, server.getReadTimeout(), null);
		}

		xmlWriter.endTag(null, TAG_SERVER);

		xmlWriter.closeAndSave();
		return true;
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

	/**
	 * @return The local ip address.
	 */
	public String getLocalHost() {
		return mLocalHost;
	}

	public int getLocalPort() {
		return mLocalPort;
	}

	public String getRemoteHost() {
		return mRemoteHost;
	}

	public int getRemotePort() {
		return mRemotePort;
	}

	public String getMacAddress() {
		return mMacAddress;
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