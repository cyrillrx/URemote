package org.es.uremote.request;

import org.es.uremote.device.NetworkDevice;
import org.es.uremote.request.protobuf.RemoteCommand;

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
    public void sendRequest(RemoteCommand.Request request);

    /** @return The current server configuration. */
    public NetworkDevice getDevice();

    /** @return The security token. */
    public String getSecurityToken();
}
