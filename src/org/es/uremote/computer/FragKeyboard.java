package org.es.uremote.computer;


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
import android.widget.ToggleButton;

import org.es.network.ExchangeProtos.Request;
import org.es.network.ExchangeProtos.Request.Code;
import org.es.network.ExchangeProtos.Request.Type;
import org.es.network.ExchangeProtos.Response;
import org.es.network.RequestSender;
import org.es.uremote.Computer;
import org.es.uremote.R;
import org.es.uremote.dao.ServerSettingDao;
import org.es.uremote.network.AsyncMessageMgr;
import org.es.uremote.network.MessageHelper;
import org.es.uremote.utils.Constants;
import org.es.utils.Log;

import static android.view.HapticFeedbackConstants.VIRTUAL_KEY;
import static org.es.network.ExchangeProtos.Request.Type.KEYBOARD;
import static org.es.network.ExchangeProtos.Response.ReturnCode.RC_ERROR;

/**
 * Class to connect and send commands to a remote server through AsyncTask.
 *
 * @author Cyril Leroux
 */
public class FragKeyboard extends Fragment implements OnClickListener, RequestSender {
	private static final String TAG = "FragKeyboard";
	private Computer mParent;

	private ToggleButton mTbControl;
	private ToggleButton mTbAlt;
	private ToggleButton mTbShift;
	private ToggleButton mTbWindows;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mParent = (Computer) getActivity();
	}

	/** Called after {@link #onActivityCreated(Bundle)} */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.server_frag_keyboard, container, false);

		mTbControl	= ((ToggleButton) view.findViewById(R.id.kbControl));
		mTbAlt		= ((ToggleButton) view.findViewById(R.id.kbAlt));
		mTbShift	= ((ToggleButton) view.findViewById(R.id.kbShift));
		mTbWindows	= ((ToggleButton) view.findViewById(R.id.kbWindows));

		mTbControl.setOnClickListener(this);
		mTbAlt.setOnClickListener(this);
		mTbShift.setOnClickListener(this);
		mTbWindows.setOnClickListener(this);

		((Button) view.findViewById(R.id.kbEnter)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kbSpace)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kbBackspace)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kbEscape)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kbAltF4)).setOnClickListener(this);

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
	public void onClick(View view) {
		view.performHapticFeedback(VIRTUAL_KEY);

		final Code extraCode = getExtraCode(view);

		switch (view.getId()) {

			case R.id.kbControl:
				if (mTbControl.isChecked()) {
					mTbAlt.setChecked(false);
					mTbShift.setChecked(false);
					mTbWindows.setChecked(false);
				}
				break;

			case R.id.kbAlt:
				if (mTbAlt.isChecked()) {
					mTbControl.setChecked(false);
					mTbShift.setChecked(false);
					mTbWindows.setChecked(false);
				}
				break;

			case R.id.kbShift:
				if (mTbShift.isChecked()) {
					mTbControl.setChecked(false);
					mTbAlt.setChecked(false);
					mTbWindows.setChecked(false);
				}
				break;

			case R.id.kbWindows:
				if (mTbWindows.isChecked()) {
					mTbControl.setChecked(false);
					mTbAlt.setChecked(false);
					mTbShift.setChecked(false);
				}
				break;

			case R.id.kbEnter:
				sendAsyncRequest(KEYBOARD, Code.KB_RETURN, extraCode);
				break;

			case R.id.kbSpace:
				sendAsyncRequest(KEYBOARD, Code.KB_SPACE, extraCode);
				break;

			case R.id.kbBackspace:
				sendAsyncRequest(KEYBOARD, Code.KB_BACKSPACE, extraCode);
				break;

			case R.id.kbEscape:
				sendAsyncRequest(KEYBOARD, Code.KB_ESCAPE, extraCode);
				break;

			case R.id.kbAltF4:
				sendAsyncRequest(KEYBOARD, Code.KB_F4, Code.KB_ALT);
				break;

			case R.id.kb0:
				sendAsyncRequest(KEYBOARD, Code.DEFINE, extraCode, "0");
				break;

			case R.id.kb1:
				sendAsyncRequest(KEYBOARD, Code.DEFINE, extraCode, "1");
				break;

			case R.id.kb2:
				sendAsyncRequest(KEYBOARD, Code.DEFINE, extraCode, "2");
				break;
			case R.id.kb3:
				sendAsyncRequest(KEYBOARD, Code.DEFINE, extraCode, "3");
				break;
			case R.id.kb4:
				sendAsyncRequest(KEYBOARD, Code.DEFINE, extraCode, "4");
				break;
			case R.id.kb5:
				sendAsyncRequest(KEYBOARD, Code.DEFINE, extraCode, "5");
				break;
			case R.id.kb6:
				sendAsyncRequest(KEYBOARD, Code.DEFINE, extraCode, "6");
				break;
			case R.id.kb7:
				sendAsyncRequest(KEYBOARD, Code.DEFINE, extraCode, "7");
				break;
			case R.id.kb8:
				sendAsyncRequest(KEYBOARD, Code.DEFINE, extraCode, "8");
				break;
			case R.id.kb9:
				sendAsyncRequest(KEYBOARD, Code.DEFINE, extraCode, "9");
				break;

			case R.id.kbA:
				sendAsyncRequest(KEYBOARD, Code.DEFINE, extraCode, "A");
				break;
			case R.id.kbB:
				sendAsyncRequest(KEYBOARD, Code.DEFINE, extraCode, "B");
				break;
			case R.id.kbC:
				sendAsyncRequest(KEYBOARD, Code.DEFINE, extraCode, "C");
				break;
			case R.id.kbD:
				sendAsyncRequest(KEYBOARD, Code.DEFINE, extraCode, "D");
				break;
			case R.id.kbE:
				sendAsyncRequest(KEYBOARD, Code.DEFINE, extraCode, "E");
				break;
			case R.id.kbF:
				sendAsyncRequest(KEYBOARD, Code.DEFINE, extraCode, "F");
				break;
			case R.id.kbG:
				sendAsyncRequest(KEYBOARD, Code.DEFINE, extraCode, "G");
				break;
			case R.id.kbH:
				sendAsyncRequest(KEYBOARD, Code.DEFINE, extraCode, "H");
				break;
			case R.id.kbI:
				sendAsyncRequest(KEYBOARD, Code.DEFINE, extraCode, "I");
				break;
			case R.id.kbJ:
				sendAsyncRequest(KEYBOARD, Code.DEFINE, extraCode, "J");
				break;
			case R.id.kbK:
				sendAsyncRequest(KEYBOARD, Code.DEFINE, extraCode, "K");
				break;
			case R.id.kbL:
				sendAsyncRequest(KEYBOARD, Code.DEFINE, extraCode, "L");
				break;
			case R.id.kbM:
				sendAsyncRequest(KEYBOARD, Code.DEFINE, extraCode, "M");
				break;
			case R.id.kbN:
				sendAsyncRequest(KEYBOARD, Code.DEFINE, extraCode, "N");
				break;
			case R.id.kbO:
				sendAsyncRequest(KEYBOARD, Code.DEFINE, extraCode, "O");
				break;
			case R.id.kbP:
				sendAsyncRequest(KEYBOARD, Code.DEFINE, extraCode, "P");
				break;
			case R.id.kbQ:
				sendAsyncRequest(KEYBOARD, Code.DEFINE, extraCode, "Q");
				break;
			case R.id.kbR:
				sendAsyncRequest(KEYBOARD, Code.DEFINE, extraCode, "R");
				break;
			case R.id.kbS:
				sendAsyncRequest(KEYBOARD, Code.DEFINE, extraCode, "S");
				break;
			case R.id.kbT:
				sendAsyncRequest(KEYBOARD, Code.DEFINE, extraCode, "T");
				break;
			case R.id.kbU:
				sendAsyncRequest(KEYBOARD, Code.DEFINE, extraCode, "U");
				break;
			case R.id.kbV:
				sendAsyncRequest(KEYBOARD, Code.DEFINE, extraCode, "V");
				break;
			case R.id.kbW:
				sendAsyncRequest(KEYBOARD, Code.DEFINE, extraCode, "W");
				break;
			case R.id.kbX:
				sendAsyncRequest(KEYBOARD, Code.DEFINE, extraCode, "X");
				break;
			case R.id.kbY:
				sendAsyncRequest(KEYBOARD, Code.DEFINE, extraCode, "Y");
				break;
			case R.id.kbZ:
				sendAsyncRequest(KEYBOARD, Code.DEFINE, extraCode, "Z");
				break;

			default:
				break;
		}
	}

	private Code getExtraCode(View view) {
		if (mTbControl.isChecked()) {
			return Code.KB_CTRL;
		} else if (mTbAlt.isChecked()) {
			return Code.KB_ALT;
		} else if (mTbShift.isChecked()) {
			return Code.KB_SHIFT;
		} else if (mTbWindows.isChecked()) {
			return Code.KB_WINDOWS;
		}
		return Code.NONE;
	}

	////////////////////////////////////////////////////////////////////
	// *********************** Message Sender *********************** //
	////////////////////////////////////////////////////////////////////

	/**
	 * Initializes the message handler then send the request.
	 *
	 * @param type The request type.
	 * @param code The request code.
	 * @param extraCode The request extra code.
	 */
	public void sendAsyncRequest(final Type type, final Code code, final Code extraCode) {
		sendAsyncRequest(MessageHelper.buildRequest(AsyncMessageMgr.getSecurityToken(), type, code, extraCode));
	}


	/**
	 * Initializes the message handler then send the request.
	 *
	 * @param type The request type.
	 * @param code The request code.
	 * @param extraCode The request extra code.
	 * @param stringParam A string parameter.
	 */
	public void sendAsyncRequest(final Type type, final Code code, final Code extraCode, final String stringParam) {
		sendAsyncRequest(MessageHelper.buildRequest(AsyncMessageMgr.getSecurityToken(), type, code, extraCode, stringParam));
	}

	/**
	 * Initializes the message sender manager then send a request.
	 *
	 * @param request The request.
	 */
	@Override
	public void sendAsyncRequest(Request request) {
		if (KeyboardMessageMgr.availablePermits() > 0) {
			new KeyboardMessageMgr(Computer.getHandler()).execute(request);
		} else {
			Log.warning(TAG, "#sendAsyncRequest - " + getString(R.string.msg_no_more_permit));
		}
	}

	/**
	 * Ask for the user to confirm before sending a request to the server.
	 *
	 * @param request The request to send.
	 */
	public void confirmRequest(final Request request) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setIcon(android.R.drawable.ic_menu_more);
		builder.setMessage(R.string.confirm_command);
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				// Send the request if the user confirms it
				sendAsyncRequest(request);
			}
		});

		builder.setNegativeButton(android.R.string.cancel, null);
		builder.show();
	}

	/**
	 * Class that handle asynchronous requests sent to a remote server.
	 * Specialize for Keyboard commands.
	 *
	 * @author Cyril Leroux
	 */
	public class KeyboardMessageMgr extends AsyncMessageMgr {

		/** @param handler */
		public KeyboardMessageMgr(Handler handler) {
			super(handler, ServerSettingDao.loadFromPreferences(getActivity().getApplicationContext()));
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mParent.updateConnectionState(Constants.STATE_CONNECTING);
		}

		@Override
		protected void onPostExecute(Response response) {
			super.onPostExecute(response);

			sendToastToUI(response.getMessage());

			if (RC_ERROR.equals(response.getReturnCode())) {
				mParent.updateConnectionState(Constants.STATE_KO);
			} else {
				mParent.updateConnectionState(Constants.STATE_OK);
			}
		}
	}
}
