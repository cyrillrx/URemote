package org.es.network;

import static org.es.network.ExchangeProtos.Request.Type.VOLUME;
import static org.es.uremote.BuildConfig.DEBUG;
import static org.es.uremote.utils.Constants.MESSAGE_WHAT_TOAST;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.Semaphore;

import org.es.network.ExchangeProtos.Request;
import org.es.network.ExchangeProtos.Request.Code;
import org.es.network.ExchangeProtos.Request.Type;
import org.es.network.ExchangeProtos.Response;
import org.es.network.ExchangeProtos.Response.ReturnCode;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Class that handle asynchronous messages to send to the server.
 * 
 * @author Cyril Leroux
 * 
 */
public class AsyncMessageMgr extends AsyncTask<Request, int[], Response> {
	protected static Semaphore sSemaphore = new Semaphore(2);
	private static final String TAG = "AsyncMessageMgr";

	private static String sHost;
	private static int sPort;
	private static int sTimeout;

	protected Handler mHandler;
	private Socket mSocket;

	/**
	 * Initialize class with the message handler as a parameter.
	 * @param _handler The handler for toast messages.
	 */
	public AsyncMessageMgr(Handler _handler) {
		mHandler = _handler;
	}

	@Override
	protected void onPreExecute() {
		try {
			sSemaphore.acquire();
		} catch (InterruptedException e) {
			if (DEBUG) {
				Log.e(TAG, "onPreExecute Semaphore acquire error.");
			}
		}
		if (DEBUG) {
			Log.i(TAG, "onPreExecute Semaphore acquire. " + sSemaphore.availablePermits() + " left");
		}
	}

	@Override
	protected Response doInBackground(Request... _requests) {

		final Request request = _requests[0];
		String errorMessage = "";

		mSocket = null;
		try {
			// Socket creation
			mSocket = connectToRemoteSocket(sHost, sPort, sTimeout);
			if (mSocket != null && mSocket.isConnected()) {
				return sendAndReceive(mSocket, request);
			}
			errorMessage = "Socket null or not connected";

		} catch (IOException e) {
			errorMessage = "IOException" + e.getMessage();
			if (DEBUG) {
				Log.e(TAG, errorMessage);
			}

		} catch (Exception e) {
			errorMessage = "Exception" + e.getMessage();
			if (DEBUG) {
				Log.e(TAG, errorMessage);
			}

		} finally {
			closeSocketIO();
		}

		return Response.newBuilder()
		.setReturnCode(ReturnCode.RC_ERROR)
		.setMessage(errorMessage)
		.build();
	}

	// TODO fr to en
	/**
	 * Cette fonction est exécutée après l'appel à {@link #doInBackground(String...)}
	 * Exécutée dans le thread principal.
	 * It releases the semaphore acquired in OnPreExecute method.
	 * @param _serverReply La réponse du serveur renvoyée par la fonction {@link #doInBackground(String...)}.
	 */
	@Override
	protected void onPostExecute(Response _response) {
		if (DEBUG) {
			Log.i(TAG, "Got a reply : " + _response.toString());
		}
		sSemaphore.release();
		if (DEBUG) {
			Log.i(TAG, "Semaphore release");
		}

		if (VOLUME.equals(_response.getRequest().getType())) {
			showToast(_response.getMessage());
		}
	}

	@Override
	protected void onCancelled() {
		closeSocketIO();
		super.onCancelled();
	}

	/**
	 * Send a toast message on the UI thread.
	 * @param _toastMessage The message to display.
	 */
	protected void showToast(String _toastMessage) {
		if (mHandler == null) {
			if (DEBUG) {
				Log.i(TAG, "showToast() handler is null");
			}
			return;
		}

		Message msg = new Message();
		msg.what = MESSAGE_WHAT_TOAST;
		msg.obj = _toastMessage;
		mHandler.sendMessage(msg);
	}

	/**
	 * Creates the socket, connects it to the server then returns it.
	 * 
	 * @param _host The remote server IP address.
	 * @param _port The port on which we want to establish a connection with the remote server.
	 * @param _timeout The timeout of the connection with the server (in milliseconds).
	 * @return The socket on which to send the message.
	 * @throws IOException exception
	 */
	private Socket connectToRemoteSocket(String _host, int _port, int _timeout) throws IOException {

		final SocketAddress socketAddress = new InetSocketAddress(_host, _port);
		Socket socket = new Socket();
		socket.connect(socketAddress, _timeout);

		return socket;
	}

	/**
	 * Called from the UI Thread.
	 * Send a message through a Socket to a server and get the reply.
	 * 
	 * @param _socket The socket on which to send the message.
	 * @param _req Client request.
	 * @return The server reply.
	 * @throws IOException exception.
	 */
	private Response sendAndReceive(Socket _socket, Request _req) throws IOException {
		if (DEBUG) {
			Log.i(TAG, "sendMessage: " + _req.toString());
		}
		Response reply = null;
		if (_socket.isConnected()) {
			_socket.getOutputStream().write(_req.toByteArray());
			_socket.getOutputStream().flush();
			_socket.shutdownOutput();

			reply = Response.parseFrom(_socket.getInputStream());
		}
		return reply;
	}

	/**
	 * Close socket IO then close the socket.
	 */
	private void closeSocketIO() {
		if (mSocket == null) {
			return;
		}

		try { if (mSocket.getInputStream() != null) {
			mSocket.getInputStream().close();
		}	} catch(IOException e) {}
		try { if (mSocket.getOutputStream() != null) {
			mSocket.getOutputStream().close();
		}	} catch(IOException e) {}
		try { mSocket.close(); } catch(IOException e) {}
	}

	/**
	 * @return The count of available permits.
	 */
	public static int availablePermits() {
		return sSemaphore.availablePermits();
	}

	/**
	 * @return Concatenation of host and port of the remote server.
	 */
	public static String getServerInfos() {
		return sHost + ":" + sPort;
	}

	/**
	 * Set the IP address of the remote server.
	 * @param _host the IP address of the remote server.
	 */
	public static void setHost(String _host) {
		sHost = _host;
	}

	/**
	 * Set the port on which we want to establish a connection with the remote server.
	 * @param _port the port value.
	 */
	public static void setPort(int _port) {
		sPort = _port;
	}

	/**
	 * Define the timeout of the connection with the server.
	 * @param _timeout The timeout in milliseconds.
	 */
	public static void setTimeout(int _timeout) {
		sTimeout = _timeout;
	}

	/**
	 * Build a request.
	 * @param _type The request type.
	 * @param _code The request code.
	 * @param _stringParam A string parameter.
	 * @return The request if it had been initialized. Return null otherwise.
	 */
	public static Request buildRequest(Type _type, Code _code, String _stringParam) {
		Request request = Request.newBuilder()
		.setType(_type)
		.setCode(_code)
		.setStringParam(_stringParam)
		.build();

		if (request.isInitialized()) {
			return request;
		}
		return null;
	}

	/**
	 * Build a request.
	 * @param _type The request type.
	 * @param _code The request code.
	 * @return The request if it had been initialized. Return null otherwise.
	 */
	public static Request buildRequest(Type _type, Code _code) {
		return buildRequest(_type, _code, "");
	}
}