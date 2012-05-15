package org.es.uremote.computer;


import static android.app.Activity.RESULT_OK;
import static android.view.HapticFeedbackConstants.VIRTUAL_KEY;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static org.es.uremote.utils.ServerMessage.CODE_AI;
import static org.es.uremote.utils.ServerMessage.CODE_APP;
import static org.es.uremote.utils.ServerMessage.CODE_CLASSIC;
import static org.es.uremote.utils.ServerMessage.CODE_MEDIA;
import static org.es.uremote.utils.ServerMessage.CODE_VOLUME;

import org.es.uremote.R;
import org.es.uremote.network.AsyncMessageMgr;
import org.es.uremote.network.WakeOnLan;
import org.es.uremote.utils.IntentKeys;
import org.es.uremote.utils.ServerMessage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
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
	private static final int STATE_KO	= 0;
	private static final int STATE_OK	= 1;
	private static final int STATE_CONNECTING	= 2;
	private ImageButton mCmdMute;

	private Handler mHandler;
	private TextView mTvServerState;
	private ProgressBar mPbConnection;

	public FragDashboard(Handler _handler) {
		mHandler = _handler;
	}

	/** 
	 * Cette fonction est appelée lors de la création de l'activité
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.server_dashboard, container, false);

		// TODO Externaliser l'état dans l'activité host
		mTvServerState = (TextView) view.findViewById(R.id.tvServerState);
		mPbConnection = (ProgressBar) view.findViewById(R.id.pbConnection);

		((Button) view.findViewById(R.id.cmdWakeOnLan)).setOnClickListener(this);
		((Button) view.findViewById(R.id.cmdShutdown)).setOnClickListener(this);
		((Button) view.findViewById(R.id.cmdAiMute)).setOnClickListener(this);
		((Button) view.findViewById(R.id.cmdKillServer)).setOnClickListener(this);
		((Button) view.findViewById(R.id.cmdTest)).setOnClickListener(this);
		((Button) view.findViewById(R.id.cmdSwitch)).setOnClickListener(this);
		((Button) view.findViewById(R.id.cmdGomStretch)).setOnClickListener(this);
		((Button) view.findViewById(R.id.btnAppLauncher)).setOnClickListener(this);

		((ImageButton) view.findViewById(R.id.cmdPrevious)).setOnClickListener(this);
		((ImageButton) view.findViewById(R.id.cmdPlayPause)).setOnClickListener(this);
		((ImageButton) view.findViewById(R.id.cmdStop)).setOnClickListener(this);
		((ImageButton) view.findViewById(R.id.cmdNext)).setOnClickListener(this);
		mCmdMute = (ImageButton) view.findViewById(R.id.cmdMute);
		mCmdMute.setOnClickListener(this);

		((TextView) view.findViewById(R.id.tvServerInfos)).setText(MessageMgr.getServerInfos());

		return view;
	}

	@Override
	public void onStart() {
		getActivity().getActionBar().setIcon(R.drawable.ic_launcher);
		super.onStart();
	}

	/**
	 * Prise en comptes des événements onClick 
	 */
	@Override
	public void onClick(View _view) {
		_view.performHapticFeedback(VIRTUAL_KEY);

		switch (_view.getId()) {

		case R.id.cmdWakeOnLan :
			wakeOnLan();
			break;
		case R.id.cmdShutdown :
			confirmCommand(CODE_CLASSIC, ServerMessage.SHUTDOWN);
			break;
		case R.id.cmdAiMute :
			sendAsyncMessage(CODE_AI, ServerMessage.AI_MUTE);
			break;
		case R.id.cmdKillServer :
			confirmCommand(CODE_CLASSIC, ServerMessage.KILL_SERVER);
			break;
		case R.id.cmdTest :
			sendAsyncMessage(CODE_CLASSIC, ServerMessage.TEST_COMMAND);
			break;
		case R.id.cmdSwitch :
			sendAsyncMessage(CODE_CLASSIC, ServerMessage.MONITOR_SWITCH_WINDOW);
			break;
		case R.id.btnAppLauncher :
			startActivityForResult(new Intent(getActivity().getApplicationContext(), AppLauncher.class), RC_APP_LAUNCHER);
			break;
		case R.id.cmdGomStretch :
			sendAsyncMessage(CODE_APP, ServerMessage.GOM_PLAYER_STRETCH);
			break;

		case R.id.cmdPrevious :
			sendAsyncMessage(CODE_MEDIA, ServerMessage.MEDIA_PREVIOUS);
			break;
		case R.id.cmdPlayPause :
			sendAsyncMessage(CODE_MEDIA, ServerMessage.MEDIA_PLAY_PAUSE);
			break;
		case R.id.cmdStop :
			sendAsyncMessage(CODE_MEDIA, ServerMessage.MEDIA_STOP);
			break;
		case R.id.cmdNext :
			sendAsyncMessage(CODE_MEDIA, ServerMessage.MEDIA_NEXT);
			break;
		case R.id.cmdMute :
			sendAsyncMessage(CODE_VOLUME, ServerMessage.VOLUME_MUTE);
			break;
		default:
			break;
		}
	}

	/** 
	 * Gestion des actions en fonction du code de retour renvoyé après un StartActivityForResult.
	 * 
	 * @param _requestCode Code d'identification de l'activité appelée.
	 * @param _resultCode Code de retour de l'activité (RESULT_OK/RESULT_CANCEL).
	 * @param _data Les données renvoyées par l'application.
	 */
	@Override
	public void onActivityResult(int _requestCode, int _resultCode, Intent _data) {	// Résultat de l'activité Application Launcher
		if (_requestCode == RC_APP_LAUNCHER && _resultCode == RESULT_OK) {
			final String message = _data.getStringExtra(IntentKeys.APPLICATION_MESSAGE);
			sendAsyncMessage(CODE_APP, message);
		} 
	}

	/** 
	 * Demande une confirmation à l'utilisateur avant d'executer la commande.
	 * @param _code Le code du message. 
	 * @param _param Le paramètre du message.
	 */
	private void confirmCommand(final String _code, final String _param) {
		int resId = (_param.equals(ServerMessage.KILL_SERVER)) ? R.string.confirm_kill_server : R.string.confirm_command;

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setIcon(android.R.drawable.ic_menu_more);
		builder.setMessage(resId);
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				// Envoi du message si l'utilisateur confirme
				sendAsyncMessage(_code, _param);
			}
		});

		builder.setNegativeButton(android.R.string.cancel, null);
		builder.show();
	}

	private void wakeOnLan() {

		final WifiManager wifiMgr = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		final boolean wifi = wifiMgr.isWifiEnabled();
		final int resKeyHost = wifi ? R.string.pref_key_broadcast : R.string.pref_key_remote_host;
		final int resDefHost = wifi ? R.string.pref_default_broadcast : R.string.pref_default_remote_host;

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
		final String keyHost = getString(resKeyHost);
		final String defHost = getString(resDefHost);
		String host = pref.getString(keyHost, defHost);

		// TODO Passer l'adresse mac dans les préférences
		new WakeOnLan(mHandler).execute(host, "00-22-15-1A-9B-B1");	
	}

	/**
	 * Fonction de mise à jour de l'interface utilisateur
	 * @param _state L'état à mettre à jour (OK, KO, CONNECTING)
	 */
	private void updateConnectionState(int _state) {
		int drawableResId;
		int messageResId;
		int visibility;

		switch (_state) {
		case STATE_OK:
			drawableResId = android.R.drawable.presence_online; 
			messageResId = R.string.msg_command_succeeded;
			visibility = INVISIBLE;
			break;

		case STATE_CONNECTING:
			drawableResId = android.R.drawable.presence_away; 
			messageResId = R.string.msg_command_running;
			visibility = VISIBLE;
			break;

		default: // KO
			drawableResId = android.R.drawable.presence_offline; 
			messageResId = R.string.msg_command_failed;
			visibility = INVISIBLE;
			break;
		}
		final Drawable imgLeft = getResources().getDrawable(drawableResId);
		imgLeft.setBounds(0, 0, 24, 24);
		mTvServerState.setCompoundDrawables(imgLeft, null, null, null);
		mTvServerState.setText(messageResId);
		mPbConnection.setVisibility(visibility);
	}

	/**
	 * Cette fonction initialise le composant gérant l'envoi des messages 
	 * puis envoie le message passé en paramètre.
	 * @param _code Le code du message. 
	 * @param _param Le paramètre du message.
	 */
	private void sendAsyncMessage(String _code, String _param) {
		if (MessageMgr.availablePermits() > 0) {
			new MessageMgr().execute(_code, _param);
		} else {
			// TODO Internationaliser
			Toast.makeText(getActivity().getApplicationContext(), "No more permit available !", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Classe asynchrone de gestion d'envoi des messages au serveur
	 * @author cyril.leroux
	 */
	private class MessageMgr extends AsyncMessageMgr {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			updateConnectionState(STATE_CONNECTING);
		}

		@Override
		protected void onPostExecute(String _serverReply) {
			super.onPostExecute(_serverReply);

			if (getActivity() != null)
				Toast.makeText(getActivity().getApplicationContext(), _serverReply, Toast.LENGTH_SHORT).show();

			if (ServerMessage.RC_ERROR.equals(mReturnCode)) {
				updateConnectionState(STATE_KO);

			} else { 
				if (ServerMessage.REPLY_VOLUME_MUTED.equals(_serverReply)) {
					mCmdMute.setImageResource(R.drawable.volume_muted);

				} else if (ServerMessage.REPLY_VOLUME_ON.equals(_serverReply)) {
					mCmdMute.setImageResource(R.drawable.volume_on);

				}
				updateConnectionState(STATE_OK);
			}
		}
	}
}
