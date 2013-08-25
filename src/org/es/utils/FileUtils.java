package org.es.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

/**
 * Class for file manipulation.
 *
 * @author Cyril Leroux
 */
public class FileUtils {

	/**
	 * List all files and directory with a specific extension in a given directory.
	 * @param dirPath Path of the root directory.
	 * @param extensions List of accepted extensions.
	 * @return An array containing the found files.
	 */
	public static File[] listFiles(String dirPath, final String[] extensions) {

		FilenameFilter extensionFilter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				for (String extension : extensions) {
					if (filename.endsWith(extension)) {
						return true;
					}
				}
				return false;
			}
		};

		File root = new File(dirPath);
		return root.listFiles();
	}

	/**
	 * @param filename The filename to test.
	 * @return True if the file is a video file.
	 */
	public static boolean isAVideo(final String filename) {
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
	 *
	 * @param filePath The complete path.
	 * @return The truncated path.
	 */
	public static String truncatePath(String filePath) {
		// Get the last position of the file separator
		int separatorPos = filePath.lastIndexOf(File.separator);

		if (separatorPos > 0 && separatorPos <= filePath.length() - 2) {
			return filePath.substring(0, separatorPos);
		}

		return filePath;
	}
}
