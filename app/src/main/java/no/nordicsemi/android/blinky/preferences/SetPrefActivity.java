package no.nordicsemi.android.blinky.preferences;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;

import no.nordicsemi.android.blinky.ButtonsViewModel;
import no.nordicsemi.android.blinky.Cmd;
import no.nordicsemi.android.blinky.R;
import no.nordicsemi.android.blinky.viewmodels.BlinkyViewModel;

public class SetPrefActivity extends PreferenceActivity {

    private static final String TAG = "SetPrefActivity";

    public void onBuildHeaders(List<PreferenceActivity.Header> target){
        loadHeadersFromResource(R.xml.pref_head,target);
    }


    @Override
    protected boolean isValidFragment(String fragmentName){
        return SettingsFragment.class.getName().equals(fragmentName) ||
                PrefWeightFrag.class.getName().equals(fragmentName)||
                PrefArchive.class.getName().equals(fragmentName);
    }

    //    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_set_pref);

    //final Toolbar toolbar1 = findViewById(R.id.toolbar1);
//    private void setupActionBar(){
//        getLayoutInflater().inflate(R.layout.toolbar, (ViewGroup)findViewById(R.id.sett_frame1));
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar3);
//        ActionBar actionBar =
//    }


    //setSupportActionBar(toolbar1);
    //getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

    //
//        Fragment fragment = new SettingsFragment();
//        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//        if (savedInstanceState == null) {
//            //created for the first time
//            fragmentTransaction.add(R.id.sett_frame, fragment, "settings_fragment");
//            fragmentTransaction.commit();
//        }
////        else {
////            //fragment = getFragmentManager().findFragmentByTag("settings_fragment");
////        }
//    }
//
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                onBackPressed();
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }


}





