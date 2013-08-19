package org.es.network;

import org.es.network.ExchangeProtos.Request;

/**
 * Interface to send a request.
 *
 * @author Cyril
 */
public interface RequestSender {
	/**
	 * Initializes the message handler then send the request.
	 *
	 * @param request The request to send.
	 */
	public void sendAsyncRequest(Request request);
}
