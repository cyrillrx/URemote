package org.es.uremote.exchange;

import org.es.uremote.device.ServerSetting;
import org.es.uremote.exchange.ExchangeMessages.Request;

/**
 * Interface to send a request.
 *
 * @author Cyril Leroux
 * Created on 26/08/12.
 */
public interface RequestSender {

	/**
	 * Initializes the message handler then send the request.
	 *
	 * @param request The request to send.
	 */
	public void sendRequest(Request request);

    /** @return The current server configuration. */
    public ServerSetting getServerSetting();
}
