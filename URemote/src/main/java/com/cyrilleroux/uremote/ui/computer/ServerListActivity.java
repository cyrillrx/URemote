package com.cyrilleroux.uremote.ui.computer;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.cyrilleroux.android.toolbox.Logger;
import com.cyrilleroux.uremote.BuildConfig;
import com.cyrilleroux.uremote.R;
import com.cyrilleroux.uremote.common.adapter.ServerArrayAdapter;
import com.cyrilleroux.uremote.common.device.NetworkDevice;
import com.cyrilleroux.uremote.ui.ComputerActivity;
import com.cyrilleroux.uremote.ui.computer.dao.NetworkDeviceDao;
import com.cyrilleroux.uremote.utils.IntentKeys;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.ACTION_DELETE;
import static android.content.Intent.ACTION_EDIT;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static com.cyrilleroux.uremote.common.device.NetworkDevice.FILENAME;
import static com.cyrilleroux.uremote.utils.IntentKeys.ACTION_ADD;
import static com.cyrilleroux.uremote.utils.IntentKeys.ACTION_LOAD;
import static com.cyrilleroux.uremote.utils.IntentKeys.ACTION_SAVE;
import static com.cyrilleroux.uremote.utils.IntentKeys.ACTION_SELECT;
import static com.cyrilleroux.uremote.utils.IntentKeys.EXTRA_SERVER_ACTION;
import static com.cyrilleroux.uremote.utils.IntentKeys.EXTRA_SERVER_CONF_FILE;
import static com.cyrilleroux.uremote.utils.IntentKeys.EXTRA_SERVER_DATA;
import static com.cyrilleroux.uremote.utils.IntentKeys.EXTRA_SERVER_ID;

/**
 * @author Cyril Leroux
 *         Created on 31/05/13.
 */
public class ServerListActivity extends ListActivity {

    private static final String TAG = ServerListActivity.class.getSimpleName();

    private static final int RC_ADD_SERVER = 0;
    private static final int RC_EDIT_SERVER = 1;
    private static final int RC_LOAD_SERVER = 2;

    protected String mAction;
    private List<NetworkDevice> mDevices = new ArrayList<>();
    private File mConfFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_list);

        mAction = getIntent().getAction();
        if (mAction == null) { mAction = ACTION_SELECT; }

        setTitle(ACTION_EDIT.equals(mAction) ? R.string.title_server_edit : R.string.title_server_select);

        mConfFile = new File(getExternalFilesDir(null), FILENAME);
        if (mConfFile.exists()) {
            asyncLoadServers(mConfFile, mDevices);
        }

        // Handle "Add server" button
        // TODO replace by lambda whenever possible
        //        findViewById(R.id.add_server).setOnClickListener((view) -> addServer());
        findViewById(R.id.add_server).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addServer();
            }
        });
    }

    /**
     * Load the device list from the specified file.
     *
     * @param configFile
     * @param devices
     */
    private void asyncLoadServers(File configFile, List<NetworkDevice> devices) {
        (new AsyncLoadServer(configFile, devices)).execute();
    }

    /**
     * Save the device list in the specified file.
     *
     * @param devices
     * @param configFile
     */
    private void asyncSaveServers(List<NetworkDevice> devices, File configFile) {
        (new AsyncSaveServer(devices, configFile)).execute();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        final NetworkDevice device = ((ServerArrayAdapter) getListAdapter()).getItem(position);

        final Intent intent = new Intent();
        intent.putExtra(EXTRA_SERVER_DATA, device);
        intent.putExtra(EXTRA_SERVER_ID, position);

        if (ACTION_SELECT.equals(mAction)) {
            setResult(RESULT_OK, intent);
            finish();

        } else if (ACTION_EDIT.equals(mAction)) {
            intent.setClass(getApplicationContext(), ServerEditActivity.class);
            intent.setAction(ACTION_EDIT);
            startActivityForResult(intent, RC_EDIT_SERVER);
        }
    }

    private void updateView(final List<NetworkDevice> devices) {

        if (getListAdapter() == null) {
            ServerArrayAdapter adapter = new ServerArrayAdapter(getApplicationContext(), devices);
            setListAdapter(adapter);
        } else {
            asyncSaveServers(devices, mConfFile);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.server_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                Intent intent = new Intent(getApplicationContext(), ComputerActivity.class);
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;

            case R.id.add_server:
                addServer();
                return true;

            case R.id.load_from_file:
                Intent loadIntent = new Intent(getApplicationContext(), LoadServerActivity.class);
                loadIntent.setAction(ACTION_LOAD);
                loadIntent.putExtra(IntentKeys.DIRECTORY_PATH, Environment.getExternalStorageDirectory().getPath());
                startActivityForResult(loadIntent, RC_LOAD_SERVER);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addServer() {
        Intent addIntent = new Intent(getApplicationContext(), ServerEditActivity.class);
        addIntent.setAction(ACTION_ADD);
        startActivityForResult(addIntent, RC_ADD_SERVER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {
            Logger.debug(TAG, "#onActivityResult - resultCode != RESULT_OK : " + resultCode);
            return;
        }

        final NetworkDevice device = data.getParcelableExtra(EXTRA_SERVER_DATA);

        switch (requestCode) {

            case RC_ADD_SERVER:
                if (device != null) {
                    addServer(device);
                }
                break;

            case RC_EDIT_SERVER:
                final String action = data.getStringExtra(EXTRA_SERVER_ACTION);
                final int deviceId = data.getIntExtra(EXTRA_SERVER_ID, -1);

                if (ACTION_DELETE.equals(action)) {
                    deleteServer(deviceId);
                } else if (ACTION_SAVE.equals(action) && device != null) {
                    updateServer(deviceId, device);
                }
                break;

            case RC_LOAD_SERVER:
                final String filePath = data.getStringExtra(EXTRA_SERVER_CONF_FILE);
                asyncLoadServers(new File(filePath), mDevices);
                break;
        }
    }

    /**
     * Add the device to the list.
     *
     * @param device
     */
    private void addServer(NetworkDevice device) {
        mDevices.add(device);
        asyncSaveServers(mDevices, mConfFile);
        updateView(mDevices);
    }

    /**
     * Edit the selected device.
     *
     * @param deviceId
     * @param newData
     */
    private void updateServer(int deviceId, NetworkDevice newData) {
        mDevices.get(deviceId).update(newData);
        asyncSaveServers(mDevices, mConfFile);
        updateView(mDevices);
    }

    /**
     * Delete the selected device from the list.
     *
     * @param deviceId
     */
    private void deleteServer(int deviceId) {
        mDevices.remove(deviceId);
        asyncSaveServers(mDevices, mConfFile);
        updateView(mDevices);
    }

    /** Load the devices from a list of {@link File} objects. */
    private class AsyncLoadServer extends AsyncTask<Void, Void, Boolean> {

        final File mSourceFile;
        final List<NetworkDevice> mDestination;

        private AsyncLoadServer(File configFile, List<NetworkDevice> devices) {
            mSourceFile = configFile;
            mDestination = devices;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return NetworkDeviceDao.loadFromFile(mSourceFile, mDestination);
        }

        @Override
        protected void onPostExecute(Boolean loaded) {

            updateView(mDestination);

            if (loaded) {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(getApplicationContext(), R.string.server_loaded, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), R.string.error_while_loading_servers, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /** Save the devices to a {@link File} . */
    private class AsyncSaveServer extends AsyncTask<Void, Void, Boolean> {

        final List<NetworkDevice> mSource;
        final File mTargetFile;

        private AsyncSaveServer(List<NetworkDevice> devices, File targetFile) {
            mSource = devices;
            mTargetFile = targetFile;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return NetworkDeviceDao.saveToFile(mSource, mTargetFile);
        }

        @Override
        protected void onPostExecute(Boolean saved) {

            if (saved) {
                Toast.makeText(getApplicationContext(), R.string.server_saved, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), R.string.error_while_saving_servers, Toast.LENGTH_SHORT).show();
            }
        }
    }
}