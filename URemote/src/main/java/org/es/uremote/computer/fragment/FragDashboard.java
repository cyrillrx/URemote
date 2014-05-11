package org.es.uremote.computer.fragment;


import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import org.es.uremote.AppLauncherActivity;
import org.es.uremote.Computer;
import org.es.uremote.R;
import org.es.uremote.device.ServerSetting;
import org.es.uremote.exchange.Message.Request;
import org.es.uremote.exchange.Message.Request.Code;
import org.es.uremote.exchange.Message.Request.Type;
import org.es.uremote.exchange.Message.Response;
import org.es.uremote.exchange.MessageUtils;
import org.es.uremote.exchange.RequestSender;
import org.es.uremote.network.AsyncMessageMgr;
import org.es.uremote.objects.AppItem;
import org.es.uremote.utils.IntentKeys;
import org.es.uremote.utils.TaskCallbacks;
import org.es.utils.Log;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static android.view.HapticFeedbackConstants.VIRTUAL_KEY;
import static org.es.uremote.exchange.Message.Request.Code.DEFINE;
import static org.es.uremote.exchange.Message.Request.Code.MUTE;
import static org.es.uremote.exchange.Message.Request.Type.KEYBOARD;
import static org.es.uremote.exchange.Message.Request.Type.VOLUME;

/**
 * Class to connect and send commands to a remote server through AsyncTask.
 *
 * @author Cyril Leroux
 * Created on 21/04/12.
 */
public class FragDashboard extends Fragment implements OnClickListener, OnSeekBarChangeListener, RequestSender {

	private static final String TAG	= "FragDashboard";

	/** ActivityForResults request codes */
	private static final int RC_APP_LAUNCHER = 0;

	private static final int DELAY		= 500;
	private static final int DURATION	= 500;

	private TaskCallbacks mCallbacks;

	private ObjectAnimator mFadeIn;
	private ObjectAnimator mFadeOut;

	private TextView mTvVolume;
	private ImageButton mIbMute;
	private SeekBar mSbVolume;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mCallbacks = (TaskCallbacks) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Retain this fragment across configuration changes.
		setRetainInstance(true);
	}

	/**
	 * Set the callback to null so we don't accidentally leak the
	 * Activity instance.
	 */
	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	/** Called when the application is created. */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.server_frag_dashboard, container, false);

		view.findViewById(R.id.kbLeft).setOnClickListener(this);
		view.findViewById(R.id.kbRight).setOnClickListener(this);
		view.findViewById(R.id.kbUp).setOnClickListener(this);
		view.findViewById(R.id.kbDown).setOnClickListener(this);
		view.findViewById(R.id.kbOk).setOnClickListener(this);

		view.findViewById(R.id.cmdTest).setOnClickListener(this);
		view.findViewById(R.id.cmdSwitch).setOnClickListener(this);
		view.findViewById(R.id.cmdGomStretch).setOnClickListener(this);
		view.findViewById(R.id.btnAppLauncher).setOnClickListener(this);

		view.findViewById(R.id.cmdPrevious).setOnClickListener(this);
		view.findViewById(R.id.cmdPlayPause).setOnClickListener(this);
		view.findViewById(R.id.cmdStop).setOnClickListener(this);
		view.findViewById(R.id.cmdNext).setOnClickListener(this);

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
				sendRequest(KEYBOARD, Code.DPAD_LEFT);
				break;

			case R.id.kbRight:
				sendRequest(KEYBOARD, Code.DPAD_RIGHT);
				break;

			case R.id.kbUp:
				sendRequest(KEYBOARD, Code.DPAD_UP);
				break;

			case R.id.kbDown:
				sendRequest(KEYBOARD, Code.DPAD_DOWN);
				break;

			case R.id.kbOk:
				sendRequest(KEYBOARD, Code.KEYCODE_ENTER);
				break;

			case R.id.cmdTest:
				sendRequest(Type.SIMPLE, Code.TEST);
				break;

			case R.id.cmdSwitch:
				sendRequest(Type.SIMPLE, Code.SWITCH_WINDOW);
				break;

			case R.id.btnAppLauncher:
				final Intent appLauncherIntent = new Intent(getActivity().getApplicationContext(), AppLauncherActivity.class);
                ArrayList<AppItem> applicationList = new ArrayList<>();
                applicationList.add(new AppItem(getString(R.string.cmd_gom_start), "", R.drawable.app_gom_player));
                applicationList.add(new AppItem(getString(R.string.cmd_gom_kill), "", R.drawable.app_gom_player));
                appLauncherIntent.putParcelableArrayListExtra(IntentKeys.EXTRA_APPLICATION_LIST, applicationList);
				startActivityForResult(appLauncherIntent, RC_APP_LAUNCHER);
				break;

			case R.id.cmdGomStretch:
				sendRequest(Type.APP, Code.KEYCODE_0);
				break;

			case R.id.cmdPrevious:
				sendRequest(KEYBOARD, Code.MEDIA_PREVIOUS);
				break;

			case R.id.cmdPlayPause:
				sendRequest(KEYBOARD, Code.MEDIA_PLAY_PAUSE);
				break;

			case R.id.cmdStop:
				sendRequest(KEYBOARD, Code.MEDIA_STOP);
				break;

			case R.id.cmdNext:
				sendRequest(KEYBOARD, Code.MEDIA_NEXT);
				break;

			case R.id.cmdMute:
				sendRequest(VOLUME, Code.MUTE);
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
				sendRequest(MessageUtils.buildRequest(getSecurityToken(), type, code));
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
		sendRequest(VOLUME, Code.DEFINE, volume);
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
	public void sendRequest(Type requestType, Code requestCode, int intParam) {
		sendRequest(MessageUtils.buildRequest(getSecurityToken(), requestType, requestCode, intParam));
	}

	/**
	 * Initializes the message handler then send the request.
	 *
	 * @param requestType The request type.
	 * @param requestCode The request code.
	 */
	public void sendRequest(Type requestType, Code requestCode) {
		sendRequest(MessageUtils.buildRequest(getSecurityToken(), requestType, requestCode));
	}

	/**
	 * Initializes the message handler then send the request.
	 *
	 * @param request The request to send.
	 */
	@Override
	public void sendRequest(Request request) {

		if (DashboardMessageMgr.availablePermits() > 0) {
			new DashboardMessageMgr().execute(request);
		} else {
			final boolean defineVolume = VOLUME.equals(request.getType()) && DEFINE.equals(request.getCode());
			if (!defineVolume) {
				final String message = "#sendRequest - " + getString(R.string.msg_no_more_permit) + "\n" + request.toString();
				Log.warning(TAG, message);
			}
		}
	}

    @Override
    public ServerSetting getServerSetting() {
        return ((Computer)mCallbacks).getServer();
    }

    public String getSecurityToken() {
        ServerSetting settings = getServerSetting();
        if (settings != null) {
            return settings.getSecurityToken();
        }
        return "";
    }

	/**
	 * Class that handle asynchronous requests sent to a remote server.
	 * Specialize for Dashboard.
	 *
	 * @author Cyril Leroux
	 */
	private class DashboardMessageMgr extends AsyncMessageMgr {

		public DashboardMessageMgr() {
			super(getServerSetting());
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mCallbacks.onPreExecute();
		}

		@Override
		protected void onPostExecute(Response response) {
			super.onPostExecute(response);
			mCallbacks.onPostExecute(response);

			final String message = response.getMessage();
			Log.debug(TAG, "#onPostExecute - Sending response : " + message);

			final Type type = response.getRequestType();
			final Code code = response.getRequestCode();

			boolean usingVolumeSeekbar = VOLUME.equals(type) && DEFINE.equals(code);
			if (!usingVolumeSeekbar) {
                // TODO specify when to display a message => Wrap Request
				//sendToastToUI(message);
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
