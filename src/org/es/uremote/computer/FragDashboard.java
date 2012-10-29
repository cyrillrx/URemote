package org.es.uremote.computer;


import static android.app.Activity.RESULT_OK;
import static android.view.HapticFeedbackConstants.VIRTUAL_KEY;
import static org.es.network.ExchangeProtos.Request.Code.DEFINE;
import static org.es.network.ExchangeProtos.Request.Code.MUTE;
import static org.es.network.ExchangeProtos.Request.Type.KEYBOARD;
import static org.es.network.ExchangeProtos.Request.Type.VOLUME;
import static org.es.network.ExchangeProtos.Response.ReturnCode.RC_ERROR;

import org.es.network.AsyncMessageMgr;
import org.es.network.ExchangeProtos.Request;
import org.es.network.ExchangeProtos.Request.Code;
import org.es.network.ExchangeProtos.Request.Type;
import org.es.network.ExchangeProtos.Response;
import org.es.network.IRequestSender;
import org.es.uremote.R;
import org.es.uremote.ServerControl;
import org.es.uremote.utils.IntentKeys;
import org.es.utils.Log;

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
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

/**
 * Class to connect and send commands to a remote server through AsyncTask.
 * 
 * @author Cyril Leroux
 *
 */
public class FragDashboard extends Fragment implements OnClickListener, OnSeekBarChangeListener, IRequestSender {

	private static final String TAG	= "FragDashboard";
	// ActivityForResults request codes
	private static final int RC_APP_LAUNCHER	= 0;

	private static final int STATE_KO	= 0;
	private static final int STATE_OK	= 1;
	private static final int STATE_CONNECTING	= 2;
	private ImageButton mCmdMute;

	private ServerControl mParent;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mParent = (ServerControl) getActivity();
	}

	/**
	 * Called when the application is created.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.server_frag_dashboard, container, false);

		((ImageButton) view.findViewById(R.id.kbLeft)).setOnClickListener(this);
		((ImageButton) view.findViewById(R.id.kbRight)).setOnClickListener(this);
		((ImageButton) view.findViewById(R.id.kbUp)).setOnClickListener(this);
		((ImageButton) view.findViewById(R.id.kbDown)).setOnClickListener(this);
		((Button) view.findViewById(R.id.kbOk)).setOnClickListener(this);

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


		((SeekBar) view.findViewById(R.id.sbVolume)).setOnSeekBarChangeListener(this);

		return view;
	}

	@Override
	public void onStart() {
		getActivity().getActionBar().setIcon(R.drawable.ic_launcher);
		super.onStart();
	}

	@Override
	public void onClick(View _view) {
		_view.performHapticFeedback(VIRTUAL_KEY);

		switch (_view.getId()) {

		case R.id.kbLeft :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(KEYBOARD, Code.LEFT));
			break;

		case R.id.kbRight :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(KEYBOARD, Code.RIGHT));
			break;

		case R.id.kbUp :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(KEYBOARD, Code.UP));
			break;

		case R.id.kbDown :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(KEYBOARD, Code.DOWN));
			break;

		case R.id.kbOk :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(KEYBOARD, Code.KB_RETURN));
			break;

		case R.id.cmdTest :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.SIMPLE, Code.TEST));
			break;

		case R.id.cmdSwitch :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.SIMPLE, Code.SWITCH_WINDOW));
			break;

		case R.id.btnAppLauncher :
			startActivityForResult(new Intent(getActivity().getApplicationContext(), AppLauncher.class), RC_APP_LAUNCHER);
			break;

		case R.id.cmdGomStretch :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.APP, Code.GOM_PLAYER_STRETCH));
			break;

		case R.id.cmdPrevious :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(KEYBOARD, Code.MEDIA_PREVIOUS));
			break;

		case R.id.cmdPlayPause :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(KEYBOARD, Code.MEDIA_PLAY_PAUSE));
			break;

		case R.id.cmdStop :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(KEYBOARD, Code.MEDIA_STOP));
			break;

		case R.id.cmdNext :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(KEYBOARD, Code.MEDIA_NEXT));
			break;

		case R.id.cmdMute :
			sendAsyncRequest(AsyncMessageMgr.buildRequest(VOLUME, Code.MUTE));
			break;

		default:
			break;
		}
	}

	@Override
	public void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
		if (_requestCode == RC_APP_LAUNCHER && _resultCode == RESULT_OK) {
			final Type type  = Type.valueOf(_data.getIntExtra(IntentKeys.REQUEST_TYPE, -1));
			final Code code  = Code.valueOf(_data.getIntExtra(IntentKeys.REQUEST_CODE, -1));

			if (type != null && code != null) {
				sendAsyncRequest(AsyncMessageMgr.buildRequest(type, code));
			}
		}
	}

	////////////////////////////////////////////////////////////////////
	// *********************** Message Sender *********************** //
	////////////////////////////////////////////////////////////////////

	/**
	 * Initializes the message handler then send the request.
	 * @param _request The request to send.
	 */
	@Override
	public void sendAsyncRequest(Request _request) {
		if (_request == null) {
			Toast.makeText(getActivity().getApplicationContext(), R.string.msg_null_request, Toast.LENGTH_SHORT).show();
			return;
		}

		if (DashboardMessageMgr.availablePermits() > 0) {
			new DashboardMessageMgr(ServerControl.getHandler()).execute(_request);
		} else {
			final boolean defineVolume = VOLUME.equals(_request.getType()) && DEFINE.equals(_request.getCode());
			if (!defineVolume) {
				final String toastMsg = getString(R.string.msg_no_more_permit) + "\n" + _request.toString();
				Toast.makeText(getActivity().getApplicationContext(), toastMsg, Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * Class that handle asynchronous requests sent to a remote server.
	 * Specialize for Dashboard.
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
		protected void onPostExecute(Response _response) {
			super.onPostExecute(_response);
			Log.debug(TAG, "onPostExecute() response : " + _response.getMessage());

			Type type = _response.getRequestType();
			Code code = _response.getRequestCode();

			if (!(VOLUME.equals(type) && DEFINE.equals(code))) {
				showToast(_response.toString());
			}

			if (RC_ERROR.equals(_response.getReturnCode())) {
				mParent.updateConnectionState(STATE_KO);

			} else {
				if (VOLUME.equals(type) && MUTE.equals(code)) {
					if (_response.getIntValue() == 0) { // Mute
						mCmdMute.setImageResource(R.drawable.volume_muted);
					} else if (_response.getIntValue() == 1) { // Volume On
						mCmdMute.setImageResource(R.drawable.volume_on);
					}
				}
				mParent.updateConnectionState(STATE_OK);
			}
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		final int volume = seekBar.getProgress();
		sendAsyncRequest(AsyncMessageMgr.buildRequest(VOLUME, Code.DEFINE, volume));
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}
}
