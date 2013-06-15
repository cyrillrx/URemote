package org.es.uremote.objects;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.es.uremote.BuildConfig;
import org.es.uremote.R;
import org.es.utils.XmlWriter;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Class that holds server connection informations.
 * 
 * @author Cyril Leroux
 *
 */
public class ServerInfo implements Parcelable {

	public static final String SAVE_FILE		= "serverConfig.xml";
	public static final String TAG_ROOT			= "servers";
	public static final String TAG_SERVER		= "server";
	public static final String TAG_NAME			= "name";
	public static final String TAG_LOCAL_HOST	= "local_ip_address";
	public static final String TAG_LOCAL_PORT	= "local_port";
	public static final String TAG_BROADCAST	= "broadcast address";
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

	private String mName;
	private String mLocalHost;
	private int mLocalPort;
	private String mBroadcast;
	private String mRemoteHost;
	private int mRemotePort;
	private String mMacAddress;
	private final int mConnectionType;

	/**
	 * If the connection with the remote server is not established within this timeout, it is dismiss.
	 */
	private int mConnectionTimeout;
	private int mReadTimeout;

	/**
	 * Constructor with parameters
	 * @param name
	 * @param localHost
	 * @param localPort
	 * @param broadcastIp
	 * @param remoteHost
	 * @param remotePort
	 * @param macAddress
	 * @param connectionTimeout
	 * @param readTimeout
	 */
	public ServerInfo(final String name, final String localHost, final int localPort,
			final String broadcastIp, final String remoteHost, final int remotePort,
			final String macAddress,
			final int connectionTimeout, final int readTimeout) {

		mName		= name;
		mLocalHost	= localHost;
		mLocalPort	= localPort;
		mBroadcast	= broadcastIp;
		mRemoteHost	= remoteHost;
		mRemotePort	= remotePort;
		mMacAddress	= macAddress;
		mConnectionTimeout	= connectionTimeout;
		mReadTimeout		= readTimeout;
		mConnectionType		= CONNECTION_TYPE_LOCAL;
	}

	public void copy(final ServerInfo server) {
		mName		= server.getName();
		mLocalHost	= server.getLocalHost();
		mLocalPort	= server.getLocalPort();
		mBroadcast	= server.getBroadcast();
		mRemoteHost	= server.getRemoteHost();
		mRemotePort	= server.getRemotePort();
		mMacAddress	= server.getMacAddress();
		mConnectionTimeout	= server.getConnectionTimeout();
		mReadTimeout		= server.getReadTimeout();

	}

	/**
	 * CREATOR is a required attribute to create an instance of a class that implements Parcelable
	 */
	public static final Parcelable.Creator<ServerInfo> CREATOR = new Parcelable.Creator<ServerInfo>() {
		@Override
		public ServerInfo createFromParcel(Parcel src) {
			return new ServerInfo(src);
		}

		@Override
		public ServerInfo[] newArray(int size) {
			return new ServerInfo[size];
		}
	};

	public ServerInfo(Parcel src) {
		mName			= src.readString();
		mLocalHost		= src.readString();
		mLocalPort		= src.readInt();
		mBroadcast		= src.readString();
		mRemoteHost		= src.readString();
		mRemotePort		= src.readInt();
		mMacAddress		= src.readString();
		mConnectionTimeout	= src.readInt();
		mReadTimeout		= src.readInt();
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
		dest.writeString(mBroadcast);
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
		final String keyBroadcast	= context.getString(R.string.key_broadcast);
		final String keyRemoteHost	= context.getString(R.string.key_remote_host);
		final String keyRemotePort	= context.getString(R.string.key_remote_port);
		final String keyMacAddress	= context.getString(R.string.key_mac_address);

		// Get key for other properties
		final String keyConnectionTimeout	= context.getString(R.string.key_connection_timeout);
		final String keyReadTimeout			= context.getString(R.string.key_read_timeout);

		// Get default values for Host and Port
		final String defaultLocalHost	= context.getString(R.string.default_local_host);
		final String defaultLocalPort	= context.getString(R.string.default_local_port);
		final String defaultBroadcast	= context.getString(R.string.default_broadcast);
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
		final String broadcast		= pref.getString(keyBroadcast, defaultLocalHost);
		final String remoteHost		= pref.getString(keyRemoteHost, defaultRemoteHost);
		final int remotePort		= Integer.parseInt(pref.getString(keyRemotePort, defaultRemotePort));
		final String macAddress		= pref.getString(keyMacAddress, defaultMacAddress);
		final int connectionTimeout	= Integer.parseInt(pref.getString(keyConnectionTimeout, defaultConnectionTimeout));
		final int readTimeout		= Integer.parseInt(pref.getString(keyReadTimeout, defaultReadTimeout));

		return new ServerInfo("", localHost, localPort, broadcast, remoteHost, remotePort, macAddress, connectionTimeout, readTimeout);
	}

	public boolean isLocal(Context context) {
		// TODO define when local and remote
		final WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		return wifiMgr.isWifiEnabled();
	}

	/**
	 * Save the object attributes into a XML file.
	 * @param confFile
	 * @param servers
	 * @return true if save succeeded false otherwise.
	 */
	public static boolean saveToXmlFile(File confFile, List<ServerInfo> servers) {

		if (servers == null || servers.isEmpty()) {
			return false;
		}

		try {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, confFile.getPath());
			}
			FileOutputStream fos = new FileOutputStream(confFile);

			XmlWriter xmlWriter = new XmlWriter(fos, TAG_ROOT);

			xmlWriter.startTag(null, TAG_SERVER);

			for (ServerInfo server : servers) {
				xmlWriter.addChild(TAG_NAME, server.getName(), null);
				xmlWriter.addChild(TAG_LOCAL_HOST, server.getLocalHost(), null);
				xmlWriter.addChild(TAG_LOCAL_PORT, server.getLocalPort(), null);
				xmlWriter.addChild(TAG_BROADCAST, server.getBroadcast(), null);
				xmlWriter.addChild(TAG_REMOTE_HOST, server.getRemoteHost(), null);
				xmlWriter.addChild(TAG_REMOTE_PORT, server.getRemotePort(), null);

				xmlWriter.addChild(TAG_MAC_ADDRESS, server.getMacAddress(), null);
				xmlWriter.addChild(TAG_CONNECTION_TIMEOUT, server.getConnectionTimeout(), null);
				xmlWriter.addChild(TAG_READ_TIMEOUT, server.getReadTimeout(), null);
			}

			xmlWriter.endTag(null, TAG_SERVER);
			xmlWriter.closeAndSave();

		} catch (IOException e) {
			return false;
		}
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

	/**
	 * @return The broadcast address.
	 */
	public String getBroadcast() {
		return mBroadcast;
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