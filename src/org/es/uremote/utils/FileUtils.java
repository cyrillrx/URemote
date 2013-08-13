package org.es.uremote.utils;

import java.io.File;

/**
 * Class for file manipulation.
 * 
 * @author Cyril Leroux
 */
public class FileUtils {

	/**
	 * @param filename The filename to test.
	 * @return True if the file is a video file.
	 */
	public static boolean isAVideo(final String filename){
		if (filename == null) {
			return false;
		}
		if (	filename.endsWith(".avi") ||
				filename.endsWith(".mp4") ||
				filename.endsWith(".flv") ||
				filename.endsWith(".mov")) {
			return true;
		}
		return false;
	}

	/**
	 * Truncate the file path by deleting the characters after the last occurrence of the file separator.
	 * Works for both files and directories.
	 * @param filePath The complete path.
	 * @return The truncated path.
	 */
	public static String truncatePath(String filePath) {
		// Get the last position of the file separator
		int separatorPos = filePath.lastIndexOf(File.separator);

		if (separatorPos > 0 && separatorPos <= filePath.length() - 2 ) {
			return filePath.substring(0, separatorPos);
		}

		return filePath;
	}
	//-------------------------------------------------
}
