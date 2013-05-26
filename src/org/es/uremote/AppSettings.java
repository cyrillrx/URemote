package org.es.uremote;

import java.util.List;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/**
 * Activity that hosts application preferences.
 * @author Cyril Leroux
 *
 */
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
	 * This fragment shows the preferences for the server.
	 */
	public static class PrefServer extends PreferenceFragment implements OnSharedPreferenceChangeListener {

		private String mKeyLocalHost;
		private String mKeyLocalPort;
		private String mKeyBroadcast;
		private String mKeyRemoteHost;
		private String mKeyRemotePort;
		private String mKeySecurityToken;
		private String mKeyConnectionTimeout;
		private String mKeyReadTimeout;
		private String mKeyMacAddress;

		private String mDefaultLocalHost;
		private String mDefaultLocalPort;
		private String mDefaultBroadcast;
		private String mDefaultRemoteHost;
		private String mDefaultRemotePort;
		private String mDefaultSecurityToken;
		private String mDefaultConnectionTimeout;
		private String mDefaultReadTimeout;
		private String mDefaultMacAddress;

		private EditTextPreference mPrefLocalHost;
		private EditTextPreference mPrefLocalPort;
		private EditTextPreference mPrefBroadcast;
		private EditTextPreference mPrefRemoteHost;
		private EditTextPreference mPrefRemotePort;
		private EditTextPreference mPrefSecurityToken;
		private EditTextPreference mPrefConnectionTimeout;
		private EditTextPreference mPrefReadTimeout;
		private EditTextPreference mPrefMacAddress;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_server);
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

			mKeyLocalHost	= getString(R.string.key_local_host);
			mKeyLocalPort	= getString(R.string.key_local_port);
			mKeyBroadcast	= getString(R.string.key_broadcast);
			mKeyRemoteHost	= getString(R.string.key_remote_host);
			mKeyRemotePort	= getString(R.string.key_remote_port);
			mKeySecurityToken		= getString(R.string.key_security_token);
			mKeyConnectionTimeout	= getString(R.string.key_connection_timeout);
			mKeyReadTimeout			= getString(R.string.key_read_timeout);
			mKeyMacAddress	= getString(R.string.key_mac_address);

			mDefaultLocalHost	= getString(R.string.default_local_host);
			mDefaultLocalPort	= getString(R.string.default_local_port);
			mDefaultBroadcast	= getString(R.string.default_broadcast);
			mDefaultRemoteHost	= getString(R.string.default_remote_host);
			mDefaultRemotePort	= getString(R.string.default_remote_port);
			mDefaultSecurityToken		= getString(R.string.default_security_token);
			mDefaultConnectionTimeout	= getString(R.string.default_connection_timeout);
			mDefaultReadTimeout			= getString(R.string.default_read_timeout);
			mDefaultMacAddress	= getString(R.string.default_mac_address);

			mPrefLocalHost	= (EditTextPreference) findPreference(mKeyLocalHost);
			mPrefLocalPort	= (EditTextPreference) findPreference(mKeyLocalPort);
			mPrefBroadcast	= (EditTextPreference) findPreference(mKeyBroadcast);
			mPrefRemoteHost	= (EditTextPreference) findPreference(mKeyRemoteHost);
			mPrefRemotePort	= (EditTextPreference) findPreference(mKeyRemotePort);
			mPrefSecurityToken		= (EditTextPreference) findPreference(mKeySecurityToken);
			mPrefConnectionTimeout	= (EditTextPreference) findPreference(mKeyConnectionTimeout);
			mPrefReadTimeout		= (EditTextPreference) findPreference(mKeyReadTimeout);
			mPrefMacAddress	= (EditTextPreference) findPreference(mKeyMacAddress);
		}

		@Override
		public void onResume() {
			super.onResume();

			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

			// Setup the initial values
			mPrefLocalHost.setSummary(pref.getString(mKeyLocalHost, mDefaultLocalHost));
			mPrefLocalPort.setSummary(pref.getString(mKeyLocalPort, mDefaultLocalPort));
			mPrefBroadcast.setSummary(pref.getString(mKeyBroadcast, mDefaultBroadcast));
			mPrefRemoteHost.setSummary(pref.getString(mKeyRemoteHost, mDefaultRemoteHost));
			mPrefRemotePort.setSummary(pref.getString(mKeyRemotePort, mDefaultRemotePort));
			mPrefSecurityToken.setSummary(pref.getString(mKeySecurityToken, mDefaultSecurityToken));
			mPrefConnectionTimeout.setSummary(pref.getString(mKeyConnectionTimeout, mDefaultConnectionTimeout) + " ms");
			mPrefReadTimeout.setSummary(pref.getString(mKeyReadTimeout, mDefaultReadTimeout) + " ms");
			mPrefMacAddress.setSummary(pref.getString(mKeyMacAddress, mDefaultMacAddress));

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
		 * Change the summary of a value whenever the value had been modified.
		 */
		@Override
		public void onSharedPreferenceChanged(SharedPreferences pref, String key) {

			if (key.equals(mKeyLocalHost)) {
				mPrefLocalHost.setSummary(pref.getString(mKeyLocalHost, mDefaultLocalHost));

			} else if (key.equals(mKeyLocalPort)) {
				mPrefLocalPort.setSummary(pref.getString(mKeyLocalPort, mDefaultLocalPort));

			} else if (key.equals(mKeyBroadcast)) {
				mPrefBroadcast.setSummary(pref.getString(mKeyBroadcast, mDefaultBroadcast));

			} else if (key.equals(mKeyRemoteHost)) {
				mPrefRemoteHost.setSummary(pref.getString(mKeyRemoteHost, mDefaultRemoteHost));

			} else if (key.equals(mKeyLocalHost)) {
				mPrefRemotePort.setSummary(pref.getString(mKeyRemotePort, mDefaultRemotePort));

			} else if (key.equals(mKeySecurityToken)) {
				mPrefSecurityToken.setSummary(pref.getString(mKeySecurityToken, mDefaultSecurityToken));

			} else if (key.equals(mKeyConnectionTimeout)) {
				mPrefConnectionTimeout.setSummary(pref.getString(mKeyConnectionTimeout, mDefaultConnectionTimeout) + " ms");

			} else if (key.equals(mKeyReadTimeout)) {
				mPrefReadTimeout.setSummary(pref.getString(mKeyReadTimeout, mDefaultReadTimeout) + " ms");

			} else if (key.equals(mKeyMacAddress)) {
				mPrefMacAddress.setSummary(pref.getString(mKeyMacAddress, mDefaultMacAddress));

			}
		}
	}
}