package org.es.uremote;

import java.util.List;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class AppSettings extends PreferenceActivity {
// TODO changer les commentaires
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onBuildHeaders(List<Header> target) {
		loadHeadersFromResource(R.xml.preference_headers, target);
	}

	/**
	 * This fragment shows the preferences for the first header.
	 */
	public static class PrefServer extends PreferenceFragment implements OnSharedPreferenceChangeListener {

		protected AppSettings mActivity;
		protected SharedPreferences mPref = null;

		private String mKeyLocalHost;
		private String mKeyLocalPort;
		private String mKeyBroadcast;
		private String mKeyRemoteHost;
		private String mKeyRemotePort;
		private String mKeyTimeout;

		private String mDefaultLocalHost;
		private String mDefaultLocalPort;
		private String mDefaultBroadcast;
		private String mDefaultRemoteHost;
		private String mDefaultRemotePort;
		private String mDefaultTimeout;

		private EditTextPreference mPrefLocalHost;
		private EditTextPreference mPrefLocalPort;
		private EditTextPreference mPrefBroadcast;
		private EditTextPreference mPrefRemoteHost;
		private EditTextPreference mPrefRemotePort;
		private EditTextPreference mPrefTimeout;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_server);
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			mActivity = (AppSettings) getActivity();
			mPref = PreferenceManager.getDefaultSharedPreferences(mActivity.getApplicationContext());
			
			mKeyLocalHost	= getString(R.string.pref_key_local_host);
			mKeyLocalPort	= getString(R.string.pref_key_local_port);
			mKeyBroadcast	= getString(R.string.pref_key_broadcast);
			mKeyRemoteHost	= getString(R.string.pref_key_remote_host);
			mKeyRemotePort	= getString(R.string.pref_key_remote_port);
			mKeyTimeout		= getString(R.string.pref_key_timeout);

			mDefaultLocalHost	= getString(R.string.pref_default_local_host);
			mDefaultLocalPort	= getString(R.string.pref_default_local_port);
			mDefaultBroadcast	= getString(R.string.pref_default_broadcast);
			mDefaultRemoteHost	= getString(R.string.pref_default_remote_host);
			mDefaultRemotePort	= getString(R.string.pref_default_remote_port);
			mDefaultTimeout		= getString(R.string.pref_default_timeout);

			mPrefLocalHost	= (EditTextPreference) findPreference(mKeyLocalHost);
			mPrefLocalPort	= (EditTextPreference) findPreference(mKeyLocalPort);
			mPrefBroadcast	= (EditTextPreference) findPreference(mKeyBroadcast);
			mPrefRemoteHost	= (EditTextPreference) findPreference(mKeyRemoteHost);
			mPrefRemotePort	= (EditTextPreference) findPreference(mKeyRemotePort);
			mPrefTimeout	= (EditTextPreference) findPreference(mKeyTimeout);
		}

		@Override
		public void onResume() {
			super.onResume();

			// Setup the initial values
			mPrefLocalHost.setSummary(mPref.getString(mKeyLocalHost, mDefaultLocalHost));
			mPrefLocalPort.setSummary(mPref.getString(mKeyLocalPort, mDefaultLocalPort));
			mPrefBroadcast.setSummary(mPref.getString(mKeyBroadcast, mDefaultBroadcast));
			mPrefRemoteHost.setSummary(mPref.getString(mKeyRemoteHost, mDefaultRemoteHost));
			mPrefRemotePort.setSummary(mPref.getString(mKeyRemotePort, mDefaultRemotePort));
			mPrefTimeout.setSummary(mPref.getString(mKeyTimeout, mDefaultTimeout) + " ms");

			// Set up a listener whenever a key changes            
			getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		}

		@Override
		public void onPause() {
			super.onPause();

			// Unregister the listener whenever a key changes            
			getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);    
		}

		/**
		 * On change la valeur affichée d'un paramètre après l'avoir modifié. 
		 */
		@Override
		public void onSharedPreferenceChanged(SharedPreferences _sharedPreferences, String _key) {

			if (_key.equals(mKeyLocalHost)) {
				mPrefLocalHost.setSummary(mPref.getString(mKeyLocalHost, mDefaultLocalHost));

			} else if (_key.equals(mKeyLocalPort)) {
				mPrefLocalPort.setSummary(mPref.getString(mKeyLocalPort, mDefaultLocalPort));

			} else if (_key.equals(mKeyBroadcast)) {
				mPrefBroadcast.setSummary(mPref.getString(mKeyBroadcast, mDefaultBroadcast));

			} else if (_key.equals(mKeyRemoteHost)) {
				mPrefRemoteHost.setSummary(mPref.getString(mKeyRemoteHost, mDefaultRemoteHost));

			} else if (_key.equals(mKeyLocalHost)) {
				mPrefRemotePort.setSummary(mPref.getString(mKeyRemotePort, mDefaultRemotePort));

			} else if (_key.equals(mKeyTimeout)) {
				mPrefTimeout.setSummary(mPref.getString(mKeyTimeout, mDefaultTimeout) + " ms");

			}

		}
	}
	
	/**
	 * @param _appContext Le contexte de l'application
	 * @return La valeur de la préférence Adresse ip locale du serveur.
	 *//*
	private static String getLocalHost(Context _context) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(_context.getApplicationContext());
		final String keyLocalHost		= _context.getString(R.string.pref_key_local_host);
		final String defaultLocalHost	= _context.getString(R.string.pref_default_local_host);
		return pref.getString(keyLocalHost, defaultLocalHost);
	}*/
}