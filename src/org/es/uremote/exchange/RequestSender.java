package org.es.uremote.exchange;

import org.es.uremote.exchange.ExchangeMessages.Request;

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
