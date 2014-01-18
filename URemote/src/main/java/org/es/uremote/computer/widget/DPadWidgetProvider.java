package org.es.uremote.computer.widget;

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

import org.es.uremote.Computer;
import org.es.uremote.R;
import org.es.uremote.computer.dao.ServerSettingDao;
import org.es.uremote.exchange.ExchangeMessages.Request;
import org.es.uremote.exchange.ExchangeMessages.Request.Code;
import org.es.uremote.exchange.ExchangeMessages.Request.Type;
import org.es.uremote.exchange.ExchangeMessagesUtils;
import org.es.uremote.network.AsyncMessageMgr;
import org.es.utils.Log;

import static android.widget.Toast.LENGTH_SHORT;
import static org.es.uremote.utils.Constants.MESSAGE_WHAT_TOAST;

/**
 * @author Cyril Leroux.
 *         Created on 18/01/14.
 */
public class DPadWidgetProvider extends AppWidgetProvider {

	private static final String TAG = "DPadWidgetProvider";

    private static final String ACTION_START_ACTIVITY   = "ACTION_START_ACTIVITY";
	private static final String ACTION_OK    = "ACTION_OK";
	private static final String ACTION_LEFT  = "ACTION_LEFT";
    private static final String ACTION_RIGHT = "ACTION_RIGHT";
    private static final String ACTION_UP    = "ACTION_UP";
	private static final String ACTION_DOWN  = "ACTION_DOWN";

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
		ComponentName widget = new ComponentName(context, DPadWidgetProvider.class);
		int[] widgetIds = appWidgetManager.getAppWidgetIds(widget);

		for (int widgetId : widgetIds) {

			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_dpad);

			// Register onClickListeners
            Intent okIntent = new Intent(context, DPadWidgetProvider.class);
            okIntent.setAction(ACTION_OK);
            PendingIntent okPendingIntent = PendingIntent.getBroadcast(context, widgetId, okIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.dpadOk, okPendingIntent);

            Intent leftIntent = new Intent(context, DPadWidgetProvider.class);
            leftIntent.setAction(ACTION_LEFT);
			PendingIntent leftPendingIntent = PendingIntent.getBroadcast(context, widgetId, leftIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.dpadLeft, leftPendingIntent);

			Intent rightIntent = new Intent(context, DPadWidgetProvider.class);
			rightIntent.setAction(ACTION_RIGHT);
			PendingIntent rightPendingIntent = PendingIntent.getBroadcast(context, widgetId, rightIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.dpadRight, rightPendingIntent);

			Intent upIntent = new Intent(context, DPadWidgetProvider.class);
			upIntent.setAction(ACTION_UP);
			PendingIntent upPendingIntent = PendingIntent.getBroadcast(context, widgetId, upIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.dpadUp, upPendingIntent);

			Intent downIntent = new Intent(context, DPadWidgetProvider.class);
			downIntent.setAction(ACTION_DOWN);
            PendingIntent downPendingIntent = PendingIntent.getBroadcast(context, widgetId, downIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.dpadDown, downPendingIntent);

            Intent startActivityIntent = new Intent(context, MediaWidgetProvider.class);
            startActivityIntent.setAction(ACTION_START_ACTIVITY);
            PendingIntent startActivityPendingIntent = PendingIntent.getBroadcast(context, widgetId, startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.launchApp, startActivityPendingIntent);

			appWidgetManager.updateAppWidget(widgetId, remoteViews);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);

		Log.debug(TAG, "DPadWidgetProvider onReceive");

		final String action = intent.getAction();
		Log.debug(TAG, "Action : " + intent.getAction());

        if (ACTION_OK.equals(action)) {
            Toast.makeText(context, "OK", LENGTH_SHORT).show();
            sendAsyncRequest(context, Type.KEYBOARD, Code.KB_RETURN);

        } else if (ACTION_LEFT.equals(action)) {
			Toast.makeText(context, "LEFT", LENGTH_SHORT).show();
			sendAsyncRequest(context, Type.KEYBOARD, Code.LEFT);

		} else if (ACTION_RIGHT.equals(action)) {
			Toast.makeText(context, "RIGHT", LENGTH_SHORT).show();
			sendAsyncRequest(context, Type.KEYBOARD, Code.RIGHT);

		} else if (ACTION_UP.equals(action)) {
			Toast.makeText(context, "UP", LENGTH_SHORT).show();
			sendAsyncRequest(context, Type.KEYBOARD, Code.UP);

		} else if (ACTION_DOWN.equals(action)) {
			Toast.makeText(context, "DOWN", LENGTH_SHORT).show();
			sendAsyncRequest(context, Type.KEYBOARD, Code.DOWN);

		} else if (ACTION_START_ACTIVITY.equals(action)) {
            Intent startActivityIntent = new Intent(context, Computer.class);
            startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startActivityIntent);
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
		Request request = ExchangeMessagesUtils.buildRequest(ExchangeMessagesUtils.getSecurityToken(context), requestType, requestCode);

		if (request == null) {
			Toast.makeText(context, R.string.msg_null_request, LENGTH_SHORT).show();
			return;
		}

		if (AsyncMessageMgr.availablePermits() > 0) {
			new AsyncMessageMgr(sHandler, ServerSettingDao.loadSelected(context)).execute(request);
		} else {
			Toast.makeText(context, R.string.msg_no_more_permit, LENGTH_SHORT).show();
		}
	}

	/**
	 * Initialize the toast message handler.
	 *
	 * @param context The context used to display toast messages.
	 */
	private static void initHandler(final Context context) {
		if (sHandler == null) {
			sHandler = new Handler() {
				@Override
				public void handleMessage(Message _msg) {
					switch (_msg.what) {
						case MESSAGE_WHAT_TOAST:
							showStaticToast(context, (String) _msg.obj);
							break;

						default: break;
					}
					super.handleMessage(_msg);
				}

			};
		}
	}

	private static void showStaticToast(final Context context, final String message) {
		if (sToast == null) {
			sToast = Toast.makeText(context, "", LENGTH_SHORT);
		}
		sToast.setText(message);
		sToast.show();
	}
}
