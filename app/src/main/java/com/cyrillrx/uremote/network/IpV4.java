package com.cyrillrx.uremote.network;

import android.text.TextUtils;

/**
 * @author Cyril Leroux
 *         Created on 03/11/13.
 */
public class IpV4 {

    private static final CharSequence SEPARATOR = ".";

    private Integer[] parts = new Integer[4];
    private int port;

//    public IpV4(final String host, final int port) {
//        //mHost = host;
//        port = port;
//    }

    public IpV4(final int part1, final int part2, final int part3, final int part4, final int port) {
        parts[0] = part1;
        parts[1] = part2;
        parts[2] = part3;
        parts[3] = part4;
        this.port = port;
    }

    public String getHost() { return TextUtils.join(SEPARATOR, parts); }

    public int getPort() { return port; }
}
