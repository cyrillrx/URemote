package org.es.uremote.utils;

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
		if (_filename.endsWith(".avi")) {
			return true;
		}
		return false;
	}

}
