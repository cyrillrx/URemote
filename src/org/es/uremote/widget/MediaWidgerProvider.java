package org.es.uremote.widget;

import static android.widget.Toast.LENGTH_SHORT;
import static org.es.network.ExchangeProtos.Request.Code.MEDIA_NEXT;
import static org.es.network.ExchangeProtos.Request.Code.MEDIA_PLAY_PAUSE;
import static org.es.network.ExchangeProtos.Request.Code.MEDIA_PREVIOUS;
import static org.es.network.ExchangeProtos.Request.Code.MEDIA_STOP;
import static org.es.network.ExchangeProtos.Request.Type.KEYBOARD;
import static org.es.uremote.utils.Constants.MESSAGE_WHAT_TOAST;

import java.util.Random;

import org.es.network.AsyncMessageMgr;
import org.es.network.ExchangeProtos.Request;
import org.es.uremote.R;

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

	/** Handler the display of toast messages. */
	private static Handler sHandler;
	private static Toast sToast = null;

	private static final String EXTRA_COMMAND		= "EXTRA_COMMAND";
	private static final String INTENT_MEDIA_PREVIOUS	= "MEDIA_PREVIOUS";
	private static final String INTENT_MEDIA_PLAY_PAUSE	= "MEDIA_PLAY_PAUSE";
	private static final String INTENT_MEDIA_STOP		= "MEDIA_STOP";
	private static final String INTENT_MEDIA_NEXT		= "MEDIA_NEXT";

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		initHandler(context);
		initServer(context);
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
			previousIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			previousIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
			previousIntent.putExtra(EXTRA_COMMAND, INTENT_MEDIA_PREVIOUS);
			PendingIntent previousPendingIntent = PendingIntent.getBroadcast(context, widgetId, previousIntent, PendingIntent.FLAG_UPDATE_CURRENT );
			remoteViews.setOnClickPendingIntent(R.id.cmdPrevious, previousPendingIntent);

			Intent playPauseIntent = new Intent(context, MediaWidgerProvider.class);
			playPauseIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			playPauseIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
			playPauseIntent.putExtra(EXTRA_COMMAND, INTENT_MEDIA_PLAY_PAUSE);
			PendingIntent playPausePendingIntent = PendingIntent.getBroadcast(context, widgetId, playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT );
			remoteViews.setOnClickPendingIntent(R.id.cmdPlayPause, playPausePendingIntent);

			Intent stopIntent = new Intent(context, MediaWidgerProvider.class);
			stopIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			stopIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
			stopIntent.putExtra(EXTRA_COMMAND, INTENT_MEDIA_STOP);
			PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, widgetId, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT );
			remoteViews.setOnClickPendingIntent(R.id.cmdStop, stopPendingIntent);

			Intent nextIntent = new Intent(context, MediaWidgerProvider.class);
			nextIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			nextIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
			nextIntent.putExtra(EXTRA_COMMAND, INTENT_MEDIA_NEXT);
			PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context, widgetId, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT );
			remoteViews.setOnClickPendingIntent(R.id.cmdNext, nextPendingIntent);

			appWidgetManager.updateAppWidget(widgetId, remoteViews);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);

		final String mediaCommand = intent.getStringExtra(EXTRA_COMMAND);

		if (MEDIA_PREVIOUS.equals(mediaCommand)) {
			Toast.makeText(context, "MEDIA_PREVIOUS", LENGTH_SHORT).show();
			sendAsyncRequest(context, AsyncMessageMgr.buildRequest(KEYBOARD, MEDIA_PREVIOUS));

		} else if (MEDIA_PLAY_PAUSE.equals(mediaCommand)) {
			Toast.makeText(context, "MEDIA_PLAY_PAUSE", LENGTH_SHORT).show();
			sendAsyncRequest(context, AsyncMessageMgr.buildRequest(KEYBOARD, MEDIA_PLAY_PAUSE));

		} else if (MEDIA_STOP.equals(mediaCommand)) {
			Toast.makeText(context, "MEDIA_STOP", LENGTH_SHORT).show();
			sendAsyncRequest(context, AsyncMessageMgr.buildRequest(KEYBOARD, MEDIA_STOP));

		} else if (MEDIA_NEXT.equals(mediaCommand)) {
			Toast.makeText(context, "MEDIA_NEXT", LENGTH_SHORT).show();
			sendAsyncRequest(context, AsyncMessageMgr.buildRequest(KEYBOARD, MEDIA_NEXT));

		} else {
			Toast.makeText(context, "Nothing", LENGTH_SHORT).show();
			sendAsyncRequest(context, AsyncMessageMgr.buildRequest(KEYBOARD, MEDIA_PLAY_PAUSE));
		}
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

	private void initServer(Context context) {

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

		// TODO hash the security token
		AsyncMessageMgr.setHost(host);
		AsyncMessageMgr.setPort(port);
		AsyncMessageMgr.setSecurityToken(securityToken);
		AsyncMessageMgr.setTimeout(connectionTimeout);
		AsyncMessageMgr.setSoTimeout(readTimeout);
	}
}
