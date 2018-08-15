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
import android.widget.TextView;

import no.nordicsemi.android.blinky.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PrefArchive extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{


    public static final String KEY_DISCRETE_MAX = "max_discrete";
    public static final String KEY_MIN_WEIGHT = "min_weight";
    public static final String KEY_TIME_STAB = "time_stab";
    public static final String KEY_DEBUG = "debug_archive";
    public static final String KEY_ARCHIVE_SAVE = "archive_save";
    public static final String KEY_ARCHIVE_SAVE_ADC = "archive_save_adc";


    Preference discreteMaxPref;
    Preference timeStabPref;
    Preference minWeightPref;

    public PrefArchive() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_archive);
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
        discreteMaxPref = findPreference(KEY_DISCRETE_MAX);
        timeStabPref = findPreference(KEY_TIME_STAB);
        minWeightPref = findPreference(KEY_MIN_WEIGHT);
        String maxWeight = sharedPreferences.getString(KEY_DISCRETE_MAX,"1");
        String discreteValue = sharedPreferences.getString(KEY_TIME_STAB,"3");
        String minWeightValue = sharedPreferences.getString(KEY_MIN_WEIGHT,"1");

        discreteMaxPref.setSummary(maxWeight);
        timeStabPref.setSummary(discreteValue);
        minWeightPref.setSummary(minWeightValue);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference curPref = findPreference(key);

        if (key.equals(KEY_DISCRETE_MAX)) {
            curPref = discreteMaxPref;
            curPref.setSummary(sharedPreferences.getString(key, "0"));
        } else if (key.equals(KEY_TIME_STAB)) {
            curPref = timeStabPref;
            curPref.setSummary(sharedPreferences.getString(key, "0"));
        } else if (key.equals(KEY_MIN_WEIGHT)) {
            curPref = minWeightPref;
            curPref.setSummary(sharedPreferences.getString(key, "0"));
        }

    }
}

