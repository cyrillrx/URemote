package com.cyrilleroux.uremote.computer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;

import com.cyrilleroux.uremote.R;
import com.cyrilleroux.uremote.common.LocalExplorerFragment;
import com.cyrilleroux.utils.Log;

import static com.cyrilleroux.uremote.utils.IntentKeys.EXTRA_SERVER_CONF_FILE;

/**
 * Created by Cyril on 31/08/13.
 */
public class LoadServerActivity extends FragmentActivity {

    private static final String TAG = LoadServerActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer_load);
    }

    /**
     * Handle volume physical buttons.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            LocalExplorerFragment fragment = (LocalExplorerFragment) getSupportFragmentManager().findFragmentById(R.id.local_explorer_fragment);
            if (fragment.navigateUp()) {
                Log.debug(TAG, "#onKeyDown - Back key overridden.");
                return true;
            }
        }
        Log.debug(TAG, "#onKeyDown - Normal key behavior.");
        return super.onKeyDown(keyCode, event);
    }

    public void onFileClick(String filename) {
        // Returns the value to the parent
        Intent data = new Intent();
        data.putExtra(EXTRA_SERVER_CONF_FILE, filename);
        setResult(RESULT_OK, data);
        finish();
    }
}
