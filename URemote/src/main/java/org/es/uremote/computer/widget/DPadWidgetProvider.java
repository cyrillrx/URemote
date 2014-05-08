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

import org.es.uremote.Computer;
import org.es.uremote.R;
import org.es.uremote.computer.dao.ServerSettingDao;
import org.es.uremote.device.ServerSetting;
import org.es.uremote.exchange.Message.Request.Code;
import org.es.uremote.exchange.Message.Request.Type;
import org.es.uremote.exchange.MessageUtils;
import org.es.uremote.graphics.ConnectedDeviceDrawable;
import org.es.uremote.graphics.GraphicUtil;
import org.es.uremote.network.AsyncMessageMgr;
import org.es.utils.Log;

import static android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE;
import static android.widget.Toast.LENGTH_SHORT;
import static org.es.uremote.exchange.Message.Request;
import static org.es.uremote.exchange.Message.Request.Code.DOWN;
import static org.es.uremote.exchange.Message.Request.Code.LEFT;
import static org.es.uremote.exchange.Message.Request.Code.RIGHT;
import static org.es.uremote.exchange.Message.Request.Code.UP;
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

    public static void updateWidget(Context context, AppWidgetManager appWidgetManager, int widgetId, ServerSetting server, int serverId) {

        Log.warning(TAG, "updateWidget serverId : " + serverId);

        final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_dpad);

        if (server != null) {
            final Drawable drawable = new ConnectedDeviceDrawable(server);
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

        //        final ServerSetting server = intent.getParcelableExtra(EXTRA_SERVER_DATA);
        final int serverId = intent.getIntExtra(EXTRA_SERVER_ID, -1);
        final ServerSetting server = ServerSettingDao.loadServer(context, serverId);

        Log.warning(TAG, "onReceive serverId : " + serverId);

        switch (action) {
            case ACTION_APPWIDGET_UPDATE:

                final int serverId2 = intent.getIntExtra(EXTRA_SERVER_ID, -1);
                Log.warning(TAG, "onReceive ACTION_APPWIDGET_UPDATE serverId : " + serverId2);

                //            mServer = intent.getParcelableExtra(EXTRA_SERVER_DATA);
                //                ServerSetting server2 = intent.getParcelableExtra(EXTRA_SERVER_DATA);
                break;

            case ACTION_OK:
                Toast.makeText(context, "OK", LENGTH_SHORT).show();
                sendAsyncRequest(server, context, Type.KEYBOARD, Code.KB_ENTER);
                break;

            case ACTION_LEFT:
                Toast.makeText(context, LEFT.name(), LENGTH_SHORT).show();
                sendAsyncRequest(server, context, Type.KEYBOARD, LEFT);
                break;

            case ACTION_RIGHT:
                Toast.makeText(context, RIGHT.name(), LENGTH_SHORT).show();
                sendAsyncRequest(server, context, Type.KEYBOARD, RIGHT);
                break;

            case ACTION_UP:
                Toast.makeText(context, UP.name(), LENGTH_SHORT).show();
                sendAsyncRequest(server, context, Type.KEYBOARD, UP);
                break;

            case ACTION_DOWN:
                Toast.makeText(context, DOWN.name(), LENGTH_SHORT).show();
                sendAsyncRequest(server, context, Type.KEYBOARD, DOWN);
                break;

            case ACTION_START_ACTIVITY:
                Intent startActivityIntent = new Intent(context, Computer.class);
                startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(startActivityIntent);
                break;
        }
    }

    /**
     * Initializes the message handler then send the request.
     *
     * @param server The server to control.
     * @param context The application context.
     * @param requestType The request type.
     * @param requestCode The request code.
     */
    public static void sendAsyncRequest(ServerSetting server, Context context, Type requestType, Code requestCode) {

        if (server == null) {
            Toast.makeText(context, R.string.no_server_configured, LENGTH_SHORT).show();
            return;
        }

        final Request request = MessageUtils.buildRequest(server.getSecurityToken(), requestType, requestCode);

        if (request == null) {
            Toast.makeText(context, R.string.msg_null_request, LENGTH_SHORT).show();
            return;
        }

        if (AsyncMessageMgr.availablePermits() > 0) {
            new AsyncMessageMgr(server).execute(request);
        } else {
            Toast.makeText(context, R.string.msg_no_more_permit, LENGTH_SHORT).show();
        }
    }
}
