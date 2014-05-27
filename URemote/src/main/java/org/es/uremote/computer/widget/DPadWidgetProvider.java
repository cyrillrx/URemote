package org.es.uremote.computer.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.es.uremote.ComputerActivity;
import org.es.uremote.R;
import org.es.uremote.computer.dao.NetworkDeviceDao;
import org.es.uremote.device.NetworkDevice;
import org.es.uremote.exchange.Message.Request.Code;
import org.es.uremote.exchange.Message.Request.Type;
import org.es.uremote.graphics.ConnectedDeviceDrawable;
import org.es.uremote.graphics.GraphicUtil;
import org.es.uremote.network.AsyncMessageMgr;
import org.es.utils.Log;

import static android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE;
import static android.widget.Toast.LENGTH_SHORT;
import static org.es.uremote.exchange.Message.Request;
import static org.es.uremote.utils.IntentKeys.EXTRA_SERVER_ID;

/**
 * @author Cyril Leroux.
 *         Created on 18/01/14.
 */
public class DPadWidgetProvider extends AppWidgetProvider {

    private static final String TAG = "DPadWidgetProvider";

    private static final String ACTION_START_ACTIVITY = "ACTION_START_ACTIVITY";
    private static final String ACTION_OK = "ACTION_OK";
    private static final String ACTION_LEFT = "ACTION_LEFT";
    private static final String ACTION_RIGHT = "ACTION_RIGHT";
    private static final String ACTION_UP = "ACTION_UP";
    private static final String ACTION_DOWN = "ACTION_DOWN";

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        Log.warning(TAG, "onUpdate");

        // Get ids of all the instances of the widget
        final ComponentName widget = new ComponentName(context, DPadWidgetProvider.class);
        final int[] widgetIds = appWidgetManager.getAppWidgetIds(widget);

        for (int widgetId : widgetIds) {
            updateWidget(context, appWidgetManager, widgetId, null, -1);
        }
    }

    public static void updateWidget(Context context, AppWidgetManager appWidgetManager, int widgetId, NetworkDevice device, int deviceId) {

        Log.warning(TAG, "updateWidget deviceId : " + deviceId);

        final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_dpad);

        if (device != null) {
            final Drawable drawable = new ConnectedDeviceDrawable(device);
            final Bitmap bmp = GraphicUtil.drawableToBitmap(drawable);
            remoteViews.setImageViewBitmap(R.id.thumbnail, bmp);
        }

        // Register onClickListeners

        final Intent okIntent = new Intent(context, DPadWidgetProvider.class);
        okIntent.setAction(ACTION_OK);
        PendingIntent okPendingIntent = PendingIntent.getBroadcast(context, widgetId, okIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.dpadOk, okPendingIntent);

        final Intent leftIntent = new Intent(context, DPadWidgetProvider.class);
        leftIntent.setAction(ACTION_LEFT);
        PendingIntent leftPendingIntent = PendingIntent.getBroadcast(context, widgetId, leftIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.dpadLeft, leftPendingIntent);

        final Intent rightIntent = new Intent(context, DPadWidgetProvider.class);
        rightIntent.setAction(ACTION_RIGHT);
        PendingIntent rightPendingIntent = PendingIntent.getBroadcast(context, widgetId, rightIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.dpadRight, rightPendingIntent);

        final Intent upIntent = new Intent(context, DPadWidgetProvider.class);
        upIntent.setAction(ACTION_UP);
        PendingIntent upPendingIntent = PendingIntent.getBroadcast(context, widgetId, upIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.dpadUp, upPendingIntent);

        final Intent downIntent = new Intent(context, DPadWidgetProvider.class);
        downIntent.setAction(ACTION_DOWN);
        PendingIntent downPendingIntent = PendingIntent.getBroadcast(context, widgetId, downIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.dpadDown, downPendingIntent);

        final Intent startActivityIntent = new Intent(context, DPadWidgetProvider.class);
        startActivityIntent.setAction(ACTION_START_ACTIVITY);
        PendingIntent startActivityPendingIntent = PendingIntent.getBroadcast(context, widgetId, startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.launchApp, startActivityPendingIntent);

        appWidgetManager.updateAppWidget(widgetId, remoteViews);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        final String action = intent.getAction();
        Log.warning(TAG, "onReceive Action : " + intent.getAction());

        final int deviceId = intent.getIntExtra(EXTRA_SERVER_ID, -1);
        final NetworkDevice device = NetworkDeviceDao.loadDevice(context, deviceId);

        Log.warning(TAG, "onReceive deviceId : " + deviceId);

        switch (action) {
            case ACTION_APPWIDGET_UPDATE:

                final int deviceId2 = intent.getIntExtra(EXTRA_SERVER_ID, -1);
                Log.warning(TAG, "onReceive ACTION_APPWIDGET_UPDATE deviceId : " + deviceId2);
                break;

            case ACTION_OK:
                Toast.makeText(context, "OK", LENGTH_SHORT).show();
                sendAsyncRequest(device, context, Type.KEYBOARD, Code.KEYCODE_ENTER);
                break;

            case ACTION_LEFT:
                Toast.makeText(context, Code.DPAD_LEFT.name(), LENGTH_SHORT).show();
                sendAsyncRequest(device, context, Type.KEYBOARD, Code.DPAD_LEFT);
                break;

            case ACTION_RIGHT:
                Toast.makeText(context, Code.DPAD_RIGHT.name(), LENGTH_SHORT).show();
                sendAsyncRequest(device, context, Type.KEYBOARD, Code.DPAD_RIGHT);
                break;

            case ACTION_UP:
                Toast.makeText(context, Code.DPAD_UP.name(), LENGTH_SHORT).show();
                sendAsyncRequest(device, context, Type.KEYBOARD, Code.DPAD_UP);
                break;

            case ACTION_DOWN:
                Toast.makeText(context, Code.DPAD_DOWN.name(), LENGTH_SHORT).show();
                sendAsyncRequest(device, context, Type.KEYBOARD, Code.DPAD_DOWN);
                break;

            case ACTION_START_ACTIVITY:
                Intent startActivityIntent = new Intent(context, ComputerActivity.class);
                startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(startActivityIntent);
                break;
        }
    }

    /**
     * Initializes the message handler then send the request.
     *
     * @param device The device to control.
     * @param context The application context.
     * @param requestType The request type.
     * @param requestCode The request code.
     */
    public static void sendAsyncRequest(NetworkDevice device, Context context, Type requestType, Code requestCode) {

        if (device == null) {
            Toast.makeText(context, R.string.no_server_configured, LENGTH_SHORT).show();
            return;
        }

        final Request request = Request.newBuilder()
                .setSecurityToken(device.getSecurityToken())
                .setType(requestType)
                .setCode(requestCode)
                .build();

        if (request == null) {
            Toast.makeText(context, R.string.msg_null_request, LENGTH_SHORT).show();
            return;
        }

        if (AsyncMessageMgr.availablePermits() > 0) {
            new AsyncMessageMgr(device, null).execute(request);
        } else {
            Toast.makeText(context, R.string.msg_no_more_permit, LENGTH_SHORT).show();
        }
    }
}
