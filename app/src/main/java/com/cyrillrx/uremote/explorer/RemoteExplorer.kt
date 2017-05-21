package com.cyrillrx.uremote.explorer

import com.cyrillrx.uremote.request.RequestSender
import com.cyrillrx.uremote.request.protobuf.RemoteCommand

/**
 * @author Cyril Leroux
 *         Created 14/05/2017.
 */
class RemoteExplorer(private val requestSender: RequestSender, callback: Explorer.Callback)
    : Explorer(callback) {

    /**
     * Ask the server to list the content of the passed directory.
     * Updates the view once the data have been received from the server.
     *
     * @param dirPath The path of the directory to display.
     */
    override fun navigateTo(dirPath: String) {
        super.navigateTo(dirPath)

        requestSender.sendRequest(RemoteCommand.Request.newBuilder()
                .setSecurityToken(requestSender.securityToken)
                .setType(RemoteCommand.Request.Type.EXPLORER)
                .setCode(RemoteCommand.Request.Code.QUERY_CHILDREN)
                .setStringExtra(dirPath)
                .build())
    }
}
