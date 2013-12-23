package org.es.uremote.objects;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * Parcelable class that holds server connection settings.
 *
 * @author Cyril Leroux
 * Created on 19/05/13.
 */
// TODO rename class to match ConnectedDevice pattern
public class ServerSetting extends ConnectedDevice implements Parcelable {

	public static final String FILENAME = "serverConfig.xml";

	/** CREATOR is a required attribute to create an instance of a class that implements Parcelable */
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

	private String mLocalHost;
	private int mLocalPort;
	private String mBroadcast;
	private String mRemoteHost;
	private int mRemotePort;
	private String mMacAddress;
	private ConnectionType mConnectionType;

	/**
	 * Constructor with parameters
	 *
	 * @param name
	 * @param localHost
	 * @param localPort
	 * @param broadcastIp
	 * @param remoteHost
	 * @param remotePort
	 * @param macAddress
	 * @param connectionTimeout
	 * @param readTimeout
     * @param securityToken
     * @param connectionType
	 */
	private ServerSetting(
			final String name, final String localHost, final int localPort,
			final String broadcastIp, final String remoteHost, final int remotePort,
			final String macAddress,
			final int connectionTimeout, final int readTimeout,
            final String securityToken,
			final ConnectionType connectionType) {

		mName				= name;
		mLocalHost			= localHost;
		mLocalPort			= localPort;
		mBroadcast			= broadcastIp;
		mRemoteHost			= remoteHost;
		mRemotePort			= remotePort;
		mMacAddress			= macAddress;
		mConnectionTimeout	= connectionTimeout;
		mReadTimeout		= readTimeout;
        mSecurityToken      = securityToken;
		mConnectionType		= connectionType;
	}

	/** @param src */
	public ServerSetting(final Parcel src) {
		mName               = src.readString();
		mLocalHost          = src.readString();
		mLocalPort          = src.readInt();
		mBroadcast          = src.readString();
		mRemoteHost         = src.readString();
		mRemotePort         = src.readInt();
		mMacAddress         = src.readString();
		mConnectionTimeout  = src.readInt();
		mReadTimeout        = src.readInt();
		mSecurityToken      = src.readString();
		mConnectionType     = ConnectionType.valueOf(src.readString());
	}

	/**
	 * Update the server with the object passed.
	 *
	 * @param server The server updated data.
	 */
	public void update(final ServerSetting server) {
		mName				= server.getName();
		mLocalHost			= server.getLocalHost();
		mLocalPort			= server.getLocalPort();
		mBroadcast			= server.getBroadcast();
		mRemoteHost			= server.getRemoteHost();
		mRemotePort			= server.getRemotePort();
		mMacAddress			= server.getMacAddress();
		mConnectionTimeout	= server.getConnectionTimeout();
		mReadTimeout		= server.getReadTimeout();
        mSecurityToken      = server.getSecurityToken();
		mConnectionType		= server.getConnectionType();
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
		destination.writeString(mSecurityToken);
		destination.writeString(mConnectionType.toString());
	}

	/**
	 * @param context
	 * @return True is the server is in the same network than the device.
	 */
	public boolean isLocal(Context context) {
		// TODO define when local and remote => User defined
		final WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		return wifiMgr.isWifiEnabled();
	}

	/** @return Concatenation of host and port of the local server. */
	public String getFullLocal() {
		return mLocalHost + ":" + mLocalPort;
	}

	/** @return Concatenation of host and port of the remote server. */
	public String getFullRemote() {
		return mRemoteHost + ":" + mRemotePort;
	}

	/** @return The ip address of the local server. */
	public String getLocalHost() {
		return mLocalHost;
	}

	/** @return The port of the local server. */
	public int getLocalPort() {
		return mLocalPort;
	}

	/** @return The broadcast address. */
	public String getBroadcast() {
		return mBroadcast;
	}

	/** @return The ip address of the remote server. */
	public String getRemoteHost() {
		return mRemoteHost;
	}

	/** @return The port of the remote server. */
	public int getRemotePort() {
		return mRemotePort;
	}

	/** @return The mac address of the server. */
	public String getMacAddress() {
		return mMacAddress;
	}

	/** @return The type of connection (remote or local). */
	public ConnectionType getConnectionType() {
		return mConnectionType;
	}

	/** The type of connection. */
	public static enum ConnectionType {
		/** The server IS in the same network than the device. It must be accessed locally. */
		LOCAL,
		/** The server IS NOT in the same network than the device. It must be accessed remotely. */
		REMOTE
	}

	/**
	 * @return An instance of ServerSetting.Builder.
	 */
	public static Builder newBuilder() {
		return new Builder();
	}

	/**
	 * Class that holds server connection data.
	 *
	 * @author Cyril Leroux
	 * Created on 03/06/13.
	 */
    // TODO Move a part of the builder in ConnectedDevice class
	public static class Builder {

		private static String DEFAULT_NAME              = "";
		private static String DEFAULT_LOCAL_HOST        = "";
		private static int DEFAULT_LOCAL_PORT           = 0000;
		private static String DEFAULT_BROADCAST         = "";
		private static String DEFAULT_REMOTE_HOST       = "";
		private static int DEFAULT_REMOTE_PORT          = 0000;
		private static String DEFAULT_MAC_ADDRESS       = "";
		private static ConnectionType DEFAULT_CONNECTION = ConnectionType.LOCAL;
		private static int DEFAULT_CONNECTION_TIMEOUT   = 500;
		private static int DEFAULT_READ_TIMEOUT         = 500;
		private static String DEFAULT_SECURITY_TOKEN    = "";

		private String mName		= DEFAULT_NAME;
		private String mLocalHost	= DEFAULT_LOCAL_HOST;
		private int mLocalPort		= DEFAULT_LOCAL_PORT;
		private String mBroadcast	= DEFAULT_BROADCAST;
		private String mRemoteHost	= DEFAULT_REMOTE_HOST;
		private int mRemotePort		= DEFAULT_REMOTE_PORT;
		private String mMacAddress	= DEFAULT_MAC_ADDRESS;
		private ConnectionType mConnectionType	= DEFAULT_CONNECTION;
		/** If the connection with the remote server is not established within this timeout, it is dismissed. */
		private int mConnectionTimeout	= DEFAULT_CONNECTION_TIMEOUT;
		private int mReadTimeout		= DEFAULT_READ_TIMEOUT;
		private String mSecurityToken   = DEFAULT_SECURITY_TOKEN;

		private Builder() { }

		/**
		 * Reset builder
		 */
		public void clear() {
			mName       = DEFAULT_NAME;
			mLocalHost  = DEFAULT_LOCAL_HOST;
			mLocalPort  = DEFAULT_LOCAL_PORT;
			mBroadcast  = DEFAULT_BROADCAST;
			mRemoteHost = DEFAULT_REMOTE_HOST;
			mRemotePort = DEFAULT_REMOTE_PORT;
			mMacAddress = DEFAULT_MAC_ADDRESS;
			mConnectionType     = DEFAULT_CONNECTION;
			mConnectionTimeout  = DEFAULT_CONNECTION_TIMEOUT;
			mReadTimeout        = DEFAULT_READ_TIMEOUT;
            mSecurityToken      = DEFAULT_SECURITY_TOKEN;
		}
		/**
		 * @return A fully loaded {@link ServerSetting} object.
		 *
		 * @throws Exception if the builder has not all the data to build the object.
		 */
		public ServerSetting build() throws Exception {

			boolean error = false;

			StringBuilder sb = new StringBuilder();

			if (TextUtils.isEmpty(mName)) {
				error = true;
				sb.append("- Name is null or empty.\n");
			}

			if (TextUtils.isEmpty(mLocalHost)) {
				error = true;
				sb.append("- Localhost is empty.\n");
			}

			if (mLocalPort == 0) {
				error = true;
				sb.append("- Local port is 0.\n");
			}

			if (TextUtils.isEmpty(mBroadcast)) {
				error = true;
				sb.append("- Broadcast is empty.\n");
			}

			if (TextUtils.isEmpty(mRemoteHost)) {
				error = true;
				sb.append("- Remote host is empty.\n");
			}

			if (mRemotePort == 0) {
				error = true;
				sb.append("- Remote port is 0.\n");
			}

			if (TextUtils.isEmpty(mMacAddress)) {
				error = true;
				sb.append("- Mac address is empty.\n");
			}

			if (error) {
				throw new Exception("ServerSetting #build() - Can not build the Server :\n" + sb.toString());
			}

			return new ServerSetting(mName, mLocalHost, mLocalPort, mBroadcast, mRemoteHost, mRemotePort, mMacAddress, mConnectionTimeout, mReadTimeout, mSecurityToken, mConnectionType);
		}

		public void setConnectionType(ConnectionType type) { mConnectionType = type; }

		public void setName(final String name) { mName = name; }

		public void setLocalHost(final String ipAddress) { mLocalHost = ipAddress; }

		public void setLocalPort(final int port) { mLocalPort = port; }

		public void setBroadcast(final String broadcastAddress) { mBroadcast = broadcastAddress; }

		public void setRemoteHost(final String ipAddress) { mRemoteHost = ipAddress; }

		public void setRemotePort(final int port) { mRemotePort = port; }

		public void setMacAddress(final String macAddress) { mMacAddress = macAddress; }

		/**
		 * If the connection with the remote server is not established
		 * within this timeout, it is dismissed.
		 */
		public void setConnectionTimeout(final int timeout) { mConnectionTimeout = timeout; }

		public void setReadTimeout(final int timeout) { mReadTimeout = timeout; }

		public void setSecurityToken(final String securityToken) { mSecurityToken = securityToken; }
	}
}