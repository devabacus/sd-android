package no.nordicsemi.android.sdr.preferences;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

import no.nordicsemi.android.blinky.R;

import static no.nordicsemi.android.sdr.preferences.PrefUserFrag.KEY_CUR_USER;

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
    public static final String KEY_ARCHIVE_DRIVER_WEIGHT_MAX = "archive_driver_weight_max";
    public static final String KEY_OPTION_ARCHIVE = "option_archive";
    public static final String KEY_ADC_WEIGHT = "adc_weight";
    public static final String KEY_CORR_ARCHIVE = "cor_archive_save";
    public static final String KEY_MAX_WEIGHT_TOLERANCE = "archive_suspect_max_weight";
    public static final String KEY_ONLY_MAX_DETECT = "archive_suspect_only_max";
    public static final String KEY_STAB_WHILE_UNLOAD = "archive_suspect_stab_while_unload";


    Preference discreteMaxPref;
    Preference timeStabPref;
    Preference minWeightPref;
    Preference driverMaxPref;
    Preference archiveSavePref;
    Preference archiveCorSave;
    Preference maxWeightTolerance;

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
        String curUser = sharedPreferences.getString(KEY_CUR_USER, "user");

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        discreteMaxPref = findPreference(KEY_DISCRETE_MAX);
        timeStabPref = findPreference(KEY_TIME_STAB);
        minWeightPref = findPreference(KEY_MIN_WEIGHT);
        driverMaxPref = findPreference(KEY_ARCHIVE_DRIVER_WEIGHT_MAX);
        archiveSavePref = findPreference(KEY_ARCHIVE_SAVE);
        archiveCorSave = findPreference(KEY_CORR_ARCHIVE);
        maxWeightTolerance = findPreference(KEY_MAX_WEIGHT_TOLERANCE);
        String maxDiscrete = sharedPreferences.getString(KEY_DISCRETE_MAX,"1");
        String stableTime = sharedPreferences.getString(KEY_TIME_STAB,"3");
        String minWeightValue = sharedPreferences.getString(KEY_MIN_WEIGHT,"1");
        String driverMaxValue = sharedPreferences.getString(KEY_ARCHIVE_DRIVER_WEIGHT_MAX,"0");
        String weightTolerance = sharedPreferences.getString(KEY_MAX_WEIGHT_TOLERANCE,"0");


        if (curUser.equals("admin") || curUser.equals("admin1")) {
            archiveSavePref.setEnabled(true);
            archiveCorSave.setEnabled(true);
        } else {
            archiveSavePref.setEnabled(false);
            archiveCorSave.setEnabled(false);
        }

        discreteMaxPref.setSummary(maxDiscrete);
        timeStabPref.setSummary(stableTime);
        minWeightPref.setSummary(minWeightValue);
        driverMaxPref.setSummary(driverMaxValue);
        maxWeightTolerance.setSummary(weightTolerance);

    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference curPref = findPreference(key);

        if (key.equals(KEY_DISCRETE_MAX)) {
            curPref = discreteMaxPref;
            if(Objects.requireNonNull(sharedPreferences.getString(key, "0")).isEmpty()){
                sharedPreferences.edit().putString(key, "0").apply();
            }
            curPref.setSummary(sharedPreferences.getString(key, "0"));
        } else if (key.equals(KEY_TIME_STAB)) {
            curPref = timeStabPref;
            curPref.setSummary(sharedPreferences.getString(key, "0"));
            if(Objects.requireNonNull(sharedPreferences.getString(key, "0")).isEmpty()){
                sharedPreferences.edit().putString(key, "0").apply();
            }
        } else if (key.equals(KEY_MIN_WEIGHT)) {
            curPref = minWeightPref;
            curPref.setSummary(sharedPreferences.getString(key, "0"));
            if(Objects.requireNonNull(sharedPreferences.getString(key, "0")).isEmpty()){
                sharedPreferences.edit().putString(key, "0").apply();
            }
        } else if (key.equals(KEY_ARCHIVE_DRIVER_WEIGHT_MAX)) {
            curPref = driverMaxPref;
            curPref.setSummary(sharedPreferences.getString(key, "0"));
            if(Objects.requireNonNull(sharedPreferences.getString(key, "0")).isEmpty()){
                sharedPreferences.edit().putString(key, "0").apply();
            }
        } else if (key.equals(KEY_MAX_WEIGHT_TOLERANCE)) {
            maxWeightTolerance.setSummary(sharedPreferences.getString(key, "0"));
            if(Objects.requireNonNull(sharedPreferences.getString(key, "0")).isEmpty()){
                sharedPreferences.edit().putString(key, "0").apply();
            }
        }

    }
}

