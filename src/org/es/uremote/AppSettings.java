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
		protected SharedPreferences mPref;

		private String mKeyLocalHost;
		private String mKeyLocalPort;
		private String mKeyRemoteHost;
		private String mKeyRemotePort;
		private String mKeyTimeout;

		private String mDefaultLocalHost;
		private String mDefaultLocalPort;
		private String mDefaultRemoteHost;
		private String mDefaultRemotePort;
		private String mDefaultTimeout;

		private EditTextPreference mPrefLocalHost;
		private EditTextPreference mPrefLocalPort;
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
			mKeyRemoteHost	= getString(R.string.pref_key_remote_host);
			mKeyRemotePort	= getString(R.string.pref_key_remote_port);
			mKeyTimeout		= getString(R.string.pref_key_timeout);

			mDefaultLocalHost	= getString(R.string.pref_key_local_host);
			mDefaultLocalPort	= getString(R.string.pref_key_local_port);
			mDefaultRemoteHost	= getString(R.string.pref_key_remote_host);
			mDefaultRemotePort	= getString(R.string.pref_key_remote_port);
			mDefaultTimeout		= getString(R.string.pref_key_timeout);

			mPrefLocalHost	= (EditTextPreference) findPreference(mKeyLocalHost);
			mPrefLocalPort	= (EditTextPreference) findPreference(mKeyLocalPort);
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

		public void onSharedPreferenceChanged(SharedPreferences _sharedPreferences, String _key) {

			// Let's do something a preference value changes
			if (_key.equals(mKeyLocalHost)) {
				mPrefLocalHost.setSummary(mPref.getString(mKeyLocalHost, mDefaultLocalHost));

			} else if (_key.equals(mKeyLocalHost)) {
				mPrefLocalPort.setSummary(mPref.getString(mKeyLocalPort, mDefaultLocalPort));

			} else if (_key.equals(mKeyLocalHost)) {
				mPrefRemoteHost.setSummary(mPref.getString(mKeyRemoteHost, mDefaultRemoteHost));

			} else if (_key.equals(mKeyLocalHost)) {
				mPrefRemotePort.setSummary(mPref.getString(mKeyRemotePort, mDefaultRemotePort));

			} else if (_key.equals(mKeyTimeout)) {
				mPrefTimeout.setSummary(mPref.getString(mKeyTimeout, mDefaultTimeout) + " ms");

			}

		}
	}

}