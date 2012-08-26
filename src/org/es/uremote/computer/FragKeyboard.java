package org.es.uremote.computer;


import static android.view.HapticFeedbackConstants.VIRTUAL_KEY;
import static org.es.network.ExchangeProtos.Response.ReturnCode.RC_ERROR;

import org.es.network.AsyncMessageMgr;
import org.es.network.ExchangeProtos.Request;
import org.es.network.ExchangeProtos.Request.Code;
import org.es.network.ExchangeProtos.Request.Type;
import org.es.network.ExchangeProtos.Response;
import org.es.network.IRequestSender;
import org.es.uremote.R;
import org.es.uremote.ServerControl;
import org.es.uremote.utils.Constants;

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
 * Class to connect and send commands to a remote server through AsyncTask.
 * 
 * @author Cyril Leroux
 *
 */
public class FragKeyboard extends Fragment implements OnClickListener, IRequestSender {

	private ServerControl mParent;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mParent = (ServerControl) getActivity();
	}

	// TODO fr to en
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

	@Override
	public void onClick(View _view) {
		_view.performHapticFeedback(VIRTUAL_KEY);

		switch (_view.getId()) {

		case R.id.kbEnter :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.KEYBOARD, Code.KB_RETURN));
			break;

		case R.id.kbSpace :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.KEYBOARD, Code.KB_SPACE));
			break;

		case R.id.kbBackspace :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.KEYBOARD, Code.KB_BACKSPACE));
			break;

		case R.id.kbEscape :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.KEYBOARD, Code.KB_ESCAPE));
			break;

		case R.id.kbAltf4 :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.KEYBOARD, Code.KB_ALT_F4));
			break;

		case R.id.kbCtrlEnter :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.KEYBOARD, Code.KB_CTRL_RETURN));
			break;

		case R.id.kb0 :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.KEYBOARD, Code.DEFINE, "0"));
			break;

		case R.id.kb1 :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.KEYBOARD, Code.DEFINE, "1"));
			break;

		case R.id.kb2 :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.KEYBOARD, Code.DEFINE, "2"));
			break;
		case R.id.kb3 :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.KEYBOARD, Code.DEFINE, "3"));
			break;
		case R.id.kb4 :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.KEYBOARD, Code.DEFINE, "4"));
			break;
		case R.id.kb5 :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.KEYBOARD, Code.DEFINE, "5"));
			break;
		case R.id.kb6 :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.KEYBOARD, Code.DEFINE, "6"));
			break;
		case R.id.kb7 :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.KEYBOARD, Code.DEFINE, "7"));
			break;
		case R.id.kb8 :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.KEYBOARD, Code.DEFINE, "8"));
			break;
		case R.id.kb9 :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.KEYBOARD, Code.DEFINE, "9"));
			break;

		case R.id.kbA :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.KEYBOARD, Code.DEFINE, "A"));
			break;
		case R.id.kbB :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.KEYBOARD, Code.DEFINE, "B"));
			break;
		case R.id.kbC :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.KEYBOARD, Code.DEFINE, "C"));
			break;
		case R.id.kbD :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.KEYBOARD, Code.DEFINE, "D"));
			break;
		case R.id.kbE :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.KEYBOARD, Code.DEFINE, "E"));
			break;
		case R.id.kbF :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.KEYBOARD, Code.DEFINE, "F"));
			break;
		case R.id.kbG :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.KEYBOARD, Code.DEFINE, "G"));
			break;
		case R.id.kbH :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.KEYBOARD, Code.DEFINE, "H"));
			break;
		case R.id.kbI :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.KEYBOARD, Code.DEFINE, "I"));
			break;
		case R.id.kbJ :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.KEYBOARD, Code.DEFINE, "J"));
			break;
		case R.id.kbK :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.KEYBOARD, Code.DEFINE, "K"));
			break;
		case R.id.kbL :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.KEYBOARD, Code.DEFINE, "L"));
			break;
		case R.id.kbM :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.KEYBOARD, Code.DEFINE, "M"));
			break;
		case R.id.kbN :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.KEYBOARD, Code.DEFINE, "N"));
			break;
		case R.id.kbO :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.KEYBOARD, Code.DEFINE, "O"));
			break;
		case R.id.kbP :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.KEYBOARD, Code.DEFINE, "P"));
			break;
		case R.id.kbQ :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.KEYBOARD, Code.DEFINE, "Q"));
			break;
		case R.id.kbR :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.KEYBOARD, Code.DEFINE, "R"));
			break;
		case R.id.kbS :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.KEYBOARD, Code.DEFINE, "S"));
			break;
		case R.id.kbT :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.KEYBOARD, Code.DEFINE, "T"));
			break;
		case R.id.kbU :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.KEYBOARD, Code.DEFINE, "U"));
			break;
		case R.id.kbV :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.KEYBOARD, Code.DEFINE, "V"));
			break;
		case R.id.kbW :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.KEYBOARD, Code.DEFINE, "W"));
			break;
		case R.id.kbX :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.KEYBOARD, Code.DEFINE, "X"));
			break;
		case R.id.kbY :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.KEYBOARD, Code.DEFINE, "Y"));
			break;
		case R.id.kbZ :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.KEYBOARD, Code.DEFINE, "Z"));
			break;

		default:
			break;
		}
	}


	////////////////////////////////////////////////////////////////////
	// *********************** Message Sender *********************** //
	////////////////////////////////////////////////////////////////////

	/**
	 * Initializes the message sender manager then send a request.
	 * @param _request The request.
	 */
	@Override
	public void sendAsyncRequest(Request _request) {
		if (KeyboardMessageMgr.availablePermits() > 0) {
			new KeyboardMessageMgr(ServerControl.getHandler()).execute(_request);
		} else {
			Toast.makeText(getActivity().getApplicationContext(), R.string.msg_no_more_permit, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Ask for the user to confirm before sending a request to the server.
	 * @param _request The request to send.
	 */
	public void confirmRequest(final Request _request) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setIcon(android.R.drawable.ic_menu_more);
		builder.setMessage( R.string.confirm_command);
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				// Send the request if the user confirms it
				sendAsyncRequest(_request);
			}
		});

		builder.setNegativeButton(android.R.string.cancel, null);
		builder.show();
	}

	/**
	 * Class that handle asynchronous requests sent to a remote server.
	 * Specialize for Keyboard commands.
	 * @author Cyril Leroux
	 * 
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
		protected void onPostExecute(Response _response) {
			super.onPostExecute(_response);

			showToast(_response.toString());

			if (RC_ERROR.equals(_response.getReturnCode())) {
				mParent.updateConnectionState(Constants.STATE_KO);
			} else {
				mParent.updateConnectionState(Constants.STATE_OK);
			}
		}
	}
}
