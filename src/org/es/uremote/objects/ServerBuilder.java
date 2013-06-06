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
			return new ServerInfo(mLocalHost, mLocalPort, mRemoteHost, mRemotePort, mMacAddress, mConnectionTimeout, mReadTimeout);
		}
		throw new Exception("You can not build a Server if it is not fully Loaded.");
	}

	public boolean isLoaded() {
		return mName != null && !mName.isEmpty()
				&& !mLocalHost.isEmpty() && mLocalPort != 0
				&& !mRemoteHost.isEmpty() && mRemotePort != 0
				&& !mMacAddress.isEmpty();
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