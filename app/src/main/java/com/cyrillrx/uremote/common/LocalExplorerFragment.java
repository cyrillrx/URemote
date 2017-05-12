package com.cyrillrx.uremote.common;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.cyrillrx.android.utils.PermissionUtils;
import com.cyrillrx.uremote.request.FileInfoFactory;
import com.cyrillrx.uremote.ui.computer.LoadServerActivity;

import static com.cyrillrx.uremote.request.protobuf.RemoteCommand.FileInfo;

/**
 * Local file explorer fragment.
 * This fragment allow you to browse the content of the device.
 *
 * @author Cyril Leroux
 *         Created on 31/08/13.
 */
public class LocalExplorerFragment extends AbstractExplorerFragment {

    private static final int RC_READ = 0;
    private static final int RC_WRITE = 1;

    private static final String[] FILE_FILTER = new String[]{".*"};
//    private static final String[] FILE_FILTER = new String[]{".xml"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final FragmentActivity activity = getActivity();
        if (!PermissionUtils.hasReadPermission(activity)) {
            PermissionUtils.requestReadPermission(activity, RC_READ);
            activity.finish();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        final FragmentActivity activity = getActivity();

        if (!PermissionUtils.hasReadPermission(activity)) {
            PermissionUtils.requestReadPermission(activity, RC_READ);
            activity.finish();
            return;
        }

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void navigateTo(String dirPath) {

        final FileInfo dirContent = FileInfoFactory.createFromLocalPath(dirPath, null, true);
        updateView(dirContent);
    }

    @Override
    protected void onDirectoryClick(String dirPath) { navigateTo(dirPath); }

    @Override
    protected void onFileClick(String filename) {
        // Returns the value to the parent
        ((LoadServerActivity) getActivity()).onFileClick(filename);
    }
}
