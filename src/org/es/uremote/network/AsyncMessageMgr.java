package org.es.uremote.network;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import org.es.network.ExchangeProtos.Request;
import org.es.network.ExchangeProtos.Response;
import org.es.network.ExchangeProtos.Response.ReturnCode;
import org.es.uremote.objects.ServerSetting;
import org.es.utils.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.Semaphore;

import static org.es.uremote.utils.Constants.MESSAGE_WHAT_TOAST;

/**
 * Class that handle asynchronous messages to send to the server.
 * 
 * @author Cyril Leroux
 * 
 */
public class AsyncMessageMgr extends AsyncTask<Request, int[], Response> {
	protected static Semaphore sSemaphore = new Semaphore(2, true);
	private static final String TAG = "AsyncMessageMgr";

	private static String sSecurityToken;

	protected Handler mHandler;

	//TODO
	protected final ServerSetting mServerSetting;
	private Socket mSocket;

	/**
	 * Initialize class with the message handler as a parameter.
	 * @param handler The handler for toast messages.
	 * @param serverSetting Server connection informations.
	 */
	public AsyncMessageMgr(Handler handler, ServerSetting serverSetting) {
		mHandler = handler;
		mServerSetting = serverSetting;
	}

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

		mSocket = null;
		try {
			// Socket creation
			mSocket = connectToRemoteSocket(mServerSetting);
			if (mSocket != null && mSocket.isConnected()) {
				return MessageHelper.sendRequest(mSocket, request);
			}
			errorMessage = "Socket null or not connected";

		} catch (IOException e) {
			errorMessage = "IOException" + e;
			Log.error(TAG, errorMessage);

		} catch (Exception e) {
			errorMessage = "Exception" + e;
			Log.error(TAG, errorMessage);

		} finally {
			closeSocketIO();
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
	 * Send a toast message on the UI thread.
	 * @param toastMessage The message to display.
	 */
	protected void sendToastToUI(String toastMessage) {
		if (mHandler == null) {
			Log.error(TAG, "showToast() handler is null");
			return;
		}

		Message msg = new Message();
		msg.what = MESSAGE_WHAT_TOAST;
		msg.obj = toastMessage;
		mHandler.sendMessage(msg);
	}

	/**
	 * Creates the socket, connects it to the server then returns it.
	 * 
	 * @param server The object that holds server connection informations.
	 * @return The socket on which to send the message.
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

	/**
	 * Close socket IO then close the socket.
	 */
	private void closeSocketIO() {
		Log.warning(TAG, "closeSocket");
		if (mSocket == null) {
			return;
		}

		try {
			if (mSocket.getInputStream() != null) {
				mSocket.getInputStream().close();
			}
		} catch(IOException e) {}

		try {
			if (mSocket.getOutputStream() != null) {
				mSocket.getOutputStream().close();
			}
		} catch(IOException e) {}
		try {
			mSocket.close();
		} catch(IOException e) {}
	}

	/**
	 * @return The count of available permits.
	 */
	public static int availablePermits() {
		return sSemaphore.availablePermits();
	}

	/**
	 * Set the security token that will be use to authenticate the user.
	 * @param securityToken the security token.
	 */
	public static void setSecurityToken(final String securityToken) {
		sSecurityToken = securityToken;
	}

	/**
	 * @return the security token that will be use to authenticate the user.
	 */
	public static String getSecurityToken() {
		return sSecurityToken;
	}
}