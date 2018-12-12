package no.nordicsemi.android.sdr.contr_settings;


import android.annotation.SuppressLint;
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
import android.widget.EditText;
import android.widget.TextView;

import java.util.Objects;

import no.nordicsemi.android.sdr.Cmd;
import no.nordicsemi.android.blinky.R;
import no.nordicsemi.android.sdr.StateFragment;
import no.nordicsemi.android.sdr.viewmodels.BlinkyViewModel;
import no.nordicsemi.android.sdr.viewmodels.StateViewModel;

import static no.nordicsemi.android.sdr.preferences.PrefWeightFrag.KEY_DISCRETE_VALUE;
import static no.nordicsemi.android.sdr.preferences.PrefWeightFrag.KEY_MAX_WEIGHT_VALUE;
import static no.nordicsemi.android.sdr.preferences.SettingsFragment.KEY_SHOW_CONT_SETTINGS_FRAG;


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
    int maxWeight = 0;
    float discrete = 0;
    EditText etMaxWeight, etDiscrete, etCalWeight;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

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

        etMaxWeight = v.findViewById(R.id.et_max_weight);
        etDiscrete = v.findViewById(R.id.et_discr);
        etCalWeight = v.findViewById(R.id.et_cal_weight);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        editor = sharedPreferences.edit();

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
        showContSet = sharedPreferences.getBoolean(KEY_SHOW_CONT_SETTINGS_FRAG, false);
        maxWeight = Integer.parseInt(sharedPreferences.getString(KEY_MAX_WEIGHT_VALUE, "0"));
        discrete = Float.parseFloat(sharedPreferences.getString(KEY_DISCRETE_VALUE, "0"));
        etMaxWeight.setText(String.valueOf(maxWeight));
        etDiscrete.setText(String.valueOf(discrete));

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
                //turn ADC off
                blinkyViewModel.sendTX("s1/0");
                StateFragment.txQueue.add("s14/" + etMaxWeight.getText().toString());
                StateFragment.txQueue.add("s15/" + etDiscrete.getText().toString());
                StateFragment.txQueue.add("s16/" + etCalWeight.getText().toString());
                StateFragment.txQueue.add(Cmd.CAL_UNLOAD);


                break;
            case R.id.btn_cal_weight:
                blinkyViewModel.sendTX(Cmd.CAL_WEIGHT);
                editor.putString(KEY_MAX_WEIGHT_VALUE, etMaxWeight.getText().toString());
                editor.putString(KEY_DISCRETE_VALUE, etDiscrete.getText().toString());
                editor.apply();
                break;

            case R.id.btn_cal_on:
            blinkyViewModel.sendTX(Cmd.CAL_LOAD);

            break;
        }
    }
}
