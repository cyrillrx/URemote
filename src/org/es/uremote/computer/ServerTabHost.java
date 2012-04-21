package org.es.uremote.computer;

import org.es.uremote.R;
import org.es.uremote.utils.IntentKeys;
import org.es.uremote.utils.Message;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

public class ServerTabHost extends Activity {
	
	// Liste des RequestCodes pour les ActivityForResults
	private static final int RC_APP_LAUNCHER	= 0;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_host);

		// Instanciation de l'ActionBar
		ActionBar mBar = getActionBar();
		// Utilisation des onglets dans l'ActionBar
		mBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		Tab tabDashboard = mBar.newTab().setText("Dashboard");
		Tab tabExplorer = mBar.newTab().setText("Explorer");
		
		// Création des fragments à utiliser dans chacun des onglets
		Fragment fragDashboard = new DashboardFrag();
		Fragment fragExplorer = new ExplorerFrag();;
		
		// Listener sur les onglets
		tabDashboard.setTabListener(new MyTabsListener(fragDashboard));
		tabExplorer.setTabListener(new MyTabsListener(fragExplorer));
		
		// Ajout des onglets à l'ActionBar
		mBar.addTab(tabDashboard);
		mBar.addTab(tabExplorer);
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
	 * Gestion des actions en fonction du code de retour renvoyé après un StartActivityForResult.
	 * 
	 * @param _requestCode Code d'identification de l'activité appelée.
	 * @param _resultCode Code de retour de l'activité (RESULT_OK/RESULT_CANCEL).
	 * @param _data Les données renvoyées par l'application.
	 */
	@Override
	protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
		// Résultat de l'activité Application Launcher
		if (_requestCode == RC_APP_LAUNCHER && _resultCode == RESULT_OK) {
			final String message = _data.getStringExtra(IntentKeys.APPLICATION_MESSAGE);
			sendAsyncMessage(message);
		} 
	}
	
	/**
	 * Cette fonction initialise le composant gérant l'envoi des messages 
	 * puis envoie le message passé en paramètre.
	 * @param _message Le message à envoyer.
	 */
	private void sendAsyncMessage(String _message) {
		//@TODO
//		if (mMessageManager == null) {
//			mMessageManager = new AsyncMessageMgr();
//			mMessageManager.execute(_message);
//		} else {
//			Toast.makeText(getActivity().getApplicationContext(), "Already initialized", Toast.LENGTH_SHORT).show();
//		}

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
}
