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
 * Class that holds server connection settings.
 * 
 * @author Cyril Leroux
 *
 */
public class ServerSetting implements Parcelable {

	public static final String SAVE_FILE		= "serverConfig.xml";

	/**
	 * The type of connection.
	 */
	public static enum ConnectionType {
		/** The server IS in the same network than the device. It must be accessed locally. */
		LOCAL,
		/** The server IS NOT in the same network than the device. It must be accessed remotely. */
		REMOTE
	};

	private String mName;
	private String mLocalHost;
	private int mLocalPort;
	private String mBroadcast;
	private String mRemoteHost;
	private int mRemotePort;
	private String mMacAddress;
	private ConnectionType mConnectionType;

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
	 * @param connectionType
	 */
	public ServerSetting(final String name, final String localHost, final int localPort,
                         final String broadcastIp, final String remoteHost, final int remotePort,
                         final String macAddress,
                         final int connectionTimeout, final int readTimeout,
                         final ConnectionType connectionType) {

		mName		= name;
		mLocalHost	= localHost;
		mLocalPort	= localPort;
		mBroadcast	= broadcastIp;
		mRemoteHost	= remoteHost;
		mRemotePort	= remotePort;
		mMacAddress	= macAddress;
		mConnectionTimeout	= connectionTimeout;
		mReadTimeout		= readTimeout;
		mConnectionType		= connectionType;
	}

	public void copy(final ServerSetting server) {
		mName		= server.getName();
		mLocalHost	= server.getLocalHost();
		mLocalPort	= server.getLocalPort();
		mBroadcast	= server.getBroadcast();
		mRemoteHost	= server.getRemoteHost();
		mRemotePort	= server.getRemotePort();
		mMacAddress	= server.getMacAddress();
		mConnectionTimeout	= server.getConnectionTimeout();
		mReadTimeout		= server.getReadTimeout();
		mConnectionType		= server.getConnectionType();
	}

	/**
	 * CREATOR is a required attribute to create an instance of a class that implements Parcelable
	 */
	public static final Parcelable.Creator<ServerSetting> CREATOR = new Parcelable.Creator<ServerSetting>() {
		@Override
		public ServerSetting createFromParcel(Parcel src) {
			return new ServerSetting(src);
		}

		@Override
		public ServerSetting[] newArray(int size) {
			return new ServerSetting[size];
		}
	};

	/**
	 * @param src
	 */
	public ServerSetting(Parcel src) {
		mName			= src.readString();
		mLocalHost		= src.readString();
		mLocalPort		= src.readInt();
		mBroadcast		= src.readString();
		mRemoteHost		= src.readString();
		mRemotePort		= src.readInt();
		mMacAddress		= src.readString();
		mConnectionTimeout	= src.readInt();
		mReadTimeout		= src.readInt();
		mConnectionType		= ConnectionType.valueOf(src.readString());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel destination, int flags) {
		destination.writeString(mName);
		destination.writeString(mLocalHost);
		destination.writeInt(mLocalPort);
		destination.writeString(mBroadcast);
		destination.writeString(mRemoteHost);
		destination.writeInt(mRemotePort);
		destination.writeString(mMacAddress);
		destination.writeInt(mConnectionTimeout);
		destination.writeInt(mReadTimeout);
		destination.writeString(mConnectionType.toString());
	}

	/**
	 * @param context The application context.
	 * @return The Server connection settings stored in User Preferences
	 */
	public static ServerSetting loadFromPreferences(Context context) {

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
		final String broadcast		= pref.getString(keyBroadcast, defaultBroadcast);
		final String remoteHost		= pref.getString(keyRemoteHost, defaultRemoteHost);
		final int remotePort		= Integer.parseInt(pref.getString(keyRemotePort, defaultRemotePort));
		final String macAddress		= pref.getString(keyMacAddress, defaultMacAddress);
		final int connectionTimeout	= Integer.parseInt(pref.getString(keyConnectionTimeout, defaultConnectionTimeout));
		final int readTimeout		= Integer.parseInt(pref.getString(keyReadTimeout, defaultReadTimeout));

		return new ServerSetting("", localHost, localPort, broadcast, remoteHost, remotePort, macAddress, connectionTimeout, readTimeout, ConnectionType.LOCAL);
	}

	/**
	 * @param context
	 * @return True is the server is in the same network than the device.
	 */
	public boolean isLocal(Context context) {
		// TODO define when local and remote
		final WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		return wifiMgr.isWifiEnabled();
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

	/**
	 * @return The server name.
	 */
	public String getName() {
		return mName;
	}

	/**
	 * @return The ip address of the local server.
	 */
	public String getLocalHost() {
		return mLocalHost;
	}

	/**
	 * @return The port of the local server.
	 */
	public int getLocalPort() {
		return mLocalPort;
	}

	/**
	 * @return The broadcast address.
	 */
	public String getBroadcast() {
		return mBroadcast;
	}

	/**
	 * @return The ip address of the remote server.
	 */
	public String getRemoteHost() {
		return mRemoteHost;
	}

	/**
	 * @return The port of the remote server.
	 */
	public int getRemotePort() {
		return mRemotePort;
	}

	/**
	 * @return The mac address of the server.
	 */
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

	/**
	 * @return The type of connection (remote or local).
	 */
	public ConnectionType getConnectionType() {
		return mConnectionType;
	}
}