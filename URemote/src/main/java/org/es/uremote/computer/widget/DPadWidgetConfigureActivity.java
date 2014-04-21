package org.es.uremote.computer.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import org.es.uremote.R;
import org.es.uremote.computer.ServerListActivity;

/**
 * @author Cyril Leroux
 *         Created 21/04/2014.
 */
public class DPadWidgetConfigureActivity extends ServerListActivity {

    @Override
    protected void updateWidget(Intent intent, int appWidgetId) {
        super.updateWidget(intent, appWidgetId);

        final Context context = getApplicationContext();
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_dpad);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

}
