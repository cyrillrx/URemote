package org.es.uremote.computer;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import org.es.uremote.exchange.ExchangeMessages.Request;
import org.es.uremote.exchange.ExchangeMessages.Response;
import org.es.uremote.exchange.RequestSender;
import org.es.uremote.Computer;
import org.es.uremote.R;
import org.es.uremote.computer.dao.ServerSettingDao;
import org.es.uremote.network.AsyncMessageMgr;
import org.es.uremote.exchange.ExchangeMessagesUtils;
import org.es.uremote.network.WakeOnLan;
import org.es.uremote.objects.ServerSetting;
import org.es.uremote.utils.TaskCallbacks;
import org.es.utils.Log;

import static android.view.HapticFeedbackConstants.VIRTUAL_KEY;
import static org.es.uremote.exchange.ExchangeMessages.Request.Code.KILL_SERVER;
import static org.es.uremote.exchange.ExchangeMessages.Request.Code.LOCK;
import static org.es.uremote.exchange.ExchangeMessages.Request.Code.MUTE;
import static org.es.uremote.exchange.ExchangeMessages.Request.Code.SHUTDOWN;
import static org.es.uremote.exchange.ExchangeMessages.Request.Type.AI;
import static org.es.uremote.exchange.ExchangeMessages.Request.Type.SIMPLE;

/**
 * Class to connect and send commands to a remote server through AsyncTask.
 *
 * @author Cyril Leroux
 * Created on 22/05/12.
 */
public class FragAdmin extends Fragment implements OnClickListener, RequestSender {

	private static final String TAG = "FragAdmin";

	private TaskCallbacks mCallbacks;
	private Computer mParent;

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
		mParent = (Computer) getActivity();
	}

	/** Called when the application is created. */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.server_frag_admin, container, false);

		((Button) view.findViewById(R.id.cmdWakeOnLan)).setOnClickListener(this);
		((Button) view.findViewById(R.id.cmdShutdown)).setOnClickListener(this);
		((Button) view.findViewById(R.id.cmdAiMute)).setOnClickListener(this);
		((Button) view.findViewById(R.id.cmdKillServer)).setOnClickListener(this);
		((Button) view.findViewById(R.id.cmdLock)).setOnClickListener(this);

		return view;
	}

	@Override
	public void onStart() {
		getActivity().getActionBar().setIcon(R.drawable.ic_launcher);
		super.onStart();
	}

	@Override
	public void onClick(View view) {
		view.performHapticFeedback(VIRTUAL_KEY);

		switch (view.getId()) {

			case R.id.cmdWakeOnLan:
				wakeOnLan();
				break;

			case R.id.cmdShutdown:
				confirmRequest(ExchangeMessagesUtils.buildRequest(AsyncMessageMgr.getSecurityToken(), SIMPLE, SHUTDOWN));
				break;

			case R.id.cmdAiMute:
				sendRequest(ExchangeMessagesUtils.buildRequest(AsyncMessageMgr.getSecurityToken(), AI, MUTE));
				break;

			case R.id.cmdKillServer:
				confirmRequest(ExchangeMessagesUtils.buildRequest(AsyncMessageMgr.getSecurityToken(), SIMPLE, KILL_SERVER));
				break;

			case R.id.cmdLock:
				confirmRequest(ExchangeMessagesUtils.buildRequest(AsyncMessageMgr.getSecurityToken(), SIMPLE, LOCK));
				break;

			default:
				break;
		}
	}

	private void wakeOnLan() {

		final WifiManager wifiMgr = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		final boolean wifi = wifiMgr.isWifiEnabled();
		final int resKeyHost = wifi ? R.string.key_broadcast : R.string.key_remote_host;
		final int resDefHost = wifi ? R.string.default_broadcast : R.string.default_remote_host;

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

		final String keyHost	= getString(resKeyHost);
		final String defHost	= getString(resDefHost);
		final String host		= pref.getString(keyHost, defHost);

		final String keyMAcAddress		= getString(R.string.key_mac_address);
		final String defaultMAcAddress	= getString(R.string.default_mac_address);
		final String macAddress			= pref.getString(keyMAcAddress, defaultMAcAddress);

		new WakeOnLan(Computer.getHandler()).execute(host, macAddress);
	}

	////////////////////////////////////////////////////////////////////
	// *********************** Message Sender *********************** //
	////////////////////////////////////////////////////////////////////

	@Override
	public void sendRequest(Request request) {

		if (AdminMessageMgr.availablePermits() > 0) {
			new AdminMessageMgr(Computer.getHandler()).execute(request);
		} else {
			Log.warning(TAG, "#sendRequest - " + getString(R.string.msg_no_more_permit));
		}
	}

    @Override
    public ServerSetting getServerSetting() {
        return ((Computer)mCallbacks).getServer();
    }

    /**
	 * Ask for the user to confirm before sending a request to the server.
	 *
	 * @param request The request to send.
	 */
	public void confirmRequest(final Request request) {
		int resId = (KILL_SERVER.equals(request.getCode())) ? R.string.confirm_kill_server : R.string.confirm_command;

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setIcon(android.R.drawable.ic_menu_more);
		builder.setMessage(resId);
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				// Send the request if the user confirms it
				sendRequest(request);
			}
		});

		builder.setNegativeButton(android.R.string.cancel, null);
		builder.show();
	}

	/**
	 * Class that handle asynchronous requests sent to a remote server.
	 * Specialize for Admin commands.
	 *
	 * @author Cyril Leroux
	 */
	public class AdminMessageMgr extends AsyncMessageMgr {

		/** @param handler The toast messages handler. */
		public AdminMessageMgr(Handler handler) {
            super(handler, getServerSetting());
            // TODO clean
            //super(handler, ServerSettingDao.loadFromPreferences(getActivity().getApplicationContext()));
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
			sendToastToUI(response.getMessage());
		}
	}
}
