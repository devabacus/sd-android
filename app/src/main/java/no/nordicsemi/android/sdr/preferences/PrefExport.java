package no.nordicsemi.android.sdr.preferences;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import java.util.Calendar;

import no.nordicsemi.android.blinky.R;
import no.nordicsemi.android.sdr.archive.AlarmTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class PrefExport extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_FTP_SERVER = "ftp_server";
    public static final String KEY_FTP_LOGIN = "ftp_login";
    public static final String KEY_FTP_PASSWORD = "ftp_pass";
    public static final String KEY_FTP_PATH = "ftp_path";
    public static final String KEY_EXPORT_DETAIL = "export_detail";
    public static final String KEY_EXPORT_AUTO = "export_auto";
    public static final String KEY_EXPORT_TIME = "export_time";

    Preference ftp_server;
    Preference ftp_login;
    Preference ftp_pass;
    Preference ftp_path;
    //    Preference export_detail;
    Preference export_auto;
    Preference export_time;
    SharedPreferences sp;
    String timeExport;

    public PrefExport() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_export);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.registerOnSharedPreferenceChangeListener(this);
        ftp_server = findPreference(KEY_FTP_SERVER);
        ftp_login = findPreference(KEY_FTP_LOGIN);
        ftp_pass = findPreference(KEY_FTP_PASSWORD);
        ftp_path = findPreference(KEY_FTP_PATH);
        export_auto = findPreference(KEY_EXPORT_AUTO);
        export_time = findPreference(KEY_EXPORT_TIME);


        String server = sp.getString(KEY_FTP_SERVER, "");
        String login = sp.getString(KEY_FTP_LOGIN, "");
        String pass = sp.getString(KEY_FTP_PASSWORD, "");
        String path = sp.getString(KEY_FTP_PATH, "");
        timeExport = sp.getString(KEY_EXPORT_TIME, "");
        boolean auto = sp.getBoolean(KEY_EXPORT_AUTO, false);

        export_time.setEnabled(auto);
        ftp_server.setSummary(server);
        ftp_login.setSummary(login);
        ftp_path.setSummary(path);
        export_time.setSummary(timeExport);
    }


    long getTimeMillis() {
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String[] time = timeExport.split(":");
        int hour = Integer.parseInt(time[0]);
        int minute = Integer.parseInt(time[1]);
        Calendar calendar = Calendar.getInstance();

        calendar.set(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                hour,
                minute
        );
        return calendar.getTimeInMillis();
    }

    void alarmSet() {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(PrefExport.this.getActivity(), AlarmTask.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
        alarmManager.setRepeating(AlarmManager.RTC, getTimeMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        Toast.makeText(getActivity(), "Alarm is set at " + timeExport, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Preference curPref = findPreference(key);

        if (key.equals(KEY_FTP_SERVER)) {
            ftp_server.setSummary(sharedPreferences.getString(key, ""));
            ;
        } else if (key.equals(KEY_FTP_LOGIN)) {
            ftp_login.setSummary(sharedPreferences.getString(key, ""));
            ;
        } else if (key.equals(KEY_FTP_PATH)) {
            ftp_path.setSummary(sharedPreferences.getString(key, ""));
            ;
        } else if (key.equals(KEY_EXPORT_TIME)) {
            timeExport = sharedPreferences.getString(key, "");
            export_time.setSummary(timeExport);
            alarmSet();
        } else if (key.equals(KEY_EXPORT_AUTO)) {
            export_time.setEnabled(sharedPreferences.getBoolean(key, false));
            alarmSet();
        }
    }
}