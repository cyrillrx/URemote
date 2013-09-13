package org.es.uremote;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import org.es.utils.EditIntPreference;

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

		private String mKeyServerId;
		private String mKeyLocalHost;
		private String mKeyLocalPort;
		private String mKeyBroadcast;
		private String mKeyRemoteHost;
		private String mKeyRemotePort;
		private String mKeySecurityToken;
		private String mKeyConnectionTimeout;
		private String mKeyReadTimeout;
		private String mKeyMacAddress;

		private int mDefaultServerId;
		private String mDefaultLocalHost;
		private int mDefaultLocalPort;
		private String mDefaultBroadcast;
		private String mDefaultRemoteHost;
		private int mDefaultRemotePort;
		private String mDefaultSecurityToken;
		private int mDefaultConnectionTimeout;
		private int mDefaultReadTimeout;
		private String mDefaultMacAddress;

		private EditIntPreference mPrefServerId;
		private EditTextPreference mPrefLocalHost;
		private EditIntPreference mPrefLocalPort;
		private EditTextPreference mPrefBroadcast;
		private EditTextPreference mPrefRemoteHost;
		private EditIntPreference mPrefRemotePort;
		private EditTextPreference mPrefSecurityToken;
		private EditIntPreference mPrefConnectionTimeout;
		private EditIntPreference mPrefReadTimeout;
		private EditTextPreference mPrefMacAddress;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_server);
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

			mKeyServerId			= getString(R.string.key_server_id);
			mKeyLocalHost			= getString(R.string.key_local_host);
			mKeyLocalPort			= getString(R.string.key_local_port);
			mKeyBroadcast			= getString(R.string.key_broadcast);
			mKeyRemoteHost			= getString(R.string.key_remote_host);
			mKeyRemotePort			= getString(R.string.key_remote_port);
			mKeySecurityToken		= getString(R.string.key_security_token);
			mKeyConnectionTimeout	= getString(R.string.key_connection_timeout);
			mKeyReadTimeout			= getString(R.string.key_read_timeout);
			mKeyMacAddress			= getString(R.string.key_mac_address);

			mDefaultServerId	= getResources().getInteger(R.integer.default_server_id);
			mDefaultLocalHost	= getString(R.string.default_local_host);
			mDefaultLocalPort	= getResources().getInteger(R.integer.default_local_port);
			mDefaultBroadcast	= getString(R.string.default_broadcast);
			mDefaultRemoteHost	= getString(R.string.default_remote_host);
			mDefaultRemotePort	= getResources().getInteger(R.integer.default_remote_port);
			mDefaultSecurityToken		= getString(R.string.default_security_token);
			mDefaultConnectionTimeout	= getResources().getInteger(R.integer.default_connection_timeout);
			mDefaultReadTimeout			= getResources().getInteger(R.integer.default_read_timeout);
			mDefaultMacAddress	= getString(R.string.default_mac_address);

			mPrefServerId	= (EditIntPreference) findPreference(mKeyServerId);
			mPrefLocalHost	= (EditTextPreference) findPreference(mKeyLocalHost);
			mPrefLocalPort	= (EditIntPreference) findPreference(mKeyLocalPort);
			mPrefBroadcast	= (EditTextPreference) findPreference(mKeyBroadcast);
			mPrefRemoteHost	= (EditTextPreference) findPreference(mKeyRemoteHost);
			mPrefRemotePort	= (EditIntPreference) findPreference(mKeyRemotePort);
			mPrefSecurityToken		= (EditTextPreference) findPreference(mKeySecurityToken);
			mPrefConnectionTimeout	= (EditIntPreference) findPreference(mKeyConnectionTimeout);
			mPrefReadTimeout		= (EditIntPreference) findPreference(mKeyReadTimeout);
			mPrefMacAddress	= (EditTextPreference) findPreference(mKeyMacAddress);
		}

		@Override
		public void onResume() {
			super.onResume();

			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

			mPrefServerId.setSummary(getSummaryTargetServer(pref));
			mPrefLocalHost.setSummary(getSummaryLocalHost(pref));
			mPrefLocalPort.setSummary(getSummaryLocalPort(pref));
			mPrefBroadcast.setSummary(getSummaryBroadcast(pref));
			mPrefRemoteHost.setSummary(getSummaryRemoteHost(pref));
			mPrefRemotePort.setSummary(getSummaryRemotePort(pref));
			mPrefSecurityToken.setSummary(getSummarySecurityToken(pref));
			mPrefConnectionTimeout.setSummary(getSummaryConnectionTimeout(pref));
			mPrefReadTimeout.setSummary(getSummaryReadTimeout(pref));
			mPrefMacAddress.setSummary(getSummaryMacAddress(pref));

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

			if (key.equals(mKeyServerId)) {
				mPrefServerId.setSummary(getSummaryTargetServer(pref));

			} else if (key.equals(mKeyLocalHost)) {
				mPrefLocalHost.setSummary(getSummaryLocalHost(pref));

			} else if (key.equals(mKeyLocalPort)) {
				mPrefLocalPort.setSummary(getSummaryLocalPort(pref));

			} else if (key.equals(mKeyBroadcast)) {
				mPrefBroadcast.setSummary(getSummaryBroadcast(pref));

			} else if (key.equals(mKeyRemoteHost)) {
				mPrefRemoteHost.setSummary(getSummaryRemoteHost(pref));

			} else if (key.equals(mKeyLocalHost)) {
				mPrefRemotePort.setSummary(getSummaryRemotePort(pref));

			} else if (key.equals(mKeySecurityToken)) {
				mPrefSecurityToken.setSummary(getSummarySecurityToken(pref));

			} else if (key.equals(mKeyConnectionTimeout)) {
				mPrefConnectionTimeout.setSummary(getSummaryConnectionTimeout(pref));

			} else if (key.equals(mKeyReadTimeout)) {
				mPrefReadTimeout.setSummary(getSummaryReadTimeout(pref));

			} else if (key.equals(mKeyMacAddress)) {
				mPrefMacAddress.setSummary(getSummaryMacAddress(pref));
			}
		}

		private String getSummaryTargetServer(SharedPreferences pref) {
			final int serverId = pref.getInt(mKeyServerId, mDefaultServerId);
			// TODO Replace serverId by server name in the summary
			// => return getServerName(serverId);
			return String.valueOf(serverId);
		}

		private String getSummaryLocalHost(SharedPreferences pref) {
			return pref.getString(mKeyLocalHost, mDefaultLocalHost);
		}

		private String getSummaryLocalPort(SharedPreferences pref) {
			final int localPort = pref.getInt(mKeyLocalPort, mDefaultLocalPort);
			return String.valueOf(localPort);
		}

		private String getSummaryBroadcast(SharedPreferences pref) {
			return pref.getString(mKeyBroadcast, mDefaultBroadcast);
		}

		private String getSummaryRemoteHost(SharedPreferences pref) {
			return pref.getString(mKeyRemoteHost, mDefaultRemoteHost);
		}

		private String getSummaryRemotePort(SharedPreferences pref) {
			final int remotePort = pref.getInt(mKeyRemotePort, mDefaultRemotePort);
			return String.valueOf(remotePort);
		}

		private String getSummarySecurityToken(SharedPreferences pref) {
			// TODO If no pass => warn user. else display ***
			return pref.getString(mKeySecurityToken, mDefaultSecurityToken);
		}

		private String getSummaryConnectionTimeout(SharedPreferences pref) {
			final int connTimeout = pref.getInt(mKeyConnectionTimeout, mDefaultConnectionTimeout);
			return connTimeout  + " ms";
		}

		private String getSummaryReadTimeout(SharedPreferences pref) {
			final int readTimeout = pref.getInt(mKeyReadTimeout, mDefaultReadTimeout);
			return readTimeout + " ms";
		}

		private String getSummaryMacAddress(SharedPreferences pref) {
			return pref.getString(mKeyMacAddress, mDefaultMacAddress);
		}
	}
}