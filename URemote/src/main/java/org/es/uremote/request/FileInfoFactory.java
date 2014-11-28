package org.es.uremote.request;

import org.es.uremote.request.protobuf.RemoteCommand.FileInfo;
import org.es.utils.FileUtils;
import org.es.utils.Log;

import java.io.File;

/**
 * Creates FileInfo objects from various entries.
 *
 * @author Cyril Leroux
 *         Created on 31/08/13.
 */
public class FileInfoFactory {

    private static final String TAG = FileInfoFactory.class.getSimpleName();

    /**
     * Creates a directory content from byte array.
     *
     * @param directoryContent The directory content as a byte array.
     * @return A FileInfo object.
     */
    public static FileInfo createFromByteArray(final byte[] directoryContent) {

        try {
            return FileInfo.parseFrom(directoryContent);
        } catch (Exception e) {
            Log.error(TAG, "#onActivityCreated - Error occurred while parsing directory content.", e);
        }
        return null;
    }

    /**
     * Creates a directory content from a local directory path.
     *
     * @param dirPath The path of the directory.
     * @return A FileInfo object.
     */
    public static FileInfo createFromLocalPath(final String dirPath, final String[] extensions, final boolean listDirectories) {

        File[] files = FileUtils.listFiles(dirPath, extensions, listDirectories);

        FileInfo.Builder dirBuilder = FileInfo.newBuilder();
        dirBuilder.setAbsoluteFilePath(dirPath);

        FileInfo.Builder fileBuilder = FileInfo.newBuilder();
        for (File file : files) {
            dirBuilder.addChild(populateFileBuilder(fileBuilder, file));
        }

        return dirBuilder.build();
    }

    /**
     * Create a protobuf-ready File from a java.io.File.
     *
     * @return A FileInfo.File object.
     */
    public FileInfo createFileInfo(File srcFile) {

        final FileInfo.Builder builder = FileInfo.newBuilder();
        populateFileBuilder(builder, srcFile);
        return builder.build();
    }

    /**
     * Fills the FileInfo.File.Builder with the File data and returns it.
     *
     * @param fileBuilder
     * @param file
     * @return The file builder filled.
     */
    private static FileInfo.Builder populateFileBuilder(FileInfo.Builder fileBuilder, File file) {

        fileBuilder.clear();

        fileBuilder.setFilename(file.getName());
        fileBuilder.setIsDirectory(file.isDirectory());
        fileBuilder.setSize((int) file.getTotalSpace());

        return fileBuilder;
    }
}
