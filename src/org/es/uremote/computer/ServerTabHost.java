package org.es.uremote.computer;

import org.es.uremote.R;
import org.es.uremote.network.AsyncMessageMgr;
import org.es.uremote.utils.Message;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

public class ServerTabHost extends Activity {	
	private HostMessageMgr mMessageManager;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.fragment_host);

		// Instanciation de l'ActionBar
		ActionBar actionBar = getActionBar();
		// Utilisation des onglets dans l'ActionBar
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		Tab tabDashboard = actionBar.newTab().setText("Dashboard");
		Tab tabExplorer = actionBar.newTab().setText("Explorer");
		Tab tabKeyboard = actionBar.newTab().setText("Keyboard");
		
		// Création des fragments à utiliser dans chacun des onglets
		Fragment fragDashboard	= new FragDashboard();
		Fragment fragExplorer	= new FragFileManager();
		Fragment fragKeyboard	= new FragKeyboard();
		
		// Listener sur les onglets
		tabDashboard.setTabListener(new MyTabsListener(fragDashboard));
		tabExplorer.setTabListener(new MyTabsListener(fragExplorer));
		tabKeyboard.setTabListener(new MyTabsListener(fragKeyboard));
		
		// Ajout des onglets à l'ActionBar
		actionBar.addTab(tabDashboard);
		actionBar.addTab(tabExplorer);
		actionBar.addTab(tabKeyboard);
		
		if (_savedInstanceState != null) {
			int tabIndex = _savedInstanceState.getInt("selectedTabIndex", 1);
			actionBar.setSelectedNavigationItem(tabIndex);
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
			sendAsyncMessage(Message.VOLUME_UP);
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			sendAsyncMessage(Message.VOLUME_DOWN);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Cette fonction initialise le composant gérant l'envoi des messages 
	 * puis envoie le message passé en paramètre.
	 * @param _message Le message à envoyer.
	 */
	private void sendAsyncMessage(String _message) {
		if (mMessageManager == null) {
			mMessageManager = new HostMessageMgr();
			mMessageManager.execute(_message);
		} else {
			Toast.makeText(getApplicationContext(), "Already initialized", Toast.LENGTH_SHORT).show();
		}
	}

	private void stopMessageManager() {
		if (mMessageManager != null) {
			mMessageManager.cancel(false);
			mMessageManager = null;
		}
	}
	
	/**
	 * @author cyril.leroux
	 * Listener personnalisé pour les changments d'onglets (sélection/resélection/désélection)
	 */
	protected class MyTabsListener implements TabListener {
		private Fragment mFragment;
		
		public MyTabsListener(Fragment _fragment) {
			this.mFragment = _fragment;
		}

		@Override
		public void onTabSelected(Tab _tab, FragmentTransaction _ft) {
			_ft.replace(R.id.fragment_container, mFragment);
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			Toast.makeText(getApplicationContext(), "Already selected !", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onTabUnselected(Tab _tab, FragmentTransaction _ft) {
			_ft.remove(mFragment);
		}
	}
	
	/**
	 * Classe asynchrone de gestion d'envoi des messages
	 * @author cyril.leroux
	 */
	private class HostMessageMgr extends AsyncMessageMgr {
		
		@Override
		protected void onPostExecute(String _serverReply) {
			super.onPostExecute(_serverReply);
			
			if (_serverReply != null && !_serverReply.isEmpty()) {
				Toast.makeText(getApplicationContext(), _serverReply, Toast.LENGTH_SHORT).show();
			}
			stopMessageManager();
		}
	}
}
