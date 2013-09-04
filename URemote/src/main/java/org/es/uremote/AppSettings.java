package org.es.uremote;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import java.util.List;

/**
 * Activity that hosts application preferences.
 *
 * @author Cyril Leroux
 * Created on 05/05/12.
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

	/** This fragment shows the preferences for the server. */
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
		private int mDefaultLocalPort;
		private String mDefaultBroadcast;
		private String mDefaultRemoteHost;
		private int mDefaultRemotePort;
		private String mDefaultSecurityToken;
		private int mDefaultConnectionTimeout;
		private int mDefaultReadTimeout;
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

			mKeyLocalHost			= getString(R.string.key_local_host);
			mKeyLocalPort			= getString(R.string.key_local_port);
			mKeyBroadcast			= getString(R.string.key_broadcast);
			mKeyRemoteHost			= getString(R.string.key_remote_host);
			mKeyRemotePort			= getString(R.string.key_remote_port);
			mKeySecurityToken		= getString(R.string.key_security_token);
			mKeyConnectionTimeout	= getString(R.string.key_connection_timeout);
			mKeyReadTimeout			= getString(R.string.key_read_timeout);
			mKeyMacAddress			= getString(R.string.key_mac_address);

			mDefaultLocalHost	= getString(R.string.default_local_host);
			mDefaultLocalPort	= getResources().getInteger(R.integer.default_local_port);
			mDefaultBroadcast	= getString(R.string.default_broadcast);
			mDefaultRemoteHost	= getString(R.string.default_remote_host);
			mDefaultRemotePort	= getResources().getInteger(R.integer.default_remote_port);
			mDefaultSecurityToken		= getString(R.string.default_security_token);
			mDefaultConnectionTimeout	= getResources().getInteger(R.integer.default_connection_timeout);
			mDefaultReadTimeout			= getResources().getInteger(R.integer.default_read_timeout);
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
			mPrefLocalPort.setSummary(pref.getInt(mKeyLocalPort, mDefaultLocalPort));
			mPrefBroadcast.setSummary(pref.getString(mKeyBroadcast, mDefaultBroadcast));
			mPrefRemoteHost.setSummary(pref.getString(mKeyRemoteHost, mDefaultRemoteHost));
			mPrefRemotePort.setSummary(pref.getInt(mKeyRemotePort, mDefaultRemotePort));
			mPrefSecurityToken.setSummary(pref.getString(mKeySecurityToken, mDefaultSecurityToken));
			mPrefConnectionTimeout.setSummary(pref.getInt(mKeyConnectionTimeout, mDefaultConnectionTimeout) + " ms");
			mPrefReadTimeout.setSummary(pref.getInt(mKeyReadTimeout, mDefaultReadTimeout) + " ms");
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

		/** Change the summary of a value whenever the value had been modified. */
		@Override
		public void onSharedPreferenceChanged(SharedPreferences pref, String key) {

			if (key.equals(mKeyLocalHost)) {
				mPrefLocalHost.setSummary(pref.getString(mKeyLocalHost, mDefaultLocalHost));

			} else if (key.equals(mKeyLocalPort)) {
				mPrefLocalPort.setSummary(pref.getInt(mKeyLocalPort, mDefaultLocalPort));

			} else if (key.equals(mKeyBroadcast)) {
				mPrefBroadcast.setSummary(pref.getString(mKeyBroadcast, mDefaultBroadcast));

			} else if (key.equals(mKeyRemoteHost)) {
				mPrefRemoteHost.setSummary(pref.getString(mKeyRemoteHost, mDefaultRemoteHost));

			} else if (key.equals(mKeyLocalHost)) {
				mPrefRemotePort.setSummary(pref.getInt(mKeyRemotePort, mDefaultRemotePort));

			} else if (key.equals(mKeySecurityToken)) {
				mPrefSecurityToken.setSummary(pref.getString(mKeySecurityToken, mDefaultSecurityToken));

			} else if (key.equals(mKeyConnectionTimeout)) {
				mPrefConnectionTimeout.setSummary(pref.getInt(mKeyConnectionTimeout, mDefaultConnectionTimeout) + " ms");

			} else if (key.equals(mKeyReadTimeout)) {
				mPrefReadTimeout.setSummary(pref.getInt(mKeyReadTimeout, mDefaultReadTimeout) + " ms");

			} else if (key.equals(mKeyMacAddress)) {
				mPrefMacAddress.setSummary(pref.getString(mKeyMacAddress, mDefaultMacAddress));

			}
		}
	}
}