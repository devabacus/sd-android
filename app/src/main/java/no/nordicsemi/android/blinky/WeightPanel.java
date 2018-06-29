package no.nordicsemi.android.blinky;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
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

import java.util.Date;
import java.util.Objects;

import no.nordicsemi.android.blinky.archiveListOfItems.ArchiveViewModel;
import no.nordicsemi.android.blinky.database_archive.ArchiveData;
import no.nordicsemi.android.blinky.viewmodels.BlinkyViewModel;

import static no.nordicsemi.android.blinky.preferences.SetPrefActivity.SettingsFragment.KEY_ARCHIVE_SAVE;
import static no.nordicsemi.android.blinky.preferences.SetPrefActivity.SettingsFragment.KEY_WEIGHT_SHOW;


/**
 * A simple {@link Fragment} subclass.
 */
public class WeightPanel extends Fragment implements View.OnClickListener {


    BlinkyViewModel blinkyViewModel;
    ConstraintLayout weightLayout;
    TextView tvWeight;
    Button btnArhive, btnTest;
    float weightValueFloat = 0;
    int weightValueInt = 0;
    private ArchiveViewModel archiveViewModel;
    private ArchiveData archiveData;
    int weight = 0;
    int tare = 0;


    public WeightPanel() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_weight_panel, container, false);
        tvWeight = v.findViewById(R.id.tv_weight);
        btnArhive = v.findViewById(R.id.btn_archive);
        btnTest = v.findViewById(R.id.btn_test);
        weightLayout = v.findViewById(R.id.weight_panel_id);
        blinkyViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(BlinkyViewModel.class);
        archiveViewModel = ViewModelProviders.of(getActivity()).get(ArchiveViewModel.class);



        btnArhive.setOnClickListener(this);
        btnTest.setOnClickListener(this);

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
        Boolean arhive = sharedPreferences.getBoolean(KEY_ARCHIVE_SAVE, false);
        if(show_weight) {
            weightLayout.setVisibility(View.VISIBLE);
        } else {
            weightLayout.setVisibility(View.GONE);
        }

        if (arhive) {
            btnArhive.setVisibility(View.VISIBLE);
        } else {
            btnArhive.setVisibility(View.GONE);
        }



        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_archive:
                Intent intent = new Intent(getContext(), Archive.class);
                startActivity(intent);
                break;
            case R.id.btn_test:
                weight += 100;
                tare += 55;
                archiveViewModel.addArchiveItem(new ArchiveData(new Date(),weight, tare));
                break;
        }

    }
}

