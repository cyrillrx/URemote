package org.es.uremote.computer;


import static android.view.HapticFeedbackConstants.VIRTUAL_KEY;
import static org.es.uremote.utils.Message.CODE_KEYBOARD;
import static org.es.uremote.utils.Message.CODE_KEYBOARD_COMBO;

import org.es.uremote.R;
import org.es.uremote.network.AsyncMessageMgr;
import org.es.uremote.utils.Message;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
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
	private static final String TAG = "FragKeyboard";

	/** 
	 * Cette fonction est appelée lors de la création de l'activité
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.server_keyboard, container, false);

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

		((Button) view.findViewById(R.id.kbEnter)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kbSpace)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kbBackspace)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kbEscape)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kbCtrlEnter)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kbAltf4)).setOnClickListener(this);

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
			sendAsyncMessage(CODE_KEYBOARD, Message.KB_ENTER);
			break;
		case R.id.kbSpace :
			sendAsyncMessage(CODE_KEYBOARD, Message.KB_SPACE);
			break;
		case R.id.kbBackspace :
			sendAsyncMessage(CODE_KEYBOARD, Message.KB_BACKSPACE);
			break;
		case R.id.kbEscape :
			sendAsyncMessage(CODE_KEYBOARD, Message.KB_ESCAPE);
			break;
		case R.id.kbAltf4 :
			sendAsyncMessage(CODE_KEYBOARD_COMBO, Message.KB_ALT_F4);
			break;
		case R.id.kbCtrlEnter :
			sendAsyncMessage(CODE_KEYBOARD_COMBO, Message.KB_CTRL_ENTER);
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

		case R.id.cmdPrevious :
			sendAsyncMessage(Message.MEDIA_PREVIOUS, null);
			break;

		case R.id.cmdPlayPause :
			sendAsyncMessage(Message.MEDIA_PLAY_PAUSE, null);
			break;

		case R.id.cmdStop :
			sendAsyncMessage(Message.MEDIA_STOP, null);
			break;

		case R.id.cmdNext :
			sendAsyncMessage(Message.MEDIA_NEXT, null);
			break;

		case R.id.cmdMute :
			sendAsyncMessage(Message.VOLUME_MUTE, null);
			break;
		default:
			break;
		}
	}

	/**
	 * Cette fonction initialise le composant gérant l'envoi des messages 
	 * puis envoie le message passé en paramètre.
	 * @param _code Le code de message. 
	 * @param _message Le message à envoyer.
	 */
	private void sendAsyncMessage(String _code, String _message) {
		if (MessageMgr.availablePermits() > 0) {
			new MessageMgr().execute(_code, _message);
		} else {
			Toast.makeText(getActivity().getApplicationContext(), "No more permit available !", Toast.LENGTH_SHORT).show();
		}

	}
	
	/**
	 * Classe asynchrone de gestion d'envoi des messages
	 * @author cyril.leroux
	 */
	private class MessageMgr extends AsyncMessageMgr {

		/**
		 * Cette fonction est exécutée avant l'appel à {@link #doInBackground(String...)} 
		 * Exécutée dans le thread principal.
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Log.i(TAG, "onPreExecute");
			//updateConnectionStateConnecting();
		}

		/**
		 * Cette fonction est exécutée après l'appel à {@link #doInBackground(String...)} 
		 * Exécutée dans le thread principal.
		 * @param _result Le résultat de la fonction {@link #doInBackground(String...)}.
		 */
		@Override
		protected void onPostExecute(String _serverReply) {
			super.onPostExecute(_serverReply);
			if (_serverReply != null && !_serverReply.isEmpty()) {
				Toast.makeText(getActivity().getApplicationContext(), _serverReply, Toast.LENGTH_SHORT).show();
			}
		}
	}
}
