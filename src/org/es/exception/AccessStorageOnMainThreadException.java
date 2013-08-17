package org.es.exception;

/**
 * Created by Cyril Leroux on 17/08/13.
 */
public class AccessStorageOnMainThreadException extends RuntimeException {

	public AccessStorageOnMainThreadException() {
		super("You cannot access storage on the main thread. Use AsyncTask, Callable on Thread classes.");
	}

	public AccessStorageOnMainThreadException(String detailMessage) {
		super(detailMessage);
	}
}
