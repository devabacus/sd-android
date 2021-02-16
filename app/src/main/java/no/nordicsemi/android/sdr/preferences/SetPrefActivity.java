package no.nordicsemi.android.sdr.preferences;

import android.preference.PreferenceActivity;

import java.util.List;

import no.nordicsemi.android.blinky.R;

public class SetPrefActivity extends PreferenceActivity {

    private static final String TAG = "SetPrefActivity";

    public void onBuildHeaders(List<PreferenceActivity.Header> target){
        loadHeadersFromResource(R.xml.pref_head,target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName){
        return SettingsFragment.class.getName().equals(fragmentName) ||
                PrefWeightFrag.class.getName().equals(fragmentName)||
                PrefArchive.class.getName().equals(fragmentName) ||
                PrefUserFrag.class.getName().equals(fragmentName) ||
                PrefHardBtns.class.getName().equals(fragmentName) ||
                PrefExport.class.getName().equals(fragmentName);
    }
}





