package org.es.uremote.computer;


import static android.view.HapticFeedbackConstants.VIRTUAL_KEY;
import static org.es.network.ExchangeProtos.Request.Code.KILL_SERVER;
import static org.es.network.ExchangeProtos.Request.Code.LOCK;
import static org.es.network.ExchangeProtos.Request.Code.MUTE;
import static org.es.network.ExchangeProtos.Request.Code.SHUTDOWN;
import static org.es.network.ExchangeProtos.Request.Type.AI;
import static org.es.network.ExchangeProtos.Request.Type.SIMPLE;
import static org.es.network.ExchangeProtos.Response.ReturnCode.RC_ERROR;
import static org.es.uremote.utils.Constants.STATE_CONNECTING;
import static org.es.uremote.utils.Constants.STATE_KO;
import static org.es.uremote.utils.Constants.STATE_OK;

import org.es.network.AsyncMessageMgr;
import org.es.network.ExchangeProtos.Request;
import org.es.network.ExchangeProtos.Response;
import org.es.network.IRequestSender;
import org.es.network.NetworkMessage;
import org.es.uremote.Computer;
import org.es.uremote.R;
import org.es.uremote.network.WakeOnLan;
import org.es.utils.Log;

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

/**
 * Class to connect and send commands to a remote server through AsyncTask.
 * 
 * @author Cyril Leroux
 *
 */
public class FragAdmin extends Fragment implements OnClickListener, IRequestSender {
	private static final String TAG	= "FragAdmin";

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
	public void onClick(View _view) {
		_view.performHapticFeedback(VIRTUAL_KEY);

		switch (_view.getId()) {

		case R.id.cmdWakeOnLan :
			wakeOnLan();
			break;

		case R.id.cmdShutdown :
			confirmRequest(NetworkMessage.buildRequest(AsyncMessageMgr.getSecurityToken(), SIMPLE, SHUTDOWN));
			break;

		case R.id.cmdAiMute :
			sendAsyncRequest(NetworkMessage.buildRequest(AsyncMessageMgr.getSecurityToken(), AI, MUTE));
			break;

		case R.id.cmdKillServer :
			confirmRequest(NetworkMessage.buildRequest(AsyncMessageMgr.getSecurityToken(), SIMPLE, KILL_SERVER));
			break;

		case R.id.cmdLock :
			confirmRequest(NetworkMessage.buildRequest(AsyncMessageMgr.getSecurityToken(), SIMPLE, LOCK));
			break;

		default:
			break;
		}
	}

	private void wakeOnLan() {

		final WifiManager wifiMgr = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		final boolean wifi = wifiMgr.isWifiEnabled();
		final int resKeyHost = wifi ? R.string.pref_key_broadcast : R.string.pref_key_remote_host;
		final int resDefHost = wifi ? R.string.pref_default_broadcast : R.string.pref_default_remote_host;

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

		final String keyHost = getString(resKeyHost);
		final String defHost = getString(resDefHost);
		final String host = pref.getString(keyHost, defHost);

		final String keyMAcAddress	= getString(R.string.pref_key_mac_address);
		final String defaultMAcAddress	= getString(R.string.pref_default_mac_address);
		final String macAddress = pref.getString(keyMAcAddress, defaultMAcAddress);

		new WakeOnLan(Computer.getHandler()).execute(host, macAddress);
	}

	////////////////////////////////////////////////////////////////////
	// *********************** Message Sender *********************** //
	////////////////////////////////////////////////////////////////////

	@Override
	public void sendAsyncRequest(Request _request) {

		if (AdminMessageMgr.availablePermits() > 0) {
			new AdminMessageMgr(Computer.getHandler()).execute(_request);
		} else {
			Log.warning(TAG, getString(R.string.msg_no_more_permit));
		}
	}

	/**
	 * Ask for the user to confirm before sending a request to the server.
	 * @param _request The request to send.
	 */
	public void confirmRequest(final Request _request) {
		int resId = (KILL_SERVER.equals(_request.getCode())) ? R.string.confirm_kill_server : R.string.confirm_command;

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setIcon(android.R.drawable.ic_menu_more);
		builder.setMessage(resId);
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
	 * Specialize for Admin commands.
	 * @author Cyril Leroux
	 * 
	 */
	public class AdminMessageMgr extends AsyncMessageMgr {

		/**
		 * @param _handler The toast messages handler.
		 */
		public AdminMessageMgr(Handler _handler) {
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

			sendToastToUI(_response.getMessage());

			if (RC_ERROR.equals(_response.getReturnCode())) {
				mParent.updateConnectionState(STATE_KO);
			} else {
				mParent.updateConnectionState(STATE_OK);
			}
		}
	}
}
