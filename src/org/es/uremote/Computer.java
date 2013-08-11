package org.es.uremote;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.view.KeyEvent.KEYCODE_VOLUME_DOWN;
import static android.view.KeyEvent.KEYCODE_VOLUME_UP;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_SHORT;
import static org.es.network.ExchangeProtos.Request.Code.DOWN;
import static org.es.network.ExchangeProtos.Request.Code.UP;
import static org.es.network.ExchangeProtos.Request.Type.SIMPLE;
import static org.es.network.ExchangeProtos.Request.Type.VOLUME;
import static org.es.uremote.utils.Constants.MESSAGE_WHAT_TOAST;
import static org.es.uremote.utils.Constants.STATE_CONNECTING;
import static org.es.uremote.utils.Constants.STATE_OK;

import java.util.ArrayList;
import java.util.List;

import org.es.network.ExchangeProtos.Request;
import org.es.network.ExchangeProtos.Request.Code;
import org.es.network.ExchangeProtos.Request.Type;
import org.es.uremote.computer.FragAdmin;
import org.es.uremote.computer.FragDashboard;
import org.es.uremote.computer.FragExplorer;
import org.es.uremote.computer.FragKeyboard;
import org.es.uremote.network.AsyncMessageMgr;
import org.es.uremote.network.MessageHelper;
import org.es.uremote.objects.ServerSetting;
import org.es.uremote.utils.Constants;
import org.es.utils.Log;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Cyril Leroux
 *
 */
public class Computer extends FragmentActivity implements OnPageChangeListener {

	private static final String TAG = "ServerControl";
	private static final String SELECTED_TAB_INDEX = "SELECTED_TAB_INDEX";
	private static final int PAGES_COUNT = 4;
	private static final int EXPLORER_PAGE_ID = 2;

	/** Handler the display of toast messages. */
	private static Handler sHandler;

	private FragExplorer mFragExplorer;
	private int mCurrentPage = 0;
	private TextView mTvServerState;
	private ProgressBar mPbConnection;

	private static Toast sToast = null;

	/** @return the handler used to display the toast messages. */
	public static Handler getHandler() {
		return sHandler;
	}

	private ServerSetting getCurrentServer() {
		return ServerSetting.loadFromPreferences(getApplicationContext());
	}

	private String getServerString() {
		if (getCurrentServer().isLocal(getApplicationContext())){			
			return getCurrentServer().getFullLocal();
		}
		return getCurrentServer().getFullRemote();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_computer);

		initHandler(getApplicationContext());
		initServer();

		// ActionBar configuration
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		// Enable Home as Up
		actionBar.setDisplayHomeAsUpEnabled(true);

		// Fragment to use in each tab
		FragAdmin fragAdmin				= new FragAdmin();
		FragDashboard fragDashboard		= new FragDashboard();
		mFragExplorer		= new FragExplorer();
		FragKeyboard fragKeyboard		= new FragKeyboard();
		List<Fragment> fragments = new ArrayList<Fragment>(PAGES_COUNT);
		fragments.add(fragAdmin);
		fragments.add(fragDashboard);
		fragments.add(mFragExplorer);
		fragments.add(fragKeyboard);

		ViewPager viewPager = (ViewPager) findViewById(R.id.vpMain);
		MyPagerAdapter pagerAdapter = new MyPagerAdapter(super.getSupportFragmentManager(), fragments);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setOnPageChangeListener(this);
		viewPager.setCurrentItem(mCurrentPage);

		mTvServerState = (TextView) findViewById(R.id.tvServerState);
		mPbConnection = (ProgressBar) findViewById(R.id.pbConnection);
		((TextView) findViewById(R.id.tvServerInfos)).setText(getServerString());

		if (savedInstanceState != null) {
			final int newTabIndex = savedInstanceState.getInt(SELECTED_TAB_INDEX, 1);
			if (newTabIndex != actionBar.getSelectedNavigationIndex()) {
				actionBar.setSelectedNavigationItem(newTabIndex);
			}
		} else {
			sendAsyncRequest(SIMPLE, Code.HELLO);
		}
	}

	@Override
	protected void onResume() {
		initServer();
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

		int tabIndex = getActionBar().getSelectedNavigationIndex();
		outState.putInt(SELECTED_TAB_INDEX, tabIndex);
		super.onSaveInstanceState(outState);
	}

	/**
	 * Handle volume physical buttons.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KEYCODE_VOLUME_UP) {
			sendAsyncRequest(VOLUME, UP);
			return true;

		} else if (keyCode == KEYCODE_VOLUME_DOWN) {
			sendAsyncRequest(VOLUME, DOWN);
			return true;
		} else {
			if (keyCode == KeyEvent.KEYCODE_BACK && mCurrentPage == EXPLORER_PAGE_ID) {

				if (mFragExplorer.navigateUpIfPossible()) {
					return true;
				}
			}
			Log.error(TAG, KeyEvent.keyCodeToString(keyCode));

		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu _menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.server, _menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		
		case android.R.id.home:
			Intent intent = new Intent(getApplicationContext(), Home.class);
			intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;

		case R.id.server_settings:
			startActivity(new Intent(getApplicationContext(), AppSettings.class));
			return true;
			
		case R.id.server_list:
			startActivity(new Intent(getApplicationContext(), ServerList.class));
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Initialize server using shared preferences.
	 * @return The {@link org.es.uremote.objects.ServerSetting} loaded from preferences.
	 */
	private ServerSetting initServer() {

		// Get key for other properties
		final String keySecurityToken			= getString(R.string.key_security_token);
		final String defaultSecurityToken		= getString(R.string.default_security_token);

		// Get the properties values
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		final String securityToken	= pref.getString(keySecurityToken, defaultSecurityToken);

		// TODO hash the security token
		AsyncMessageMgr.setSecurityToken(securityToken);

		return ServerSetting.loadFromPreferences(getApplicationContext());
	}

	/**
	 * Initialize the toast message handler.
	 * @param _context The context used to display toast messages.
	 */
	private static void initHandler(final Context _context) {
		if (sHandler == null) {
			sHandler = new Handler() {
				@Override
				public void handleMessage(Message _msg) {
					switch (_msg.what) {
					case MESSAGE_WHAT_TOAST:
						showStaticToast(_context, (String)_msg.obj);
						break;

					default : break;
					}
					super.handleMessage(_msg);
				}

			};
		}
	}

	private static void showStaticToast(final Context _context, final String _message) {
		if (sToast == null) {
			sToast = Toast.makeText(_context, "", LENGTH_SHORT);
		}
		sToast.setText(_message);
		sToast.show();
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {}

	@Override
	public void onPageSelected(int _position) {
		mCurrentPage = _position;
		if (_position == 1) {
			getActionBar().setIcon(R.drawable.ic_filemanager);
		} else if (_position == 2) {
			getActionBar().setIcon(R.drawable.ic_keyboard);
		} else {
			getActionBar().setIcon(R.drawable.ic_launcher);
		}
	}

	private class MyPagerAdapter extends FragmentPagerAdapter {

		private final List<Fragment> mFragments;

		/**
		 * @param fm The fragment manager
		 * @param fragments The fragments list.
		 */
		public MyPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
			super(fm);
			mFragments = fragments;
		}

		@Override
		public android.support.v4.app.Fragment getItem(int position) {
			return mFragments.get(position);
		}

		@Override
		public int getCount() {
			return mFragments.size();
		}
	}

	/**
	 * Initializes the message handler then send the request.
	 * @param requestType The request type.
	 * @param requestCode The request code.
	 */
	public void sendAsyncRequest(Type requestType, Code requestCode) {
		Request request = MessageHelper.buildRequest(AsyncMessageMgr.getSecurityToken(), requestType, requestCode);

		if (request == null) {
			Toast.makeText(getApplicationContext(), R.string.msg_null_request, LENGTH_SHORT).show();
			return;
		}

		if (AsyncMessageMgr.availablePermits() > 0) {
			new AsyncMessageMgr(sHandler, ServerSetting.loadFromPreferences(getApplicationContext())).execute(request);
		} else {
			Toast.makeText(getApplicationContext(), R.string.msg_no_more_permit, LENGTH_SHORT).show();
		}
	}

	/**
	 * Update the connection state of the UI
	 * @param _state The state of the connection <br />
	 * ({@link Constants#STATE_OK}, <br />
	 * {@link Constants#STATE_KO}, <br />
	 * {@link Constants#STATE_CONNECTING})
	 */
	public void updateConnectionState(int _state) {
		int drawableResId;
		int messageResId;
		int visibility;

		switch (_state) {
		case STATE_OK:
			drawableResId = android.R.drawable.presence_online;
			messageResId = R.string.msg_command_succeeded;
			visibility = INVISIBLE;
			break;

		case STATE_CONNECTING:
			drawableResId = android.R.drawable.presence_away;
			messageResId = R.string.msg_command_running;
			visibility = VISIBLE;
			break;

		default: // KO
			drawableResId = android.R.drawable.presence_offline;
			messageResId = R.string.msg_command_failed;
			visibility = INVISIBLE;
			break;
		}
		final Drawable imgLeft = getResources().getDrawable(drawableResId);
		imgLeft.setBounds(0, 0, 24, 24);
		mTvServerState.setCompoundDrawables(imgLeft, null, null, null);
		mTvServerState.setText(messageResId);
		mPbConnection.setVisibility(visibility);
	}

}
