package org.es.uremote;

import android.animation.ObjectAnimator;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.es.uremote.computer.KeyboardListener;
import org.es.uremote.computer.fragment.FragAdmin;
import org.es.uremote.computer.fragment.FragDashboard;
import org.es.uremote.computer.fragment.RemoteExplorerFragment;
import org.es.uremote.device.ConnectedDevice;
import org.es.uremote.device.NetworkDevice;
import org.es.uremote.exchange.Message.Request;
import org.es.uremote.exchange.Message.Request.Code;
import org.es.uremote.exchange.Message.Request.Type;
import org.es.uremote.exchange.Message.Response;
import org.es.uremote.exchange.Message.Response.ReturnCode;
import org.es.uremote.exchange.RequestSender;
import org.es.uremote.graphics.ConnectedDeviceDrawable;
import org.es.uremote.network.AsyncMessageMgr;
import org.es.uremote.utils.Constants;
import org.es.uremote.utils.TaskCallbacks;
import org.es.uremote.utils.ToastSender;
import org.es.utils.Log;

import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static org.es.uremote.utils.Constants.STATE_CONNECTING;
import static org.es.uremote.utils.Constants.STATE_KO;
import static org.es.uremote.utils.Constants.STATE_OK;
import static org.es.uremote.utils.IntentKeys.EXTRA_SERVER_DATA;

/**
 * @author Cyril Leroux
 *         Created on 10/05/12.
 */
public class ComputerActivity extends FragmentActivity implements TaskCallbacks, ToastSender, RequestSender {

    private static final String TAG = "Computer Activity";
    private static final String SELECTED_TAB_INDEX = "SELECTED_TAB_INDEX";
    private static final String KEYBOARD_VISIBLE = "KEYBOARD_VISIBLE";
    private static final int PAGES_COUNT = 3;
    private static final int PAGE_EXPLORER = 2;

    private FragAdmin mFragAdmin;
    private FragDashboard mFragDashboard;
    private RemoteExplorerFragment mExplorerFragment;

    private TextView mTvServerState;
    private ImageView mProgressSignal;

    private Toast mToast;

    private NetworkDevice mSelectedDevice;
    private KeyboardView mKeyboardView;
    private KeyboardView mExtendedKeyboardView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_computer);

        mSelectedDevice = initDevice();
        final Drawable deviceIcon = new ConnectedDeviceDrawable(mSelectedDevice);

        // ActionBar configuration
        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setCustomView(R.layout.computer_actionbar);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);

            ((ImageView) actionBar.getCustomView().findViewById(R.id.deviceIcon)).setImageDrawable(deviceIcon);
            mProgressSignal = (ImageView) actionBar.getCustomView().findViewById(R.id.signalIndicator);

        }

        mTvServerState = (TextView) findViewById(R.id.tvServerState);

        initKeyboard();

        // Fragment to use in each tab
        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        if (mFragAdmin == null) {
            mFragAdmin = new FragAdmin();
        }
        if (mFragDashboard == null) {
            mFragDashboard = new FragDashboard();
        }
        if (mExplorerFragment == null) {
            mExplorerFragment = new RemoteExplorerFragment();
        }

        List<Fragment> fragments = new ArrayList<>(PAGES_COUNT);
        fragments.add(mFragAdmin);
        fragments.add(mFragDashboard);
        fragments.add(mExplorerFragment);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.vpMain);
        viewPager.setOffscreenPageLimit(PAGES_COUNT);
        final ComputerPagerAdapter pagerAdapter = new ComputerPagerAdapter(super.getSupportFragmentManager(), fragments);
        viewPager.setAdapter(pagerAdapter);

        if (savedInstanceState != null) {
            final int newTabIndex = savedInstanceState.getInt(SELECTED_TAB_INDEX, 0);
            if (newTabIndex != actionBar.getSelectedNavigationIndex()) {
                actionBar.setSelectedNavigationItem(newTabIndex);
            }

            // Update custom keyboard visibility
            if (savedInstanceState.getBoolean(KEYBOARD_VISIBLE, false)) {
                showCustomKeyboard();
            }
        }
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
        final TextView tvServerInfo = (TextView) findViewById(R.id.tvServerInfos);
        tvServerInfo.setText(R.string.no_device_configured);

        final NetworkDevice device = getIntent().getParcelableExtra(EXTRA_SERVER_DATA);
        // Quit the activity if the device is not set
        if (device == null) {
            finish();
        }

        sendAsyncRequest(Type.SIMPLE, Code.PING);
        tvServerInfo.setText(device.toString());
        return device;
    }

    /** Initializes custom keyboard elements. */
    private void initKeyboard() {

        mKeyboardView = (KeyboardView) findViewById(R.id.keyboardView);
        mExtendedKeyboardView = (KeyboardView) findViewById(R.id.keyboardViewExtended);

        // Create custom keyboard
        final KeyboardListener keyboardListener = new KeyboardListener(this);
        keyboardListener.setToastSender(this);

        final Keyboard keyboard = new Keyboard(getApplicationContext(), R.xml.keyboard_qwerty);
        mKeyboardView.setKeyboard(keyboard);
        mKeyboardView.setPreviewEnabled(false);
        mKeyboardView.setOnKeyboardActionListener(keyboardListener);

        final Keyboard extendedKeyboard = new Keyboard(getApplicationContext(), R.xml.keyboard_extended);
        mExtendedKeyboardView.setKeyboard(extendedKeyboard);
        mExtendedKeyboardView.setPreviewEnabled(false);
        mExtendedKeyboardView.setOnKeyboardActionListener(keyboardListener);

        keyboardListener.setKeyboardView(mKeyboardView);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        int tabIndex = getActionBar().getSelectedNavigationIndex();
        outState.putInt(SELECTED_TAB_INDEX, tabIndex);
        outState.putBoolean(KEYBOARD_VISIBLE, isCustomKeyboardVisible());
        super.onSaveInstanceState(outState);
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
                //                intent.putExtra(EXTRA_SERVER_DATA, mSelectedDevice);
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
            if (pageId == PAGE_EXPLORER && mExplorerFragment.canNavigateUp()) {
                mExplorerFragment.navigateUp();
                return true;
            }
        }
        Log.warning(TAG, "#onKeyDown - Key " + KeyEvent.keyCodeToString(keyCode) + " not handle for page " + pageId);
        return super.onKeyDown(keyCode, event);
    }

    //
    // Custom keyboard methods
    //

    /** @return true if at least one of the custom keyboards is visible. */
    private boolean isCustomKeyboardVisible() {
        return mKeyboardView.getVisibility() == View.VISIBLE || mExtendedKeyboardView.getVisibility() == View.VISIBLE;
    }

    /** Show the custom keyboard. */
    private void showCustomKeyboard() {

        ObjectAnimator.ofFloat(mKeyboardView, "translationY", 100f, 0f).setDuration(150).start();
        ObjectAnimator.ofFloat(mKeyboardView, "alpha", 0f, 1f).setDuration(150).start();
        mKeyboardView.setVisibility(View.VISIBLE);
        mKeyboardView.setEnabled(true);

        ObjectAnimator.ofFloat(mExtendedKeyboardView, "translationY", -100f, 0f).setDuration(150).start();
        ObjectAnimator.ofFloat(mExtendedKeyboardView, "alpha", 0f, 1f).setDuration(150).start();
        mExtendedKeyboardView.setVisibility(View.VISIBLE);
        mExtendedKeyboardView.setEnabled(true);
    }

    /** Hide the custom keyboard. */
    private void hideCustomKeyboard() {

        mKeyboardView.setVisibility(View.GONE);
        mKeyboardView.setEnabled(false);

        mExtendedKeyboardView.setVisibility(View.GONE);
        mExtendedKeyboardView.setEnabled(false);
    }

    @Override
    public void sendToast(final String message) {
        final Context context = getApplicationContext();
        if (context == null) { return; }

        if (mToast == null) {
            mToast = Toast.makeText(context, "", Toast.LENGTH_LONG);
        }
        mToast.setText(message);
        mToast.show();
    }

    @Override
    public void sendToast(int messageResId) {
        sendToast(getString(messageResId));
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
        public android.support.v4.app.Fragment getItem(int position) {
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
            sendToast(R.string.no_device_configured);
            return;
        }

        final Request request = Request.newBuilder()
                .setSecurityToken(device.getSecurityToken())
                .setType(requestType).setCode(requestCode).build();

        if (request == null) {
            sendToast(R.string.msg_null_request);
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
        int visibility;

        switch (state) {
            case STATE_OK:
                drawableResId = android.R.drawable.presence_online;
                messageResId = R.string.msg_command_succeeded;
                visibility = View.INVISIBLE;
                break;

            case STATE_CONNECTING:
                drawableResId = android.R.drawable.presence_away;
                messageResId = R.string.msg_command_running;
                visibility = View.VISIBLE;
                break;

            default: // KO
                drawableResId = android.R.drawable.presence_offline;
                messageResId = R.string.msg_command_failed;
                visibility = View.INVISIBLE;
                break;
        }

        final Drawable imgLeft = getResources().getDrawable(drawableResId);
        imgLeft.setBounds(0, 0, 24, 24);
        mTvServerState.setCompoundDrawables(imgLeft, null, null, null);
        mTvServerState.setText(messageResId);
        mProgressSignal.setVisibility(visibility);
    }

    //
    // Request sender
    //

    @Override
    public NetworkDevice getDevice() { return mSelectedDevice; }

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
            new AsyncMessageMgr(getDevice(), this).execute(request);
        } else {
            sendToast(R.string.msg_no_more_permit);
        }
    }

    //
    // Task Callback
    //

    @Override
    public void onPreExecute() {
        updateConnectionState(STATE_CONNECTING);
    }

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
