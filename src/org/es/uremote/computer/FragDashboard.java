package org.es.uremote.computer;


import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
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
import android.widget.TextView;

import org.es.network.ExchangeProtos.Request;
import org.es.network.ExchangeProtos.Request.Code;
import org.es.network.ExchangeProtos.Request.Type;
import org.es.network.ExchangeProtos.Response;
import org.es.network.IRequestSender;
import org.es.uremote.Computer;
import org.es.uremote.R;
import org.es.uremote.dao.ServerSettingDao;
import org.es.uremote.network.AsyncMessageMgr;
import org.es.uremote.network.MessageHelper;
import org.es.uremote.utils.IntentKeys;
import org.es.utils.Log;

import static android.app.Activity.RESULT_OK;
import static android.view.HapticFeedbackConstants.VIRTUAL_KEY;
import static org.es.network.ExchangeProtos.Request.Code.DEFINE;
import static org.es.network.ExchangeProtos.Request.Code.MUTE;
import static org.es.network.ExchangeProtos.Request.Type.KEYBOARD;
import static org.es.network.ExchangeProtos.Request.Type.VOLUME;
import static org.es.network.ExchangeProtos.Response.ReturnCode.RC_ERROR;

/**
 * Class to connect and send commands to a remote server through AsyncTask.
 *
 * @author Cyril Leroux
 */
public class FragDashboard extends Fragment implements OnClickListener, OnSeekBarChangeListener, IRequestSender {
	private static final String TAG	= "FragDashboard";
	/** ActivityForResults request codes */
	private static final int RC_APP_LAUNCHER = 0;

	private static final int STATE_KO	= 0;
	private static final int STATE_OK	= 1;
	private static final int STATE_CONNECTING	= 2;

	private static final int DELAY		= 500;
	private static final int DURATION	= 500;

	private ObjectAnimator mFadeIn;
	private ObjectAnimator mFadeOut;

	private TextView mTvVolume;
	private ImageButton mIbMute;
	private SeekBar mSbVolume;
	private Computer mParent;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mParent = (Computer) getActivity();
	}

	/** Called when the application is created. */
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

		mTvVolume = (TextView) view.findViewById(R.id.toastText);
		mTvVolume.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Thin.ttf"));

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

			case R.id.kbLeft:
				sendAsyncRequest(KEYBOARD, Code.LEFT);
				break;

			case R.id.kbRight:
				sendAsyncRequest(KEYBOARD, Code.RIGHT);
				break;

			case R.id.kbUp:
				sendAsyncRequest(KEYBOARD, Code.UP);
				break;

			case R.id.kbDown:
				sendAsyncRequest(KEYBOARD, Code.DOWN);
				break;

			case R.id.kbOk:
				sendAsyncRequest(KEYBOARD, Code.KB_RETURN);
				break;

			case R.id.cmdTest:
				sendAsyncRequest(Type.SIMPLE, Code.TEST);
				break;

			case R.id.cmdSwitch:
				sendAsyncRequest(Type.SIMPLE, Code.SWITCH_WINDOW);
				break;

			case R.id.btnAppLauncher:
				startActivityForResult(new Intent(getActivity().getApplicationContext(), AppLauncher.class), RC_APP_LAUNCHER);
				break;

			case R.id.cmdGomStretch:
				sendAsyncRequest(Type.APP, Code.GOM_PLAYER_STRETCH);
				break;

			case R.id.cmdPrevious:
				sendAsyncRequest(KEYBOARD, Code.MEDIA_PREVIOUS);
				break;

			case R.id.cmdPlayPause:
				sendAsyncRequest(KEYBOARD, Code.MEDIA_PLAY_PAUSE);
				break;

			case R.id.cmdStop:
				sendAsyncRequest(KEYBOARD, Code.MEDIA_STOP);
				break;

			case R.id.cmdNext:
				sendAsyncRequest(KEYBOARD, Code.MEDIA_NEXT);
				break;

			case R.id.cmdMute:
				sendAsyncRequest(VOLUME, Code.MUTE);
				break;

			default:
				break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RC_APP_LAUNCHER && resultCode == RESULT_OK) {
			final Type type = Type.valueOf(data.getIntExtra(IntentKeys.REQUEST_TYPE, -1));
			final Code code = Code.valueOf(data.getIntExtra(IntentKeys.REQUEST_CODE, -1));

			if (type != null && code != null) {
				sendAsyncRequest(MessageHelper.buildRequest(AsyncMessageMgr.getSecurityToken(), type, code));
			}
		}
	}

	/**
	 * Show a static toast message.
	 * The next message replace the previous one.
	 *
	 * @param message The message to display.
	 * @param x The x position to display it.
	 * @param y The y position to display it.
	 */
	private void showVolumeToast(final String message, final int x, final int y) {
		mTvVolume.setText(message);
		fadeIn(mTvVolume);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if (!fromUser) {
			return;
		}
		final int volume = seekBar.getProgress();
		sendAsyncRequest(VOLUME, Code.DEFINE, volume);
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
	 *
	 * @param requestType The request type.
	 * @param requestCode The request code.
	 * @param intParam An integer parameter.
	 */
	public void sendAsyncRequest(Type requestType, Code requestCode, int intParam) {
		sendAsyncRequest(MessageHelper.buildRequest(AsyncMessageMgr.getSecurityToken(), requestType, requestCode, intParam));
	}

	/**
	 * Initializes the message handler then send the request.
	 *
	 * @param requestType The request type.
	 * @param requestCode The request code.
	 */
	public void sendAsyncRequest(Type requestType, Code requestCode) {
		sendAsyncRequest(MessageHelper.buildRequest(AsyncMessageMgr.getSecurityToken(), requestType, requestCode));
	}

	/**
	 * Initializes the message handler then send the request.
	 *
	 * @param request The request to send.
	 */
	@Override
	public void sendAsyncRequest(Request request) {

		if (DashboardMessageMgr.availablePermits() > 0) {
			new DashboardMessageMgr(Computer.getHandler()).execute(request);
		} else {
			final boolean defineVolume = VOLUME.equals(request.getType()) && DEFINE.equals(request.getCode());
			if (!defineVolume) {
				final String message = "#sendAsyncRequest - " + getString(R.string.msg_no_more_permit) + "\n" + request.toString();
				Log.warning(TAG, message);
			}
		}
	}

	/**
	 * Class that handle asynchronous requests sent to a remote server.
	 * Specialize for Dashboard.
	 *
	 * @author Cyril Leroux
	 */
	private class DashboardMessageMgr extends AsyncMessageMgr {

		public DashboardMessageMgr(Handler _handler) {
			super(_handler, ServerSettingDao.loadFromPreferences(getActivity().getApplicationContext()));
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mParent.updateConnectionState(STATE_CONNECTING);
		}

		@Override
		protected void onPostExecute(Response response) {
			super.onPostExecute(response);
			final String message = response.getMessage();
			Log.debug(TAG, "#onPostExecute - Sending response : " + message);

			// TODO handle return better than that
			if (RC_ERROR.equals(response.getReturnCode())) {
				mParent.updateConnectionState(STATE_KO);
			} else {
				mParent.updateConnectionState(STATE_OK);
			}

			final Type type = response.getRequestType();
			final Code code = response.getRequestCode();

			boolean usingVolumeSeekbar = VOLUME.equals(type) && DEFINE.equals(code);
			if (!usingVolumeSeekbar) {
				sendToastToUI(message);
			} else {
				final int volume = response.getIntValue();

				Rect hitRect = new Rect();
				mSbVolume.getDrawingRect(hitRect);
				final int x = mSbVolume.getLeft() + (int) ((float) volume / (float) mSbVolume.getMax() * mSbVolume.getWidth());
				final int y = mSbVolume.getTop();
				showVolumeToast(volume + "%", x, y);
			}

			// Handle UI mute icon
			if (VOLUME.equals(type) && MUTE.equals(code)) {
				if (response.getIntValue() == 0) { // Mute
					mIbMute.setImageResource(R.drawable.volume_muted);
				} else if (response.getIntValue() == 1) { // Volume On
					mIbMute.setImageResource(R.drawable.volume_on);
				}
			}
		}
	}

	/** Launch a fade in animation. */
	private void fadeIn(View view) {
		initFadeIn(view);
		initFadeOut(view);

		if (mFadeOut.isStarted()) {
			mFadeOut.cancel();
		}

		if (mFadeIn.isStarted()) {
			return;
		}

		if (view.getAlpha() != 1.0 || view.getVisibility() != View.VISIBLE) {
			mFadeIn.start();
			return;
		}
		fadeOut(view);
	}

	/** Launch a fade out animation. */
	private void fadeOut(View view) {
		if (mFadeOut.isStarted() && !mFadeOut.isRunning()) {
			mFadeOut.setStartDelay(DELAY);
		} else {
			initFadeOut(view);
			mFadeOut.start();
		}
	}

	private void initFadeIn(View view) {
		if (mFadeIn != null) {
			return;
		}
		mFadeIn = ObjectAnimator.ofFloat(view, "alpha", 1f).setDuration(DURATION);
		mFadeIn.setStartDelay(0);

		mFadeIn.addListener(new AnimatorListener() {

			private final View mView = mTvVolume;

			@Override
			public void onAnimationStart(Animator animation) {
				// TODO Replace mView by the view in parameter
				if (mView.getVisibility() != View.VISIBLE) {
					mView.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onAnimationRepeat(Animator animation) { }

			@Override
			public void onAnimationEnd(Animator animation) {
				// TODO Replace mView by the view in parameter
				final float alpha = mView.getAlpha();
				if (alpha == 1.0) {
					fadeOut(mView);

				}
			}

			@Override
			public void onAnimationCancel(Animator animation) { }
		});
	}

	private void initFadeOut(View view) {
		if (mFadeOut != null) {
			return;
		}
		mFadeOut = ObjectAnimator.ofFloat(view, "alpha", 0f).setDuration(DURATION);
		mFadeOut.setStartDelay(DELAY);

		mFadeOut.addListener(new AnimatorListener() {

			private final View mView = mTvVolume;

			@Override
			public void onAnimationStart(Animator animation) {}

			@Override
			public void onAnimationRepeat(Animator animation) { }

			@Override
			public void onAnimationEnd(Animator animation) {
				// TODO Replace mView by the view in parameter
				final float alpha = mView.getAlpha();

				if (alpha == 0.0) {
					// TODO Replace mView by the view in parameter
					if (mView.getVisibility() == View.VISIBLE) {
						mView.setVisibility(View.GONE);
						mFadeIn = null;
						mFadeOut = null;
					}
				}
			}

			@Override
			public void onAnimationCancel(Animator animation) { }
		});
	}
}
