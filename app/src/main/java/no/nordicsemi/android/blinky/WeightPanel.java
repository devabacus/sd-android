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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import no.nordicsemi.android.blinky.archiveListOfItems.Archive;
import no.nordicsemi.android.blinky.archiveListOfItems.ArchiveViewModel;
import no.nordicsemi.android.blinky.buttons.ButtonFrag;
import no.nordicsemi.android.blinky.buttons.ButtonsViewModel;
import no.nordicsemi.android.blinky.database_archive.ArchiveData;
import no.nordicsemi.android.blinky.viewmodels.BlinkyViewModel;
import no.nordicsemi.android.blinky.viewmodels.HardButsViewModel;
import no.nordicsemi.android.blinky.viewmodels.StateViewModel;

import static no.nordicsemi.android.blinky.StateFragment.adcValue;
import static no.nordicsemi.android.blinky.preferences.PrefArchive.KEY_ARCHIVE_DRIVER_WEIGHT_MAX;
import static no.nordicsemi.android.blinky.preferences.PrefArchive.KEY_ARCHIVE_SAVE_ADC;
import static no.nordicsemi.android.blinky.preferences.PrefArchive.KEY_DEBUG;
import static no.nordicsemi.android.blinky.preferences.PrefArchive.KEY_DISCRETE_MAX;
import static no.nordicsemi.android.blinky.preferences.PrefArchive.KEY_MIN_WEIGHT;
import static no.nordicsemi.android.blinky.preferences.PrefArchive.KEY_TIME_STAB;
import static no.nordicsemi.android.blinky.preferences.PrefWeightFrag.KEY_DISCRETE_VALUE;
import static no.nordicsemi.android.blinky.preferences.PrefArchive.KEY_ARCHIVE_SAVE;
import static no.nordicsemi.android.blinky.preferences.SettingsFragment.KEY_WEIGHT_SHOW;


/**
 * A simple {@link Fragment} subclass.
 */
public class WeightPanel extends Fragment implements View.OnClickListener, View.OnLongClickListener {


    BlinkyViewModel blinkyViewModel;
    ButtonsViewModel buttonsViewModel;
    HardButsViewModel hardButsViewModel;
    StateViewModel stateViewModel;
    ConstraintLayout weightLayout, debugArchiveLayout;
    TextView tvWeight;
    Button btnArhive, btnTest;
    TextView tvDebugDate, tvDebugWeight, tvDebugAdc, tvDebugTare, tvDebugType;

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
    Boolean archive;
    public static Boolean debug_archive;
    Boolean archiveADC;
    int archiveDriverMax;
    boolean enoughChange = false;

    float weightValueFloat = 0;
    float weightSaved = 0;
    float weightSavedMax = 0;

    int archMax = 0;

    float adcWeight = 0;
    //int adcValue = 0;
    int tare = 0;
    int arch = 0;
    int typeOfWeight = 0;


    float weightMax = 0;
    float adcWeightMax = 0;
    int tareMax = 0;
    int adcValueMax = 0;
    Date dateTimeMax;

    Date dateTime[];
    ArrayList<Date> dateTimeArrL;


    ArrayList<Float> weightValueArrL;
    ArrayList<Integer> tare_arrL;
    ArrayList<Integer> adcValue_arrL;
    ArrayList<Float> adcWeight_arrL;
    ArrayList<Integer> typeOfWeight_arrL;
    Boolean butSet = false;

    public WeightPanel() {
        // Required empty public constructor
    }

    public boolean minChange() {
        return Math.abs(weightValueFloat - weightSaved) > maxDeviation;
    }

    public float driveWeightFind () {
        return (weightValueArrL.get(archMax) - weightValueArrL.get(archMax + 1));
    }

    public void archive_arr_fill(int i, int type) {

        typeOfWeight_arrL.add(i, type);

        if(type == 0){
            dateTimeArrL.add(i, new Date());
            weightValueArrL.add(i, weightValueFloat);
            adcWeight_arrL.add(i, adcWeight);
            adcValue_arrL.add(i, adcValue);
            tare_arrL.add(i, tare);
        } else if (type == 2) {
            dateTimeArrL.add(arch, dateTimeMax);
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
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", new Locale("ru"));
        format.format(new Date());
        tvDebugDate.setText(tvDebugDate.getText() + "\n" + String.valueOf(format.format(dateTimeArrL.get(i))));
        tvDebugWeight.setText(tvDebugWeight.getText() + "\n" + String.valueOf(weightValueArrL.get(i)));
        if (adcValue_arrL.get(i) > 0) {
            tvDebugAdc.setText(tvDebugAdc.getText() + "\n" + String.valueOf(adcValue_arrL.get(i)));
        }
        tvDebugTare.setText(tvDebugTare.getText() + "\n" + String.valueOf(tare_arrL.get(i)));
        tvDebugType.setText(tvDebugType.getText() + "\n" + String.valueOf(typeOfWeight_arrL.get(i)));
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
        debugArchiveLayout = v.findViewById(R.id.debug_archive_layout);
        tvDebugDate = v.findViewById(R.id.tv_debug_date);
        tvDebugWeight = v.findViewById(R.id.tv_debug_weight);
        tvDebugAdc = v.findViewById(R.id.tv_debug_adc);
        tvDebugTare = v.findViewById(R.id.tv_debug_tare);
        tvDebugType = v.findViewById(R.id.tv_debug_type);


        blinkyViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(BlinkyViewModel.class);
        archiveViewModel = ViewModelProviders.of(getActivity()).get(ArchiveViewModel.class);
        buttonsViewModel = ViewModelProviders.of(getActivity()).get(ButtonsViewModel.class);
        stateViewModel = ViewModelProviders.of(getActivity()).get(StateViewModel.class);
        hardButsViewModel = ViewModelProviders.of(getActivity()).get(HardButsViewModel.class);

        dateTime = new Date[20];
        dateTimeMax = new Date();
        dateTimeArrL = new ArrayList<>();
        weightValueArrL = new ArrayList<>();
        adcWeight_arrL = new ArrayList<>();
        adcValue_arrL = new ArrayList<>();
        tare_arrL = new ArrayList<>();
        typeOfWeight_arrL = new ArrayList<>();

        archiveViewModel.getArchiveListLast().observe(getActivity(), archiveDataList -> {
            assert archiveDataList != null;
            if (archiveDataList.size() > 0)
                numOfWeight = archiveDataList.get(0).getNumOfWeight();
        });
        buttonsViewModel.ismSetButton().observe(getActivity(), aBoolean -> {
            butSet = aBoolean;
        });

        hardButsViewModel.getHardActive().observe(getActivity(), aBoolean ->{
            if (ButtonFrag.curUser.equals("user1") || ButtonFrag.curUser.equals("admin1")) {
                tvWeight.setTextSize(30);
            } else {
                tvWeight.setTextSize(60);
            }
        });



        Handler handler = new Handler();
        int delay = 1000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (timeCounting) {
                    timeCounter++;
                    if ((timeCounter >= timeStab) && (weightValueFloat > minWeightForSave) && minChange()) {
                        if (arch == 0) {
                            cleanDebug();
                        }
                        archive_arr_fill(arch, 0);
                        archive_arr_show(arch);
                        weightSaved = weightValueFloat;
                        if (weightSaved > weightSavedMax) {
                            weightSavedMax = weightSaved;
                            archMax = arch;
                        }

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
        btnTest.setOnLongClickListener(this);

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
                        dateTimeMax = new Date();
                        weightMax = weightValueFloat;
                        tareMax = tare;
                        adcValueMax = adcValue;
                        adcWeightMax = adcWeight;
                        //Log.d(TAG, "onCreateView: weightMax = " + weightMax);
                    }

                    if (weightValueFloat > weightValueLast) {
                        //Log.d("test", "weightMax = " + weightMax);
                        incWeight = true;
                        decWeight = false;
                    } else if (weightValueFloat < weightValueLast) {
                        //Log.d(TAG, "weightValueFloat = " + weightValueFloat + ", weightValueLast = " + weightValueLast);
                        incWeight = false;
                        decWeight = true;
                        archive_arr_fill(arch, 2);
                    }
                    //Log.d(TAG, "onCreateView: вес отличается. Запустили таймер");
                    timeCounting = true;

                }
                //save to archive. The weight is zero

                else if (weightValueFloat < minWeightForSave && decWeight && (weightValueArrL.size() > 0)) {
                    numOfWeight++;
                    cleanDebug();

                    //Toast.makeText(getContext(), "max stab weight = " + String.valueOf(weightValueArrL.get(archMax)), Toast.LENGTH_SHORT).show();
                    //change type of weight for mark max stab item


                    // проверяем ли предыдущее сохраненное сохр взвешивание для сохр веса без людей
                    if ((arch > 1) &&(driveWeightFind() <= archiveDriverMax && driveWeightFind() > 0)) {
                        typeOfWeight_arrL.set(archMax+1, 1);
                    } else if (weightSavedMax != 0) {
                        Log.d(TAG, "weightSavedMax != 0");
                        typeOfWeight_arrL.set(archMax, 1);
                    }


                    Log.d(TAG, "weightMax = " + weightMax);
                    Log.d(TAG, "weightSavedMax = " + weightSavedMax);
                    //}
                    //чтобы запись с максимальным весом и стабильным не дублировались в архиве если разница между ними в пределах погрешности
                    if (Math.abs(weightMax - weightSavedMax) > maxDeviation) {
                        Log.d(TAG, "onCreateView: weightMax != weightSavedMax");
                        archive_arr_fill(arch, 2);
                    }
                    else {
                        arch--;
                    }
                    Log.d(TAG, "***MAX******MAX******MAX******MAX******MAX******MAX******MAX******MAX***");
                    for (int i = 0; i < arch+1; i++) {

                        archive_arr_show(i);
//                        archiveViewModel.addArchiveItem(new ArchiveData(dateTime[i],
//                                weightValueFloat_arr[i], numOfWeight, adcWeight_arr[i],
//                                adcValue_arr[i], tare_arr[i], typeOfWeight_arr[i]));
//
                        if (archive) {
                            archiveViewModel.addArchiveItem(new ArchiveData(dateTimeArrL.get(i),
                                    weightValueArrL.get(i), numOfWeight, adcWeight_arrL.get(i),
                                    adcValue_arrL.get(i), tare_arrL.get(i), typeOfWeight_arrL.get(i)));
                            //Toast.makeText(getContext(), "saved", Toast.LENGTH_SHORT).show();
                        }


                    }
                    Log.d(TAG, "***MAX******MAX******MAX******MAX******MAX******MAX******MAX******MAX***");

                    arch = 0;
                    decWeight = false;
                    weightMax = 0;
                    weightMax = 0;
                    tareMax = 0;
                    adcValueMax = 0;
                    adcWeightMax = 0;
                    archMax = 0;
                    weightSavedMax = 0;
                    weightSaved = 0;
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
        archive = sharedPreferences.getBoolean(KEY_ARCHIVE_SAVE, false);
        debug_archive = sharedPreferences.getBoolean(KEY_DEBUG, false);
        archiveADC = sharedPreferences.getBoolean(KEY_ARCHIVE_SAVE_ADC, false);

        archiveDriverMax = Integer.parseInt(sharedPreferences.getString(KEY_ARCHIVE_DRIVER_WEIGHT_MAX, "0"));
        discrete = Float.parseFloat(sharedPreferences.getString(KEY_DISCRETE_VALUE, "0"));
        maxDeviation = Float.parseFloat(sharedPreferences.getString(KEY_DISCRETE_MAX, "1"));
        timeStab = Integer.parseInt(sharedPreferences.getString(KEY_TIME_STAB, "3"));
        minWeightForSave = Float.parseFloat(sharedPreferences.getString(KEY_MIN_WEIGHT, "1"));

        if (debug_archive) {
            //Toast.makeText(getContext(), "отладка вкл", Toast.LENGTH_SHORT).show();
            debugArchiveLayout.setVisibility(View.VISIBLE);
        } else {
            //Toast.makeText(getContext(), "отладка выкл", Toast.LENGTH_SHORT).show();
            debugArchiveLayout.setVisibility(View.GONE);

        }

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

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.btn_test:
                cleanDebug();
                break;
        }
        return false;
    }

    void cleanDebug() {
        tvDebugDate.setText("");
        tvDebugWeight.setText("");
        tvDebugTare.setText("");
        tvDebugAdc.setText("");
        tvDebugType.setText("");
    }
}

