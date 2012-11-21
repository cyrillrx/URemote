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
import org.es.uremote.Computer;
import org.es.uremote.R;
import org.es.uremote.utils.IntentKeys;
import org.es.utils.Log;

import android.app.Service;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Class to connect and send commands to a remote server through AsyncTask.
 * 
 * @author Cyril Leroux
 *
 */
public class FragDashboard extends Fragment implements OnClickListener, OnSeekBarChangeListener, IRequestSender {
	private static final String TAG	= "FragDashboard";
	private static final int STATE_KO	= 0;
	private static final int STATE_OK	= 1;
	private static final int STATE_CONNECTING	= 2;

	// ActivityForResults request codes
	private static final int RC_APP_LAUNCHER	= 0;

	private ImageButton mIbMute;
	private SeekBar mSbVolume;
	private Toast mToast;
	private TextView mTvToast;
	private Computer mParent;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mParent = (Computer) getActivity();
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

		mIbMute = (ImageButton) view.findViewById(R.id.cmdMute);
		mIbMute.setOnClickListener(this);

		mSbVolume = ((SeekBar) view.findViewById(R.id.sbVolume));
		mSbVolume.setOnSeekBarChangeListener(this);

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

	/**
	 * Show a static toast message.
	 * The next message replace the previous one.
	 * 
	 * @param _message The message to display.
	 * @param _point The position to display it.
	 */
	private void showVolumeToast(final String _message, final int _x, final int _y) {
		if (mToast == null) {
			initVolumeToast();
		}
		mTvToast.setText(_message);
		//		final int x = (_x);
		//		final int y = (int) (_y - mTvToast.getHeight() / 2.0);
		mToast.setGravity(Gravity.CENTER, 0, 0);
		mToast.show();
	}

	private void initVolumeToast() {
		LayoutInflater inflater = ( LayoutInflater ) getActivity().getSystemService(Service.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.custom_toast, null);

		mToast = new Toast(getActivity().getApplicationContext());
		mToast.setDuration(Toast.LENGTH_SHORT);
		mToast.setView(view);

		mTvToast = (TextView) view.findViewById(R.id.toastText);
		mTvToast.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Thin.ttf"));
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if (!fromUser) {
			return;
		}
		final int volume = seekBar.getProgress();
		sendAsyncRequest(AsyncMessageMgr.buildRequest(VOLUME, Code.DEFINE, volume));
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
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

		if (DashboardMessageMgr.availablePermits() > 0) {
			new DashboardMessageMgr(Computer.getHandler()).execute(_request);
		} else {
			final boolean defineVolume = VOLUME.equals(_request.getType()) && DEFINE.equals(_request.getCode());
			if (!defineVolume) {
				final String message = getString(R.string.msg_no_more_permit) + "\n" + _request.toString();
				Log.warning(TAG, message);
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
			final String message = _response.getMessage();
			Log.debug(TAG, "onPostExecute() response : " + message);

			// TODO handle return better than that
			if (RC_ERROR.equals(_response.getReturnCode())) {
				mParent.updateConnectionState(STATE_KO);
			} else {
				mParent.updateConnectionState(STATE_OK);
			}

			final Type type = _response.getRequestType();
			final Code code = _response.getRequestCode();

			boolean usingVolumeSeekbar = VOLUME.equals(type) && DEFINE.equals(code);
			if (!usingVolumeSeekbar) {
				sendToastToUI(message);
			} else {
				final int volume = _response.getIntValue();

				Rect hitRect = new Rect();
				mSbVolume.getDrawingRect(hitRect);
				final int x = mSbVolume.getLeft() + (int)((float)volume / (float)mSbVolume.getMax() * mSbVolume.getWidth());
				final int y = mSbVolume.getTop();
				showVolumeToast(volume + "%", x, y);
			}

			// Handle UI mute icon
			if (VOLUME.equals(type) && MUTE.equals(code)) {
				if (_response.getIntValue() == 0) { // Mute
					mIbMute.setImageResource(R.drawable.volume_muted);
				} else if (_response.getIntValue() == 1) { // Volume On
					mIbMute.setImageResource(R.drawable.volume_on);
				}
			}
		}
	}
}
