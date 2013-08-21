package org.es.uremote.utils;

/**
 * Created by Cyril Leroux on 22/08/13.
 */
public class PrefKeys {

	// Preference keys
	private static final String KEY_LOCAL_HOST	= "local_host";
	private static final String KEY_LOCAL_PORT	= "local_port";
	private static final String KEY_BROADCAST	= "broadcast";
	private static final String KEY_REMOTE_HOST	= "remote_host";
	private static final String KEY_REMOTE_PORT	= "remote_port";
	private static final String KEY_SECURITY_TOKEN		= "security_token";
	private static final String KEY_CONNECTION_TIMEOUT	= "connection_timeout";
	private static final String KEY_READ_TIMEOUT		= "read_timeout";
	private static final String KEY_MAC_ADDRESS			= "mac_address";

	// Default values
	private static final String DEFAULT_SERVER_LABEL		= "Server";
	private static final String DEFAULT_LOCAL_HOST			= "192.168.0.1";
	private static final String DEFAULT_LOCAL_PORT			= "0000";
	private static final String DEFAULT_BROADCAST			= "192.168.0.255";
	private static final String DEFAULT_REMOTE_HOST			= "0.0.0.0";
	private static final String DEFAULT_REMOTE_PORT			= "0000";
	private static final String DEFAULT_SECURITY_TOKEN		= "0000";
	private static final String DEFAULT_CONNECTION_TIMEOUT	= "500";
	private static final String DEFAULT_READ_TIMEOUT		= "1000";
	private static final String DEFAULT_MAC_ADDRESS			= "00&#8208;00&#8208;00&#8208;00&#8208;00&#8208;00";
}
