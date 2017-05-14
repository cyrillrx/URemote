package com.cyrillrx.uremote.explorer;

import com.cyrillrx.android.utils.FileUtils;
import com.cyrillrx.uremote.request.protobuf.RemoteCommand;

import java.io.File;

/**
 * @author Cyril Leroux
 *         Created 12/05/2017.
 */
public abstract class Explorer {

    public static final String PREVIOUS_DIRECTORY_PATH = "..";
    public static final String KEY_DIRECTORY_CONTENT = "DIRECTORY_CONTENT";
    public static final String DEFAULT_DIRECTORY_PATH = "default_path";

    protected String root;
    protected String path;

    protected RemoteCommand.FileInfo currentFileInfo;

    protected Callback callback;

    public Explorer(Callback callback) {
        this.callback = callback;
        root = DEFAULT_DIRECTORY_PATH;
    }

    public boolean setRoot(String root) {

        if (root == null) {
            this.root = DEFAULT_DIRECTORY_PATH;
            return false;
        }

        this.root = root;
        navigateTo(root);
        return true;
    }

    public void reload() {
        if (path != null) {
            navigateTo(path);
        }
    }

    /**
     * Lists the content of the passed directory.<br />
     * Updates the view once the data have been received.
     *
     * @param dirPath The path of the directory to display.
     */
    public void navigateTo(String dirPath) { path = dirPath; }

    /**
     * Navigates up if possible.<br />
     * This method is supposed to be called from the parent Activity (most likely through the ActionBar).<br />
     * Updates the view once the data have been received from the server.
     *
     * @return True if we can navigate up from the current directory. False otherwise.
     */
    public boolean navigateUp() {

        if (canNavigateUp()) {
            doNavigateUp();
            return true;
        }
        return false;
    }

    /**
     * Call by the activity that holds the fragment if the back button is override.
     * If the function returns true, the back button is override to go up.
     * Else, it behaves normally.
     *
     * @return True if we can navigate up from the current directory. False otherwise.
     */
    public boolean canNavigateUp() {

        if (currentFileInfo == null) { return false; }

        final String absolutePath = currentFileInfo.getAbsoluteFilePath();

        return absolutePath != null &&
                !absolutePath.equals(root) &&
                absolutePath.contains(File.separator);

    }

    /**
     * Calls navigateTo on the parent directory.
     */
    protected void doNavigateUp() {
        final String parentPath = FileUtils.truncatePath(currentFileInfo.getAbsoluteFilePath());
        navigateTo(parentPath);
    }

    public interface Callback {
        void onDirectoryLoaded(RemoteCommand.FileInfo dirContent);
    }
}
