package org.es.uremote;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import org.es.uremote.components.ActionArrayAdapter;
import org.es.uremote.computer.ServerListActivity;
import org.es.uremote.device.ServerSetting;
import org.es.uremote.objects.ActionItem;

import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.ACTION_EDIT;
import static android.view.HapticFeedbackConstants.VIRTUAL_KEY;
import static android.widget.Toast.LENGTH_SHORT;
import static org.es.uremote.utils.IntentKeys.ACTION_SELECT;
import static org.es.uremote.utils.IntentKeys.EXTRA_SERVER_DATA;

/**
 * The dashboard class that leads everywhere in the application.
 *
 * @author Cyril Leroux
 *         Created on 11/09/10.
 */
public class HomeActivity extends ListActivity implements OnItemClickListener {

    // The request codes of ActivityForResults
    private static final int RC_SELECT_SERVER = 0;
    private static final int RC_ENABLE_BT     = 1;
    private static final int RC_ENABLE_WIFI   = 2;

    private static final int ACTION_COMPUTER = 0;
    private static final int ACTION_LIGHTS   = 1;
    private static final int ACTION_TV       = 2;
    private static final int ACTION_ROBOTS   = 3;
    private static final int ACTION_HIFI     = 4;
    private static final int ACTION_NAO      = 5;

    private List<ActionItem> mActionList;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);

        setContentView(R.layout.activity_home);

        final Typeface typeface = Typeface.createFromAsset(getAssets(), getString(R.string.action_title_font));
        initActionList();

        final ActionArrayAdapter adapter = new ActionArrayAdapter(getApplicationContext(), mActionList, typeface);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.about:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                return true;

            case R.id.server_list:
                startActivity(new Intent(getApplicationContext(), ServerListActivity.class).setAction(ACTION_EDIT));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initActionList() {
        if (mActionList != null) {
            return;
        }
        mActionList = new ArrayList<>(6);
        mActionList.add(ACTION_COMPUTER, new ActionItem(getString(R.string.title_computer), R.drawable.home_computer));
        mActionList.add(ACTION_LIGHTS, new ActionItem(getString(R.string.title_lights), R.drawable.home_light));
        mActionList.add(ACTION_TV, new ActionItem(getString(R.string.title_tv), R.drawable.home_tv));
        mActionList.add(ACTION_ROBOTS, new ActionItem(getString(R.string.title_robots), R.drawable.home_robot));

        if (BuildConfig.DEBUG) {
            mActionList.add(ACTION_HIFI, new ActionItem(getString(R.string.title_hifi), R.drawable.home_hifi));
            mActionList.add(ACTION_NAO, new ActionItem(getString(R.string.title_nao), R.drawable.home_nao));
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        view.performHapticFeedback(VIRTUAL_KEY);

        switch (position) {

            case ACTION_COMPUTER:
                startActivityForResult(new Intent(getApplicationContext(), ServerListActivity.class).setAction(ACTION_SELECT), RC_SELECT_SERVER);
                break;

            case ACTION_LIGHTS:
                Toast.makeText(HomeActivity.this, getString(R.string.msg_light_control_not_available), Toast.LENGTH_SHORT).show();
                break;

            case ACTION_TV:
                startActivity(new Intent(getApplicationContext(), TvActivity.class));
                break;

            case ACTION_ROBOTS:
                startRobotControl();
                break;

            case ACTION_HIFI:
                Toast.makeText(HomeActivity.this, getString(R.string.msg_hifi_control_not_available), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), HexHomeActivity.class));
                break;

            case ACTION_NAO:
                startActivity(new Intent(getApplicationContext(), NaoActivity.class));
                break;

            default:
                break;
        }
    }

    private void startComputerRemote(final ServerSetting server) {
        // If Wifi is disabled, ask for activation
        final WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiMgr.isWifiEnabled()) {
            //Intent enableIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
            Toast.makeText(HomeActivity.this, "Wifi is not enable. You are using 3G remote control.", Toast.LENGTH_SHORT).show();
            //startActivityForResult(enableIntent, RC_ENABLE_WIFI);
        } else {

            final Intent computerIntent = new Intent(getApplicationContext(), ComputerActivity.class);
            computerIntent.putExtra(EXTRA_SERVER_DATA, server);
            startActivity(computerIntent);
        }
    }

    private void startRobotControl() {

        BluetoothAdapter bluetoothAdapter = null;
        // When running on JELLY_BEAN_MR1 and below
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        } else {
            // When running on JELLY_BEAN_MR2 and higher
            final BluetoothManager manager = (BluetoothManager) getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = manager.getAdapter();
        }

        // If Bluetooth is disabled, ask for activation
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, RC_ENABLE_BT);
        } else {
            startActivity(new Intent(getApplicationContext(), RobotActivity.class));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case RC_SELECT_SERVER:
                if (resultCode == Activity.RESULT_OK) {
                    final ServerSetting server = data.getParcelableExtra(EXTRA_SERVER_DATA);
                    if (server == null) {
                        Toast.makeText(getApplicationContext(), R.string.no_server_configured, LENGTH_SHORT).show();
                        return;
                    }
                    startComputerRemote(server);
                }
                break;

            // Return from bluetooth activation
            case RC_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    // Start robot control activity if bluetooth is enable
                    startActivity(new Intent(getApplicationContext(), RobotActivity.class));
                }
                break;

            // Return from wifi activation
            case RC_ENABLE_WIFI:
                // Start computer control activity
                startActivity(new Intent(getApplicationContext(), ComputerActivity.class));
                break;
        }
    }
}