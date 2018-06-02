package no.nordicsemi.android.blinky.contr_settings;


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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Objects;

import no.nordicsemi.android.blinky.Cmd;
import no.nordicsemi.android.blinky.R;
import no.nordicsemi.android.blinky.viewmodels.BlinkyViewModel;
import no.nordicsemi.android.blinky.viewmodels.StateViewModel;

import static no.nordicsemi.android.blinky.preferences.SetPrefActivity.SettingsFragment.KEY_ADC_SHOW;
import static no.nordicsemi.android.blinky.preferences.SetPrefActivity.SettingsFragment.KEY_SHOW_CONT_SETTINGS_FRAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class ScaleSetFragment extends Fragment implements View.OnClickListener {

    Button btnCalZero, btnCalOn;
    TextView tvCalZero, tvCalOn;
    BlinkyViewModel blinkyViewModel;
    ConstraintLayout contSettLayout;
    StateViewModel stateViewModel;
    int adc_value = 0;
    Boolean showContSet;

    public ScaleSetFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_scale_set, container, false);
        btnCalZero = v.findViewById(R.id.btn_cal_zero);
        btnCalOn = v.findViewById(R.id.btn_cal_on);
        tvCalZero = v.findViewById(R.id.tv_cal_zero);
        tvCalOn = v.findViewById(R.id.tv_cal_on);
        contSettLayout = v.findViewById(R.id.cont_sett_layout);


        blinkyViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(BlinkyViewModel.class);
        StateViewModel stateViewModel = ViewModelProviders.of(getActivity()).get(StateViewModel.class);

        blinkyViewModel.sendTX("test from cal fragment");

        stateViewModel.getADCvalue().observe(getActivity(), adc->{
            assert adc != null;
            adc_value = adc;
        });


        btnCalZero.setOnClickListener(this);
        btnCalOn.setOnClickListener(this);

        return v;
    }

    @Override
    public void onResume() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        showContSet = sharedPreferences.getBoolean(KEY_SHOW_CONT_SETTINGS_FRAG, false);

        if (showContSet) {
            contSettLayout.setVisibility(View.VISIBLE);
        }
        else  contSettLayout.setVisibility(View.GONE);

        super.onResume();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cal_zero:
                blinkyViewModel.sendTX("zero");
                tvCalZero.setText(String.valueOf(adc_value));
                break;
            case R.id.btn_cal_on:
                blinkyViewModel.sendTX("cal_on");
                tvCalOn.setText(String.valueOf(adc_value));
                break;

        }
    }
}
