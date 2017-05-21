package com.cyrillrx.uremote.explorer

import com.cyrillrx.android.utils.FileUtils
import com.cyrillrx.uremote.request.protobuf.RemoteCommand

import java.io.File

/**
 * @author Cyril Leroux
 *         Created 12/05/2017.
 */
abstract class Explorer(protected var callback: Explorer.Callback) {

    protected var root: String
    protected var path: String

    protected var currentFileInfo: RemoteCommand.FileInfo? = null

    init {
        root = DEFAULT_DIRECTORY_PATH
        path = root
    }


    fun changeRoot(root: String): Boolean {

        this.root = root
        navigateTo(root)
        return true
    }

    fun reload() {
        navigateTo(path)
    }

    /**
     * Lists the content of the passed directory.<br></br>
     * Updates the view once the data have been received.
     *
     * @param dirPath The path of the directory to display.
     */
    open fun navigateTo(dirPath: String) {
        path = dirPath
    }

    /**
     * Navigates up if possible.<br></br>
     * This method is supposed to be called from the parent Activity (most likely through the ActionBar).<br></br>
     * Updates the view once the data have been received from the server.
     *
     * @return True if we can navigate up from the current directory. False otherwise.
     */
    fun navigateUp(): Boolean {

        if (canNavigateUp()) {
            doNavigateUp()
            return true
        }
        return false
    }

    /**
     * Call by the activity that holds the fragment if the back button is override.
     * If the function returns true, the back button is override to go up.
     * Else, it behaves normally.
     *
     * @return True if we can navigate up from the current directory. False otherwise.
     */
    fun canNavigateUp(): Boolean {

        if (currentFileInfo == null) {
            return false
        }

        val absolutePath = currentFileInfo?.absoluteFilePath

        return absolutePath != null &&
                absolutePath != root &&
                absolutePath.contains(File.separator)

    }

    /**
     * Calls navigateTo on the parent directory.
     */
    protected fun doNavigateUp() {
        val parentPath = FileUtils.truncatePath(currentFileInfo!!.absoluteFilePath)
        navigateTo(parentPath)
    }

    interface Callback {
        fun onDirectoryLoaded(dirContent: RemoteCommand.FileInfo)
    }

    companion object {
        @JvmField
        val PREVIOUS_DIRECTORY_PATH = ".."
        @JvmField
        val KEY_DIRECTORY_CONTENT = "DIRECTORY_CONTENT"
        @JvmField
        val DEFAULT_DIRECTORY_PATH = "default_path"
    }
}
