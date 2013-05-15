package org.es.uremote.widget;

import static android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_IDS;
import static android.widget.Toast.LENGTH_SHORT;
import static org.es.network.ExchangeProtos.Request.Code.MEDIA_NEXT;
import static org.es.network.ExchangeProtos.Request.Code.MEDIA_PLAY_PAUSE;
import static org.es.network.ExchangeProtos.Request.Code.MEDIA_PREVIOUS;
import static org.es.network.ExchangeProtos.Request.Code.MEDIA_STOP;
import static org.es.network.ExchangeProtos.Request.Code.NONE;
import static org.es.network.ExchangeProtos.Request.Type.KEYBOARD;
import static org.es.uremote.utils.Constants.MESSAGE_WHAT_TOAST;

import java.util.Random;

import org.es.network.AsyncMessageMgr;
import org.es.network.ExchangeProtos.Request;
import org.es.network.ExchangeProtos.Request.Code;
import org.es.network.ExchangeProtos.Request.Type;
import org.es.uremote.R;
import org.es.uremote.service.SendRequestService;
import org.es.utils.Log;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * @author Cyril Leroux
 *
 */
public class MediaWidgerProvider extends AppWidgetProvider {

	private static final String TAG = "MediaWidgerProvider";

	private static final String ACTION_MEDIA_PREVIOUS	= "ACTION_MEDIA_PREVIOUS";
	private static final String ACTION_MEDIA_PLAY_PAUSE	= "ACTION_MEDIA_PLAY_PAUSE";
	private static final String ACTION_MEDIA_STOP		= "ACTION_MEDIA_STOP";
	private static final String ACTION_MEDIA_NEXT		= "ACTION_MEDIA_NEXT";

	/** Handler the display of toast messages. */
	private static Handler sHandler;
	private static Toast sToast = null;

	private ServerData mServerData;

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		initHandler(context);
		mServerData = initServer(context);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

		// Get ids of all the instances of the widget
		ComponentName widget = new ComponentName(context, MediaWidgerProvider.class);
		int[] widgetIds = appWidgetManager.getAppWidgetIds(widget);

		for (int widgetId : widgetIds) {

			// Create some random data
			int number = (new Random().nextInt(100));
			final String data = String.valueOf(number);

			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_media);

			// Set the text
			//	remoteViews.setTextViewText(R.id.textToUpdate, data);
			//	remoteViews.setTextViewText(R.id.textToUpdate, data);

			// Register onClickListeners
			Intent previousIntent = new Intent(context, MediaWidgerProvider.class);
			previousIntent.setAction(ACTION_MEDIA_PREVIOUS);
			PendingIntent previousPendingIntent = PendingIntent.getBroadcast(context, widgetId, previousIntent, PendingIntent.FLAG_UPDATE_CURRENT );
			remoteViews.setOnClickPendingIntent(R.id.cmdPrevious, previousPendingIntent);

			Intent playPauseIntent = new Intent(context, MediaWidgerProvider.class);
			playPauseIntent.setAction(ACTION_MEDIA_PLAY_PAUSE);
			PendingIntent playPausePendingIntent = PendingIntent.getBroadcast(context, widgetId, playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT );
			remoteViews.setOnClickPendingIntent(R.id.cmdPlayPause, playPausePendingIntent);

			Intent stopIntent = new Intent(context, MediaWidgerProvider.class);
			stopIntent.setAction(ACTION_MEDIA_STOP);
			PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, widgetId, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT );
			remoteViews.setOnClickPendingIntent(R.id.cmdStop, stopPendingIntent);

			Intent nextIntent = new Intent(context, MediaWidgerProvider.class);
			nextIntent.setAction(ACTION_MEDIA_NEXT);
			PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context, widgetId, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT );
			remoteViews.setOnClickPendingIntent(R.id.cmdNext, nextPendingIntent);

			appWidgetManager.updateAppWidget(widgetId, remoteViews);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);

		Log.debug(TAG, "MediaWidgerProvider onReceive");

		final String mediaCommand = intent.getAction();
		Log.debug(TAG, "Action : " + intent.getAction());

		if (ACTION_MEDIA_PREVIOUS.equals(mediaCommand)) {
			Toast.makeText(context, "MEDIA_PREVIOUS", LENGTH_SHORT).show();
			sendAsyncRequest(context, buildRequest(KEYBOARD, MEDIA_PREVIOUS));

		} else if (ACTION_MEDIA_PLAY_PAUSE.equals(mediaCommand)) {
			Toast.makeText(context, "MEDIA_PLAY_PAUSE", LENGTH_SHORT).show();
			sendAsyncRequest(context, buildRequest(KEYBOARD, MEDIA_PLAY_PAUSE));

		} else if (ACTION_MEDIA_STOP.equals(mediaCommand)) {
			Toast.makeText(context, "MEDIA_STOP", LENGTH_SHORT).show();
			sendAsyncRequest(context, buildRequest(KEYBOARD, MEDIA_STOP));

		} else if (ACTION_MEDIA_NEXT.equals(mediaCommand)) {
			Toast.makeText(context, "MEDIA_NEXT", LENGTH_SHORT).show();
			sendAsyncRequest(context, buildRequest(KEYBOARD, MEDIA_NEXT));

		} else {
			Toast.makeText(context, "Nothing", LENGTH_SHORT).show();
		}
	}

	/**
	 * Build a request with a code.
	 * @param _type The request type.
	 * @param _code The request code.
	 * @return The request if it had been initialized. Return null otherwise.
	 */
	public Request buildRequest(final Type _type, final Code _code) {
		return buildRequest(_type, _code, NONE, 0, "");
	}

	/**
	 * Build a request with both integer and string parameters.
	 * @param _type The request type.
	 * @param _code The request code.
	 * @param _extraCode The request extra code.
	 * @param _intParam An integer parameter.
	 * @param _stringParam A string parameter.
	 * @return The request if it had been initialized. Return null otherwise.
	 */
	private Request buildRequest(final Type _type, final Code _code, final Code _extraCode, final int _intParam, final String _stringParam) {
		Request request = Request.newBuilder()
				.setType(_type)
				.setCode(_code)
				.setExtraCode(_extraCode)
				.setIntParam(_intParam)
				.setStringParam(_stringParam)
				.setSecurityToken(mServerData.getSecurityToken()) // Add the security token
				.build();

		if (request.isInitialized()) {
			return request;
		}

		Log.error(TAG, "buildRequest() request is NOT initialized");
		return null;
	}

	/**
	 * Initializes the message handler then send the request.
	 * @param context
	 * @param _request The request to send.
	 */
	public void sendAsyncRequest(Context context, Request _request) {
		if (_request == null) {
			Toast.makeText(context, R.string.msg_null_request, LENGTH_SHORT).show();
			return;
		}

		if (AsyncMessageMgr.availablePermits() > 0) {
			new AsyncMessageMgr(sHandler).execute(_request);
		} else {
			Toast.makeText(context, R.string.msg_no_more_permit, LENGTH_SHORT).show();
		}
	}

	/**
	 * Initialize the toast message handler.
	 * @param _context The context used to display toast messages.
	 */
	private static void initHandler(final Context _context) {
		if (sHandler == null) {
			sHandler = new Handler() {
				@Override
				public void handleMessage(Message _msg) {
					switch (_msg.what) {
					case MESSAGE_WHAT_TOAST:
						showStaticToast(_context, (String)_msg.obj);
						break;

					default : break;
					}
					super.handleMessage(_msg);
				}

			};
		}
	}

	private static void showStaticToast(final Context _context, final String _message) {
		if (sToast == null) {
			sToast = Toast.makeText(_context, "", LENGTH_SHORT);
		}
		sToast.setText(_message);
		sToast.show();
	}

	private class ServerData {

		private final String mSecurityToken;
		private final String mHost;
		private final int mPort;
		private final int mConnectionTimeout;
		private final int mReadTimeout;

		public ServerData(String host, int port, int connectionTimeout, int readTimeout, String securityToken) {
			mHost = host;
			mPort = port;
			mConnectionTimeout = connectionTimeout;
			mReadTimeout = readTimeout;
			mSecurityToken = securityToken;
		}

		public String getSecurityToken() {
			return mSecurityToken;
		}

		public String getHost() {
			return mHost;
		}

		public int getPort() {
			return mPort;
		}

		public int getConnectionTimeout() {
			return mConnectionTimeout;
		}

		public int getReadTimeout() {
			return mReadTimeout;
		}
	}

	private ServerData initServer(Context context) {

		final WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		final boolean wifi = wifiMgr.isWifiEnabled();

		// Get Host and Port key
		final int resKeyHost = wifi ? R.string.pref_key_local_host : R.string.pref_key_remote_host;
		final int resKeyPort = wifi ? R.string.pref_key_local_port : R.string.pref_key_remote_port;
		final String keyHost	= context.getString(resKeyHost);
		final String keyPort	= context.getString(resKeyPort);

		// Get key for other properties
		final String keySecurityToken		= context.getString(R.string.pref_key_security_token);
		final String keyConnectionTimeout	= context.getString(R.string.pref_key_connection_timeout);
		final String keyReadTimeout			= context.getString(R.string.pref_key_read_timeout);

		// Get default values for Host and Port
		final int resDefHost = wifi ? R.string.pref_default_local_host : R.string.pref_default_remote_host;
		final int resDefPort = wifi ? R.string.pref_default_local_port : R.string.pref_default_remote_port;
		final String defaultHost	= context.getString(resDefHost);
		final String defaultPort	= context.getString(resDefPort);

		// Get default values for other properties
		final String defaultSecurityToken		= context.getString(R.string.pref_default_security_token);
		final String defaultConnectionTimeout	= context.getString(R.string.pref_default_connection_timeout);
		final String defaultReadTimeout			= context.getString(R.string.pref_default_read_timeout);

		// Get the properties values
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
		final String host	= pref.getString(keyHost, defaultHost);
		final int port		= Integer.parseInt(pref.getString(keyPort, defaultPort));
		final String securityToken	= pref.getString(keySecurityToken, defaultSecurityToken);
		final int connectionTimeout	= Integer.parseInt(pref.getString(keyConnectionTimeout, defaultConnectionTimeout));
		final int readTimeout		= Integer.parseInt(pref.getString(keyReadTimeout, defaultReadTimeout));

		return new ServerData(host, port, connectionTimeout, readTimeout, securityToken);
	}
}
