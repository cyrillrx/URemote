package org.es.uremote.utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.widget.Toast;

import org.es.uremote.ComputerActivity;
import org.es.uremote.HexHomeActivity;
import org.es.uremote.R;
import org.es.uremote.RobotActivity;
import org.es.uremote.TvActivity;
import org.es.uremote.computer.ServerListActivity;
import org.es.uremote.device.NetworkDevice;

import static org.es.uremote.utils.IntentKeys.EXTRA_SERVER_DATA;

/**
 * @author Cyril Leroux
 *         Created 08/03/2015.
 */
public class NavigationUtils {

    // The request codes of ActivityForResults
    public static final int RC_SELECT_SERVER = 0;
    public static final int RC_ENABLE_BT = 1;
    public static final int RC_ENABLE_WIFI = 2;
    public static final int RC_APP_LAUNCHER = 3;

    public static void startServerList(final Activity activity) {
        final Intent intent = new Intent(activity.getApplicationContext(), ServerListActivity.class);
        intent.setAction(IntentKeys.ACTION_SELECT);
        activity.startActivityForResult(intent, RC_SELECT_SERVER);
    }

    public static void startTvRemote(final Activity activity) {
        final Intent intent = new Intent(activity.getApplicationContext(), TvActivity.class);
        activity.startActivity(intent);
    }

    public static void startComputerRemote(final Activity activity, final NetworkDevice device) {
        // TODO Handle airplane mode
//        final ConnectivityManager connectivityMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        // If Wifi is disabled, ask for activation
        final WifiManager wifiMgr = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiMgr.isWifiEnabled()) {
            Toast.makeText(activity.getApplicationContext(), R.string.msg_wifi_disabled, Toast.LENGTH_SHORT).show();
        }

        final Intent computerIntent = new Intent(activity.getApplicationContext(), ComputerActivity.class);
        computerIntent.putExtra(EXTRA_SERVER_DATA, device);
        activity.startActivity(computerIntent);
    }

    public static void startRobotControl(Activity activity) {

        BluetoothAdapter bluetoothAdapter = null;
        // When running on JELLY_BEAN_MR1 and below
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        } else {
            // When running on JELLY_BEAN_MR2 and higher
            final BluetoothManager manager = (BluetoothManager) activity.getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = manager.getAdapter();
        }

        // If Bluetooth is disabled, ask for activation
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableIntent, RC_ENABLE_BT);
        } else {
            activity.startActivity(new Intent(activity.getApplicationContext(), RobotActivity.class));
        }
    }

    public static void startHexActivity(Activity activity) {
        activity.startActivity(new Intent(activity.getApplicationContext(), HexHomeActivity.class));
    }


}
