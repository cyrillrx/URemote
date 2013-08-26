package org.es.uremote.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.es.uremote.exchange.ExchangeMessages.Request;
import org.es.uremote.exchange.ExchangeMessages.Request.Code;
import org.es.uremote.exchange.ExchangeMessages.Request.Type;
import org.es.uremote.R;
import org.es.uremote.dao.ServerSettingDao;
import org.es.uremote.network.AsyncMessageMgr;
import org.es.uremote.network.MessageHelper;
import org.es.utils.Log;

import static android.widget.Toast.LENGTH_SHORT;
import static org.es.uremote.exchange.ExchangeMessages.Request.Code.MEDIA_NEXT;
import static org.es.uremote.exchange.ExchangeMessages.Request.Code.MEDIA_PLAY_PAUSE;
import static org.es.uremote.exchange.ExchangeMessages.Request.Code.MEDIA_PREVIOUS;
import static org.es.uremote.exchange.ExchangeMessages.Request.Code.MEDIA_STOP;
import static org.es.uremote.exchange.ExchangeMessages.Request.Type.KEYBOARD;
import static org.es.uremote.utils.Constants.MESSAGE_WHAT_TOAST;

/** @author Cyril Leroux */
public class MediaWidgetProvider extends AppWidgetProvider {

	private static final String TAG = "MediaWidgetProvider";

	private static final String ACTION_MEDIA_PREVIOUS	= "ACTION_MEDIA_PREVIOUS";
	private static final String ACTION_MEDIA_PLAY_PAUSE	= "ACTION_MEDIA_PLAY_PAUSE";
	private static final String ACTION_MEDIA_STOP		= "ACTION_MEDIA_STOP";
	private static final String ACTION_MEDIA_NEXT		= "ACTION_MEDIA_NEXT";

	/** Handler the display of toast messages. */
	private static Handler sHandler;
	private static Toast sToast = null;

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		initHandler(context);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

		// Get ids of all the instances of the widget
		ComponentName widget = new ComponentName(context, MediaWidgetProvider.class);
		int[] widgetIds = appWidgetManager.getAppWidgetIds(widget);

		for (int widgetId : widgetIds) {

			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_media);

			// Register onClickListeners

			Intent previousIntent = new Intent(context, MediaWidgetProvider.class);
			previousIntent.setAction(ACTION_MEDIA_PREVIOUS);
			PendingIntent previousPendingIntent = PendingIntent.getBroadcast(context, widgetId, previousIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.cmdPrevious, previousPendingIntent);

			Intent playPauseIntent = new Intent(context, MediaWidgetProvider.class);
			playPauseIntent.setAction(ACTION_MEDIA_PLAY_PAUSE);
			PendingIntent playPausePendingIntent = PendingIntent.getBroadcast(context, widgetId, playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.cmdPlayPause, playPausePendingIntent);

			Intent stopIntent = new Intent(context, MediaWidgetProvider.class);
			stopIntent.setAction(ACTION_MEDIA_STOP);
			PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, widgetId, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.cmdStop, stopPendingIntent);

			Intent nextIntent = new Intent(context, MediaWidgetProvider.class);
			nextIntent.setAction(ACTION_MEDIA_NEXT);
			PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context, widgetId, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.cmdNext, nextPendingIntent);

			appWidgetManager.updateAppWidget(widgetId, remoteViews);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);

		Log.debug(TAG, "MediaWidgetProvider onReceive");

		final String mediaCommand = intent.getAction();
		Log.debug(TAG, "Action : " + intent.getAction());

		if (ACTION_MEDIA_PREVIOUS.equals(mediaCommand)) {
			Toast.makeText(context, "MEDIA_PREVIOUS", LENGTH_SHORT).show();
			sendAsyncRequest(context, KEYBOARD, MEDIA_PREVIOUS);

		} else if (ACTION_MEDIA_PLAY_PAUSE.equals(mediaCommand)) {
			Toast.makeText(context, "MEDIA_PLAY_PAUSE", LENGTH_SHORT).show();
			sendAsyncRequest(context, KEYBOARD, MEDIA_PLAY_PAUSE);

		} else if (ACTION_MEDIA_STOP.equals(mediaCommand)) {
			Toast.makeText(context, "MEDIA_STOP", LENGTH_SHORT).show();
			sendAsyncRequest(context, KEYBOARD, MEDIA_STOP);

		} else if (ACTION_MEDIA_NEXT.equals(mediaCommand)) {
			Toast.makeText(context, "MEDIA_NEXT", LENGTH_SHORT).show();
			sendAsyncRequest(context, KEYBOARD, MEDIA_NEXT);

		}
	}

	/**
	 * Initializes the message handler then send the request.
	 *
	 * @param context
	 * @param requestType The request type.
	 * @param requestCode The request code.
	 */
	public void sendAsyncRequest(Context context, Type requestType, Code requestCode) {
		Request request = MessageHelper.buildRequest(MessageHelper.getSecurityToken(context), requestType, requestCode);

		if (request == null) {
			Toast.makeText(context, R.string.msg_null_request, LENGTH_SHORT).show();
			return;
		}

		if (AsyncMessageMgr.availablePermits() > 0) {
			new AsyncMessageMgr(sHandler, ServerSettingDao.loadFromPreferences(context)).execute(request);
		} else {
			Toast.makeText(context, R.string.msg_no_more_permit, LENGTH_SHORT).show();
		}
	}

	/**
	 * Initialize the toast message handler.
	 *
	 * @param _context The context used to display toast messages.
	 */
	private static void initHandler(final Context _context) {
		if (sHandler == null) {
			sHandler = new Handler() {
				@Override
				public void handleMessage(Message _msg) {
					switch (_msg.what) {
						case MESSAGE_WHAT_TOAST:
							showStaticToast(_context, (String) _msg.obj);
							break;

						default: break;
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
}
