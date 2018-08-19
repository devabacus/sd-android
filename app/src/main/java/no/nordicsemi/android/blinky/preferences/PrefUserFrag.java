package no.nordicsemi.android.blinky.preferences;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import no.nordicsemi.android.blinky.R;

public class PrefUserFrag extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {


    public static final String KEY_PASS_INPUT = "input_with_pass";
    public static final String KEY_USER_PASS = "user_pass";
    public static final String KEY_USER1_PASS = "user1_pass";
    public static final String KEY_ADMIN_PASS = "admin_pass";
    public static final String KEY_ADMIN1_PASS = "admin1_pass";
    public static final String KEY_CUR_USER = "cur_user";

    String curUser;
    String curUser1;
    String curAdmin;
    String curAdmin1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_user);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        curUser = sharedPreferences.getString(KEY_CUR_USER, "user");
        curUser1 = sharedPreferences.getString(KEY_CUR_USER, "user1");
        curAdmin = sharedPreferences.getString(KEY_CUR_USER, "admin");
        curAdmin1 = sharedPreferences.getString(KEY_CUR_USER, "admin1");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {

        Preference inputWithPass = findPreference(KEY_PASS_INPUT);
        Preference inputUserPass = findPreference(KEY_USER_PASS);
        Preference inputUser1Pass = findPreference(KEY_USER1_PASS);
        Preference inputAdminPass = findPreference(KEY_ADMIN_PASS);
        Preference inputAdmin1Pass = findPreference(KEY_ADMIN1_PASS);
        if (curUser.equals("admin1") || curUser.equals("admin")) {
            inputWithPass.setEnabled(true);
            inputUserPass.setEnabled(true);
            inputUser1Pass.setEnabled(true);
            inputAdminPass.setEnabled(true);
            if (curUser.equals("admin1")) {
                inputAdmin1Pass.setEnabled(true);
            } else {
                inputAdmin1Pass.setEnabled(false);
            }
        } else {
            inputWithPass.setEnabled(false);
            inputUserPass.setEnabled(false);
            inputUser1Pass.setEnabled(false);
            inputAdminPass.setEnabled(false);
            inputAdmin1Pass.setEnabled(false);
        }


        super.onResume();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }
}
