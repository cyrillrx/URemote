package org.es.uremote.exchange;

import com.google.protobuf.InvalidProtocolBufferException;

import org.es.utils.FileUtils;
import org.es.utils.Log;

import java.io.File;

import static org.es.uremote.exchange.ExchangeMessages.DirContent;

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
	public static DirContent createFromByteArray(final byte[] directoryContent) {

		try {
			return DirContent.parseFrom(directoryContent);
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
	public static DirContent createFromLocalPath(final String dirPath, final String[] extensions, final boolean listDirectories) {

		File[] files = FileUtils.listFiles(dirPath, extensions, listDirectories);

		DirContent.Builder dirBuilder = DirContent.newBuilder();
		dirBuilder.setPath(dirPath);

		DirContent.File.Builder fileBuilder = DirContent.File.newBuilder();
		for (File file : files) {
			dirBuilder.addFile(populateFileBuilder(fileBuilder, file));
		}

		return dirBuilder.build();
	}

	/**
	 * Create a protobuf-ready File from a java.io.File.
	 * @return A DirContent.File object.
	 */
	public DirContent.File createExchangeFile(File srcFile) {

		final DirContent.File.Builder builder = DirContent.File.newBuilder();
		populateFileBuilder(builder, srcFile);
		return builder.build();
	}

	/**
	 * Fills the DirContent.File.Builder with the File data and returns it.
	 * @param fileBuilder
	 * @param file
	 * @return The file builder filled.
	 */
	private static DirContent.File.Builder populateFileBuilder(DirContent.File.Builder fileBuilder, File file) {

		fileBuilder.clear();

		final DirContent.File.FileType fileType = (file.isDirectory()) ? DirContent.File.FileType.DIRECTORY : DirContent.File.FileType.FILE;

		fileBuilder.setName(file.getName());
		fileBuilder.setType(fileType);
		fileBuilder.setSize((int)file.getTotalSpace());

		return fileBuilder;
	}
}
