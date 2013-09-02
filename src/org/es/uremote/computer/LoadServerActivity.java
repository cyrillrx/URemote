package org.es.uremote.computer;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

import org.es.uremote.R;
import org.es.uremote.common.LocalExplorerFragment;
import org.es.uremote.utils.IntentKeys;
import org.es.utils.Log;

import static android.view.KeyEvent.KEYCODE_VOLUME_DOWN;
import static android.view.KeyEvent.KEYCODE_VOLUME_UP;
import static org.es.uremote.exchange.ExchangeMessages.Request.Code.DOWN;
import static org.es.uremote.exchange.ExchangeMessages.Request.Code.UP;
import static org.es.uremote.exchange.ExchangeMessages.Request.Type.VOLUME;

/**
 * Created by Cyril on 31/08/13.
 */
public class LoadServerActivity extends FragmentActivity {

    private static final String TAG = "LoadServerActivity";

    private LocalExplorerFragment mFragment = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load from caller activity
		final String path = getIntent().getStringExtra(IntentKeys.DIRECTORY_PATH);

		// Send to fragment
		Bundle fragmentArg = new Bundle();
		fragmentArg.putString(IntentKeys.DIRECTORY_PATH, path);

		if (savedInstanceState == null) {
			LocalExplorerFragment fragment = new LocalExplorerFragment();
			fragment.setArguments(fragmentArg);
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.add(android.R.id.content, fragment).commit();
		}

		setContentView(R.layout.activity_explorer_load);

		/////////////////////////////////
		/*
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			// Something else is wrong. It may be one of many other states, but all we need
			//  to know is we can neither read nor write
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		/////////////////////////////////
		Environment.getExternalStorageDirectory();
		String path = "/sdcard";
		*/
	}

	/**
	 * Handle volume physical buttons.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mFragment.canNavigateUp()) {
				Log.debug(TAG, "#onKeyDown - Back key overridden.");
				return true;
			}
		}
		Log.debug(TAG, "#onKeyDown - Normal key behavior.");
		return super.onKeyDown(keyCode, event);
	}
}
