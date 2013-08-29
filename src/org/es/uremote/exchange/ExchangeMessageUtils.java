package org.es.uremote.exchange;

import com.google.protobuf.InvalidProtocolBufferException;

import org.es.utils.Log;

/**
 * @author Cyril
 * Created on 29/08/13.
 */
public class ExchangeMessageUtils {

	private static final String TAG = "ExchangeMessageUtils";

	public static final ExchangeMessages.DirContent createDirectoryContent(byte[] directoryContent) {

		if (directoryContent == null) {
			Log.error(TAG, "#onActivityCreated - Argument directoryContent is null");
			return null;
		}

		try {
			return ExchangeMessages.DirContent.parseFrom(directoryContent);
		} catch (InvalidProtocolBufferException e) {
			Log.error(TAG, "#onActivityCreated - Error occurred while parsing directory content.", e);
		}

		return null;
	}
}
