package org.es.uremote.widget;

import java.util.Random;

import org.es.uremote.Computer;
import org.es.uremote.R;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * @author Cyril Leroux
 *
 */
public class MediaWidgerProvider extends AppWidgetProvider {

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

			// Register an onClickListener
			Intent intent = new Intent(context, Computer.class);
			//	intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			//	intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

			PendingIntent pendingIntent = PendingIntent.getActivity(context, widgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT );

			remoteViews.setOnClickPendingIntent(R.id.cmdPrevious, pendingIntent);
			remoteViews.setOnClickPendingIntent(R.id.cmdPlayPause, pendingIntent);
			remoteViews.setOnClickPendingIntent(R.id.cmdStop, pendingIntent);
			remoteViews.setOnClickPendingIntent(R.id.cmdNext, pendingIntent);

			appWidgetManager.updateAppWidget(widgetId, remoteViews);
		}
	}
}
