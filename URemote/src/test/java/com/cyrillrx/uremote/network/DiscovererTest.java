package com.cyrillrx.uremote.network;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Cyril Leroux
 *         Created on 13/04/15
 */
public class DiscovererTest {


    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testAddSession() {

        final String ip = NetUtils.getLocalIpAddress();
        if (ip == null) {
            return;
        }

        final String trunk = ip.substring(0, ip.lastIndexOf('.') + 1);
        // TODO replace hardcoded subnet and port
        Discoverer.discover(trunk, 9002);
    }
}
