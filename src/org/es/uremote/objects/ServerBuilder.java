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
public class ServerBuilder {

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
	 * Default constructor
	 */
	public ServerBuilder() {
		mName		= "";
		mLocalHost	= "";
		mLocalPort	= 0000;
		mRemoteHost	= "";
		mRemotePort	= 0000;
		mMacAddress	= "";
		mConnectionTimeout	= 500;
		mReadTimeout		= 500;
	}

	public ServerInfo build() throws Exception {
		if (isLoaded()) {
			return new ServerInfo(mName, mLocalHost, mLocalPort, mRemoteHost, mRemotePort, mMacAddress, mConnectionTimeout, mReadTimeout);
		}
		return null;
	}

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

	public void setLocal() {
		mConnectionType = ServerInfo.CONNECTION_TYPE_LOCAL;
	}

	public void setRemote() {
		mConnectionType = ServerInfo.CONNECTION_TYPE_REMOTE;
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