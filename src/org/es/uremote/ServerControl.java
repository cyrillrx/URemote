package org.es.uremote;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static org.es.uremote.utils.Constants.MESSAGE_WHAT_TOAST;
import static org.es.uremote.utils.Constants.STATE_CONNECTING;
import static org.es.uremote.utils.Constants.STATE_OK;
import static org.es.uremote.utils.ServerMessage.CODE_CLASSIC;
import static org.es.uremote.utils.ServerMessage.CODE_VOLUME;

import java.util.ArrayList;
import java.util.List;

import org.es.network.AsyncMessageMgr;
import org.es.uremote.R;
import org.es.uremote.computer.FragAdmin;
import org.es.uremote.computer.FragDashboard;
import org.es.uremote.computer.FragExplorer;
import org.es.uremote.computer.FragKeyboard;
import org.es.uremote.utils.ServerMessage;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiManager;
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
public class ServerControl extends FragmentActivity implements OnPageChangeListener {

	private static final int PAGES_COUNT = 3;
	private int mCurrentPage = 0;

	private TextView mTvServerState;
	private ProgressBar mPbConnection;
	/** Handler the display of toast messages. */
	private static Handler sHandler;

	/** @return the handler used to display the toast messages. */
	public static Handler getHandler() {
		return sHandler;
	}

	@Override
	public void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.activity_server);

		initHandler(getApplicationContext());
		initServer();

		// Instanciation de l'ActionBar
		ActionBar actionBar = getActionBar();
		// Utilisation des onglets dans l'ActionBar
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		// L'icone de l'application sert pour le "navigation Up"
		actionBar.setDisplayHomeAsUpEnabled(true);

		List<Fragment> fragments = new ArrayList<Fragment>(PAGES_COUNT);
		// Création des fragments à utiliser dans chacun des onglets
		FragAdmin fragAdmin				= new FragAdmin();
		FragDashboard fragDashboard		= new FragDashboard();
		FragExplorer fragExplorer		= new FragExplorer();
		FragKeyboard fragKeyboard		= new FragKeyboard();
		fragments.add(fragAdmin);
		fragments.add(fragDashboard);
		fragments.add(fragExplorer);
		fragments.add(fragKeyboard);

		ViewPager viewPager = (ViewPager) findViewById(R.id.vpMain);
		MyPagerAdapter pagerAdapter = new MyPagerAdapter(super.getSupportFragmentManager(), fragments);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setOnPageChangeListener(this);
		viewPager.setCurrentItem(mCurrentPage);

		mTvServerState = (TextView) findViewById(R.id.tvServerState);
		mPbConnection = (ProgressBar) findViewById(R.id.pbConnection);
		((TextView) findViewById(R.id.tvServerInfos)).setText(AsyncMessageMgr.getServerInfos());

		if (_savedInstanceState != null) {
			// TODO Replacer par un constante
			final int newTabIndex = _savedInstanceState.getInt("selectedTabIndex", 1);
			if (newTabIndex != actionBar.getSelectedNavigationIndex()) {
				actionBar.setSelectedNavigationItem(newTabIndex);
			}
		} else {
			sendAsyncMessage(CODE_CLASSIC, ServerMessage.HELLO_SERVER);
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		int tabIndex = getActionBar().getSelectedNavigationIndex();
		outState.putInt("selectedTabIndex", tabIndex);
		super.onSaveInstanceState(outState);
	}

	/**
	 * Prise en compte de l'appui sur les boutons physique.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			sendAsyncMessage(CODE_VOLUME, ServerMessage.VOLUME_UP);
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			sendAsyncMessage(CODE_VOLUME, ServerMessage.VOLUME_DOWN);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu _menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_server, _menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(this, Home.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;

		case R.id.miServerSettings:
			startActivity(new Intent(this, AppSettings.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}


	/** Initialisation des paramètres du serveur via les préférences */
	private void initServer() {

		final WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		// Si l'utilisateur a activé le Wifi, on ouvre l'activité
		final boolean wifi = wifiMgr.isWifiEnabled();

		final int resKeyHost = wifi ? R.string.pref_key_local_host : R.string.pref_key_remote_host;
		final int resKeyPort = wifi ? R.string.pref_key_local_port : R.string.pref_key_remote_port;
		final String keyHost	= getString(resKeyHost);
		final String keyPort	= getString(resKeyPort);
		final String keyTimeout	= getString(R.string.pref_key_timeout);

		final int resDefHost = wifi ? R.string.pref_default_local_host : R.string.pref_default_remote_host;
		final int resDefPort = wifi ? R.string.pref_default_local_port : R.string.pref_default_remote_port;
		final String defaultHost	= getString(resDefHost);
		final String defaultPort	= getString(resDefPort);
		final String defaultTimeout	= getString(R.string.pref_default_timeout);

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		final String host = pref.getString(keyHost, defaultHost);
		final int port = Integer.parseInt(pref.getString(keyPort, defaultPort));
		final int timeout = Integer.parseInt(pref.getString(keyTimeout, defaultTimeout));

		AsyncMessageMgr.setHost(host);
		AsyncMessageMgr.setPort(port);
		AsyncMessageMgr.setTimeout(timeout);
	}

	/**
	 * Initialisation de l'handler gérant l'envoi des messages Toast.
	 * @param _activity L'activité associée au fragment
	 */
	private static void initHandler(final Context _ctx) {
		if (sHandler == null) {
			sHandler = new Handler() {
				@Override
				public void handleMessage(Message _msg) {
					switch (_msg.what) {
					case MESSAGE_WHAT_TOAST:
						Toast.makeText(_ctx, (String)_msg.obj, Toast.LENGTH_SHORT).show();
						break;
					default : break;
					}
					super.handleMessage(_msg);
				}

			};
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

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
		 * @param _fm
		 * @param _fragments
		 */
		public MyPagerAdapter(FragmentManager _fm, List<Fragment> _fragments) {
			super(_fm);
			mFragments = _fragments;
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
	 * Cette fonction initialise le composant gérant l'envoi des messages
	 * puis envoie le message passé en paramètre.
	 * @param _code Le code du message.
	 * @param _param Le paramètre du message.
	 */
	public void sendAsyncMessage(String _code, String _param) {
		if (AsyncMessageMgr.availablePermits() > 0) {
			new AsyncMessageMgr(sHandler).execute(_code, _param);
		} else {
			Toast.makeText(getApplicationContext(), R.string.msg_no_more_permit, Toast.LENGTH_SHORT).show();
		}
	}


	/**
	 * Fonction de mise à jour de l'interface utilisateur
	 * @param _state L'état à mettre à jour (OK, KO, CONNECTING)
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
