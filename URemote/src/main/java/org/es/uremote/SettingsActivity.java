package org.es.uremote;

import android.preference.PreferenceActivity;

import org.es.uremote.preference.AboutFragment;

import java.util.List;

/**
 * Activity that hosts application preferences.
 *
 * @author Cyril Leroux
 *         Created on 05/05/12.
 */
public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onBuildHeaders(List<Header> headers) {
        loadHeadersFromResource(R.xml.preference_headers, headers);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return AboutFragment.class.getName().equals(fragmentName);
    }
}