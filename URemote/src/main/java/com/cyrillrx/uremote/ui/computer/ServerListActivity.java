package com.cyrillrx.uremote.ui.computer;

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

import com.cyrillrx.logger.Logger;
import com.cyrillrx.uremote.BuildConfig;
import com.cyrillrx.uremote.R;
import com.cyrillrx.uremote.common.adapter.ServerArrayAdapter;
import com.cyrillrx.uremote.common.device.NetworkDevice;
import com.cyrillrx.uremote.ui.ComputerActivity;
import com.cyrillrx.uremote.ui.computer.dao.NetworkDeviceDao;
import com.cyrillrx.uremote.utils.IntentKeys;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.ACTION_DELETE;
import static android.content.Intent.ACTION_EDIT;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static com.cyrillrx.uremote.common.device.NetworkDevice.FILENAME;
import static com.cyrillrx.uremote.utils.IntentKeys.ACTION_ADD;
import static com.cyrillrx.uremote.utils.IntentKeys.ACTION_LOAD;
import static com.cyrillrx.uremote.utils.IntentKeys.ACTION_SAVE;
import static com.cyrillrx.uremote.utils.IntentKeys.ACTION_SELECT;
import static com.cyrillrx.uremote.utils.IntentKeys.EXTRA_SERVER_ACTION;
import static com.cyrillrx.uremote.utils.IntentKeys.EXTRA_SERVER_CONF_FILE;
import static com.cyrillrx.uremote.utils.IntentKeys.EXTRA_SERVER_DATA;
import static com.cyrillrx.uremote.utils.IntentKeys.EXTRA_SERVER_ID;

/**
 * @author Cyril Leroux
 *         Created on 31/05/13.
 */
public class ServerListActivity extends ListActivity {

    private static final String TAG = ServerListActivity.class.getSimpleName();

    private static final int RC_ADD_SERVER = 0;
    private static final int RC_EDIT_SERVER = 1;
    private static final int RC_LOAD_SERVER = 2;

    protected String action;
    private List<NetworkDevice> devices = new ArrayList<>();
    private File confFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_list);

        action = getIntent().getAction();
        if (action == null) { action = ACTION_SELECT; }

        setTitle(ACTION_EDIT.equals(action) ? R.string.title_server_edit : R.string.title_server_select);

        confFile = new File(getExternalFilesDir(null), FILENAME);
        if (confFile.exists()) {
            asyncLoadServers(confFile, devices);
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

        if (ACTION_SELECT.equals(action)) {
            setResult(RESULT_OK, intent);
            finish();

        } else if (ACTION_EDIT.equals(action)) {
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
            asyncSaveServers(devices, confFile);
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
                asyncLoadServers(new File(filePath), devices);
                break;
        }
    }

    /**
     * Add the device to the list.
     *
     * @param device
     */
    private void addServer(NetworkDevice device) {
        devices.add(device);
        asyncSaveServers(devices, confFile);
        updateView(devices);
    }

    /**
     * Edit the selected device.
     *
     * @param deviceId
     * @param newData
     */
    private void updateServer(int deviceId, NetworkDevice newData) {
        devices.get(deviceId).update(newData);
        asyncSaveServers(devices, confFile);
        updateView(devices);
    }

    /**
     * Delete the selected device from the list.
     *
     * @param deviceId
     */
    private void deleteServer(int deviceId) {
        devices.remove(deviceId);
        asyncSaveServers(devices, confFile);
        updateView(devices);
    }

    /** Load the devices from a list of {@link File} objects. */
    private class AsyncLoadServer extends AsyncTask<Void, Void, Boolean> {

        final File sourceFile;
        final List<NetworkDevice> destination;

        private AsyncLoadServer(File configFile, List<NetworkDevice> devices) {
            sourceFile = configFile;
            destination = devices;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return NetworkDeviceDao.loadFromFile(sourceFile, destination);
        }

        @Override
        protected void onPostExecute(Boolean loaded) {

            updateView(destination);

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

        final List<NetworkDevice> source;
        final File targetFile;

        private AsyncSaveServer(List<NetworkDevice> devices, File targetFile) {
            source = devices;
            this.targetFile = targetFile;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return NetworkDeviceDao.saveToFile(source, targetFile);
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