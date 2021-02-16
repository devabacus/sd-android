package no.nordicsemi.android.sdr.preferences;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import no.nordicsemi.android.blinky.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class PrefExport extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_FTP_SERVER = "ftp_server";
    public static final String KEY_FTP_LOGIN = "ftp_login";
    public static final String KEY_FTP_PASS = "ftp_pass";
    public static final String KEY_EXPORT_DETAIL = "export_detail";
    public static final String KEY_EXPORT_AUTO = "export_auto";
    public static final String KEY_EXPORT_TIME = "export_time";

    Preference ftp_server;
    Preference ftp_login;
    Preference ftp_pass;
//    Preference export_detail;
    Preference export_auto;
    Preference export_time;

    public PrefExport() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.export_pref);
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
        ftp_server = findPreference(KEY_FTP_SERVER);
        ftp_login = findPreference(KEY_FTP_LOGIN);
        ftp_pass = findPreference(KEY_FTP_PASS);
        export_auto = findPreference(KEY_EXPORT_AUTO);
        export_time = findPreference(KEY_EXPORT_TIME);


        String server = sharedPreferences.getString(KEY_FTP_SERVER,"");
        String login = sharedPreferences.getString(KEY_FTP_LOGIN,"");
        String pass = sharedPreferences.getString(KEY_FTP_PASS,"");
        String time = sharedPreferences.getString(KEY_EXPORT_TIME,"");
        boolean auto = sharedPreferences.getBoolean(KEY_EXPORT_AUTO,false);

        export_time.setEnabled(auto);
        ftp_server.setSummary(server);
        ftp_login.setSummary(login);
        export_time.setSummary(time);
    }





    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Preference curPref = findPreference(key);

        if (key.equals(KEY_FTP_SERVER)) {
            ftp_server.setSummary(sharedPreferences.getString(key, ""));;
        } else if (key.equals(KEY_FTP_LOGIN)) {
            ftp_login.setSummary(sharedPreferences.getString(key, ""));;
        } else if (key.equals(KEY_EXPORT_TIME)){
            export_time.setSummary(sharedPreferences.getString(key, ""));;
        } else if (key.equals(KEY_EXPORT_AUTO)){
            export_time.setEnabled(sharedPreferences.getBoolean(key, false));
        }
    }
}