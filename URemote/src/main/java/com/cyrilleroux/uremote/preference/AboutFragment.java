package com.cyrilleroux.uremote.preference;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.cyrilleroux.uremote.R;

/**
 * Created by Cyril Leroux on 23/05/2014.
 */
public class AboutFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_about);

        // Set app version string
        final String appVersionKey = getString(R.string.app_version_key);
        findPreference(appVersionKey).setSummary(getVersionName());
    }

    private String getVersionName() {

        final String buildType = getString(R.string.build_type);

        final StringBuilder versionName = new StringBuilder("v");
        try {
            versionName.append(getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
        }
        if (!buildType.isEmpty()) {
            versionName.append(" ").append(buildType);
        }

        return versionName.toString();
    }
}