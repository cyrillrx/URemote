package com.cyrillrx.uremote.ui;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cyrillrx.logger.Logger;
import com.cyrillrx.notifier.Toaster;
import com.cyrillrx.uremote.R;
import com.cyrillrx.uremote.common.device.ConnectedDevice;
import com.cyrillrx.uremote.common.device.NetworkDevice;
import com.cyrillrx.uremote.component.ConnectedDeviceDrawable;
import com.cyrillrx.uremote.network.AsyncMessageMgr;
import com.cyrillrx.uremote.request.RequestSender;
import com.cyrillrx.uremote.request.protobuf.RemoteCommand.Request;
import com.cyrillrx.uremote.request.protobuf.RemoteCommand.Request.Code;
import com.cyrillrx.uremote.request.protobuf.RemoteCommand.Request.Type;
import com.cyrillrx.uremote.request.protobuf.RemoteCommand.Response;
import com.cyrillrx.uremote.request.protobuf.RemoteCommand.Response.ReturnCode;
import com.cyrillrx.uremote.ui.computer.KeyboardListener;
import com.cyrillrx.uremote.ui.computer.fragment.FragAdmin;
import com.cyrillrx.uremote.ui.computer.fragment.FragDashboard;
import com.cyrillrx.uremote.ui.computer.fragment.RemoteExplorerFragment;
import com.cyrillrx.uremote.utils.Constants;
import com.cyrillrx.uremote.utils.TaskCallbacks;

import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static com.cyrillrx.uremote.utils.Constants.STATE_CONNECTING;
import static com.cyrillrx.uremote.utils.Constants.STATE_KO;
import static com.cyrillrx.uremote.utils.Constants.STATE_OK;
import static com.cyrillrx.uremote.utils.IntentKeys.EXTRA_SERVER_DATA;

/**
 * @author Cyril Leroux
 *         Created on 10/05/12.
 */
public class ComputerActivity extends AppCompatActivity implements TaskCallbacks, RequestSender {

    private static final String TAG = ComputerActivity.class.getSimpleName();

    private static final String SELECTED_PAGE_ID = "SELECTED_PAGE_ID";
    private static final String KEYBOARD_VISIBLE = "KEYBOARD_VISIBLE";
    private static final int PAGES_COUNT = 3;
    private static final int DEFAULT_PAGE = 1;
    private static final int PAGE_EXPLORER = 2;

    private FragAdmin fragAdmin;
    private FragDashboard fragDashboard;
    private RemoteExplorerFragment explorerFragment;

    private TextView tvServerState;
    private ImageView progressSignal;
    private ViewPager viewPager;

    private Toast toast;

    private NetworkDevice selectedDevice;
    private KeyboardView keyboardView;
    private KeyboardView extendedKeyboardView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_computer);

        selectedDevice = initDevice();
        final Drawable deviceIcon = new ConnectedDeviceDrawable(selectedDevice, Color.WHITE);

        // ActionBar configuration
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setCustomView(R.layout.computer_actionbar);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);

            View customView = actionBar.getCustomView();
            ((ImageView) customView.findViewById(R.id.deviceIcon)).setImageDrawable(deviceIcon);
            progressSignal = customView.findViewById(R.id.signalIndicator);

        }

        tvServerState = findViewById(R.id.tvServerState);

        initKeyboard();

        // Fragment to use in each tab
        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        if (fragAdmin == null) {
            fragAdmin = new FragAdmin();
        }
        if (fragDashboard == null) {
            fragDashboard = new FragDashboard();
        }
        if (explorerFragment == null) {
            explorerFragment = new RemoteExplorerFragment();
        }

        List<Fragment> fragments = new ArrayList<>(PAGES_COUNT);
        fragments.add(fragAdmin);
        fragments.add(fragDashboard);
        fragments.add(explorerFragment);

        viewPager = findViewById(R.id.vpMain);
        viewPager.setOffscreenPageLimit(PAGES_COUNT);
        final ComputerPagerAdapter pagerAdapter = new ComputerPagerAdapter(super.getSupportFragmentManager(), fragments);
        viewPager.setAdapter(pagerAdapter);

        if (savedInstanceState != null) {
            final int savedPageId = savedInstanceState.getInt(SELECTED_PAGE_ID, DEFAULT_PAGE);
            if (savedPageId != viewPager.getCurrentItem()) {
                viewPager.setCurrentItem(savedPageId);
            }

            // Update custom keyboard visibility
            if (savedInstanceState.getBoolean(KEYBOARD_VISIBLE, false)) {
                showCustomKeyboard();
            }
        } else {
            viewPager.setCurrentItem(DEFAULT_PAGE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        int pageId = viewPager.getCurrentItem();
        outState.putInt(SELECTED_PAGE_ID, pageId);
        outState.putBoolean(KEYBOARD_VISIBLE, isCustomKeyboardVisible());
        super.onSaveInstanceState(outState);
    }

    /**
     * Initializes the server.
     * <ul>
     * <li>Send ping message to the server.</li>
     * <li>Update server info TextView.</li>
     * </ul>
     */
    protected NetworkDevice initDevice() {

        // Server info default value.
        final TextView tvServerInfo = findViewById(R.id.tvServerInfos);
        tvServerInfo.setText(R.string.no_device_configured);

        final NetworkDevice device = getIntent().getParcelableExtra(EXTRA_SERVER_DATA);
        // Quit the activity if the device is not set
        if (device == null) {
            finish();
            return null;
        }

        sendAsyncRequest(Type.SIMPLE, Code.PING);
        tvServerInfo.setText(device.toString());
        return device;
    }

    /** Initializes custom keyboard elements. */
    private void initKeyboard() {

        keyboardView = findViewById(R.id.keyboardView);
        extendedKeyboardView = findViewById(R.id.keyboardViewExtended);

        // Create custom keyboard
        final KeyboardListener keyboardListener = new KeyboardListener(this);

        final Keyboard keyboard = new Keyboard(getApplicationContext(), R.xml.keyboard_qwerty);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setPreviewEnabled(false);
        keyboardView.setOnKeyboardActionListener(keyboardListener);

        final Keyboard extendedKeyboard = new Keyboard(getApplicationContext(), R.xml.keyboard_extended);
        extendedKeyboardView.setKeyboard(extendedKeyboard);
        extendedKeyboardView.setPreviewEnabled(false);
        extendedKeyboardView.setOnKeyboardActionListener(keyboardListener);

        keyboardListener.setKeyboardView(keyboardView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.server, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                final Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                homeIntent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
                return true;

            case R.id.server_config:

                //                final Intent intent = new Intent(getApplicationContext(), ServerEditActivity.class);
                //                intent.putExtra(EXTRA_SERVER_DATA, selectedDevice);
                //                intent.setAction(ACTION_EDIT);
                //                startActivityForResult(intent, RC_EDIT_SERVER);
                return true;

            case R.id.server_keyboard:

                // Toggle custom keyboard visibility
                if (isCustomKeyboardVisible()) {
                    hideCustomKeyboard();
                } else {
                    showCustomKeyboard();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Handle volume physical buttons.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        final int pageId = ((ViewPager) findViewById(R.id.vpMain)).getCurrentItem();

        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            sendAsyncRequest(Type.VOLUME, Code.DPAD_UP);
            return true;

        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            sendAsyncRequest(Type.VOLUME, Code.DPAD_DOWN);
            return true;

        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isCustomKeyboardVisible()) {
                hideCustomKeyboard();
                return true;
            }
            if (pageId == PAGE_EXPLORER && explorerFragment.canNavigateUp()) {
                explorerFragment.navigateUp();
                return true;
            }
        }
        Logger.warning(TAG, "#onKeyDown - Key " + KeyEvent.keyCodeToString(keyCode) + " not handle for page " + pageId);
        return super.onKeyDown(keyCode, event);
    }

    //
    // Custom keyboard methods
    //

    /** @return true if at least one of the custom keyboards is visible. */
    private boolean isCustomKeyboardVisible() {
        return keyboardView.getVisibility() == View.VISIBLE || extendedKeyboardView.getVisibility() == View.VISIBLE;
    }

    /** Show the custom keyboard. */
    private void showCustomKeyboard() {

        ObjectAnimator.ofFloat(keyboardView, "translationY", 100f, 0f).setDuration(150).start();
        ObjectAnimator.ofFloat(keyboardView, "alpha", 0f, 1f).setDuration(150).start();
        keyboardView.setVisibility(View.VISIBLE);
        keyboardView.setEnabled(true);

        ObjectAnimator.ofFloat(extendedKeyboardView, "translationY", -100f, 0f).setDuration(150).start();
        ObjectAnimator.ofFloat(extendedKeyboardView, "alpha", 0f, 1f).setDuration(150).start();
        extendedKeyboardView.setVisibility(View.VISIBLE);
        extendedKeyboardView.setEnabled(true);
    }

    /** Hide the custom keyboard. */
    private void hideCustomKeyboard() {

        keyboardView.setVisibility(View.GONE);
        keyboardView.setEnabled(false);

        extendedKeyboardView.setVisibility(View.GONE);
        extendedKeyboardView.setEnabled(false);
    }

    private class ComputerPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragments;

        /**
         * @param fm        The fragment manager
         * @param fragments The fragments list.
         */
        public ComputerPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            mFragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() { return mFragments.size();}
    }

    /**
     * Initializes the message handler then send the request.
     *
     * @param requestType The request type.
     * @param requestCode The request code.
     */
    public void sendAsyncRequest(Type requestType, Code requestCode) {

        final ConnectedDevice device = getDevice();

        if (device == null) {
            Toaster.toast(R.string.no_device_configured);
            return;
        }

        final Request request = Request.newBuilder()
                .setSecurityToken(device.getSecurityToken())
                .setType(requestType)
                .setCode(requestCode)
                .build();

        if (request == null) {
            Toaster.toast(R.string.msg_null_request);
            return;
        }

        sendRequest(request);
    }

    /**
     * Update the connection state of the UI
     *
     * @param state The state of the connection :
     *              <ul>
     *              <li>{@link Constants#STATE_OK}</li>
     *              <li>{@link Constants#STATE_KO}</li>
     *              <li>{@link Constants#STATE_CONNECTING}</li>
     *              </ul>
     */
    public void updateConnectionState(int state) {
        int drawableResId;
        int messageResId;

        switch (state) {
            case STATE_OK:
                drawableResId = android.R.drawable.presence_online;
                messageResId = R.string.msg_command_succeeded;
                break;

            case STATE_CONNECTING:
                drawableResId = android.R.drawable.presence_away;
                messageResId = R.string.msg_command_running;
                AnimationDrawable animation = (AnimationDrawable) progressSignal.getDrawable();
                if (animation != null) {
                    animation.stop();
                    animation.start();
                }
                break;

            default: // KO
                drawableResId = android.R.drawable.presence_offline;
                messageResId = R.string.msg_command_failed;
                break;
        }

        final Drawable imgLeft = getResources().getDrawable(drawableResId);
        imgLeft.setBounds(0, 0, 24, 24);
        tvServerState.setCompoundDrawables(imgLeft, null, null, null);
        tvServerState.setText(messageResId);
    }

    //
    // Request sender
    //

    @Override
    public NetworkDevice getDevice() { return selectedDevice; }

    @Override
    public String getSecurityToken() {
        final ConnectedDevice device = getDevice();
        if (device != null) {
            return device.getSecurityToken();
        }

        return getString(R.string.default_security_token);
    }

    /**
     * Initializes the message sender manager and send a request.
     *
     * @param request The request to send.
     */
    @Override
    public void sendRequest(Request request) {
        if (AsyncMessageMgr.availablePermits() > 0) {
            // Todo log
            new AsyncMessageMgr(getDevice(), this).execute(request);
        } else {
            Toaster.toast(R.string.msg_no_more_permit);
        }
    }

    //
    // Task Callback
    //

    @Override
    public void onPreExecute() { updateConnectionState(STATE_CONNECTING); }

    @Override
    public void onProgressUpdate(int percent) { }

    @Override
    public void onCancelled(Response response) { }

    @Override
    public void onPostExecute(Response response) {
        if (ReturnCode.RC_ERROR.equals(response.getReturnCode())) {
            updateConnectionState(STATE_KO);
        } else {
            updateConnectionState(STATE_OK);
        }
    }
}
