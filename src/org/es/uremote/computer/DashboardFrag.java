package org.es.uremote.computer;


import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static org.es.uremote.utils.Constants.DEBUG;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import org.es.uremote.R;
import org.es.uremote.utils.Message;
import org.es.utils.ConnectionUtils;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Cyril Leroux
 * 
 * Classe permettant de se connecter et d'envoyer des commandes à un serveur distant via une AsyncTask.
 *
 */
public class DashboardFrag extends Fragment implements OnClickListener {
	private static final String TAG = "ServerDashboard";

	// Liste des RequestCodes pour les ActivityForResults
	private static final int RC_APP_LAUNCHER	= 0;
	
	private static final int PORT = 8082;
	//private static final String HOST = "192.168.0.1";
	private static final String PARTIAL_HOST = "192.168.0.";
	private String mCurrentHost;
	//private boolean mConnected = false; 
	private ImageButton mCmdMute;

	private AsyncMessageMgr mMessageManager = null;
	private TextView mTvServerState;
	private ProgressBar mPbConnection;

	/** 
	 * Cette fonction est appelée lors de la création de l'activité
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.server_dashboard_frag, container, false);
		
		mTvServerState = (TextView) view.findViewById(R.id.tvServerState);
		//mIvServerState = (ImageView) view.findViewById(R.id.ivServerState);
		mPbConnection = (ProgressBar) view.findViewById(R.id.pbConnection);


		((ImageButton) view.findViewById(R.id.cmdTest)).setOnClickListener(this);
		((ImageButton) view.findViewById(R.id.cmdKillServer)).setOnClickListener(this);
		
		((Button) view.findViewById(R.id.cmdAltF4)).setOnClickListener(this);
		((ImageButton) view.findViewById(R.id.cmdSwitch)).setOnClickListener(this);
		((Button) view.findViewById(R.id.cmdSpace)).setOnClickListener(this);
		((Button) view.findViewById(R.id.cmdEnter)).setOnClickListener(this);
		((ImageButton) view.findViewById(R.id.cmdGomStretch)).setOnClickListener(this);
		
		((ImageButton) view.findViewById(R.id.btnAppLauncher)).setOnClickListener(this);

		((ImageButton) view.findViewById(R.id.cmdPrevious)).setOnClickListener(this);
		((ImageButton) view.findViewById(R.id.cmdPlayPause)).setOnClickListener(this);
		((ImageButton) view.findViewById(R.id.cmdStop)).setOnClickListener(this);
		((ImageButton) view.findViewById(R.id.cmdNext)).setOnClickListener(this);

		mCmdMute = (ImageButton) view.findViewById(R.id.cmdMute);
		mCmdMute.setOnClickListener(this);

		// Affichage d'un message si on a pas l'accès au serveur
		ConnectivityManager connectMgr = ConnectionUtils.getConnectivityManager(getActivity().getApplicationContext());

		for (int i = 1; i < 256; i++) {
			mCurrentHost = PARTIAL_HOST + i;
			//			if (mConnected) {
			if (ConnectionUtils.canAccessHost(connectMgr, mCurrentHost)) {
				Toast.makeText(getActivity().getApplicationContext(), "Got an acces to the host " + mCurrentHost, Toast.LENGTH_SHORT).show();
				//sendAsyncMessage(Messages.HELLO_SERVER);
				break;
			}
		}

		final String serverInfos = "Host : " + mCurrentHost + ":" + PORT;
		((TextView) view.findViewById(R.id.tvServerInfos)).setText(serverInfos);
		
		return view;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopMessageManager();
	}

	/**
	 * Prise en comptes des événements onClick 
	 */
	@Override
	public void onClick(View _v) {

		switch (_v.getId()) {

		case R.id.cmdKillServer :
			askToKillServer();
			break;
			
		case R.id.cmdTest :
			sendAsyncMessage(Message.TEST_COMMAND);
			break;
			
		case R.id.cmdAltF4 :
			sendAsyncMessage(Message.KEYBOARD_ALTF4);
			break;
			
		case R.id.cmdSwitch :
			sendAsyncMessage(Message.MONITOR_SWITCH_WINDOW);
			break;

		case R.id.cmdSpace :
			sendAsyncMessage(Message.KEYBOARD_SPACE);
			break;

		case R.id.cmdEnter :
			sendAsyncMessage(Message.KEYBOARD_ENTER);
			break;

		case R.id.btnAppLauncher :
			startActivityForResult(new Intent(getActivity().getApplicationContext(), AppLauncher.class), RC_APP_LAUNCHER);
			break;

		case R.id.btnFileManager :
			startActivity(new Intent(getActivity().getApplicationContext(), FileManager.class));
			break;
			
		case R.id.cmdGomStretch :
			sendAsyncMessage(Message.GOM_PLAYER_STRETCH);
			break;

		case R.id.cmdPrevious :
			sendAsyncMessage(Message.MEDIA_PREVIOUS);
			break;

		case R.id.cmdPlayPause :
			sendAsyncMessage(Message.MEDIA_PLAY_PAUSE);
			break;

		case R.id.cmdStop :
			sendAsyncMessage(Message.MEDIA_STOP);
			break;

		case R.id.cmdNext :
			sendAsyncMessage(Message.MEDIA_NEXT);
			break;

		case R.id.cmdMute :
			sendAsyncMessage(Message.VOLUME_MUTE);
			break;
		default:
			break;
		}
	}
	
	/** Demande une confirmation à l'utilisateur avant de tuer le serveur */
    private void askToKillServer() {
    	final String message = getString(R.string.confirm_kill_server);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity().getApplicationContext());
		builder.setIcon(android.R.drawable.ic_menu_more);
		builder.setMessage(message);
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				// Envoi du message si l'utilisateur confirme
				sendAsyncMessage(Message.KILL_SERVER);
			}
		});
		
		builder.setNegativeButton(android.R.string.cancel, null);
		builder.show();
    }
	//-------------------------------------------------
    
    
	/**
	 * Fonction de mise à jour de l'interface utilisateur
	 * Connexion/Commande valide
	 */
	private void updateConnectionStateOK() {
		final Drawable imgLeft = getResources().getDrawable(android.R.drawable.presence_online);
		imgLeft.setBounds(0, 0, 24, 24);
		mTvServerState.setCompoundDrawables(imgLeft, null, null, null);
		mTvServerState.setText(R.string.msg_command_succeeded);
		mPbConnection.setVisibility(INVISIBLE);
	}

	/**
	 * Fonction de mise à jour de l'interface utilisateur
	 * Connexion/Commande en erreur
	 */
	private void updateConnectionStateKO() {
		final Drawable imgLeft = getResources().getDrawable(android.R.drawable.presence_offline);
		imgLeft.setBounds(0, 0, 24, 24);
		mTvServerState.setCompoundDrawables(imgLeft, null, null, null);
		mTvServerState.setText(R.string.msg_command_failed);
		mPbConnection.setVisibility(INVISIBLE);
	}

	/**
	 * Fonction de mise à jour de l'interface utilisateur
	 * Connexion/Commande en cours
	 */
	private void updateConnectionStateConnecting() {
		final Drawable imgLeft = getResources().getDrawable(android.R.drawable.presence_away);
		imgLeft.setBounds(0, 0, 24, 24);
		mTvServerState.setCompoundDrawables(imgLeft, null, null, null);
		mTvServerState.setText(R.string.msg_command_running);
		mPbConnection.setVisibility(VISIBLE);
	}

	/**
	 * Cette fonction initialise le composant gérant l'envoi des messages 
	 * puis envoie le message passé en paramètre.
	 * @param _message Le message à envoyer.
	 */
	private void sendAsyncMessage(String _message) {

		if (mMessageManager == null) {
			mMessageManager = new AsyncMessageMgr();
			mMessageManager.execute(_message);
		} else {
			Toast.makeText(getActivity().getApplicationContext(), "Already initialized", Toast.LENGTH_SHORT).show();
		}

	}

	private void stopMessageManager() {
		if (mMessageManager != null) {
			mMessageManager.closeSocketIO();
			mMessageManager = null;
		}
	}

	/**
	 * Classe asynchrone de gestion d'envoi des messages
	 * @author cyril.leroux
	 */
	private class AsyncMessageMgr extends AsyncTask<String, byte[], Boolean> {
		private static final int CONNECTION_TIMEOUT = 500;

		//		private int mConnectionState;
		private Socket mSocket		= null;
		
		private OutputStream mOut	= null;
		private InputStream mIn		= null;
		private String mReply		= null;

		/**
		 * Cette fonction est exécutée avant l'appel à {@link #doInBackground(String...)} 
		 * Exécutée dans le thread principal.
		 */
		@Override
		protected void onPreExecute() {
			Log.i(TAG, "onPreExecute");
			updateConnectionStateConnecting();
		}

		/**
		 * Cette fonction est exécutée sur un thread différent du thread principal
		 */
		@Override
		protected Boolean doInBackground(String... params) {

			// Création du socket
			mSocket = new Socket();
			final SocketAddress socketAddress = new InetSocketAddress(mCurrentHost, PORT);
			return connectToRemoteSocket(socketAddress, params);
		}
		
		/**
		 * Cette fonction est exécutée après l'appel à {@link #doInBackground(String...)} 
		 * Exécutée dans le thread principal.
		 * @param _result Le résultat de la fonction {@link #doInBackground(String...)}.
		 */
		@Override
		protected void onPostExecute(Boolean _result) {
			//			// Si le serveur n'est pas connecté et que l'on a une réponse au hello server, on passe à l'état connecté
			//			if (!mConnected && Messages.HELLO_CLIENT.equals(mReply))
			//				mConnected = true;

			if (mReply != null && !mReply.isEmpty()) {
				Toast.makeText(getActivity().getApplicationContext(), mReply, Toast.LENGTH_SHORT).show();
				if (mReply.equals(Message.REPLY_VOLUME_MUTED)) {
					mCmdMute.setImageResource(R.drawable.volume_muted);
				} else if (mReply.equals(Message.REPLY_VOLUME_ON)) {
					mCmdMute.setImageResource(R.drawable.volume_on);
				}
			}
			stopMessageManager();

			if (_result) {
				updateConnectionStateOK();
			} else {
				updateConnectionStateKO();
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			closeSocketIO();
		}

		private void closeSocketIO() {
			try { if (mIn != null)	mIn.close();	} catch(IOException e) {}
			try { if (mOut != null)	mOut.close();	} catch(IOException e) {}
			try { mSocket.close(); } catch(IOException e) {}
		}

		/**
		 * Fonction de connexion à un socket disant.
		 * @param _socketAddress L'adresse du socket distant.
		 * @param _params Le message à envoyer
		 * @return true si la connexion s'est effectuée correctement, false dans les autres cas.
		 */
		private boolean connectToRemoteSocket(SocketAddress _socketAddress, String[] _params) {
			boolean result = false;

			if (DEBUG) {
				for (String param : _params)
					Log.d(TAG, "Param : " + param);
			}
			
			try {
				mSocket.connect(_socketAddress, CONNECTION_TIMEOUT);
				if (mSocket.isConnected()) {
					mIn = mSocket.getInputStream();
					mOut = mSocket.getOutputStream();
					
					sendMessage(_params[0]);
					getServerReply();

					result = true;
				} 
			} catch (IOException e) {
				if (DEBUG)
					Log.e(TAG, "doInBackground: Try to connect\r\n" + e.getMessage().toString());
			}  catch (Exception e) {
				if (DEBUG)
					Log.e(TAG, "doInBackground: Try to connect\r\n" + e.getMessage().toString());
			} finally {
				//closeAll();
				if (DEBUG)
					Log.i(TAG, "doInBackground: Finished");
			}

			return result;
		}


		/**
		 * Cette fonction est appelée depuis le thread principal
		 * Elle permet l'envoi d'un message textuel au serveur
		 * 
		 * @param _message Le message à transmettre
		 * @param _context Le contexte de l'application s'exécutant sur le thread principal
		 */
		private void sendMessage(String _message) {
			if (DEBUG)
				Log.i(TAG, "sendMessage: " + _message);
			if (mSocket == null) {
				Toast.makeText(getActivity().getApplicationContext(), "La connexion n'a pas pu être établie avec le serveur", Toast.LENGTH_LONG).show();
				return;
			}

			try {
				if (mSocket.isConnected()) {
					mOut.write(_message.getBytes());
					mOut.flush();
					mSocket.shutdownOutput();
				} else {
					if (DEBUG)
						Log.e(TAG, "sendMessage: mSocket is not connected");
				}
			} catch (IOException e) {
				if (DEBUG)
					Log.i(TAG, "Try to send a message to" + mCurrentHost.toString() + ":" + PORT +"\r\n" + e.getMessage());
			}  catch (Exception e) {
				if (DEBUG)
					Log.i(TAG, "Try to send a message to" + mCurrentHost.toString() + ":" + PORT +"\r\n" + e.getMessage());
			} 
		}

		private void getServerReply() throws IOException {
			final int BUFSIZ = 512; 
			mReply = "";

			BufferedReader bufferReader = new BufferedReader(new InputStreamReader(mIn), BUFSIZ);
			String line = "";
			while ((line = bufferReader.readLine()) != null) {
				mReply += line;
			}

			if (DEBUG)
				Log.i(TAG, "Got a response : " + mReply);
		} 
	}
}
