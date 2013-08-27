package org.es.uremote.exchange;

import org.es.uremote.exchange.ExchangeMessages.DirContent;
import org.es.uremote.exchange.ExchangeMessages.DirContent.File.FileType;
import org.es.utils.FileUtils;

import java.io.File;

/**
 * @author Cyril Leroux
 * Created on 26/08/13.
 */
public class ExchangeUtils {

	public static final DirContent createDirContent(String dirPath, String[] extensions) {

		File[] files = FileUtils.listFiles(dirPath, extensions);

		DirContent.Builder dirBuilder = DirContent.newBuilder();

		dirBuilder.setPath(dirPath);
		for (File file : files) {
			dirBuilder.addFile(getFileBuilder(file));
		}

		return dirBuilder.build();
	}

	private static final DirContent.File.Builder getFileBuilder(File file) {

		final FileType fileType = (file.isDirectory()) ? FileType.DIRECTORY : FileType.FILE;

		DirContent.File.Builder fileBuilder = DirContent.File.newBuilder();
		fileBuilder.setName(file.getName());
		fileBuilder.setType(fileType);
		fileBuilder.setSize((int)file.getTotalSpace());

		return fileBuilder;
	}
}
