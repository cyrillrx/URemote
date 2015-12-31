package com.cyrillrx.uremote.utils;

/**
 * @author Cyril Leroux
 *         Created on 22/08/13.
 */
public class PrefKeys {

    // Preference keys
    public static final String KEY_SERVER_ID          = "server_id";
    public static final String KEY_LOCAL_HOST         = "local_host";
    public static final String KEY_LOCAL_PORT         = "local_port";
    public static final String KEY_BROADCAST          = "broadcast";
    public static final String KEY_REMOTE_HOST        = "remote_host";
    public static final String KEY_REMOTE_PORT        = "remote_port";
    public static final String KEY_CONNECTION_TIMEOUT = "connection_timeout";
    public static final String KEY_READ_TIMEOUT       = "read_timeout";
    public static final String KEY_MAC_ADDRESS        = "mac_address";

    // Default values
    public static final int    DEFAULT_SERVER_ID          = -1;
    public static final String DEFAULT_SERVER_LABEL       = "Server";
    public static final String DEFAULT_LOCAL_HOST         = "192.168.0.1";
    public static final int    DEFAULT_LOCAL_PORT         = 0000;
    public static final String DEFAULT_BROADCAST          = "192.168.0.255";
    public static final String DEFAULT_REMOTE_HOST        = "0.0.0.0";
    public static final int    DEFAULT_REMOTE_PORT        = 0000;
    public static final int    DEFAULT_CONNECTION_TIMEOUT = 500;
    public static final int    DEFAULT_READ_TIMEOUT       = 1000;
    public static final String DEFAULT_MAC_ADDRESS        = "00&#8208;00&#8208;00&#8208;00&#8208;00&#8208;00";
}
