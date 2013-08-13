package org.es.utils;

import org.es.uremote.BuildConfig;

import static org.es.uremote.BuildConfig.DEBUG;

/**
 * This class handle conditional log.
 * The log is only displayed if {@link BuildConfig#DEBUG} is true.
 * 
 * @author Cyril Leroux
 *
 */
public class Log {

	/**
	 * Send a INFO log message.
	 * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param message The message you would like logged.
	 */
	public static void info(String tag, String message) {
		if (DEBUG) {
			android.util.Log.i(tag, message);
		}
	}

	/**
	 * Send a DEBUG log message.
	 * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param message The message you would like logged.
	 */
	public static void debug(String tag, String message) {
		if (DEBUG) {
			android.util.Log.d(tag, message);
		}
	}

	/**
	 * Send a WARN log message.
	 * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param message The message you would like logged.
	 */
	public static void warning(String tag, String message) {
		if (DEBUG) {
			android.util.Log.w(tag, message);
		}
	}

	/**
	 * Send an ERROR log message
	 * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param message The message you would like logged.
	 */
	public static void error(String tag, String message) {
		if (DEBUG) {
			android.util.Log.e(tag, message);
		}
	}

    /**
     * Send an ERROR log message
     * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
     * @param message The message you would like logged.
     * @param e exception attached to the message
     */
    public static void error(String tag, String message, Exception e) {
        if (DEBUG) {
            android.util.Log.e(tag, message, e);
        }
    }
}
