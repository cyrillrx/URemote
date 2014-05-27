package org.es.uremote.computer.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import org.es.uremote.components.ServerArrayAdapter;
import org.es.uremote.computer.ServerListActivity;
import org.es.uremote.device.NetworkDevice;
import org.es.utils.Log;

import static android.appwidget.AppWidgetManager.ACTION_APPWIDGET_CONFIGURE;
import static android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID;
import static org.es.uremote.utils.IntentKeys.EXTRA_SERVER_ID;

/**
 * @author Cyril Leroux
 *         Created 21/04/2014.
 */
public class MediaWidgetConfigureActivity extends ServerListActivity {

    private static final String TAG = "MediaWidgetConfigureActivity";

    private int mWidgetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.warning(TAG, "onCreate");

        mWidgetId = initAppWidgetId(getIntent().getExtras());
        setResult(RESULT_CANCELED);
    }

    /**
     * Initialize mAppWidgetId in case the activity has been launch by a widget.
     */
    protected int initAppWidgetId(final Bundle extras) {

        int widgetId = (extras != null) ?
                extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID) :
                AppWidgetManager.INVALID_APPWIDGET_ID; // Default value

        Log.warning(TAG, "initAppWidgetId - id : " + widgetId);

        return widgetId;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Log.warning(TAG, "onListItemClick");

        if (ACTION_APPWIDGET_CONFIGURE.equals(mAction) &&  mWidgetId != INVALID_APPWIDGET_ID) {
            final NetworkDevice device = ((ServerArrayAdapter) getListAdapter()).getItem(position);

            final Intent widgetIntent = new Intent();
            widgetIntent.putExtra(EXTRA_SERVER_ID, position);
//            widgetIntent.putExtra(EXTRA_SERVER_DATA, device);
            widgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mWidgetId);
            setResult(RESULT_OK, widgetIntent);

            updateWidget(mWidgetId, device, position);
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
    protected void updateWidget(int widgetId, final NetworkDevice device, int deviceId) {
        Log.warning(TAG, "updateWidget - id : " + widgetId + " - deviceId : " + deviceId);

        final Context context = getApplicationContext();
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        MediaWidgetProvider.updateWidget(context, appWidgetManager, widgetId, device, deviceId);
    }

}
