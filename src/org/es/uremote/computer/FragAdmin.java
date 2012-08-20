package org.es.uremote.computer;


import static android.app.Activity.RESULT_OK;
import static android.view.HapticFeedbackConstants.VIRTUAL_KEY;
import static org.es.uremote.utils.ServerMessage.CODE_AI;
import static org.es.uremote.utils.ServerMessage.CODE_APP;
import static org.es.uremote.utils.ServerMessage.CODE_CLASSIC;
import static org.es.uremote.utils.ServerMessage.CODE_MEDIA;
import static org.es.uremote.utils.ServerMessage.CODE_VOLUME;

import org.es.uremote.R;
import org.es.uremote.ServerControl;
import org.es.uremote.network.AsyncMessageMgr;
import org.es.uremote.network.WakeOnLan;
import org.es.uremote.utils.Constants;
import org.es.uremote.utils.IntentKeys;
import org.es.uremote.utils.ServerMessage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

/**
 * @author Cyril Leroux
 * 
 * Classe permettant de se connecter et d'envoyer des commandes à un serveur distant via une AsyncTask.
 *
 */
public class FragAdmin extends Fragment implements OnClickListener {

	// Liste des RequestCodes pour les ActivityForResults
	private static final int RC_APP_LAUNCHER	= 0;

	private ServerControl mParent;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mParent = (ServerControl) getActivity();
	}

	/**
	 * Cette fonction est appelée lors de la création de l'activité
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.server_frag_admin, container, false);

		((Button) view.findViewById(R.id.cmdWakeOnLan)).setOnClickListener(this);
		((Button) view.findViewById(R.id.cmdShutdown)).setOnClickListener(this);
		((Button) view.findViewById(R.id.cmdAiMute)).setOnClickListener(this);
		((Button) view.findViewById(R.id.cmdKillServer)).setOnClickListener(this);

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

	private void wakeOnLan() {

		final WifiManager wifiMgr = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		final boolean wifi = wifiMgr.isWifiEnabled();
		final int resKeyHost = wifi ? R.string.pref_key_broadcast : R.string.pref_key_remote_host;
		final int resDefHost = wifi ? R.string.pref_default_broadcast : R.string.pref_default_remote_host;

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

		final String keyHost = getString(resKeyHost);
		final String defHost = getString(resDefHost);
		final String host = pref.getString(keyHost, defHost);

		final String keyMAcAddress	= getString(R.string.pref_key_mac_address);
		final String defaultMAcAddress	= getString(R.string.pref_default_mac_address);
		final String macAddress = pref.getString(keyMAcAddress, defaultMAcAddress);

		new WakeOnLan(mParent.getHandler()).execute(host, macAddress);
	}

	////////////////////////////////////////////////////////////////////
	// *********************** Message Sender *********************** //
	////////////////////////////////////////////////////////////////////

	/**
	 * This method initialize the message sender manager then send the message.
	 * @param _code The message code.
	 * @param _param The message parameter.
	 */
	public void sendAsyncMessage(String _code, String _param) {
		if (AdminMessageMgr.availablePermits() > 0) {
			new AdminMessageMgr(mParent.getHandler()).execute(_code, _param);
		} else {
			Toast.makeText(getActivity().getApplicationContext(), R.string.msg_no_more_permit, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Ask for the user to confirm before executing a given command.
	 * @param _code Le code du message.
	 * @param _param Le paramètre du message.
	 */
	public void confirmCommand(final String _code, final String _param) {
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

	/**
	 * Classe asynchrone de gestion d'envoi des messages au serveur
	 * @author Cyril Leroux
	 */
	public class AdminMessageMgr extends AsyncMessageMgr {

		/**
		 * @param _handler
		 */
		public AdminMessageMgr(Handler _handler) {
			super(_handler);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mParent.updateConnectionState(Constants.STATE_CONNECTING);
		}

		@Override
		protected void onPostExecute(String _serverReply) {
			super.onPostExecute(_serverReply);

			showToast(_serverReply);

			if (ServerMessage.RC_ERROR.equals(mReturnCode)) {
				mParent.updateConnectionState(Constants.STATE_KO);
			} else {
				mParent.updateConnectionState(Constants.STATE_OK);
			}
		}
	}
}
