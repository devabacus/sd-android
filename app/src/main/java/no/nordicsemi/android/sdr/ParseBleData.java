package no.nordicsemi.android.sdr;

import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import no.nordicsemi.android.blinky.R;
import no.nordicsemi.android.sdr.viewmodels.BleViewModel;
import no.nordicsemi.android.sdr.viewmodels.ParsedDataViewModel;
import no.nordicsemi.android.sdr.viewmodels.StateViewModel;

public class ParseBleData extends Fragment {

        public static final String TAG = "parse_ble_data";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView: ivan durak privet");
        BleViewModel bleViewModel = ViewModelProviders.of(getActivity()).get(BleViewModel.class);
        ParsedDataViewModel parsedDataViewModel = ViewModelProviders.of(getActivity()).get(ParsedDataViewModel.class);
        StateViewModel stateViewModel = ViewModelProviders.of(getActivity()).get(StateViewModel.class);

        bleViewModel.getUartData().observe(getActivity(), s -> {

            assert s != null;
//            Log.d(TAG, "Parse ble data: " + s.toString());
            if (s.matches("^wt.*")) {
                String weightValueStr = s.substring(s.indexOf('t') + 1);
                weightValueStr = weightValueStr.replaceAll("[^0-9.-]", "");
                float weightValueFloat = Float.parseFloat(weightValueStr);
//                stateViewModel.setmWeightValue(weightValueFloat);
                parsedDataViewModel.setmWeightValue(weightValueFloat);
            }

        });
//        return v;
        return inflater.inflate(R.layout.fragment_parse_ble_data, container, false);

    }
}