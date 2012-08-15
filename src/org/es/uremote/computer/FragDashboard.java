package org.es.uremote.computer;


import static android.app.Activity.RESULT_OK;
import static android.view.HapticFeedbackConstants.VIRTUAL_KEY;
import static org.es.uremote.utils.ServerMessage.CODE_APP;
import static org.es.uremote.utils.ServerMessage.CODE_CLASSIC;
import static org.es.uremote.utils.ServerMessage.CODE_MEDIA;
import static org.es.uremote.utils.ServerMessage.CODE_VOLUME;

import org.es.uremote.R;
import org.es.uremote.ViewPagerDashboard;
import org.es.uremote.network.AsyncMessageMgr;
import org.es.uremote.utils.IntentKeys;
import org.es.uremote.utils.ServerMessage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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

	private ViewPagerDashboard mParent;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mParent = (ViewPagerDashboard) getActivity();
	}

	/**
	 * Cette fonction est appelée lors de la création de l'activité
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.server_frag_dashboard, container, false);

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

	////////////////////////////////////////////////////////////////////
	// *********************** Message Sender *********************** //
	////////////////////////////////////////////////////////////////////

	/**
	 * Cette fonction initialise le composant gérant l'envoi des messages
	 * puis envoie le message passé en paramètre.
	 * @param _code Le code du message.
	 * @param _param Le paramètre du message.
	 */
	public void sendAsyncMessage(String _code, String _param) {
		if (DashboardMessageMgr.availablePermits() > 0) {
			new DashboardMessageMgr(mParent.getHandler()).execute(_code, _param);
		} else {
			Toast.makeText(getActivity().getApplicationContext(), R.string.msg_no_more_permit, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Classe asynchrone de gestion d'envoi des messages au serveur
	 * @author Cyril Leroux
	 */
	private class DashboardMessageMgr extends AsyncMessageMgr {

		public DashboardMessageMgr(Handler _handler) {
			super(_handler);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mParent.updateConnectionState(STATE_CONNECTING);
		}

		@Override
		protected void onPostExecute(String _serverReply) {
			super.onPostExecute(_serverReply);

			showToast(_serverReply);

			if (ServerMessage.RC_ERROR.equals(mReturnCode)) {
				mParent.updateConnectionState(STATE_KO);

			} else {
				if (ServerMessage.REPLY_VOLUME_MUTED.equals(_serverReply)) {
					mCmdMute.setImageResource(R.drawable.volume_muted);

				} else if (ServerMessage.REPLY_VOLUME_ON.equals(_serverReply)) {
					mCmdMute.setImageResource(R.drawable.volume_on);

				}
				mParent.updateConnectionState(STATE_OK);
			}
		}
	}
}
