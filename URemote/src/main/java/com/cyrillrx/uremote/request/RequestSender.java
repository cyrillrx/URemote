package com.cyrillrx.uremote.request;

import com.cyrillrx.uremote.common.device.NetworkDevice;
import com.cyrillrx.uremote.request.protobuf.RemoteCommand;

/**
 * Interface to send a request.
 *
 * @author Cyril Leroux
 *         Created on 26/08/12.
 */
public interface RequestSender {

    /**
     * Initializes the message handler then send the request.
     *
     * @param request The request to send.
     */
    void sendRequest(RemoteCommand.Request request);

    /** @return The current server configuration. */
    NetworkDevice getDevice();

    /** @return The security token. */
    String getSecurityToken();
}
