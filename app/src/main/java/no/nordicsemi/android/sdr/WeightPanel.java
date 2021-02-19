package no.nordicsemi.android.sdr;


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

import no.nordicsemi.android.blinky.R;
import no.nordicsemi.android.sdr.buttons.ButtonFrag;
import no.nordicsemi.android.sdr.preferences.PrefWeightFrag;
import no.nordicsemi.android.sdr.viewmodels.HardButsViewModel;
import no.nordicsemi.android.sdr.viewmodels.ParsedDataViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeightPanel extends Fragment {

    ParsedDataViewModel parsedDataViewModel;
    ConstraintLayout weightLayout;
    TextView tvWeight;
    SharedPreferences sp;
    public static boolean weightInTonn;

    public static final String TAG = "test";

    Boolean show_weight = true;
    float weightValueFloat = 0;


    public static String fmt(float f){
        if(f == (long) f){
            return String.format("%d", (long) f);
        } else {
            return String.format("%s",f);
        }
    }

    void weightObserve(){
        parsedDataViewModel = ViewModelProviders.of(getActivity()).get(ParsedDataViewModel.class);
        parsedDataViewModel.getWeightValue().observe(getActivity(), weight -> {
            if(weight != null) {
                StringBuilder sb = new StringBuilder();
                sb.append(fmt(weight));
                sb.append(weightInTonn?"т":"кг");
                tvWeight.setText(sb);
//                tvWeight.setText(String.valueOf(weight));
                weightValueFloat = weight;
            }
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
        sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        weightInTonn = sp.getBoolean(PrefWeightFrag.KEY_WEIGHT_TONN, false);
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

