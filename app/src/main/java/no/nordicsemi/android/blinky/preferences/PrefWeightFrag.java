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
public class PrefWeightFrag extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_MAX_WEIGHT_VALUE = "max_weight_value";
    public static final String KEY_DISCRETE_VALUE = "discrete_value";
    public static final String KEY_COR_TURN_ON = "cor_turn_on_value";

    Preference maxWeightPref;
    Preference discretePref;



    public PrefWeightFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.weight_pref);
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
        maxWeightPref = findPreference(KEY_MAX_WEIGHT_VALUE);
        discretePref = findPreference(KEY_DISCRETE_VALUE);
        String maxWeight = sharedPreferences.getString(KEY_MAX_WEIGHT_VALUE,"0");
        String discreteValue = sharedPreferences.getString(KEY_DISCRETE_VALUE,"0");

        maxWeightPref.setSummary(maxWeight);
        discretePref.setSummary(discreteValue);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Preference curPref = findPreference(key);

        if (key.equals(KEY_MAX_WEIGHT_VALUE)) {
            maxWeightPref.setSummary(sharedPreferences.getString(key, "0"));;
        } else if (key.equals(KEY_DISCRETE_VALUE)) {
            discretePref.setSummary(sharedPreferences.getString(key, "0"));;
        }

    }
}
