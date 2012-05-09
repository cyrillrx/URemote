package org.es.uremote.computer;

import static org.es.uremote.utils.Message.CODE_CLASSIC;
import static org.es.uremote.utils.Message.CODE_VOLUME;

import java.util.ArrayList;
import java.util.List;

import org.es.uremote.R;
import org.es.uremote.network.AsyncMessageMgr;
import org.es.uremote.utils.Message;

import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.widget.Toast;

public class ViewPagerDashboard extends FragmentActivity implements OnPageChangeListener {

	private static int PAGES_COUNT = 3;
	private int mCurrentPage = 0;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.fragment_pager);
		
		initServer();
		
		// Instanciation de l'ActionBar
		ActionBar actionBar = getActionBar();
		// Utilisation des onglets dans l'ActionBar
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

		List<Fragment> fragments = new ArrayList<Fragment>(PAGES_COUNT);
		// Création des fragments à utiliser dans chacun des onglets
		Fragment fragDashboard	= new FragDashboard();
		ListFragment fragExplorer	= new FragFileManager();
		Fragment fragKeyboard	= new FragKeyboard();
		fragments.add(fragDashboard);
		fragments.add(fragExplorer);
		fragments.add(fragKeyboard);
		
		ViewPager viewPager = (ViewPager) findViewById(R.id.vpMain);
		MyPagerAdapter pagerAdapter = new MyPagerAdapter(super.getSupportFragmentManager(), fragments);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setOnPageChangeListener(this);
		viewPager.setCurrentItem(mCurrentPage);

		if (_savedInstanceState != null) {
			final int newTabIndex = _savedInstanceState.getInt("selectedTabIndex", 1);
			if (newTabIndex != actionBar.getSelectedNavigationIndex())
				actionBar.setSelectedNavigationItem(newTabIndex);
		} else {
			sendAsyncMessage(CODE_CLASSIC, Message.HELLO_SERVER);
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
			sendAsyncMessage(CODE_VOLUME, Message.VOLUME_UP);
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			sendAsyncMessage(CODE_VOLUME, Message.VOLUME_DOWN);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/** Initialisation des paramètres du serveur en utilisant les préférences */
	private void initServer() {

		final WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		// Si l'utilisateur a activé le Wifi, on ouvre l'activité
		final boolean wifi = wifiMgr.isWifiEnabled();
		final int resKeyHost = wifi ? R.string.pref_key_local_host : R.string.pref_key_remote_host;
		final int resKeyPort = wifi ? R.string.pref_key_local_port : R.string.pref_key_remote_port;
		final int resDefHost = wifi ? R.string.pref_default_local_host : R.string.pref_default_remote_host;
		final int resDefPort = wifi ? R.string.pref_default_local_port : R.string.pref_default_remote_port;
		
		final String keyHost = getString(resKeyHost);
		final String keyPort = getString(resKeyPort);
		final String defHost = getString(resDefHost);
		final String defPort = getString(resDefPort);
		
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		AsyncMessageMgr.setHost(pref.getString(keyHost, defHost));
		AsyncMessageMgr.setPort(Integer.parseInt(pref.getString(keyPort, defPort)));
	}

	/**
	 * Cette fonction initialise le composant gérant l'envoi des messages 
	 * puis envoie le message passé en paramètre.
	 * @param _code Le code du message. 
	 * @param _param Le paramètre du message.
	 */
	private void sendAsyncMessage(String _code, String _param) {
		if (MessageMgr.availablePermits() > 0) {
			new MessageMgr().execute(_code, _param);
		} else {
			Toast.makeText(getApplicationContext(), "No more permit available !", Toast.LENGTH_SHORT).show();
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
	}

	private class MyPagerAdapter extends FragmentPagerAdapter {

		private List<Fragment> mFragments;
		
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
	 * Classe asynchrone de gestion d'envoi des messages
	 * @author cyril.leroux
	 */
	private class MessageMgr extends AsyncMessageMgr {

		@Override
		protected void onPostExecute(String _serverReply) {
			super.onPostExecute(_serverReply);

			if (_serverReply != null && !_serverReply.isEmpty()) {
				Toast.makeText(getApplicationContext(), _serverReply, Toast.LENGTH_SHORT).show();
			}
		}
	}
}
