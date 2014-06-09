package org.es.utils;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Class for file manipulation.
 *
 * @author Cyril Leroux
 * Created on 26/08/12.
 */
public class FileUtils {

	/**
	 * List all files and directory with a specific extension in a given directory.
	 * @param dirPath Path of the root directory.
	 * @param extensions List of accepted extensions.
	 * @return An array containing the found files.
	 */
	public static File[] listFiles(final String dirPath, final String[] extensions, final boolean listDirectories) {

		FilenameFilter extensionFilter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {

				final File file = new File(dir, filename);
				if (file.isDirectory()) {
					return listDirectories;
				}

				for (String extension : extensions) {
					if (filename.endsWith(extension)) {
						return true;
					}
				}
				return false;
			}
		};

		File root = new File(dirPath);
		return root.listFiles(extensionFilter);
	}

	/**
	 * @param filename The filename to test.
	 * @return True if the file is a video file.
	 */
	public static boolean isVideo(final String filename) {

		return filename != null && (
                filename.endsWith(".avi") ||
				filename.endsWith(".mp4") ||
				filename.endsWith(".mkv") ||
				filename.endsWith(".flv") ||
				filename.endsWith(".mov")
        );
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
