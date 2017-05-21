package com.cyrillrx.uremote.explorer

import com.cyrillrx.uremote.request.FileInfoFactory

/**
 * @author Cyril Leroux
 *         Created 14/05/2017.
 */
class LocalExplorer(callback: Explorer.Callback) : Explorer(callback) {

    override fun navigateTo(dirPath: String) {
        super.navigateTo(dirPath)

        currentFileInfo = FileInfoFactory.createFromLocalPath(dirPath, null, true)
        callback.onDirectoryLoaded(currentFileInfo!!)
    }
}
