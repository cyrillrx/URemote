package com.cyrillrx.uremote.explorer;

import com.cyrillrx.notifier.Toaster;
import com.cyrillrx.uremote.R;
import com.cyrillrx.uremote.request.FileInfoFactory;
import com.cyrillrx.uremote.request.protobuf.RemoteCommand;

/**
 * @author Cyril Leroux
 *         Created 14/05/2017.
 */
public class LocalExplorer extends Explorer {

    public LocalExplorer(Callback callback) { super(callback); }

    @Override
    public void navigateTo(String dirPath) {
        super.navigateTo(dirPath);

        if (dirPath == null) {
            Toaster.toast(R.string.msg_no_path_defined);
            return;
        }

        currentFileInfo = FileInfoFactory.createFromLocalPath(dirPath, null, true);
        callback.onDirectoryLoaded(currentFileInfo);
    }
}
