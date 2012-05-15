package org.es.uremote.network;

import static org.es.uremote.utils.Constants.DEBUG;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.Semaphore;

import org.es.uremote.utils.ServerMessage;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Classe asynchrone de gestion d'envoi de command avec paramètres au serveur.
 * @author cyril.leroux
 */
public class AsyncMessageMgr extends AsyncTask<String, int[], String> {
	protected static Semaphore sSemaphore = new Semaphore(2);
	private static final String TAG = "AsyncMessageMgr";

	private static String sHost;
	private static int sPort;
	private static int sTimeout;
	
	protected String mReturnCode = ServerMessage.RC_SUCCES;
	protected String mCommand;
	protected String mParam;
	private Socket mSocket	= null;
	
	/**
	 * Cette fonction est exécutée avant l'appel à {@link #doInBackground(String...)} 
	 * Exécutée dans le thread principal.
	 */
	@Override
	protected void onPreExecute() {
		try {
			sSemaphore.acquire();
		} catch (InterruptedException e) {
			if (DEBUG)
				Log.e(TAG, "onPreExecute Semaphore acquire error.");
		}
		if (DEBUG)
			Log.i(TAG, "onPreExecute Semaphore acquire. " + sSemaphore.availablePermits() + " left");
	}

	/**
	 * Cette fonction est exécutée sur un thread différent du thread principal
	 * @param _params le tableau de string contenant les paramètres (commande et paramètre de la commande).
	 * @return La réponse du serveur.
	 */
	@Override
	protected String doInBackground(String... _params) {
		String serverReply = "";
		
		mCommand	= _params[0];
		mParam 		= (_params.length > 1) ? _params[1] : "";
		final String message	= mCommand + "|" + mParam;

		mSocket = null;
		try {
			// Création du socket
			mSocket = connectToRemoteSocket(sHost, sPort, sTimeout, message);
			if (mSocket != null && mSocket.isConnected())
				serverReply = sendAsyncMessage(mSocket, message);
			
		} catch (IOException e) {
			mReturnCode = ServerMessage.RC_ERROR;
			serverReply = "IOException" + e.getMessage();
			if (DEBUG)
				Log.e(TAG, serverReply);

		} catch (Exception e) {
			mCommand = ServerMessage.RC_ERROR;
			serverReply = "IOException" + e.getMessage();
			if (DEBUG) 
				Log.e(TAG, serverReply);

		} finally {
			closeSocketIO();
		}

		return serverReply;
	}

	/**
	 * Cette fonction est exécutée après l'appel à {@link #doInBackground(String...)} 
	 * Exécutée dans le thread principal.
	 * @param _serverReply La réponse du serveur renvoyée par la fonction {@link #doInBackground(String...)}.
	 */
	@Override
	protected void onPostExecute(String _serverReply) {
		if (DEBUG) {
			Log.i(TAG, "Got a reply : " + _serverReply);
			Log.i(TAG, "mCommand  : " + mCommand);
			Log.i(TAG, "mParam  : " + mParam);
		}
		sSemaphore.release();
		if (DEBUG) {
			Log.i(TAG, "Semaphore release");
		}
	}

	@Override
	protected void onCancelled() {
		closeSocketIO();
		super.onCancelled();
	}
	
	/**
	 * Fonction de connexion à un socket disant.
	 * @param _host L'adresse ip de l'hôte auquel est lié le socket.
	 * @param _port Le numéro de port de l'hôte auquel est lié le socket.
	 * @param _message Le message à envoyer.
	 * @return true si la connexion s'est effectuée correctement, false dans les autres cas.
	 * @throws IOException excteption
	 */
	private Socket connectToRemoteSocket(String _host, int _port, int _timeout, String _message) throws IOException {

		final SocketAddress socketAddress = new InetSocketAddress(_host, _port);
		Socket socket = new Socket();
		socket.connect(socketAddress, _timeout);

		return socket;
	}

	/**
	 * Cette fonction est appelée depuis le thread principal
	 * Elle permet l'envoi d'une commande et d'un paramètre 
	 * @param _socket Le socket sur lequel on envoie le message.
	 * @param _message Le message à transmettre
	 * @return La réponse du serveur.
	 * @throws IOException exception.
	 */
	private String sendAsyncMessage(Socket _socket, String _message) throws IOException {
		if (DEBUG)
			Log.i(TAG, "sendMessage: " + _message);
		String serverReply = "";

		if (mSocket.isConnected()) {
			_socket.getOutputStream().write(_message.getBytes());
			_socket.getOutputStream().flush();
			_socket.shutdownOutput();
			serverReply = getServerReply(_socket);
		}
		return serverReply;
	}

	/**
	 * @param _socket Le socket auquel le message a été envoyé.
	 * @return La réponse du serveur.
	 * @throws IOException exeption
	 */
	private String getServerReply(Socket _socket) throws IOException {
		final int BUFSIZ = 512; 

		final BufferedReader bufferReader = new BufferedReader(new InputStreamReader(_socket.getInputStream()), BUFSIZ);
		String line = "", reply = "";
		while ((line = bufferReader.readLine()) != null)
			reply += line;

		if (DEBUG)
			Log.i(TAG, "Got a reply : " + reply);

		return reply;
	} 
	
	/**
	 * Ferme les entrées/sortie du socket puis ferme le socket.
	 */
	private void closeSocketIO() {
		if (mSocket == null)
			return;

		try { if (mSocket.getInputStream() != null)	mSocket.getInputStream().close();	} catch(IOException e) {}
		try { if (mSocket.getOutputStream() != null)mSocket.getOutputStream().close();	} catch(IOException e) {}
		try { mSocket.close(); } catch(IOException e) {}
	}
	
	public static int availablePermits() {
		return sSemaphore.availablePermits();
	}
	
	public static String getServerInfos() {
		return sHost + ":" + sPort;
	}
	
	public static void setHost(String _host) {
		sHost = _host;
	}
	
	public static void setPort(int _port) {
		sPort = _port;
	}
	
	public static void setTimeout(int _timeout) {
		sTimeout = _timeout;
	}
	
}