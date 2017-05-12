package com.cyrillrx.uremote.common.device;

/**
 * @author Cyril Leroux
 *         Created on 03/11/13.
 */
public class ConnectedDevice {

    protected String id;
    protected String name;
    /** If the connection with the remote server is not established within this timeout, it is dismiss. */
    protected int connectionTimeout;
    protected int readTimeout;
    protected String securityToken;

    @Override
    public String toString() { return name; }

    /** @return The device id. */
    public String getId() { return id; }

    /** @return The device name. */
    public String getName() { return name; }

    /** @return Timeout connection in milliseconds. */
    public int getConnectionTimeout() { return connectionTimeout; }

    /** @return Read timeout in milliseconds. */
    public int getReadTimeout() { return readTimeout; }

    /** @return The security token that will be use to authenticate the user. */
    public String getSecurityToken() { return securityToken; }
}
