package com.cyrillrx.uremote.explorer;

import com.cyrillrx.notifier.Toaster;
import com.cyrillrx.uremote.R;
import com.cyrillrx.uremote.request.RequestSender;
import com.cyrillrx.uremote.request.protobuf.RemoteCommand;

/**
 * @author Cyril Leroux
 *         Created 14/05/2017.
 */
public class RemoteExplorer extends Explorer {

    private RequestSender requestSender;

    public RemoteExplorer(RequestSender requestSender, Callback callback) {
        super(callback);
        this.requestSender = requestSender;
    }

    /**
     * Ask the server to list the content of the passed directory.
     * Updates the view once the data have been received from the server.
     *
     * @param dirPath The path of the directory to display.
     */
    @Override
    public void navigateTo(String dirPath) {
        super.navigateTo(dirPath);

        if (dirPath == null) {
            Toaster.toast(R.string.msg_no_path_defined);
            return;
        }

        requestSender.sendRequest(RemoteCommand.Request.newBuilder()
                .setSecurityToken(requestSender.getSecurityToken())
                .setType(RemoteCommand.Request.Type.EXPLORER)
                .setCode(RemoteCommand.Request.Code.QUERY_CHILDREN)
                .setStringExtra(dirPath)
                .build());
    }
}
