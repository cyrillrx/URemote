package com.cyrillrx.uremote.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.cyrillrx.uremote.BuildConfig;
import com.cyrillrx.uremote.R;
import com.cyrillrx.uremote.common.adapter.ActionAdapter;
import com.cyrillrx.uremote.common.device.NetworkDevice;
import com.cyrillrx.uremote.common.model.ActionItem;
import com.cyrillrx.uremote.ui.computer.ServerListActivity;
import com.cyrillrx.uremote.utils.NavigationUtils;

import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.ACTION_EDIT;
import static android.widget.Toast.LENGTH_SHORT;
import static com.cyrillrx.uremote.utils.IntentKeys.EXTRA_SERVER_DATA;

/**
 * The dashboard class that leads everywhere in the application.
 *
 * @author Cyril Leroux
 *         Created on 11/09/10.
 */
public class HomeActivity extends AppCompatActivity {

    private static final int COLUMN_COUNT = 2;
    private List<ActionItem> actionList;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        initActionList();

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);
        final ActionAdapter adapter = new ActionAdapter(actionList);

        recyclerView.setLayoutManager(new GridLayoutManager(this, COLUMN_COUNT));
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

        if (actionList != null) { return; }

        actionList = new ArrayList<>();
        actionList.add(new ActionItem(ActionItem.ACTION_COMPUTER, getString(R.string.title_computer), R.drawable.ic_computer));
        actionList.add(new ActionItem(ActionItem.ACTION_LIGHTS, getString(R.string.title_lights), R.drawable.ic_light_bulb));
        actionList.add(new ActionItem(ActionItem.ACTION_TV, getString(R.string.title_tv), R.drawable.ic_tv));
        actionList.add(new ActionItem(ActionItem.ACTION_ROBOTS, getString(R.string.title_robots), R.drawable.ic_robot));
        actionList.add(new ActionItem(ActionItem.ACTION_EXPLORER, getString(R.string.title_explorer), R.drawable.ic_file_directory));

        if (BuildConfig.DEBUG) {
            actionList.add(new ActionItem(ActionItem.ACTION_HIFI, getString(R.string.title_hifi), R.drawable.ic_hifi_system));
            actionList.add(new ActionItem(ActionItem.ACTION_APP, getString(R.string.title_app_list), R.drawable.ic_app_launcher));
            actionList.add(new ActionItem(ActionItem.ACTION_3D_CUBE, getString(R.string.title_3d_cube), R.drawable.ic_3d_cube));
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