package no.nordicsemi.android.blinky.preferences;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import no.nordicsemi.android.blinky.R;

public class PrefHardBtns extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_VOLUME_ACTIVE_DELAY = "volume_activate_delay";
    public static final String KEY_VOLUME_BUTTON = "volume_button";
    public static final String KEY_VOLUME_ACTIVATED_SHOW = "volume_activated_show";
    public static final String KEY_VOLUME_NUM_BTNS_SHOW = "volume_num_btns_show";
    public static final String KEY_VOLUME_BTN_NAME_SHOW = "volume_btn_name_show";
    public static final String KEY_VOLUME_PRESS_VIBRO = "volume_press_vibro";
    public static final String KEY_VOLUME_ACTIVATED_VIBRO = "volume_activated_vibro";
    public static final String KEY_VOLUME_LONG_PRESS_INC = "volume_long_press_inc";
    public static final String KEY_VOLUME_PASS_ASK = "volume_pass_ask";
    public static final String KEY_VOLUME_BTN_DEC = "volume_btn_dec";
    public static final String KEY_VOLUME_FONT_SIZE = "volume_font_size";

    Preference timeDelayVolume;
    Preference volumeLongPress;
    Preference volumeFontSize;
    String timeDelay;
    String longPressInc;
    String fontSize;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_hard_btns);
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
        volumeLongPress = findPreference(KEY_VOLUME_LONG_PRESS_INC);
        volumeFontSize = findPreference(KEY_VOLUME_FONT_SIZE);
        timeDelay = sharedPreferences.getString(KEY_VOLUME_ACTIVE_DELAY, "1");
        longPressInc = sharedPreferences.getString(KEY_VOLUME_LONG_PRESS_INC, "10");
        fontSize = sharedPreferences.getString(KEY_VOLUME_FONT_SIZE, "12");
        timeDelayVolume.setSummary(timeDelay);
        volumeLongPress.setSummary(longPressInc);
        volumeFontSize.setSummary(fontSize);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(KEY_VOLUME_ACTIVE_DELAY)) {
            timeDelayVolume.setSummary(sharedPreferences.getString(key, "1000"));
        } else if (key.equals(KEY_VOLUME_LONG_PRESS_INC)) {
            volumeLongPress.setSummary(sharedPreferences.getString(key, "10"));
        } else if (key.equals(KEY_VOLUME_FONT_SIZE)) {
            volumeFontSize.setSummary(sharedPreferences.getString(key, "12"));
        }
    }
}
