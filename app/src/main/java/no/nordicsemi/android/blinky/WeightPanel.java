package no.nordicsemi.android.blinky;


import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import no.nordicsemi.android.blinky.R;
import no.nordicsemi.android.blinky.viewmodels.BlinkyViewModel;

import static no.nordicsemi.android.blinky.preferences.SetPrefActivity.SettingsFragment.KEY_ADC_SHOW;
import static no.nordicsemi.android.blinky.preferences.SetPrefActivity.SettingsFragment.KEY_SHOW_CONT_SETTINGS_FRAG;
import static no.nordicsemi.android.blinky.preferences.SetPrefActivity.SettingsFragment.KEY_SHOW_DEBUG_FRAG;
import static no.nordicsemi.android.blinky.preferences.SetPrefActivity.SettingsFragment.KEY_WEIGHT_SHOW;


/**
 * A simple {@link Fragment} subclass.
 */
public class WeightPanel extends Fragment {


    BlinkyViewModel blinkyViewModel;
    ConstraintLayout weightLayout;
    TextView tvWeight;
    float weightValueFloat = 0;
    int weightValueInt = 0;

    public WeightPanel() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_weight_panel, container, false);
        tvWeight = v.findViewById(R.id.tv_weight);
        weightLayout = v.findViewById(R.id.weight_panel_id);
        blinkyViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(BlinkyViewModel.class);

        blinkyViewModel.getUartData().observe(getActivity(), s->{
            assert s != null;
            if(s.matches("^wt.*")) {
                String weightValueStr = s.substring(s.indexOf('t') + 1);
                weightValueStr = weightValueStr.replaceAll("[^0-9.]", "");
                if (weightValueStr.contains(".")) {
                    weightValueFloat = Float.parseFloat(weightValueStr);
                    tvWeight.setText(String.valueOf(weightValueFloat));
                } else {
                    weightValueInt = Integer.parseInt(weightValueStr);
                    tvWeight.setText(String.valueOf(weightValueInt));
                }
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Boolean show_weight = sharedPreferences.getBoolean(KEY_WEIGHT_SHOW, false);
        if(show_weight) {
            weightLayout.setVisibility(View.VISIBLE);
        } else {
            weightLayout.setVisibility(View.GONE);
        }
        super.onResume();
    }
}

