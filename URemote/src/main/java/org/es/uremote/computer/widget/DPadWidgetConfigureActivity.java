package org.es.uremote.computer.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import org.es.uremote.components.ServerArrayAdapter;
import org.es.uremote.computer.ServerListActivity;
import org.es.uremote.device.ServerSetting;

import static android.appwidget.AppWidgetManager.ACTION_APPWIDGET_CONFIGURE;
import static android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID;
import static org.es.uremote.utils.IntentKeys.EXTRA_SERVER_DATA;

/**
 * @author Cyril Leroux
 *         Created 21/04/2014.
 */
public class DPadWidgetConfigureActivity extends ServerListActivity {

    private int mWidgetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWidgetId = initAppWidgetId(getIntent().getExtras());
        setResult(RESULT_CANCELED);
    }

    /**
     * Initialize mAppWidgetId in case the activity has been launch by a widget.
     */
    protected int initAppWidgetId(final Bundle extras) {
        return (extras != null) ?
                extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID) :
                AppWidgetManager.INVALID_APPWIDGET_ID; // Default value
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (ACTION_APPWIDGET_CONFIGURE.equals(mAction) &&  mWidgetId != INVALID_APPWIDGET_ID) {
            final ServerSetting server = ((ServerArrayAdapter) getListAdapter()).getItem(position);

            final Intent widgetIntent = new Intent();
            widgetIntent.putExtra(EXTRA_SERVER_DATA, server);
            widgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mWidgetId);
            setResult(RESULT_OK, widgetIntent);

            updateWidget(mWidgetId, server);

            finish();
        }
    }

    /**
     * Called when the activity is used to configure a widget.
     * <ul>
     * <li>Updates the widget view.</li>
     * <li>Updates the intent used to configure the widget.</li>
     * </ul>
     *
     * @param widgetId The widget id used to update the widget.
     */
    protected void updateWidget(final int widgetId, ServerSetting server) {

        final Context context = getApplicationContext();
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        DPadWidgetProvider.updateWidget(context, appWidgetManager, widgetId, server);
    }

}
