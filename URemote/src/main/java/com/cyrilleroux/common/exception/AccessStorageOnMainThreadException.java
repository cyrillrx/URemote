package com.cyrilleroux.common.exception;

/**
 * @author Cyril Leroux
 *         Created on 17/08/13.
 */
public class AccessStorageOnMainThreadException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = " - You cannot access storage on the main thread. Use AsyncTask, Callable or Thread classes.";

    /**
     * @param source A String that identify the source of the error (class, method, etc.)
     */
    public AccessStorageOnMainThreadException(String source) {
        super(source + DEFAULT_MESSAGE);
    }
}
