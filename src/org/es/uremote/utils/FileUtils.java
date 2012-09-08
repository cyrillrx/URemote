package org.es.uremote.utils;

import java.io.File;

/**
 * Class for file manipulation.
 * 
 * @author Cyril Leroux
 */
public class FileUtils {

	/**
	 * @param _filename The filename to test.
	 * @return True if the file is a video file.
	 */
	public static boolean isAVideo(final String _filename){
		if (_filename == null) {
			return false;
		}
		if (	_filename.endsWith(".avi") ||
				_filename.endsWith(".mp4") ||
				_filename.endsWith(".flv") ||
				_filename.endsWith(".mov")) {
			return true;
		}
		return false;
	}

	/**
	 * Truncate the file path by deleting the characters after the last occurrence of the file separator.
	 * Works for both files and directories.
	 * @param _filePath The complete path.
	 * @return The truncated path.
	 */
	public static String truncatePath(String _filePath) {
		// Get the last position of the file separator
		int separatorPos = _filePath.lastIndexOf(File.separator);

		if (separatorPos > 0 && separatorPos <= _filePath.length() - 2 ) {
			return _filePath.substring(0, separatorPos);
		}

		return _filePath;
	}
	//-------------------------------------------------
}
