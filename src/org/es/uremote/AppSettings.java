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

		protected AppSettings mActivity;
		protected SharedPreferences mPref = null;

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
			mActivity = (AppSettings) getActivity();
			mPref = PreferenceManager.getDefaultSharedPreferences(mActivity.getApplicationContext());

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

			// Setup the initial values
			mPrefLocalHost.setSummary(mPref.getString(mKeyLocalHost, mDefaultLocalHost));
			mPrefLocalPort.setSummary(mPref.getString(mKeyLocalPort, mDefaultLocalPort));
			mPrefBroadcast.setSummary(mPref.getString(mKeyBroadcast, mDefaultBroadcast));
			mPrefRemoteHost.setSummary(mPref.getString(mKeyRemoteHost, mDefaultRemoteHost));
			mPrefRemotePort.setSummary(mPref.getString(mKeyRemotePort, mDefaultRemotePort));
			mPrefSecurityToken.setSummary(mPref.getString(mKeySecurityToken, mDefaultSecurityToken));
			mPrefConnectionTimeout.setSummary(mPref.getString(mKeyConnectionTimeout, mDefaultConnectionTimeout) + " ms");
			mPrefReadTimeout.setSummary(mPref.getString(mKeyReadTimeout, mDefaultReadTimeout) + " ms");
			mPrefMacAddress.setSummary(mPref.getString(mKeyMacAddress, mDefaultMacAddress));

			// Set up a listener whenever a key changes
			getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		}

		@Override
		public void onPause() {
			super.onPause();

			// Unregister the listener whenever a key changes
			getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
		}

		/** Change the summary of a value whenever the value had been modified. */
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

			} else if (_key.equals(mKeySecurityToken)) {
				mPrefSecurityToken.setSummary(mPref.getString(mKeySecurityToken, mDefaultSecurityToken));

			} else if (_key.equals(mKeyConnectionTimeout)) {
				mPrefConnectionTimeout.setSummary(mPref.getString(mKeyConnectionTimeout, mDefaultConnectionTimeout) + " ms");

			} else if (_key.equals(mKeyReadTimeout)) {
				mPrefReadTimeout.setSummary(mPref.getString(mKeyReadTimeout, mDefaultReadTimeout) + " ms");

			} else if (_key.equals(mKeyMacAddress)) {
				mPrefMacAddress.setSummary(mPref.getString(mKeyMacAddress, mDefaultMacAddress));

			}
		}
	}
}