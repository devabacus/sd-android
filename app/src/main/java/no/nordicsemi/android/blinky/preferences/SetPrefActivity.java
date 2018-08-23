package no.nordicsemi.android.blinky.preferences;

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
                PrefHardBtns.class.getName().equals(fragmentName);
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





