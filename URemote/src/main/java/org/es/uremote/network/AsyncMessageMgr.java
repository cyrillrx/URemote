package org.es.uremote.network;

import android.os.AsyncTask;

import org.es.uremote.device.ServerSetting;
import org.es.uremote.exchange.ExchangeMessages.Request;
import org.es.uremote.exchange.ExchangeMessages.Response;
import org.es.uremote.exchange.ExchangeMessages.Response.ReturnCode;
import org.es.uremote.exchange.ExchangeMessagesUtils;
import org.es.utils.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
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
	private Socket mSocket;

    /**
     * @param serverSetting Server connection settings.
     */
    public AsyncMessageMgr(ServerSetting serverSetting) {
        mServerSetting = serverSetting;
    }

//    /**
//     * @param context The application context.
//     */
//    public AsyncMessageMgr(Context context) {
//        this(ServerSettingDao.loadSelected(context));
//    }

	@Override
	protected void onPreExecute() {
		try {
			sSemaphore.acquire();
		} catch (InterruptedException e) {
			Log.error(TAG, "onPreExecute Semaphore acquire error.");
		}
		Log.info(TAG, "onPreExecute Semaphore acquire. " + sSemaphore.availablePermits() + " left");
	}

	@Override
	protected Response doInBackground(Request... requests) {

		final Request request = requests[0];
		String errorMessage = "";

        if (mServerSetting == null) {
            errorMessage = "Connected device is null. Nowhere to send data";
            Log.error(TAG, errorMessage);

        } else {
            mSocket = null;
            try {
                // Socket creation
                mSocket = connectToRemoteSocket(mServerSetting);
                if (mSocket != null && mSocket.isConnected()) {
                    return ExchangeMessagesUtils.sendRequest(mSocket, request);
                }
                errorMessage = "Socket null or not connected";

            } catch (IOException e) {
                errorMessage = "IOException" + e;
                Log.error(TAG, errorMessage, e);

            } catch (Exception e) {
                errorMessage = "Exception" + e;
                Log.error(TAG, errorMessage, e);

            } finally {
                closeSocketIO();
            }
        }

		return Response.newBuilder()
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

		if (!response.getMessage().isEmpty()) {
			Log.info(TAG, "onPostExecute message : " + response.getMessage());
		} else {
			Log.info(TAG, "onPostExecute empty message.");
		}
	}

	@Override
	protected void onCancelled() {
		closeSocketIO();
		super.cancel(false);
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

	/** Close socket IO then close the socket. */
	private void closeSocketIO() {
		Log.warning(TAG, "closeSocket");
		if (mSocket == null) {
			return;
		}

		try {
			if (mSocket.getInputStream() != null) {
				mSocket.getInputStream().close();
			}
		} catch (IOException e) {}

		try {
			if (mSocket.getOutputStream() != null) {
				mSocket.getOutputStream().close();
			}
		} catch (IOException e) {}
		try {
			mSocket.close();
		} catch (IOException e) {}
	}

	/** @return The count of available permits. */
	public static int availablePermits() {
		return sSemaphore.availablePermits();
	}
}