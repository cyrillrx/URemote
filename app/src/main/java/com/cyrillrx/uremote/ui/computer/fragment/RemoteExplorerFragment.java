package com.cyrillrx.uremote.ui.computer.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.cyrillrx.uremote.BuildConfig;
import com.cyrillrx.uremote.R;
import com.cyrillrx.uremote.common.device.NetworkDevice;
import com.cyrillrx.uremote.explorer.AbstractExplorerFragment;
import com.cyrillrx.uremote.network.AsyncMessageMgr;
import com.cyrillrx.uremote.request.RequestSender;
import com.cyrillrx.uremote.request.protobuf.RemoteCommand.Request;
import com.cyrillrx.uremote.request.protobuf.RemoteCommand.Request.Code;
import com.cyrillrx.uremote.request.protobuf.RemoteCommand.Request.Type;
import com.cyrillrx.uremote.request.protobuf.RemoteCommand.Response;
import com.cyrillrx.uremote.utils.TaskCallbacks;
import com.cyrillrx.uremote.utils.ToastSender;

import static com.cyrillrx.uremote.request.protobuf.RemoteCommand.Response.ReturnCode.RC_ERROR;

/**
 * Remote file explorer fragment.
 * This fragment allow you to browse the content of a remote device.
 *
 * @author Cyril Leroux
 *         Created on 21/04/12.
 */
public class RemoteExplorerFragment extends AbstractExplorerFragment implements RequestSender {

    private TaskCallbacks callbacks;
    private RequestSender requestSender;
    private ToastSender toastSender;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callbacks = (TaskCallbacks) activity;
        requestSender = (RequestSender) activity;
        toastSender = (ToastSender) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);
    }

    /**
     * Set the callback to null so we don't accidentally leak the
     * Activity instance.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
        requestSender = null;
        toastSender = null;
    }

    @Override
    protected void retry() { navigateTo(path); }

    @Override
    protected void onDirectoryClick(String dirPath) { navigateTo(dirPath); }

    @Override
    protected void onFileClick(String filename) {

        try {
            sendRequest(Request.newBuilder()
                    .setSecurityToken(getSecurityToken())
                    .setType(Type.EXPLORER)
                    .setCode(Code.OPEN_SERVER_SIDE)
                    .setStringExtra(filename)
                    .build());

        } catch (Exception e) {
            toastSender.sendToast(R.string.build_request_error);
        }
    }

    /**
     * Ask the server to list the content of the passed directory.
     * Updates the view once the data have been received from the server.
     *
     * @param dirPath The path of the directory to display.
     */
    @Override
    protected void navigateTo(String dirPath) {
        if (dirPath != null) {
            sendRequest(Request.newBuilder()
                    .setSecurityToken(getSecurityToken())
                    .setType(Type.EXPLORER)
                    .setCode(Code.QUERY_CHILDREN)
                    .setStringExtra(dirPath)
                    .build());
        } else {
            toastSender.sendToast(R.string.msg_no_path_defined);
        }
    }

    //
    // Message Sender
    //

    /**
     * Initializes the message handler then send the request.
     *
     * @param request The request to send.
     */
    @Override
    public void sendRequest(Request request) {
        if (ExplorerMessageMgr.availablePermits() > 0) {
            new ExplorerMessageMgr().execute(request);
        } else {
            toastSender.sendToast(R.string.msg_no_more_permit);
        }
    }

    @Override
    public NetworkDevice getDevice() { return requestSender.getDevice(); }

    @Override
    public String getSecurityToken() { return requestSender.getSecurityToken(); }

    /**
     * Class that handle asynchronous requests sent to a remote server.
     *
     * @author Cyril Leroux
     */
    private class ExplorerMessageMgr extends AsyncMessageMgr {

        public ExplorerMessageMgr() { super(getDevice(), callbacks); }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);

            if (response == null) {
                return;
            }

            // Display the message if any
            if (!response.getMessage().isEmpty() && BuildConfig.DEBUG) {
                toastSender.sendToast(response.getMessage());
            }

            // Update view in case of empty list (error or empty directory)
            if (RC_ERROR.equals(response.getReturnCode())) {
                tvEmpty.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
                return;
            }

            errorLayout.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);

            updateView(response.getFile());
        }
    }
}
