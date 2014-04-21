package org.es.uremote.computer;

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
import android.widget.TextView;
import android.widget.Toast;

import org.es.uremote.Computer;
import org.es.uremote.R;
import org.es.uremote.components.ServerArrayAdapter;
import org.es.uremote.computer.dao.ServerSettingDao;
import org.es.uremote.device.ServerSetting;
import org.es.uremote.utils.IntentKeys;
import org.es.utils.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.ACTION_DELETE;
import static android.content.Intent.ACTION_EDIT;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static org.es.uremote.device.ServerSetting.FILENAME;
import static org.es.uremote.utils.IntentKeys.ACTION_ADD;
import static org.es.uremote.utils.IntentKeys.ACTION_LOAD;
import static org.es.uremote.utils.IntentKeys.ACTION_SAVE;
import static org.es.uremote.utils.IntentKeys.ACTION_SELECT;
import static org.es.uremote.utils.IntentKeys.EXTRA_SERVER_ACTION;
import static org.es.uremote.utils.IntentKeys.EXTRA_SERVER_CONF_FILE;
import static org.es.uremote.utils.IntentKeys.EXTRA_SERVER_DATA;
import static org.es.uremote.utils.IntentKeys.EXTRA_SERVER_ID;

/**
 * @author Cyril Leroux
 *         Created on 31/05/13.
 */
public class ServerListActivity extends ListActivity {

    private static final String TAG = "ServerListActivity";

    private static final int RC_ADD_SERVER = 0;
    private static final int RC_EDIT_SERVER = 1;
    private static final int RC_LOAD_SERVER = 2;


    private String mMode;
    private List<ServerSetting> mServers = new ArrayList<>();
    private File mConfFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_list);

        mMode = getIntent().getAction();
        if (mMode == null) { mMode = ACTION_SELECT; }

        ((TextView) findViewById(R.id.server_list_title)).setText(ACTION_SELECT.equals(mMode) ? R.string.title_server_select : R.string.title_server_edit);

        mConfFile = new File(getExternalFilesDir(null), FILENAME);
        if (mConfFile.exists()) {
            asyncLoadServers(mConfFile, mServers);
        }

        findViewById(R.id.add_server).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addServer();
            }
        });
    }

    private void asyncLoadServers(File configFile, List<ServerSetting> servers) {
        (new AsyncLoadServer(configFile, servers)).execute();
    }

    private void asyncSaveServers(List<ServerSetting> servers, File configFile) {
        (new AsyncSaveServer(servers, configFile)).execute();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ServerSetting server = ((ServerArrayAdapter) getListAdapter()).getItem(position);
        Intent intent = new Intent(getApplicationContext(), ServerEditActivity.class);
        intent.putExtra(EXTRA_SERVER_DATA, server);

        if (ACTION_SELECT.equals(mMode)) {
            setResult(RESULT_OK, intent);
            finish();

        } else if (ACTION_EDIT.equals(mMode)) {
            intent.putExtra(EXTRA_SERVER_ID, position);
            intent.setAction(ACTION_EDIT);
            startActivityForResult(intent, RC_EDIT_SERVER);
        }
    }

    private void updateView(final List<ServerSetting> servers) {

        if (getListAdapter() == null) {
            ServerArrayAdapter adapter = new ServerArrayAdapter(getApplicationContext(), servers);
            setListAdapter(adapter);
        } else {
            asyncSaveServers(servers, mConfFile);
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
                Intent intent = new Intent(getApplicationContext(), Computer.class);
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
            Log.debug(TAG, "#onActivityResult - resultCode != RESULT_OK : " + resultCode);
            return;
        }

        final ServerSetting server = data.getParcelableExtra(EXTRA_SERVER_DATA);

        switch (requestCode) {

            case RC_ADD_SERVER:
                if (server != null) {
                    addServer(server);
                }
                break;

            case RC_EDIT_SERVER:
                final String action = data.getStringExtra(EXTRA_SERVER_ACTION);
                final int serverId = data.getIntExtra(EXTRA_SERVER_ID, -1);

                if (ACTION_DELETE.equals(action)) {
                    deleteServer(serverId);
                } else if (ACTION_SAVE.equals(action) && server != null) {
                    updateServer(serverId, server);
                }
                break;

            case RC_LOAD_SERVER:
                final String filePath = data.getStringExtra(EXTRA_SERVER_CONF_FILE);
                asyncLoadServers(new File(filePath), mServers);
                break;
        }
    }

    /**
     * Add the server to the list.
     *
     * @param server
     */
    private void addServer(ServerSetting server) {
        mServers.add(server);
        asyncSaveServers(mServers, mConfFile);
        updateView(mServers);
    }

    /**
     * Edit the selected server.
     *
     * @param serverId
     * @param newData
     */
    private void updateServer(int serverId, ServerSetting newData) {
        mServers.get(serverId).update(newData);
        asyncSaveServers(mServers, mConfFile);
        updateView(mServers);
    }

    /**
     * Delete the selected server from the list.
     *
     * @param serverId
     */
    private void deleteServer(int serverId) {
        mServers.remove(serverId);
        asyncSaveServers(mServers, mConfFile);
        updateView(mServers);
    }

    /**
     * Load the servers from a list of {@link File} objects.
     *
     * @author Cyril Leroux
     */
    private class AsyncLoadServer extends AsyncTask<Void, Void, Boolean> {

        final File mSourceFile;
        final List<ServerSetting> mDestination;

        private AsyncLoadServer(File configFile, List<ServerSetting> servers) {
            mSourceFile = configFile;
            mDestination = servers;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return ServerSettingDao.loadFromFile(mSourceFile, mDestination);
        }

        @Override
        protected void onPostExecute(Boolean loaded) {

            updateView(mDestination);

            if (loaded) {
                Toast.makeText(getApplicationContext(), R.string.server_loaded, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), R.string.error_while_loading_servers, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Save the servers to a {@link File} .
     *
     * @author Cyril Leroux
     */
    private class AsyncSaveServer extends AsyncTask<Void, Void, Boolean> {

        final List<ServerSetting> mSource;
        final File mTargetFile;

        private AsyncSaveServer(List<ServerSetting> servers, File targetFile) {
            mSource = servers;
            mTargetFile = targetFile;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return ServerSettingDao.saveToFile(mSource, mTargetFile);
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