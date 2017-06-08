/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.deletescape.lawnchair;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import java.util.Map;

import ch.deletescape.lawnchair.util.PackageManagerHelper;

/**
 * Settings activity for Launcher. Currently implements the following setting: Allow rotation
 */
public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new LauncherSettingsFragment())
                .commit();
    }

    /**
     * This fragment shows the launcher preferences.
     */
    public static class LauncherSettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getPreferenceManager().setSharedPreferencesName(LauncherFiles.SHARED_PREFERENCES_KEY);
            addPreferencesFromResource(R.xml.launcher_preferences);
            PackageManager pm = getActivity().getPackageManager();
            Map<String, String> iconPackPackages = PackageManagerHelper.getIconPackPackages(pm);
            final CharSequence[] entries = new String[iconPackPackages.size() + 1];
            String[] entryValues = new String[iconPackPackages.size() + 1];
            entries[0] = "None";
            entryValues[0] = "";
            int i = 1;
            for (String key : iconPackPackages.keySet()) {
                entryValues[i] = key;
                entries[i++] = iconPackPackages.get(key);
            }
            ListPreference iconPackPackagePreference = (ListPreference) findPreference("pref_iconPackPackage");
            iconPackPackagePreference.setEntries(entries);
            iconPackPackagePreference.setEntryValues(entryValues);
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            if (preference.getKey() != null && preference.getKey().equals("about")) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/deletescape-media/lawnchair"));
                startActivity(i);
                return true;
            }
            return false;
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            LauncherAppState app = LauncherAppState.getInstanceNoCreate();
            if (app != null) {
                app.reloadAll(true);
            }
        }
    }
}
