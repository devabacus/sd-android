package no.nordicsemi.android.blinky.contr_settings;


import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
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

import static no.nordicsemi.android.blinky.preferences.SettingsFragment.KEY_ADC_SHOW;
import static no.nordicsemi.android.blinky.preferences.SettingsFragment.KEY_SHOW_CONT_SETTINGS_FRAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class ScaleSetFragment extends Fragment implements View.OnClickListener {

    Button btnCalZero, btnCalOn, btnCalWeight;
    TextView tvCalInfo;
    BlinkyViewModel blinkyViewModel;
    ConstraintLayout contSettLayout;
    StateViewModel stateViewModel;
    int adc_value = 0;
    Boolean showContSet;

    public ScaleSetFragment() {
        // Required empty public constructor
    }


    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_scale_set, container, false);
        btnCalZero = v.findViewById(R.id.btn_cal_zero);
        btnCalOn = v.findViewById(R.id.btn_cal_on);
        btnCalWeight = v.findViewById(R.id.btn_cal_weight);
        tvCalInfo = v.findViewById(R.id.tv_cal_info);
        contSettLayout = v.findViewById(R.id.cont_sett_layout);


        blinkyViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(BlinkyViewModel.class);
        StateViewModel stateViewModel = ViewModelProviders.of(getActivity()).get(StateViewModel.class);

        blinkyViewModel.getUartData().observe(getActivity(), s -> {
            assert s != null;
            if(s.matches("s5..")){
                tvCalInfo.setText("Калибровка..");
            }
            else if (s.matches("s5/.*")){
                String adc_value_type = "";
                switch (s.charAt(3)){
                    case '1':
                        adc_value_type = "Значение АЦП нуля: ";
                        break;
                    case '2':
                        adc_value_type = "Значение АЦП дискр: ";
                        break;
                    case '3':
                        adc_value_type = "Значение АЦП груза: ";
                        break;
                }
                String cal_adc_value_str = s.substring(5);
                cal_adc_value_str = cal_adc_value_str.replaceAll("[^0-9]", "");
                //int adc_cal_value = Integer.parseInt(cal_adc_value_str);
                 //cal_adc_value = Integer.parseInt(s.substring(5));
                tvCalInfo.setText(adc_value_type + cal_adc_value_str);
            }

        });

        stateViewModel.getADCvalue().observe(getActivity(), adc->{
            assert adc != null;
            adc_value = adc;
        });


        btnCalZero.setOnClickListener(this);
        btnCalOn.setOnClickListener(this);
        btnCalWeight.setOnClickListener(this);

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
                blinkyViewModel.sendTX(Cmd.CAL_UNLOAD);
                break;
            case R.id.btn_cal_on:
                blinkyViewModel.sendTX(Cmd.CAL_LOAD);
                break;
            case R.id.btn_cal_weight:
                blinkyViewModel.sendTX(Cmd.CAL_WEIGHT);
        }
    }
}
