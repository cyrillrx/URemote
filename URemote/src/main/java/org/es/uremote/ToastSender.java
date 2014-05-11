package org.es.uremote;

/**
 * Created by Cyril Leroux on 19/01/14.
 */
public interface ToastSender {

    void sendToast(final String message);

    void sendToast(final int messageResourceId);
}
