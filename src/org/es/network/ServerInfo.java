package org.es.network;

/**
 * Class that holds server connection informations.
 * 
 * @author Cyril Leroux
 *
 */
public class ServerInfo {

	private final String mHost;
	private final int mPort;
	private final int mConnectionTimeout;
	private final int mReadTimeout;

	/**
	 * Default constructor
	 * @param host
	 * @param port
	 * @param connectionTimeout
	 * @param readTimeout
	 */
	public ServerInfo(final String host, final int port, final int connectionTimeout, final int readTimeout) {
		mHost = host;
		mPort = port;
		mConnectionTimeout = connectionTimeout;
		mReadTimeout = readTimeout;
	}

	/**
	 * @return Ip address of the server
	 */
	public String getHost() {
		return mHost;
	}

	/**
	 * @return Open port to connect to.
	 */
	public int getPort() {
		return mPort;
	}

	/**
	 * @return Timeout connection in milliseconds.
	 * If the connection with the remote server is not established within this timeout, it is dismiss.
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