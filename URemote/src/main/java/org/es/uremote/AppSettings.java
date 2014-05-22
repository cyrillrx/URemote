package org.es.uremote;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import org.es.uremote.computer.dao.ServerSettingDao;
import org.es.uremote.device.ServerSetting;
import org.es.utils.EditIntPreference;

import java.util.List;

/**
 * Activity that hosts application preferences.
 *
 * @author Cyril Leroux
 *         Created on 05/05/12.
 */
public class AppSettings extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBuildHeaders(List<Header> headers) {
        loadHeadersFromResource(R.xml.preference_headers, headers);
        for (Header header : headers) {
            if (header.titleRes == R.string.app_version_title) {
                final String buildType = getString(R.string.build_type);
                StringBuilder versionName = new StringBuilder();
                versionName.append("v");
                try {
                    versionName.append(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
                } catch (PackageManager.NameNotFoundException e) { }
                if (!buildType.isEmpty()) {
                    versionName.append(" ").append(buildType);
                }
                header.summary = versionName;
                break;
            }
        }
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        //Log.d("AppSettings", "isValidFragment Fragment name : " + fragmentName);
        //Log.d("AppSettings", "isValidFragment Class name " + this.getClass().getName());
        return "org.es.uremote.AppSettings$PrefServer".equals(fragmentName);
    }

    /** This fragment shows the preferences for the server. */
    public static class PrefServer extends PreferenceFragment implements OnSharedPreferenceChangeListener {

        private String mKeyServerId;
        private int mDefaultServerId;
        private EditIntPreference mPrefServerId;


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_server);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            mKeyServerId = getString(R.string.key_server_id);
            mDefaultServerId = getResources().getInteger(R.integer.default_server_id);
            mPrefServerId = (EditIntPreference) findPreference(mKeyServerId);

        }

        @Override
        public void onResume() {
            super.onResume();

            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            setSummaryTargetServer(pref);

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
                setSummaryTargetServer(pref);
            }
        }

        private void setSummaryTargetServer(SharedPreferences pref) {
            final int serverId = pref.getInt(mKeyServerId, mDefaultServerId);
            mPrefServerId.setSummary((serverId == -1) ? R.string.select_server : R.string.server_loading);
            (new AsyncLoadServer()).execute(serverId);
        }

        /**
         * Load the servers from a list of {@link java.io.File} objects.
         *
         * @author Cyril Leroux
         */
        private class AsyncLoadServer extends AsyncTask<Integer, Void, ServerSetting> {
            @Override
            protected ServerSetting doInBackground(Integer... intValues) {
                final List<ServerSetting> servers = ServerSettingDao.loadList(getActivity().getApplicationContext());
                final int serverId = intValues[0];
                try {
                    return servers.get(serverId);
                } catch (IndexOutOfBoundsException | NullPointerException e) {
                    // TODO Return serverConfig exception
                    return null;
                }
            }

            @Override
            protected void onPostExecute(ServerSetting selectedServer) {
                if (selectedServer != null) {
                    mPrefServerId.setSummary(selectedServer.getName());
                } else {
                    mPrefServerId.setSummary(R.string.select_server);
                }
            }
        }
    }
}