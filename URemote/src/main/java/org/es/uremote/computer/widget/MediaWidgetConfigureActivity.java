package org.es.uremote.computer.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import org.es.uremote.R;
import org.es.uremote.computer.ServerListActivity;

/**
 * @author Cyril Leroux
 *         Created 21/04/2014.
 */
public class MediaWidgetConfigureActivity extends ServerListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);
    }

    @Override
    protected void updateWidget(Intent intent, int widgetId) {
        super.updateWidget(intent, widgetId);

        final Context context = getApplicationContext();
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_media);
        appWidgetManager.updateAppWidget(widgetId, views);
    }

}
