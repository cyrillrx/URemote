package org.es.uremote.computer;


import static android.view.HapticFeedbackConstants.VIRTUAL_KEY;
import static org.es.uremote.utils.ServerMessage.CODE_COMBO;
import static org.es.uremote.utils.ServerMessage.CODE_KEYBOARD;

import org.es.network.AsyncMessageMgr;
import org.es.uremote.R;
import org.es.uremote.ServerControl;
import org.es.uremote.utils.Constants;
import org.es.uremote.utils.ServerMessage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
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
public class FragKeyboard extends Fragment implements OnClickListener {

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
		View view = inflater.inflate(R.layout.server_frag_keyboard, container, false);

		((Button) view.findViewById(R.id.kbEnter)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kbSpace)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kbBackspace)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kbEscape)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kbCtrlEnter)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kbAltf4)).setOnClickListener(this);

		((Button) view.findViewById(R.id.kb0)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kb1)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kb2)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kb3)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kb4)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kb5)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kb6)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kb7)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kb8)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kb9)).setOnClickListener(this);

		((Button) view.findViewById(R.id.kbA)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kbB)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kbC)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kbD)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kbE)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kbF)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kbG)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kbH)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kbI)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kbJ)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kbK)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kbL)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kbM)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kbN)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kbO)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kbP)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kbQ)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kbR)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kbS)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kbT)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kbU)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kbV)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kbW)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kbX)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kbY)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kbZ)).setOnClickListener(this);

		return view;
	}

	/**
	 * Prise en comptes des événements onClick
	 */
	@Override
	public void onClick(View _view) {
		_view.performHapticFeedback(VIRTUAL_KEY);

		switch (_view.getId()) {

		case R.id.kbEnter :
			sendAsyncMessage(CODE_KEYBOARD, ServerMessage.KB_ENTER);
			break;
		case R.id.kbSpace :
			sendAsyncMessage(CODE_KEYBOARD, ServerMessage.KB_SPACE);
			break;
		case R.id.kbBackspace :
			sendAsyncMessage(CODE_KEYBOARD, ServerMessage.KB_BACKSPACE);
			break;
		case R.id.kbEscape :
			sendAsyncMessage(CODE_KEYBOARD, ServerMessage.KB_ESCAPE);
			break;
		case R.id.kbAltf4 :
			confirmCommand(CODE_COMBO, ServerMessage.KB_ALT_F4);
			break;
		case R.id.kbCtrlEnter :
			sendAsyncMessage(CODE_COMBO, ServerMessage.KB_CTRL_ENTER);
			break;

		case R.id.kb0 :
			sendAsyncMessage(CODE_KEYBOARD, "0");
			break;
		case R.id.kb1 :
			sendAsyncMessage(CODE_KEYBOARD, "1");
			break;
		case R.id.kb2 :
			sendAsyncMessage(CODE_KEYBOARD, "2");
			break;
		case R.id.kb3 :
			sendAsyncMessage(CODE_KEYBOARD, "3");
			break;
		case R.id.kb4 :
			sendAsyncMessage(CODE_KEYBOARD, "4");
			break;
		case R.id.kb5 :
			sendAsyncMessage(CODE_KEYBOARD, "5");
			break;
		case R.id.kb6 :
			sendAsyncMessage(CODE_KEYBOARD, "6");
			break;
		case R.id.kb7 :
			sendAsyncMessage(CODE_KEYBOARD, "7");
			break;
		case R.id.kb8 :
			sendAsyncMessage(CODE_KEYBOARD, "8");
			break;
		case R.id.kb9 :
			sendAsyncMessage(CODE_KEYBOARD, "9");
			break;

		case R.id.kbA :
			sendAsyncMessage(CODE_KEYBOARD, "A");
			break;
		case R.id.kbB :
			sendAsyncMessage(CODE_KEYBOARD, "B");
			break;
		case R.id.kbC :
			sendAsyncMessage(CODE_KEYBOARD, "C");
			break;
		case R.id.kbD :
			sendAsyncMessage(CODE_KEYBOARD, "D");
			break;
		case R.id.kbE :
			sendAsyncMessage(CODE_KEYBOARD, "E");
			break;
		case R.id.kbF :
			sendAsyncMessage(CODE_KEYBOARD, "F");
			break;
		case R.id.kbG :
			sendAsyncMessage(CODE_KEYBOARD, "G");
			break;
		case R.id.kbH :
			sendAsyncMessage(CODE_KEYBOARD, "H");
			break;
		case R.id.kbI :
			sendAsyncMessage(CODE_KEYBOARD, "I");
			break;
		case R.id.kbJ :
			sendAsyncMessage(CODE_KEYBOARD, "J");
			break;
		case R.id.kbK :
			sendAsyncMessage(CODE_KEYBOARD, "K");
			break;
		case R.id.kbL :
			sendAsyncMessage(CODE_KEYBOARD, "L");
			break;
		case R.id.kbM :
			sendAsyncMessage(CODE_KEYBOARD, "M");
			break;
		case R.id.kbN :
			sendAsyncMessage(CODE_KEYBOARD, "N");
			break;
		case R.id.kbO :
			sendAsyncMessage(CODE_KEYBOARD, "O");
			break;
		case R.id.kbP :
			sendAsyncMessage(CODE_KEYBOARD, "P");
			break;
		case R.id.kbQ :
			sendAsyncMessage(CODE_KEYBOARD, "Q");
			break;
		case R.id.kbR :
			sendAsyncMessage(CODE_KEYBOARD, "R");
			break;
		case R.id.kbS :
			sendAsyncMessage(CODE_KEYBOARD, "S");
			break;
		case R.id.kbT :
			sendAsyncMessage(CODE_KEYBOARD, "T");
			break;
		case R.id.kbU :
			sendAsyncMessage(CODE_KEYBOARD, "U");
			break;
		case R.id.kbV :
			sendAsyncMessage(CODE_KEYBOARD, "V");
			break;
		case R.id.kbW :
			sendAsyncMessage(CODE_KEYBOARD, "W");
			break;
		case R.id.kbX :
			sendAsyncMessage(CODE_KEYBOARD, "X");
			break;
		case R.id.kbY :
			sendAsyncMessage(CODE_KEYBOARD, "Y");
			break;
		case R.id.kbZ :
			sendAsyncMessage(CODE_KEYBOARD, "X");
			break;

		default:
			break;
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
		if (KeyboardMessageMgr.availablePermits() > 0) {
			new KeyboardMessageMgr(ServerControl.getHandler()).execute(_code, _param);
		} else {
			Toast.makeText(getActivity().getApplicationContext(), R.string.msg_no_more_permit, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Demande une confirmation à l'utilisateur avant d'executer la commande.
	 * @param _code Le code du message.
	 * @param _param Le paramètre du message.
	 */
	public void confirmCommand(final String _code, final String _param) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setIcon(android.R.drawable.ic_menu_more);
		builder.setMessage( R.string.confirm_command);
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
	public class KeyboardMessageMgr extends AsyncMessageMgr {

		/**
		 * @param _handler
		 */
		public KeyboardMessageMgr(Handler _handler) {
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
