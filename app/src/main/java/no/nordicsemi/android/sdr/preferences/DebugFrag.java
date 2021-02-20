package no.nordicsemi.android.sdr.preferences;


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
import android.widget.EditText;
import android.widget.TextView;

import java.util.Objects;

import no.nordicsemi.android.blinky.R;
import no.nordicsemi.android.sdr.viewmodels.BleViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class DebugFrag extends Fragment {


    ConstraintLayout constrDebug;



    public DebugFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_debug, container, false);

        final TextView tvRxMsg = (TextView) v.findViewById(R.id.tv_rx_msg);
        final TextView tvTxMsg = (TextView)v.findViewById(R.id.tv_tx_msg);

        final EditText etSend = (EditText)v.findViewById(R.id.send_text);
        final Button btnSend = (Button)v.findViewById(R.id.btn_send);
        constrDebug = (ConstraintLayout)v.findViewById(R.id.constr_debug);

        final BleViewModel viewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(BleViewModel.class);

        viewModel.sendUartData().observe(this, tvTxMsg::setText);

        btnSend.setOnClickListener(view -> {
                    tvTxMsg.setText("");
                    viewModel.sendTX(etSend.getText().toString());

                    tvRxMsg.setText("");
        });

//        viewModel.getUartData().observe(this,
//
//                tvRxMsg::setText);

        viewModel.getUartData().observe(this, rxMsg ->{
            String rxMsgNew = rxMsg;
            if(rxMsg.contains("\n")){
                Log.d("test", "onCreateView: contains");
                rxMsgNew = rxMsg.substring(0, rxMsg.length() - 2);
            }
            tvRxMsg.setText(rxMsgNew);
        });

        return v;
    }

    @Override
    public void onResume() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Boolean show_debug = sharedPreferences.getBoolean(SettingsFragment.KEY_SHOW_DEBUG_FRAG, false);
        if (show_debug) constrDebug.setVisibility(View.VISIBLE);
        else constrDebug.setVisibility(View.GONE);
        super.onResume();
    }
}
