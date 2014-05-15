package org.es.uremote.network;

import android.os.AsyncTask;

import org.es.uremote.device.ServerSetting;
import org.es.uremote.exchange.Message.Request;
import org.es.uremote.exchange.Message.Response;
import org.es.uremote.exchange.Message.Response.ReturnCode;
import org.es.uremote.exchange.MessageUtils;
import org.es.uremote.utils.TaskCallbacks;
import org.es.utils.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.concurrent.Semaphore;

/**
 * Class that handle asynchronous messages to send to the server.
 *
 * @author Cyril Leroux
 *         Created before first commit (08/04/12).
 */
public class AsyncMessageMgr extends AsyncTask<Request, int[], Response> {

    protected static Semaphore sSemaphore = new Semaphore(2, true);
    private static final String TAG = "AsyncMessageMgr";

    protected final ServerSetting mRemoteDevice;
    private final TaskCallbacks mTaskCallbacks;

    /**
     * @param device The device towards which to send the request.
     * @param taskCallbacks An object to call back during the task lifecycle
     */
    public AsyncMessageMgr(ServerSetting device, TaskCallbacks taskCallbacks) {
        mRemoteDevice = device;
        mTaskCallbacks = taskCallbacks;
    }

    @Override
    protected void onPreExecute() {
        try {
            sSemaphore.acquire();
            Log.info(TAG, "#onPreExecute - Semaphore acquire. " + sSemaphore.availablePermits() + " left.");
        } catch (InterruptedException e) {
            Log.error(TAG, "#onPreExecute - Semaphore acquire error.");
        }

        if (mTaskCallbacks != null) {
            mTaskCallbacks.onPreExecute();
        }
    }

    @Override
    protected Response doInBackground(Request... requests) {
        return MessageUtils.sendRequest(requests[0], mRemoteDevice);
    }

    /**
     * Runs on the UI thread after {@link #doInBackground(Request...)}.
     * The specified result is the value returned by {@link #doInBackground(Request...)}.
     * This method won't be invoked if the task was canceled.
     * It releases the semaphore acquired in OnPreExecute method.
     *
     * @param response The response from the server returned by {@link #doInBackground(Request...)}.
     */
    @Override
    protected void onPostExecute(Response response) {
        sSemaphore.release();
        Log.info(TAG, "Semaphore release");

        if (response == null) {
            Log.error(TAG, "#onPostExecute - Response is null.");
            return;
        }

        if (!response.getMessage().isEmpty()) {
            Log.info(TAG, "#onPostExecute - message : " + response.getMessage());
        } else {
            Log.info(TAG, "#onPostExecute - empty message.");
        }

        if (mTaskCallbacks != null) {
            mTaskCallbacks.onPostExecute(response);
        }
    }

    @Override
    protected void onProgressUpdate(int[]... values) {
        if (mTaskCallbacks != null) {
            mTaskCallbacks.onProgressUpdate(values[0][0]);
        }
    }

    @Override
    protected void onCancelled(Response response) {
        if (mTaskCallbacks != null) {
            mTaskCallbacks.onCancelled(response);
        }
    }

    /**
     * @return The count of available permits.
     */
    public static int availablePermits() {
        return sSemaphore.availablePermits();
    }
}