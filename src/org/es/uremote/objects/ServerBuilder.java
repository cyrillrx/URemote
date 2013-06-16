package org.es.uremote.objects;

import org.es.uremote.objects.ServerInfo.ConnectionType;


/**
 * Class that holds server connection informations.
 * 
 * @author Cyril Leroux
 *
 */
public class ServerBuilder {

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
	 * Default constructor
	 */
	public ServerBuilder() {
		mName		= "";
		mLocalHost	= "";
		mLocalPort	= 0000;
		mBroadcast	= "";
		mRemoteHost	= "";
		mRemotePort	= 0000;
		mMacAddress	= "";
		mConnectionTimeout	= 500;
		mReadTimeout		= 500;
		mConnectionType		= ConnectionType.LOCAL;
	}

	/**
	 * @return A fully loaded {@link ServerInfo} object.
	 * @throws Exception
	 */
	public ServerInfo build() throws Exception {
		if (isLoaded()) {
			return new ServerInfo(mName, mLocalHost, mLocalPort, mBroadcast, mRemoteHost, mRemotePort, mMacAddress, mConnectionTimeout, mReadTimeout, mConnectionType);
		}
		return null;
	}

	/**
	 * @return True if the builder has all the informations to build the {@link ServerInfo} object. False otherwise.
	 * @throws Exception
	 */
	public boolean isLoaded() throws Exception {
		boolean error = false;
		StringBuilder sb = new StringBuilder();

		if (mName == null || mName.isEmpty()) {
			error = true;
			sb.append("- Name is null or empty.\n");
		}

		if (mLocalHost.isEmpty()) {
			error = true;
			sb.append("- Localhost is empty.\n");
		}

		if (mLocalPort == 0) {
			error = true;
			sb.append("- Local port is 0.\n");
		}

		if (mBroadcast.isEmpty()) {
			error = true;
			sb.append("- Broadcast is empty.\n");
		}

		if (mRemoteHost.isEmpty()) {
			error = true;
			sb.append("- Remote host is empty.\n");
		}

		if (mRemotePort == 0) {
			error = true;
			sb.append("- Remote port is 0.\n");
		}

		if (mMacAddress.isEmpty()) {
			error = true;
			sb.append("- Mac address is empty.\n");
		}

		if (error) {
			throw new Exception("Can not save the Server :\n" + sb.toString());
		}
		return true;
	}

	public void setConnectioType(ConnectionType type) {
		mConnectionType = type;
	}

	public void setName(final String name) {
		mName = name;
	}

	public void setLocalHost(final String ipAddress) {
		mLocalHost = ipAddress;
	}

	public void setLocalPort(final int port) {
		mLocalPort = port;
	}

	public void setBroadcast(final String broadcastAddress) {
		mBroadcast = broadcastAddress;
	}

	public void setRemoteHost(final String ipAddress) {
		mRemoteHost = ipAddress;
	}

	public void setRemotePort(final int port) {
		mRemotePort = port;
	}

	public void setMacAddress(final String macAddress) {
		mMacAddress = macAddress;
	}

	public void setConnectionTimeout(final int timeout) {
		mConnectionTimeout = timeout;
	}

	public void setReadTimout(final int timeout) {
		mReadTimeout = timeout;
	}
}