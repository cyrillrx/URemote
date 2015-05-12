package com.cyrillrx.uremote.network;

import android.text.TextUtils;

/**
 * @author Cyril Leroux
 *         Created on 03/11/13.
 */
public class IpV4 {

    private static final CharSequence SEPARATOR = ".";
    private Integer[] mParts = new Integer[4];
    private int mPort;

//    public IpV4(final String host, final int port) {
//        //mHost = host;
//        mPort = port;
//    }

    public IpV4(final int part1, final int part2, final int part3, final int part4, final int port) {
        mParts[0] = part1;
        mParts[1] = part2;
        mParts[2] = part3;
        mParts[3] = part4;
        mPort = port;
    }

    public String getHost() {
        return TextUtils.join(SEPARATOR, mParts);
    }

    public int getPort() { return mPort; }
}
