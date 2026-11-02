/*
 * SPDX-FileCopyrightText: 2026 The AOSPA Project
 * SPDX-License-Identifier: Apache-2.0
 */

package co.aospa.settings.display;

import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;

import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.SwitchPreferenceCompat;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;

@SearchIndexable
public class StatusBarClockSettings extends SettingsPreferenceFragment {

    private static final String KEY_SECONDS = "clock_seconds";
    private static final String KEY_AM_PM = "status_bar_clock_am_pm";
    private static final String KEY_DATE_DISPLAY = "status_bar_clock_date_display";
    private static final String KEY_DATE_STYLE = "status_bar_clock_date_style";
    private static final String KEY_DATE_POSITION = "status_bar_clock_date_position";
    private static final String KEY_DATE_FORMAT = "status_bar_clock_date_format";
    private static final String KEY_BOLD = "status_bar_clock_bold";
    private static final String KEY_PERIOD_SEPARATOR = "status_bar_clock_period_separator";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.status_bar_clock_settings, rootKey);

        bindSwitch(KEY_SECONDS, KEY_SECONDS, false);
        bindList(KEY_AM_PM, KEY_AM_PM, 2);
        bindList(KEY_DATE_DISPLAY, KEY_DATE_DISPLAY, 0);
        bindList(KEY_DATE_STYLE, KEY_DATE_STYLE, 0);
        bindList(KEY_DATE_POSITION, KEY_DATE_POSITION, 0);
        bindDateFormat();
        bindSwitch(KEY_BOLD, KEY_BOLD, false);
        bindSwitch(KEY_PERIOD_SEPARATOR, KEY_PERIOD_SEPARATOR, false);
    }

    private void bindSwitch(String preferenceKey, String settingKey, boolean defaultValue) {
        SwitchPreferenceCompat preference = findPreference(preferenceKey);
        if (preference == null) {
            return;
        }
        boolean value = Settings.Secure.getIntForUser(getContentResolver(), settingKey,
                defaultValue ? 1 : 0, UserHandle.USER_CURRENT) != 0;
        preference.setChecked(value);
        preference.setOnPreferenceChangeListener((pref, newValue) -> {
            boolean checked = (Boolean) newValue;
            Settings.Secure.putIntForUser(getContentResolver(), settingKey,
                    checked ? 1 : 0, UserHandle.USER_CURRENT);
            return true;
        });
    }

    private void bindList(String preferenceKey, String settingKey, int defaultValue) {
        ListPreference preference = findPreference(preferenceKey);
        if (preference == null) {
            return;
        }
        int value = Settings.Secure.getIntForUser(getContentResolver(), settingKey,
                defaultValue, UserHandle.USER_CURRENT);
        preference.setValue(String.valueOf(value));
        updateListSummary(preference, String.valueOf(value));
        preference.setOnPreferenceChangeListener((pref, newValue) -> {
            String valueString = (String) newValue;
            Settings.Secure.putIntForUser(getContentResolver(), settingKey,
                    Integer.parseInt(valueString), UserHandle.USER_CURRENT);
            updateListSummary((ListPreference) pref, valueString);
            return true;
        });
    }

    private void updateListSummary(ListPreference preference, String value) {
        int index = preference.findIndexOfValue(value);
        if (index >= 0 && index < preference.getEntries().length) {
            preference.setSummary(preference.getEntries()[index]);
        }
    }

    private void bindDateFormat() {
        EditTextPreference preference = findPreference(KEY_DATE_FORMAT);
        if (preference == null) {
            return;
        }
        String value = Settings.Secure.getStringForUser(getContentResolver(), KEY_DATE_FORMAT,
                UserHandle.USER_CURRENT);
        if (value == null) {
            value = "";
        }
        preference.setText(value);
        updateDateFormatSummary(preference, value);
        preference.setOnPreferenceChangeListener((pref, newValue) -> {
            String valueString = newValue == null ? "" : newValue.toString();
            Settings.Secure.putStringForUser(getContentResolver(), KEY_DATE_FORMAT,
                    valueString, UserHandle.USER_CURRENT);
            updateDateFormatSummary((EditTextPreference) pref, valueString);
            return true;
        });
    }

    private void updateDateFormatSummary(EditTextPreference preference, String value) {
        preference.setSummary(value.isEmpty()
                ? getString(R.string.status_bar_clock_date_format_default)
                : value);
    }

    @Override
    public int getMetricsCategory() {
        return -1;
    }

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.status_bar_clock_settings);
}
