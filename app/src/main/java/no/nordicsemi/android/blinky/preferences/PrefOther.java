package no.nordicsemi.android.blinky.preferences;


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

import no.nordicsemi.android.blinky.R;

public class PrefOther extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_VOLUME_ACTIVE_DELAY = "volume_activate_delay";

    Preference timeDelayVolume;
    String timeDelay;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_other);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        timeDelayVolume = findPreference(KEY_VOLUME_ACTIVE_DELAY);
        timeDelay = sharedPreferences.getString(KEY_VOLUME_ACTIVE_DELAY, "1");
        timeDelayVolume.setSummary(timeDelay);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(KEY_VOLUME_ACTIVE_DELAY)) {
            timeDelayVolume.setSummary(sharedPreferences.getString(key, "1"));
        }
    }
}
