package com.cyrillrx.uremote.widget;

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

import com.cyrillrx.logger.Logger;
import com.cyrillrx.uremote.R;
import com.cyrillrx.uremote.common.device.NetworkDevice;
import com.cyrillrx.uremote.component.ConnectedDeviceDrawable;
import com.cyrillrx.uremote.network.AsyncMessageMgr;
import com.cyrillrx.uremote.request.protobuf.RemoteCommand.Request;
import com.cyrillrx.uremote.request.protobuf.RemoteCommand.Request.Code;
import com.cyrillrx.uremote.request.protobuf.RemoteCommand.Request.Type;
import com.cyrillrx.uremote.ui.ComputerActivity;
import com.cyrillrx.uremote.ui.computer.dao.NetworkDeviceDao;
import com.cyrillrx.uremote.utils.GraphicUtil;

import static android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE;
import static android.widget.Toast.LENGTH_SHORT;
import static com.cyrillrx.uremote.request.protobuf.RemoteCommand.Request.Code.MEDIA_NEXT;
import static com.cyrillrx.uremote.request.protobuf.RemoteCommand.Request.Code.MEDIA_PLAY_PAUSE;
import static com.cyrillrx.uremote.request.protobuf.RemoteCommand.Request.Code.MEDIA_PREVIOUS;
import static com.cyrillrx.uremote.request.protobuf.RemoteCommand.Request.Code.MEDIA_STOP;
import static com.cyrillrx.uremote.request.protobuf.RemoteCommand.Request.Type.KEYBOARD;
import static com.cyrillrx.uremote.utils.IntentKeys.EXTRA_SERVER_DATA;
import static com.cyrillrx.uremote.utils.IntentKeys.EXTRA_SERVER_ID;

/**
 * @author Cyril Leroux.
 *         Created on 25/04/13.
 */
public class MediaWidgetProvider extends AppWidgetProvider {

    private static final String TAG = MediaWidgetProvider.class.getSimpleName();

    private static final String ACTION_START_ACTIVITY   = "ACTION_START_ACTIVITY";
    private static final String ACTION_MEDIA_PREVIOUS   = "ACTION_MEDIA_PREVIOUS";
    private static final String ACTION_MEDIA_PLAY_PAUSE = "ACTION_MEDIA_PLAY_PAUSE";
    private static final String ACTION_MEDIA_STOP       = "ACTION_MEDIA_STOP";
    private static final String ACTION_MEDIA_NEXT       = "ACTION_MEDIA_NEXT";

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Logger.warning(TAG, "onEnabled");
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        Logger.warning(TAG, "onUpdate");

        // Get ids of all the instances of the widget
        final ComponentName widget = new ComponentName(context, MediaWidgetProvider.class);
        final int[] widgetIds = appWidgetManager.getAppWidgetIds(widget);

        for (int widgetId : widgetIds) {
            updateWidget(context, appWidgetManager, widgetId, null, -1);
        }
    }

    public static void updateWidget(Context context, AppWidgetManager appWidgetManager, int widgetId, NetworkDevice device, int deviceId) {

        Logger.warning(TAG, "updateWidget deviceId : " + deviceId);

        final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_media);

        if (device != null) {
            final Drawable drawable = new ConnectedDeviceDrawable(device);
            final Bitmap bmp = GraphicUtil.drawableToBitmap(drawable);
            remoteViews.setImageViewBitmap(R.id.thumbnail, bmp);
        }

        // Register onClickListeners

        final Intent startActivityIntent = new Intent(context, MediaWidgetProvider.class);
        startActivityIntent.setAction(ACTION_START_ACTIVITY);
        PendingIntent startActivityPendingIntent = PendingIntent.getBroadcast(context, widgetId, startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widgetTitle, startActivityPendingIntent);

        final Intent previousIntent = new Intent(context, MediaWidgetProvider.class);
        previousIntent.setAction(ACTION_MEDIA_PREVIOUS);
        previousIntent.putExtra(EXTRA_SERVER_ID, deviceId);
        previousIntent.putExtra(EXTRA_SERVER_DATA, device);
        PendingIntent previousPendingIntent = PendingIntent.getBroadcast(context, widgetId, previousIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.cmdPrevious, previousPendingIntent);

        final Intent playPauseIntent = new Intent(context, MediaWidgetProvider.class);
        playPauseIntent.setAction(ACTION_MEDIA_PLAY_PAUSE);
        playPauseIntent.putExtra(EXTRA_SERVER_ID, deviceId);
        playPauseIntent.putExtra(EXTRA_SERVER_DATA, device);
        PendingIntent playPausePendingIntent = PendingIntent.getBroadcast(context, widgetId, playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.cmdPlayPause, playPausePendingIntent);

        final Intent stopIntent = new Intent(context, MediaWidgetProvider.class);
        stopIntent.setAction(ACTION_MEDIA_STOP);
        stopIntent.putExtra(EXTRA_SERVER_ID, deviceId);
        stopIntent.putExtra(EXTRA_SERVER_DATA, device);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, widgetId, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.cmdStop, stopPendingIntent);

        final Intent nextIntent = new Intent(context, MediaWidgetProvider.class);
        nextIntent.setAction(ACTION_MEDIA_NEXT);
        nextIntent.putExtra(EXTRA_SERVER_ID, deviceId);
        nextIntent.putExtra(EXTRA_SERVER_DATA, device);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context, widgetId, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.cmdNext, nextPendingIntent);

        appWidgetManager.updateAppWidget(widgetId, remoteViews);
        ;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        final String action = intent.getAction();
        Logger.warning(TAG, "onReceive Action : " + intent.getAction());

        //        final ServerSetting device = intent.getParcelableExtra(EXTRA_SERVER_DATA);
        final int deviceId = intent.getIntExtra(EXTRA_SERVER_ID, -1);
        final NetworkDevice device = NetworkDeviceDao.loadDevice(context, deviceId);

        Logger.warning(TAG, "onReceive deviceId : " + deviceId);

        switch (action) {
            case ACTION_APPWIDGET_UPDATE:

                final int deviceId2 = intent.getIntExtra(EXTRA_SERVER_ID, -1);
                Logger.warning(TAG, "onReceive ACTION_APPWIDGET_UPDATE deviceId : " + deviceId2);

                //            mServer = intent.getParcelableExtra(EXTRA_SERVER_DATA);
                //                ServerSetting device2 = intent.getParcelableExtra(EXTRA_SERVER_DATA);
                break;

            case ACTION_START_ACTIVITY:
                Intent startActivityIntent = new Intent(context, ComputerActivity.class);
                startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(startActivityIntent);
                break;

            case ACTION_MEDIA_PREVIOUS:
                Toast.makeText(context, MEDIA_PREVIOUS.name(), LENGTH_SHORT).show();
                sendAsyncRequest(device, context, KEYBOARD, MEDIA_PREVIOUS);
                break;

            case ACTION_MEDIA_PLAY_PAUSE:
                Toast.makeText(context, MEDIA_PLAY_PAUSE.name(), LENGTH_SHORT).show();
                sendAsyncRequest(device, context, KEYBOARD, MEDIA_PLAY_PAUSE);
                break;

            case ACTION_MEDIA_STOP:
                Toast.makeText(context, MEDIA_STOP.name(), LENGTH_SHORT).show();
                sendAsyncRequest(device, context, KEYBOARD, MEDIA_STOP);
                break;

            case ACTION_MEDIA_NEXT:
                Toast.makeText(context, MEDIA_NEXT.name(), LENGTH_SHORT).show();
                sendAsyncRequest(device, context, KEYBOARD, MEDIA_NEXT);
                break;
        }
    }

    /**
     * Initializes the message handler then send the request.
     *
     * @param device      The device to control.
     * @param context     The application context.
     * @param requestType The request type.
     * @param requestCode The request code.
     */
    public void sendAsyncRequest(NetworkDevice device, Context context, Type requestType, Code requestCode) {

        if (device == null) {
            Toast.makeText(context, R.string.no_device_configured, LENGTH_SHORT).show();
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
