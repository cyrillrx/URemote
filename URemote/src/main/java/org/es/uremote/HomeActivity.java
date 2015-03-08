package org.es.uremote;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import org.es.uremote.components.ActionAdapter;
import org.es.uremote.computer.ServerListActivity;
import org.es.uremote.device.NetworkDevice;
import org.es.uremote.network.AsyncMessageMgr;
import org.es.uremote.objects.ActionItem;
import org.es.uremote.request.protobuf.RemoteCommand;
import org.es.uremote.utils.NavigationUtils;
import org.es.uremote.utils.TaskCallbacks;

import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.ACTION_EDIT;
import static android.widget.Toast.LENGTH_SHORT;
import static org.es.uremote.utils.IntentKeys.EXTRA_SERVER_DATA;

/**
 * The dashboard class that leads everywhere in the application.
 *
 * @author Cyril Leroux
 *         Created on 11/09/10.
 */
public class HomeActivity extends ActionBarActivity {

    private List<ActionItem> mActionList;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        initActionList();

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);
        final ActionAdapter adapter = new ActionAdapter(this, mActionList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
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
        mActionList = new ArrayList<>();
        mActionList.add(new ActionItem(ActionItem.ACTION_COMPUTER, getString(R.string.title_computer), R.drawable.home_computer));
        mActionList.add(new ActionItem(ActionItem.ACTION_LIGHTS, getString(R.string.title_lights), R.drawable.home_light));
        mActionList.add(new ActionItem(ActionItem.ACTION_TV, getString(R.string.title_tv), R.drawable.home_tv));
        mActionList.add(new ActionItem(ActionItem.ACTION_ROBOTS, getString(R.string.title_robots), R.drawable.home_robot));

        if (BuildConfig.DEBUG) {
            mActionList.add(new ActionItem(ActionItem.ACTION_HIFI, getString(R.string.title_hifi), R.drawable.home_hifi));
            mActionList.add(new ActionItem(ActionItem.ACTION_APP, getString(R.string.title_app_list), R.drawable.home_nao));
            mActionList.add(new ActionItem(ActionItem.ACTION_STORE, getString(R.string.title_app_store), R.drawable.nao_app_store));
            mActionList.add(new ActionItem(ActionItem.ACTION_SHOW_NAO, getString(R.string.title_show_nao), R.drawable.home_nao));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case NavigationUtils.RC_SELECT_SERVER:
                if (resultCode == Activity.RESULT_OK) {
                    final NetworkDevice device = data.getParcelableExtra(EXTRA_SERVER_DATA);
                    if (device == null) {
                        Toast.makeText(getApplicationContext(), R.string.no_device_configured, LENGTH_SHORT).show();
                        return;
                    }
                    NavigationUtils.startComputerRemote(this, device);
                }
                break;

            // Return from bluetooth activation
            case NavigationUtils.RC_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    // Start robot control activity if bluetooth is enable
                    startActivity(new Intent(getApplicationContext(), RobotActivity.class));
                }
                break;

            // Return from wifi activation
            case NavigationUtils.RC_ENABLE_WIFI:
                // Start computer control activity
                startActivity(new Intent(getApplicationContext(), ComputerActivity.class));
                break;
        }
    }
}