package com.cyrilleroux.uremote.utils;

/**
 * @author Cyril Leroux
 *         Created on 19/01/14.
 */
public interface ToastSender {

    void sendToast(final String message);

    void sendToast(final int messageResourceId);
}
