package org.es.uremote.network;

import android.os.AsyncTask;

import org.es.uremote.device.ServerSetting;
import org.es.uremote.exchange.Message.Request;
import org.es.uremote.exchange.Message.Response;
import org.es.uremote.exchange.Message.Response.ReturnCode;
import org.es.uremote.exchange.MessageUtils;
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
 * Created before first commit (08/04/12).
 */
public class AsyncMessageMgr extends AsyncTask<Request, int[], Response> {

	protected static Semaphore sSemaphore = new Semaphore(2, true);
	private static final String TAG = "AsyncMessageMgr";

	protected final ServerSetting mServerSetting;

    /**
     * @param serverSetting Server connection settings.
     */
    public AsyncMessageMgr(ServerSetting serverSetting) {
        mServerSetting = serverSetting;
    }

	@Override
	protected void onPreExecute() {
		try {
			sSemaphore.acquire();
		} catch (InterruptedException e) {
			Log.error(TAG, "#onPreExecute - Semaphore acquire error.");
		}
		Log.info(TAG, "#onPreExecute - Semaphore acquire. " + sSemaphore.availablePermits() + " left");
	}

	@Override
	protected Response doInBackground(Request... requests) {

		final Request request = requests[0];
		String errorMessage = "";

        if (mServerSetting == null) {
            errorMessage = "Connected device is null. Nowhere to send data";

        } else {
            Socket socket = null;
            try {
                socket = connectToRemoteSocket(mServerSetting);
                if (socket != null && socket.isConnected()) {
                    return MessageUtils.sendRequest(socket, request);
                }
                errorMessage = "Socket null or not connected";

            } catch (SocketTimeoutException e) {
                errorMessage = "Can't reach remote device (bad config or server down).";
                Log.warning(TAG, "Can't reach remote device (" + e.getMessage() + ")");

            } catch (Exception e) {
                errorMessage = "Exception - " + e.getMessage();

            } finally {
                closeSocket(socket);
            }
        }

		return Response.newBuilder()
				.setRequestType(request.getType())
				.setRequestCode(request.getCode())
				.setReturnCode(ReturnCode.RC_ERROR)
				.setMessage(errorMessage)
				.build();
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
	}

	/**
	 * Creates the socket, connects it to the server then returns it.
	 *
	 * @param server The object that holds server connection settings.
	 * @return The socket on which to send the message.
	 *
	 * @throws IOException exception
	 */
	private Socket connectToRemoteSocket(ServerSetting server) throws IOException {

		final String host	= server.getLocalHost();
		final int port		= server.getLocalPort();
		final SocketAddress socketAddress = new InetSocketAddress(host, port);

		Socket socket = new Socket();
		socket.setSoTimeout(server.getReadTimeout());
		socket.connect(socketAddress, server.getConnectionTimeout());

		return socket;
	}

	/** Close socket IOs then close the socket. */
	private void closeSocket(Socket socket) {
        if (socket == null) {
            Log.warning(TAG, "#closeSocketIO - Socket is null.");
            return;
        }

        try {
            socket.shutdownInput();
        } catch (IOException e) {
            Log.warning(TAG, "#closeSocketIO - On socket.shutdownInput() : " + e);
        }
        try {
            socket.shutdownOutput();
        } catch (IOException e) {
            Log.warning(TAG, "#closeSocketIO - On socket.shutdownOutput() : " + e);
        }
        try {
            socket.close();
        } catch (IOException e) {
            Log.warning(TAG, "#closeSocketIO - On socket.close() : " + e);
        }
	}

	/** @return The count of available permits. */
	public static int availablePermits() {
		return sSemaphore.availablePermits();
	}
}