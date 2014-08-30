package org.es.uremote.common;

import org.es.uremote.computer.LoadServerActivity;
import org.es.uremote.request.FileInfoFactory;

import static org.es.uremote.request.protobuf.RemoteCommand.FileInfo;

/**
 * Local file explorer fragment.
 * This fragment allow you to browse the content of the device.
 *
 * @author Cyril Leroux
 *         Created on 31/08/13.
 */
public class LocalExplorerFragment extends AbstractExplorerFragment {

    @Override
    protected void navigateTo(String dirPath) {

        final FileInfo dirContent = FileInfoFactory.createFromLocalPath(dirPath, new String[]{".xml"}, true);
        updateView(dirContent);
    }

    @Override
    protected void onDirectoryClick(String dirPath) {
        navigateTo(dirPath);
    }

    @Override
    protected void onFileClick(String filename) {
        // Returns the value to the parent
        ((LoadServerActivity) getActivity()).onFileClick(filename);
    }
}
