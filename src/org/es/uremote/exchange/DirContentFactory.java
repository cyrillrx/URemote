package org.es.uremote.exchange;

import com.google.protobuf.InvalidProtocolBufferException;

import org.es.utils.FileUtils;
import org.es.utils.Log;

import java.io.File;

/**
 * Creates DirContent objects from various entries.
 *
 * @author Cyril Leroux
 * Created on 31/08/13.
 */
public class DirContentFactory {

	private static final String TAG = "DirContentFactory";

	/**
	 * Creates a directory content from byte array.
	 * @param directoryContent The directory content as a byte array.
	 * @return A DirContent object.
	 */
	public static ExchangeMessages.DirContent createFromByteArray(final byte[] directoryContent) {

		try {
			return ExchangeMessages.DirContent.parseFrom(directoryContent);
		} catch (InvalidProtocolBufferException e) {
			Log.error(TAG, "#onActivityCreated - Error occurred while parsing directory content.", e);
		}

		return null;
	}

	/**
	 * Creates a directory content from a local directory path.
	 * @param dirPath The path of the directory.
	 * @return A DirContent object.
	 */
	public static ExchangeMessages.DirContent createFromLocalPath(final String dirPath, final String[] extensions, final boolean listDirectories) {

		File[] files = FileUtils.listFiles(dirPath, extensions, listDirectories);

		ExchangeMessages.DirContent.Builder dirBuilder = ExchangeMessages.DirContent.newBuilder();
		dirBuilder.setPath(dirPath);

		ExchangeMessages.DirContent.File.Builder fileBuilder = ExchangeMessages.DirContent.File.newBuilder();
		for (File file : files) {
			dirBuilder.addFile(fillFileBuilder(fileBuilder, file));
		}

		return dirBuilder.build();
	}

	/**
	 * Fills the DirContent.File.Builder with the File data and returns it.
	 * @param fileBuilder
	 * @param file
	 * @return The file builder filled.
	 */
	private static ExchangeMessages.DirContent.File.Builder fillFileBuilder(ExchangeMessages.DirContent.File.Builder fileBuilder, File file) {

		fileBuilder.clear();

		final ExchangeMessages.DirContent.File.FileType fileType = (file.isDirectory()) ? ExchangeMessages.DirContent.File.FileType.DIRECTORY : ExchangeMessages.DirContent.File.FileType.FILE;

		fileBuilder.setName(file.getName());
		fileBuilder.setType(fileType);
		fileBuilder.setSize((int)file.getTotalSpace());

		return fileBuilder;
	}
}
