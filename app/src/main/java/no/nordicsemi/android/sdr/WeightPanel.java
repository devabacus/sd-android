package no.nordicsemi.android.sdr;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import no.nordicsemi.android.blinky.R;
import no.nordicsemi.android.sdr.archive.Archive;
import no.nordicsemi.android.sdr.archive.ArchiveViewModel;
import no.nordicsemi.android.sdr.archive.FileExport;
import no.nordicsemi.android.sdr.archive.FtpRoutines;
import no.nordicsemi.android.sdr.buttons.ButtonFrag;
import no.nordicsemi.android.sdr.buttons.ButtonsViewModel;
import no.nordicsemi.android.sdr.database_archive.ArchiveData;
import no.nordicsemi.android.sdr.preferences.PrefArchive;
import no.nordicsemi.android.sdr.preferences.PrefWeightFrag;
import no.nordicsemi.android.sdr.preferences.SettingsFragment;
import no.nordicsemi.android.sdr.viewmodels.BleViewModel;
import no.nordicsemi.android.sdr.viewmodels.HardButsViewModel;
import no.nordicsemi.android.sdr.viewmodels.ParsedDataViewModel;
import no.nordicsemi.android.sdr.viewmodels.StateViewModel;
import okhttp3.OkHttpClient;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeightPanel extends Fragment {

    ParsedDataViewModel parsedDataViewModel;
    ConstraintLayout weightLayout;
    TextView tvWeight;

    public static final String TAG = "test";

    Boolean show_weight = true;
    float weightValueFloat = 0;

    void weightObserve(){
        parsedDataViewModel = ViewModelProviders.of(getActivity()).get(ParsedDataViewModel.class);
        parsedDataViewModel.getWeightValue().observe(getActivity(), weight -> {
            tvWeight.setText(String.valueOf(weight));
            weightValueFloat = weight;
        });
    }

    void changeWeightFontSize(){
        //это чисто для weight panel, изменение шрифта дублирующего веса в зависимост от пользователя
        HardButsViewModel hardButsViewModel;
        hardButsViewModel = ViewModelProviders.of(getActivity()).get(HardButsViewModel.class);
        hardButsViewModel.getHardActive().observe(getActivity(), aBoolean -> {
            if (ButtonFrag.curUser.equals("user1") || ButtonFrag.curUser.equals("admin1")) {
                tvWeight.setTextSize(30);
            } else {
                tvWeight.setTextSize(60);
            }
        });
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_weight_panel, container, false);
        weightObserve();
        changeWeightFontSize();
        tvWeight = v.findViewById(R.id.tv_weight);
        weightLayout = v.findViewById(R.id.weight_panel_id);

        return v;
    }



    @Override
    public void onResume() {
        super.onResume();

        if (show_weight) {
            weightLayout.setVisibility(View.VISIBLE);
        } else {
            weightLayout.setVisibility(View.GONE);
        }

        if (ButtonFrag.curUser.equals("user1") || ButtonFrag.curUser.equals("admin1")) {
            tvWeight.setTextSize(30);
        } else {
            tvWeight.setTextSize(60);
        }

        super.onResume();
    }


}

