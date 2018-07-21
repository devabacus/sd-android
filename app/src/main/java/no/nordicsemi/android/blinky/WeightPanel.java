package no.nordicsemi.android.blinky;


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
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;

import no.nordicsemi.android.blinky.archiveListOfItems.ArchiveViewModel;
import no.nordicsemi.android.blinky.database.CorButton;
import no.nordicsemi.android.blinky.database_archive.ArchiveData;
import no.nordicsemi.android.blinky.viewmodels.BlinkyViewModel;
import no.nordicsemi.android.blinky.viewmodels.StateViewModel;

import static no.nordicsemi.android.blinky.StateFragment.adcValue;
import static no.nordicsemi.android.blinky.preferences.PrefArchive.KEY_DISCRETE_MAX;
import static no.nordicsemi.android.blinky.preferences.PrefArchive.KEY_MIN_WEIGHT;
import static no.nordicsemi.android.blinky.preferences.PrefArchive.KEY_TIME_STAB;
import static no.nordicsemi.android.blinky.preferences.PrefWeightFrag.KEY_DISCRETE_VALUE;
import static no.nordicsemi.android.blinky.preferences.PrefWeightFrag.KEY_MAX_WEIGHT_VALUE;
import static no.nordicsemi.android.blinky.preferences.SettingsFragment.KEY_ARCHIVE_SAVE;
import static no.nordicsemi.android.blinky.preferences.SettingsFragment.KEY_WEIGHT_SHOW;


/**
 * A simple {@link Fragment} subclass.
 */
public class WeightPanel extends Fragment implements View.OnClickListener {


    BlinkyViewModel blinkyViewModel;
    ButtonsViewModel buttonsViewModel;
    StateViewModel stateViewModel;
    ConstraintLayout weightLayout;
    TextView tvWeight;
    Button btnArhive, btnTest;

    public static final String TAG = "test";

    float weightValueLast = 0;
    int weightValueInt = 0;
    private ArchiveViewModel archiveViewModel;
    private ArchiveData archiveData;
    float weight = 0;

    public static int numOfWeight = 1;

    float discrete = 0;
    float maxDeviation = 1;
    int timeStab = 3;
    int timeCounter = 0;
    boolean timeCounting = false;
    int weightTimeSec = 0;
    float minWeightForSave = 1;
    boolean incWeight = false;
    boolean decWeight = false;
    boolean enoughChange = false;

    float weightValueFloat = 0;
    float weightSaved = 0;
    float adcWeight = 0;
    //int adcValue = 0;
    int tare = 0;
    int arch = 0;
    int typeOfWeight = 0;


    float weightMax = 0;
    float adcWeightMax = 0;
    int tareMax = 0;
    int adcValueMax = 0;

    Date dateTime[];
    ArrayList<Date> dateTimeArrL;


    float weightValueFloat_arr[];
    ArrayList<Float> weightValueArrL;

    //ArrayList<Float> weightFloatList;
    int tare_arr[];
    ArrayList<Integer> tare_arrL;

    int adcValue_arr[];
    ArrayList<Integer> adcValue_arrL;

    float adcWeight_arr[];
    ArrayList<Float> adcWeight_arrL;

    int typeOfWeight_arr[];
    ArrayList<Integer> typeOfWeight_arrL;

    //ArrayList<Float> weightFloatArrList;


    //CorButton curCorButton;
    Boolean butSet = false;

    public WeightPanel() {
        // Required empty public constructor
    }

    public boolean minChange() {
        return Math.abs(weightValueFloat - weightSaved) > maxDeviation;
    }

    public void archive_arr_fill(int i, int type) {
//        dateTime[i] = new Date();
//        weightValueFloat_arr[i] = weight;
//        adcWeight_arr[i] = adcWeight;
//        adcValue_arr[i] = adcValue;
//        tare_arr[i] = tare;
//        typeOfWeight_arr[i] = type;
        dateTimeArrL.add(i, new Date());
        typeOfWeight_arrL.add(i, type);

        if(type == 0){
            weightValueArrL.add(i, weightValueFloat);
            adcWeight_arrL.add(i, adcWeight);
            adcValue_arrL.add(i, adcValue);
            tare_arrL.add(i, tare);
        } else if (type == 2) {
            
            weightValueArrL.add(arch, weightMax);
            adcWeight_arrL.add(arch, adcWeightMax);
            adcValue_arrL.add(arch, adcValueMax);
            tare_arrL.add(arch, tareMax);
        }

    }

    public void archive_arr_show(int i) {
//        Log.d("test", dateTime[i] + ", " +
//                weightValueFloat_arr[i] + ", " +
//                adcWeight_arr[i] + ", " +
//                adcValue_arr[i] + ", " +
//                tare_arr[i] + ", " +
//                typeOfWeight_arr[i] + "\n");

        //Log.d(TAG, "archive_arr_show: " + weightValueArrL.get(i) + ", ");

        Log.d(TAG, "Array list" + dateTimeArrL.get(i) + ", " +
                weightValueArrL.get(i) + ", " +
                adcWeight_arrL.get(i) + ", " +
                adcValue_arrL.get(i) + ", " +
                tare_arrL.get(i) + ", " +
                typeOfWeight_arrL.get(i) + "\n");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_weight_panel, container, false);
        tvWeight = v.findViewById(R.id.tv_weight);
        btnArhive = v.findViewById(R.id.btn_archive);
        btnTest = v.findViewById(R.id.btn_test);
        weightLayout = v.findViewById(R.id.weight_panel_id);
        blinkyViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(BlinkyViewModel.class);
        archiveViewModel = ViewModelProviders.of(getActivity()).get(ArchiveViewModel.class);
        buttonsViewModel = ViewModelProviders.of(getActivity()).get(ButtonsViewModel.class);
        stateViewModel = ViewModelProviders.of(getActivity()).get(StateViewModel.class);


        dateTime = new Date[20];
        weightValueFloat_arr = new float[20];
        //weightFloatArrList = new ArrayList<>();
        tare_arr = new int[20];
        adcValue_arr = new int[20];
        adcWeight_arr = new float[20];
        typeOfWeight_arr = new int[20];

        dateTimeArrL = new ArrayList<>();
        weightValueArrL = new ArrayList<>();
        adcWeight_arrL = new ArrayList<>();
        adcValue_arrL = new ArrayList<>();
        tare_arrL = new ArrayList<>();
        typeOfWeight_arrL = new ArrayList<>();

//        for(int i = 0; i < dateTime.length; i++) {
//            dateTime[i] = new Date(0);
//        }


        archiveViewModel.getArchiveListLast().observe(getActivity(), archiveDataList -> {
            assert archiveDataList != null;
            if (archiveDataList.size() > 0)
                numOfWeight = archiveDataList.get(0).getNumOfWeight();
        });
//        stateViewModel.getWeightValue().observe(getActivity(), aFloat -> {
//        });
        buttonsViewModel.ismSetButton().observe(getActivity(), aBoolean -> {
            butSet = aBoolean;
        });

        Handler handler = new Handler();
        int delay = 1000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (timeCounting) {
                    timeCounter++;
                    //Log.d(TAG, "run: timecounter = " + timeCounter + ",weightValueFloat = " + weightValueFloat + ", minChange() = " + minChange() );
                    //Log.d(TAG, "weightValueFloat - weightValueLast = " + (weightValueFloat - weightValueLast));
                    //Log.d(TAG, "weightSaved = " + weightSaved);
                    if ((timeCounter >= timeStab) && (weightValueFloat > minWeightForSave) && minChange()) {
                        // numOfWeight++;
                        // Toast.makeText(getContext(), String.valueOf(new Date()), Toast.LENGTH_SHORT).show();
                        //dateTime[arch] = new Date();

                        archive_arr_fill(arch, 0);
                        archive_arr_show(arch);
                        weightSaved = weightValueFloat;
                        arch++;
                        //Log.d(TAG, "run: стабильный вес. Таймер сбросили`");
                        timeCounting = false;
                    }
                }

                handler.postDelayed(this, delay);
            }
        }, delay);


        buttonsViewModel.getmCurCorButton().observe(getActivity(), corButton -> {
            if (!butSet) {
                //curCorButton = corButton;
                if (corButton != null && !corButton.getButNum().isEmpty()) {
                    tare = Integer.valueOf(corButton.getButNum());
                } else {
                    tare = 0;
                }
            }

        });

        btnArhive.setOnClickListener(this);
        btnTest.setOnClickListener(this);

        blinkyViewModel.getUartData().observe(getActivity(), s -> {
            assert s != null;
            if (s.matches("^wt.*")) {
                String weightValueStr = s.substring(s.indexOf('t') + 1);
                weightValueStr = weightValueStr.replaceAll("[^0-9.-]", "");
                weightValueFloat = Float.parseFloat(weightValueStr);
                if ((weightValueFloat != weightValueLast) && (weightValueFloat > minWeightForSave)) {
                    if (minChange()) {
                        timeCounter = 0;
                    }
                    // update weight max value (it's located in first member of array)
                    if (weightValueFloat > weightMax) {
                        weightMax = weightValueFloat;
                        tareMax = tare;
                        adcValueMax = adcValue;
                        adcWeightMax = adcWeight;


                    }

                    if (weightValueFloat > weightValueLast) {
                        //Log.d("test", "weightMax = " + weightMax);
                        incWeight = true;
                        decWeight = false;
                    } else if (weightValueFloat < weightValueLast) {
                        //Log.d(TAG, "weightValueFloat = " + weightValueFloat + ", weightValueLast = " + weightValueLast);
                        incWeight = false;
                        decWeight = true;
                    }
                    //Log.d(TAG, "onCreateView: вес отличается. Запустили таймер");
                    timeCounting = true;
                }
                //save to archive. The weight is zero
                else if (weightValueFloat < minWeightForSave && decWeight) {
                    numOfWeight++;
                    archive_arr_fill(arch, 2);
                    Log.d(TAG, "***MAX******MAX******MAX******MAX******MAX******MAX******MAX******MAX***");
                    for (int i = 0; i < arch+1; i++) {
                        archive_arr_show(i);
//                        archiveViewModel.addArchiveItem(new ArchiveData(dateTime[i],
//                                weightValueFloat_arr[i], numOfWeight, adcWeight_arr[i],
//                                adcValue_arr[i], tare_arr[i], typeOfWeight_arr[i]));
                    }
                    Log.d(TAG, "***MAX******MAX******MAX******MAX******MAX******MAX******MAX******MAX***");

                    arch = 0;
                    decWeight = false;
                    weightMax = 0;
                    weightMax = 0;
                    tareMax = 0;
                    adcValueMax = 0;
                    adcWeightMax = 0;
                }
                //stateViewModel.setmWeightValue(weightValueFloat);
                tvWeight.setText(String.valueOf(weightValueFloat));

                weightValueLast = weightValueFloat;
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
        discrete = Float.parseFloat(sharedPreferences.getString(KEY_DISCRETE_VALUE, "0"));
        maxDeviation = Float.parseFloat(sharedPreferences.getString(KEY_DISCRETE_MAX, "1"));
        timeStab = Integer.parseInt(sharedPreferences.getString(KEY_TIME_STAB, "3"));
        minWeightForSave = Float.parseFloat(sharedPreferences.getString(KEY_MIN_WEIGHT, "1"));
        if (show_weight) {
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
                for (int i = 0; i < arch; i++) {
                    archive_arr_show(i);

                }
                break;
        }

    }

}

