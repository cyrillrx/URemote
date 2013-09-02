package org.es.uremote;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import org.es.uremote.components.ActionListAdapter;
import org.es.uremote.objects.ActionItem;

import java.util.ArrayList;
import java.util.List;

import static android.view.HapticFeedbackConstants.VIRTUAL_KEY;

/**
 * The dashboard class that leads everywhere in the application.
 *
 * @author Cyril Leroux
 * Created before first commit (08/04/12).
 */
public class Home extends ListActivity implements OnItemClickListener {

	// The request codes of ActivityForResults
	private static final int RC_ENABLE_BT	= 0;
	private static final int RC_ENABLE_WIFI	= 1;

	private static final int ACTION_COMPUTER	= 0;
	private static final int ACTION_LIGHTS		= 1;
	private static final int ACTION_TV			= 2;
	private static final int ACTION_ROBOTS		= 3;
	private static final int ACTION_HIFI		= 4;

	private List<ActionItem> mActionList;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		final Typeface typeface = Typeface.createFromAsset(getAssets(), getString(R.string.action_title_font));
		initActionList();

		final ActionListAdapter adpt = new ActionListAdapter(getApplicationContext(), mActionList, typeface);
		setListAdapter(adpt);
		getListView().setOnItemClickListener(this);
	}

	private void initActionList() {
		if (mActionList != null) {
			return;
		}
		mActionList = new ArrayList<ActionItem>(5);
		mActionList.add(new ActionItem(getString(R.string.title_computer),	"", R.drawable.home_computer));
		mActionList.add(new ActionItem(getString(R.string.title_lights),	"", R.drawable.home_light));
		mActionList.add(new ActionItem(getString(R.string.title_tv),		"", R.drawable.home_tv));
		mActionList.add(new ActionItem(getString(R.string.title_robots),	"", R.drawable.home_robot));
		mActionList.add(new ActionItem(getString(R.string.title_hifi),		"", R.drawable.home_hifi));

	}

	@Override
	public void onItemClick(AdapterView<?> _parent, View _view, int _position, long _id) {
		_view.performHapticFeedback(VIRTUAL_KEY);

		switch (_position) {

			case ACTION_COMPUTER:
				startComputerRemote();
				break;

			case ACTION_LIGHTS:
				Toast.makeText(Home.this, getString(R.string.msg_light_control_not_available), Toast.LENGTH_SHORT).show();
				break;

			case ACTION_ROBOTS:
				startRobotControl();
				break;

			case ACTION_TV:
				startActivity(new Intent(getApplicationContext(), TvDialer.class));
				break;

			case ACTION_HIFI:
				Toast.makeText(Home.this, getString(R.string.msg_hifi_control_not_available), Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
		}
	}

	private void startRobotControl() {

		BluetoothAdapter bluetoothAdapter = null;

		//if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		//} else {
		//	BluetoothManager manager = (BluetoothManager) getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
		//	bluetoothAdapter = manager.getAdapter();
		//}

		// If Bluetooth is disabled, ask for activation
		if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, RC_ENABLE_BT);
		} else {
			startActivity(new Intent(getApplicationContext(), RobotControl.class));
		}
	}

	private void startComputerRemote() {
		// If Wifi is disabled, ask for activation
		final WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		if (!wifiMgr.isWifiEnabled()) {
			Intent enableIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
			startActivityForResult(enableIntent, RC_ENABLE_WIFI);
		} else {
			startActivity(new Intent(getApplicationContext(), Computer.class));
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {

			// Return from bluetooth activation
			case RC_ENABLE_BT:
				if (resultCode == Activity.RESULT_OK) {
					// Start robot control activity if bluetooth is enable
					startActivity(new Intent(getApplicationContext(), RobotControl.class));
				}
				break;

			// Return from wifi activation
			case RC_ENABLE_WIFI:
				// Start computer control activity
				startActivity(new Intent(getApplicationContext(), Computer.class));
				break;
		}
	}
}