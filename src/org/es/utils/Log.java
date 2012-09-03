package org.es.utils;

import static org.es.uremote.BuildConfig.DEBUG;

import org.es.uremote.BuildConfig;

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
	 * @param _tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param _message The message you would like logged.
	 */
	public static void info(String _tag, String _message) {
		if (DEBUG) {
			android.util.Log.i(_tag, _message);
		}
	}

	/**
	 * Send a DEBUG log message.
	 * @param _tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param _message The message you would like logged.
	 */
	public static void debug(String _tag, String _message) {
		if (DEBUG) {
			android.util.Log.d(_tag, _message);
		}
	}

	/**
	 * Send a WARN log message.
	 * @param _tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param _message The message you would like logged.
	 */
	public static void warning(String _tag, String _message) {
		if (DEBUG) {
			android.util.Log.w(_tag, _message);
		}
	}

	/**
	 * Send an ERROR log message
	 * @param _tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param _message The message you would like logged.
	 */
	public static void error(String _tag, String _message) {
		if (DEBUG) {
			android.util.Log.e(_tag, _message);
		}
	}
}
