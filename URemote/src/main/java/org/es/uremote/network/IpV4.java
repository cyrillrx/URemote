package org.es.uremote.network;

/**
 * Created by Cyril on 03/11/13.
 */
public class IpV4 {

    private String mHost;
    private int mPort;

    public IpV4(String host, int port) {
        mHost = host;
        mPort = port;
    }

    public String getHost() {
        return mHost;
    }

    public int getPort() {
        return mPort;
    }
}
