package no.nordicsemi.android.sdr.preferences;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import no.nordicsemi.android.blinky.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_LIST_NUM_BUTTONS = "num_buttons";
    public static final String KEY_ADC_SHOW = "adc_show";
    public static final String KEY_WEIGHT_SHOW = "weight_show";
    public static final String KEY_REMOTE_BUT_UPDATE = "remote_but_update";
    public static final String KEY_SHOW_CONT_SETTINGS_FRAG = "show_contrsettings";
    public static final String KEY_SHOW_DEBUG_FRAG = "show_debug_frag";
    public static final String KEY_NUM_COR_BUT9 = "num_cor_but9";
    public static final String KEY_WALLPAPER_SHOW = "wallpaper_show";

    Preference curPref;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        Preference curPref = findPreference(KEY_LIST_NUM_BUTTONS);
        String numButts = sharedPreferences.getString(KEY_LIST_NUM_BUTTONS, "8");
        curPref.setSummary(numButts);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_LIST_NUM_BUTTONS)) {
            curPref = findPreference(key);
            curPref.setSummary(sharedPreferences.getString(key, "8"));
        }

    }
}
