package org.es.uremote.computer;


import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import org.es.uremote.R;
import org.es.uremote.network.AsyncMessageMgr;
import org.es.uremote.utils.Message;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
public class FragDashboard extends Fragment implements OnClickListener {
	// Liste des RequestCodes pour les ActivityForResults
	private static final int RC_APP_LAUNCHER	= 0;

	private ImageButton mCmdMute;

	private DashboardMessageMgr mMessageManager = null;
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
		//		ConnectivityManager connectMgr = ConnectionUtils.getConnectivityManager(getActivity().getApplicationContext());

		//		for (int i = 1; i < 256; i++) {
		//			mCurrentHost = PARTIAL_HOST + i;
		//			//			if (mConnected) {
		//			if (ConnectionUtils.canAccessHost(connectMgr, mCurrentHost)) {
		//				Toast.makeText(getActivity().getApplicationContext(), "Got an acces to the host " + mCurrentHost, Toast.LENGTH_SHORT).show();
		//				//sendAsyncMessage(Messages.HELLO_SERVER);
		//				break;
		//			}
		//		}

		//		final String serverInfos = "Host : " + mCurrentHost + ":" + PORT;
		//		((TextView) view.findViewById(R.id.tvServerInfos)).setText(serverInfos);

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
		//TODO Empecher le plantage sur cette fonction
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
		if (mMessageManager != null && DashboardMessageMgr.sSemaphore.availablePermits() > 0) { 
			mMessageManager = null;
		}
		if (mMessageManager == null) {
			mMessageManager = new DashboardMessageMgr();
			mMessageManager.execute(_message);
		} else {
			Toast.makeText(getActivity().getApplicationContext(), "Already initialized", Toast.LENGTH_SHORT).show();
		}
	}

	private void stopMessageManager() {
		if (mMessageManager != null) {
			mMessageManager.cancel(false);
			mMessageManager = null;
		}
	}

	/**
	 * Classe asynchrone de gestion d'envoi des messages
	 * @author cyril.leroux
	 */
	private class DashboardMessageMgr extends AsyncMessageMgr {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			updateConnectionStateConnecting();
		}

		@Override
		protected void onPostExecute(String _serverReply) {
			super.onPostExecute(_serverReply);
			//			// Si le serveur n'est pas connecté et que l'on a une réponse au hello server, on passe à l'état connecté
			//			if (!mConnected && Messages.HELLO_CLIENT.equals(mReply))
			//				mConnected = true;


			if (_serverReply != null && !_serverReply.isEmpty()) {
				Toast.makeText(getActivity().getApplicationContext(), _serverReply, Toast.LENGTH_SHORT).show();

				if (_serverReply.equals(Message.REPLY_VOLUME_MUTED)) {
					mCmdMute.setImageResource(R.drawable.volume_muted);
				} else if (_serverReply.equals(Message.REPLY_VOLUME_ON)) {
					mCmdMute.setImageResource(R.drawable.volume_on);
				}

				updateConnectionStateOK();
			} else {
				updateConnectionStateKO();
			}
			stopMessageManager();
		}
	}
}
