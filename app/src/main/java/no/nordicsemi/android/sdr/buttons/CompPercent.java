package no.nordicsemi.android.sdr.buttons;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;

import no.nordicsemi.android.blinky.R;
import no.nordicsemi.android.sdr.database.CorButton;
import no.nordicsemi.android.sdr.viewmodels.BlinkyViewModel;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class CompPercent extends Fragment implements View.OnClickListener {

    private static final String TAG = "CompPercent";

    ButtonsViewModel buttonsViewModel;
    BlinkyViewModel blinkyViewModel;

    EditText etCompValue;
    Button btnDecComp, btnIncComp;
    SeekBar seekBar;
    RadioButton rbMinus;
    int compCorValue = 0;

    CorButton curCorButton;

    public CompPercent() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        buttonsViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(ButtonsViewModel.class);
        blinkyViewModel = ViewModelProviders.of(getActivity()).get(BlinkyViewModel.class);


        View v = inflater.inflate(R.layout.fragment_comp_percent, container, false);
        etCompValue = v.findViewById(R.id.etCompValue);
        btnIncComp = v.findViewById(R.id.btn_inc_comp);
        btnDecComp = v.findViewById(R.id.btn_dec_comp);
        seekBar = v.findViewById(R.id.seekBarComp);
        rbMinus = v.findViewById(R.id.rbMinusComp);

        btnIncComp.setOnClickListener(this);
        btnDecComp.setOnClickListener(this);

//        etCompValue.setOnFocusChangeListener((v1, hasFocus) -> {
//
//            compCorValue = Integer.valueOf(etCompValue.getText().toString());
//            buttonsViewModel.setmCompCorValue(compCorValue);
//            //blinkyViewModel.sendTX("$" + dirCor + corValue + "&");
//
//        });

        buttonsViewModel.getmCurCorButton().observe(getActivity(), corButton -> {
            assert corButton != null;
            this.curCorButton = corButton;
            compCorValue = corButton.getCompValue();
        });

        buttonsViewModel.ismSetButton().observe(getActivity(), aBoolean -> {

            if (aBoolean != null) {
                if (aBoolean) {
                    if (compCorValue < 0) {
                        compCorValue = -compCorValue;
                        rbMinus.setChecked(true);
                    }
                    seekBar.setProgress(compCorValue);
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress > 0)
                compCorValue = progress;
                etCompValue.setText(String.valueOf(compCorValue));
                //int compCorValueSent = compCorValue;
                if (rbMinus.isChecked()){
                   compCorValue = - compCorValue;
                }
                Log.d(TAG, "onProgressChanged: compCorValue = " + String.valueOf(compCorValue));
                buttonsViewModel.setmCompCorValue(compCorValue);
                //blinkyViewModel.sendTX("$c" + compCorValue + "&");
                //if (curCorButton.getCorDir().equals("p")){

                    if (curCorButton != null && isVisible())

                    {

                        String s = String.valueOf(curCorButton.getCorValue());
                        Log.d(TAG, "onProgressChanged: curCorButton.getCorValue = " + s);
                        String msg = "$" + "p" + curCorButton.getCorValue() + "c" + compCorValue + "&";
                        if(compCorValue != 0) blinkyViewModel.sendTX(msg);
                    } else {
                        blinkyViewModel.sendTX("$r&");
                    }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        return v;
    }

    @Override
    public void onClick(View v) {
        if(compCorValue < 0) compCorValue = -compCorValue;
        switch (v.getId()) {
            case R.id.btn_inc_comp:
                compCorValue++;
                seekBar.setProgress(compCorValue);
                Log.d(TAG, "onClick: compCorValue = " + String.valueOf(compCorValue));

                break;

            case R.id.btn_dec_comp:
                compCorValue--;
                seekBar.setProgress(compCorValue);
                Log.d(TAG, "onClick: compCorValue = " + String.valueOf(compCorValue));
                break;
        }
    }
}
