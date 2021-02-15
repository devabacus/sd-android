package no.nordicsemi.android.sdr.archive;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
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
import java.util.Timer;

import no.nordicsemi.android.blinky.R;
import no.nordicsemi.android.sdr.Cmd;
import no.nordicsemi.android.sdr.StateFragment;
import no.nordicsemi.android.sdr.buttons.ButtonsViewModel;
import no.nordicsemi.android.sdr.database_archive.ArchiveData;
import no.nordicsemi.android.sdr.preferences.PrefArchive;
import no.nordicsemi.android.sdr.preferences.PrefWeightFrag;
import no.nordicsemi.android.sdr.preferences.SettingsFragment;
import no.nordicsemi.android.sdr.viewmodels.BleViewModel;
import no.nordicsemi.android.sdr.viewmodels.ParsedDataViewModel;
import no.nordicsemi.android.sdr.viewmodels.StateViewModel;

public class ArchiveSaving extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    BleViewModel bleViewModel;

    StateViewModel stateViewModel;
    ParsedDataViewModel parsedDataViewModel;
    ConstraintLayout debugArchiveLayout;
    Button btnArhive, btnTest;
    TextView tvDebugDate, tvDebugWeight, tvDebugAdc, tvDebugTare, tvDebugType, tvDebugTrueWeight, tvDebugStabTime;

    public static final String TAG = "test";
    float weightValueLast = 0;
    private ArchiveViewModel archiveViewModel;

    public static int numOfWeight = 1;

    float discrete = 0;
    float maxDeviation = 1;
    int timeStab = 3;
    int timeCounter = 0;
    boolean timerCounting = false;
    float minWeightForSave = 1;
    boolean incWeight = false;
    boolean decWeight = false;
    public static Boolean archive;
    public static Boolean debug_archive;
    Boolean archiveADC;
    Boolean corArchiveSave;
    int archiveDriverMax;
    Boolean show_weight;

    float weightValueFloat = 0;
    float weightSavedLast = 0;
    float weightSavedMax = 0;

    int archMax = 0;

    float adcWeight = 0;
    public static int tare = 0;
    int arch = 0;
    int archLast = 0;
    boolean weightTonn = false;

    float weightMax = 0;
    float adcWeightMax = 0;
    int tareMax = 0;
    int adcValueMax = 0;
    int timeStabMax = 0;

    long startStabWeight = 0;
    long endStabWeight = 0;
    Date dateTimeMax;
    Timer mTimer;

    Date[] dateTime;
    ArrayList<Date> dateTimeArrL;
    ArrayList<Float> weightValueArrL;
    ArrayList<Float> weightTrueArrL;
    ArrayList<Integer> tare_arrL;
    ArrayList<Integer> stab_timeL;
    ArrayList<Integer> adcValue_arrL;
    ArrayList<Float> adcWeight_arrL;
    ArrayList<Integer> typeOfWeight_arrL;
    Boolean butSet = false;

    void initArrays() {
        dateTime = new Date[20];
        dateTimeMax = new Date();
        dateTimeArrL = new ArrayList<>();
        weightValueArrL = new ArrayList<>();
        weightTrueArrL = new ArrayList<>();
        adcWeight_arrL = new ArrayList<>();
        adcValue_arrL = new ArrayList<>();
        tare_arrL = new ArrayList<>();
        stab_timeL = new ArrayList<>();
        typeOfWeight_arrL = new ArrayList<>();
    }

    void getViewModels() {
        bleViewModel = ViewModelProviders.of((getActivity())).get(BleViewModel.class);
        archiveViewModel = ViewModelProviders.of(getActivity()).get(ArchiveViewModel.class);
        stateViewModel = ViewModelProviders.of(getActivity()).get(StateViewModel.class);
    }

    void findAllViews(View v) {
        btnArhive = v.findViewById(R.id.btn_archive);
        btnTest = v.findViewById(R.id.btn_test);
        debugArchiveLayout = v.findViewById(R.id.debug_archive_layout);
        tvDebugDate = v.findViewById(R.id.tv_debug_date);
        tvDebugWeight = v.findViewById(R.id.tv_debug_weight);
        tvDebugAdc = v.findViewById(R.id.tv_debug_adc);
        tvDebugTare = v.findViewById(R.id.tv_debug_tare);
        tvDebugType = v.findViewById(R.id.tv_debug_type);
        tvDebugTrueWeight = v.findViewById(R.id.tv_debug_true_weight);
        tvDebugStabTime = v.findViewById(R.id.tv_debug_stab_time);
    }

    void sharedPrefGetData(SharedPreferences sp) {
        show_weight = sp.getBoolean(SettingsFragment.KEY_WEIGHT_SHOW, false);
        archive = sp.getBoolean(PrefArchive.KEY_ARCHIVE_SAVE, false);
        debug_archive = sp.getBoolean(PrefArchive.KEY_DEBUG, false);
        archiveADC = sp.getBoolean(PrefArchive.KEY_ARCHIVE_SAVE_ADC, false);
        corArchiveSave = sp.getBoolean(PrefArchive.KEY_CORR_ARCHIVE, false);
        weightTonn = sp.getBoolean(PrefArchive.KEY_WEIGHT_TONN, false);
        archiveDriverMax = Integer.parseInt(sp.getString(PrefArchive.KEY_ARCHIVE_DRIVER_WEIGHT_MAX, "0"));
        discrete = Float.parseFloat(sp.getString(PrefWeightFrag.KEY_DISCRETE_VALUE, "0"));
        maxDeviation = Float.parseFloat(sp.getString(PrefArchive.KEY_DISCRETE_MAX, "1"));
        timeStab = Integer.parseInt(sp.getString(PrefArchive.KEY_TIME_STAB, "3"));
        minWeightForSave = Float.parseFloat(sp.getString(PrefArchive.KEY_MIN_WEIGHT, "1"));
    }

    void getLastArchiveItem() {
        archiveViewModel.getArchiveListLast().observe(getActivity(), archiveDataList -> {
            assert archiveDataList != null;
            if ((archiveDataList.size() > 0) && (!corArchiveSave)) {
                //мы запрашиваем из базы список с лимитом 1, т.е. по факту приходит список с одной последней записью
                numOfWeight = archiveDataList.get(0).getNumOfWeight();
            }
        });
    }

    void getBtnState() {
        ButtonsViewModel buttonsViewModel;
        buttonsViewModel = ViewModelProviders.of(getActivity()).get(ButtonsViewModel.class);
        buttonsViewModel.getmCurCorButton().observe(getActivity(), corButton -> {
            assert corButton != null;
            Log.d(TAG, "onCreateView:" + corButton.getButNum());
            if (!corButton.getButNum().isEmpty()) {
                tare = Integer.parseInt(corButton.getButNum());
            }
        });

        buttonsViewModel.ismSetButton().observe(getActivity(), aBoolean -> {
            butSet = aBoolean;
        });
    }

    void weightObserve() {
        parsedDataViewModel = ViewModelProviders.of(getActivity()).get(ParsedDataViewModel.class);
        parsedDataViewModel.getWeightValue().observe(getActivity(), weight -> {
            weightValueFloat = weight;
            fillDataForArchive();
        });
    }

    void cleanDebug() {
        tvDebugDate.setText("");
        tvDebugWeight.setText("");
        tvDebugTare.setText("");
        tvDebugAdc.setText("");
        tvDebugType.setText("");
        tvDebugTrueWeight.setText("");
        tvDebugStabTime.setText("");
    }

    public boolean minChange() {
        //if the difference between a current weight and the previous one more than acceptable difference
        return Math.abs(weightValueFloat - weightSavedLast) > maxDeviation;
    }

    public float driveWeightFind() {
        return (weightValueArrL.get(archMax) - weightValueArrL.get(archMax + 1));
    }


    public void archive_arr_fill(int i, int type) {

        typeOfWeight_arrL.add(i, type);

        if (type == 0) {
            dateTimeArrL.add(i, new Date());
            weightValueArrL.add(i, weightValueFloat);
            adcWeight_arrL.add(i, adcWeight);
            adcValue_arrL.add(i, StateFragment.adcValue);
            tare_arrL.add(i, tare);
            weightTrueArrL.add(i, weightValueFloat + tare);
            stab_timeL.add(i, timeCounter);
        } else if (type == 2) {
            dateTimeArrL.add(arch, dateTimeMax);
            weightValueArrL.add(arch, weightMax);
            adcWeight_arrL.add(arch, adcWeightMax);
            adcValue_arrL.add(arch, adcValueMax);
            weightTrueArrL.add(arch, weightMax + tareMax);
            tare_arrL.add(arch, tareMax);
            stab_timeL.add(arch, timeStabMax);
        }
    }

    public void archive_arr_show(int i) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", new Locale("ru"));
        tvDebugDate.setText(tvDebugDate.getText() + "\n" + String.valueOf(format.format(dateTimeArrL.get(i))));
        tvDebugWeight.setText(tvDebugWeight.getText() + "\n" + String.valueOf(weightValueArrL.get(i)));
        tvDebugTrueWeight.setText(tvDebugTrueWeight.getText() + "\n" + String.valueOf(weightTrueArrL.get(i)));
        if (adcValue_arrL.get(i) > 0) {
            tvDebugAdc.setText(tvDebugAdc.getText() + "\n" + String.valueOf(adcValue_arrL.get(i)));
        } else {
            tvDebugAdc.setVisibility(View.INVISIBLE);
        }
        tvDebugStabTime.setText(tvDebugStabTime.getText() + "\n" + "s" + String.valueOf(stab_timeL.get(i)));
        tvDebugTare.setText(tvDebugTare.getText() + "\n" + String.valueOf(tare_arrL.get(i)));
        tvDebugType.setText(tvDebugType.getText() + "\n" + String.valueOf(typeOfWeight_arrL.get(i)));
    }

    void archiveOnlyCorr() {
        stateViewModel.getIsCorActive().observe(getActivity(), corActive -> {
            assert corActive != null;
            if (!butSet) {
                if (corArchiveSave) {
                    archive_arr_fill(0, 2);
                    // blinkyViewModel.sendTX("s13/2");
                    archiveViewModel.addArchiveItem(new ArchiveData(new Date(),
                            weightValueArrL.get(0), numOfWeight, adcWeight_arrL.get(0),
                            adcValue_arrL.get(0), tare, 2));
                    numOfWeight++;
                    // Toast.makeText(getContext(), "Сохранили", Toast.LENGTH_SHORT).show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            bleViewModel.sendTX("s13/2");
                        }
                    }, 1000);
                    // Toast.makeText(getContext(), "saved", Toast.LENGTH_SHORT).show();
                }
            } else {
                tare = 0;
            }
            // }
        });

    }

    void stabTimerWork() {
        Handler handler = new Handler();
        int delay = 1000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stabTimerIsFired();
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    void stabTimerIsFired() {
        timeCounter++;
        if (timerCounting) {
            //if the timer counted more or equal to timestab from settings and weight more
            // than minimun weight for save and it's not unstable things of the scale
            if ((timeCounter >= timeStab) && (weightValueFloat > minWeightForSave) && minChange()) {
                //arch is index of saved into arrays archive datas
                if (arch == 0) {
                    cleanDebug();
                }
                archive_arr_fill(arch, 0); // all of the weight notes by default with 0 type, later change after sorting and etc
                archive_arr_show(arch);  // show in debug window and log
                weightSavedLast = weightValueFloat;
                if (weightSavedLast > weightSavedMax) {
                    weightSavedMax = weightSavedLast;
                    archMax = arch;
                }

                arch++;
                //Log.d(TAG, "run: стабильный вес. Таймер сбросили`");
                timerCounting = false;
            }
        }
    }

    void fillDataForArchive() {
        //save to archive. The weight is zero
        if (weightTonn) {
            weightValueFloat *= 1000;
        }
        // вес изменился
        if ((weightValueFloat != weightValueLast) && (weightValueFloat > minWeightForSave)) {
            addNewItemInArr();
        }
        //if it's unload and weight within minWeightForSaveZone
        else if (weightValueFloat < minWeightForSave && decWeight && (weightValueArrL.size() > 0)) {
            saveArraysIntoDatabase();
            resetArchiveVars();
        }
//        Log.d(TAG, "weightFloatFromBLEviewmodel: " + weightValueFloat);
        weightValueLast = weightValueFloat;
    }

    void archive_log_show(int i) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", new Locale("ru"));
        Log.d(TAG, "Array list" + format.format(dateTimeArrL.get(i)) + ", " +
                weightValueArrL.get(i) + ", " +
                weightTrueArrL.get(i) + ", " +
                adcWeight_arrL.get(i) + ", " +
                adcValue_arrL.get(i) + ", " +
                tare_arrL.get(i) + ", " +
                stab_timeL.get(i) + ", " +
                typeOfWeight_arrL.get(i) + "\n");

    }


    void addNewItemInArr() {
        if (minChange()) {
//            Log.d(TAG, "arch = " + arch);
            if (arch > 0) {
                if (arch != archLast) {
                    stab_timeL.add(arch - 1, timeCounter);
                    archive_log_show(arch - 1);
                    archLast = arch;
                }
            }
            timeCounter = 0;
        }
        // update weight max value (it's located in first member of array)
        if (weightValueFloat > weightMax) {
            dateTimeMax = new Date();
            weightMax = weightValueFloat;
            tareMax = tare;
            adcValueMax = StateFragment.adcValue;
            adcWeightMax = adcWeight;
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
            //заполняем максимальный вес, хотя максимальным был предыдущий!!!!
            archive_arr_fill(arch, 2);
        }
        //Log.d(TAG, "onCreateView: вес отличается. Запустили таймер");
        timerCounting = true;
    }

    void saveArraysIntoDatabase() {
        numOfWeight++;
        cleanDebug();
        //Toast.makeText(getContext(), "max stab weight = " + String.valueOf(weightValueArrL.get(archMax)), Toast.LENGTH_SHORT).show();
        //change type of weight for mark max stab item
        // проверяем ли предыдущее сохраненное сохр взвешивание для сохр веса без людей
        if ((arch > 1) && (driveWeightFind() <= archiveDriverMax && driveWeightFind() > 0)) {
            typeOfWeight_arrL.set(archMax + 1, 1);
        } else if (weightSavedMax != 0) {
//                        Log.d(TAG, "weightSavedMax != 0");
            typeOfWeight_arrL.set(archMax, 1);
        }
//                    Log.d(TAG, "weightMax = " + weightMax);
//                    Log.d(TAG, "weightSavedMax = " + weightSavedMax);
        //}
        //чтобы запись с максимальным весом и стабильным не дублировались в архиве если разница между ними в пределах погрешности
        if (Math.abs(weightMax - weightSavedMax) > maxDeviation) {
//                        Log.d(TAG, "onCreateView: weightMax != weightSavedMax");
            archive_arr_fill(arch, 2);
        } else {
            arch--;
        }

        //write all of the array items into the database
        for (int i = 0; i < arch + 1; i++) {
            archive_arr_show(i);

            if (archive) {
                bleViewModel.sendTX(Cmd.INCREASE_ARCHIVE_COUNTER);
                // if archive activated or in demo
                if (StateFragment.option_archive != 0) {
                    // сохраняем в базу данных
                    archiveViewModel.addArchiveItem(new ArchiveData(dateTimeArrL.get(i),
                            weightValueArrL.get(i), numOfWeight, adcWeight_arrL.get(i),
                            adcValue_arrL.get(i), tare_arrL.get(i), typeOfWeight_arrL.get(i)));
                    //Toast.makeText(getContext(), "saved", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    void resetArchiveVars() {
        arch = 0;
        decWeight = false;
        weightMax = 0;
        weightMax = 0;
        tareMax = 0;
        adcValueMax = 0;
        adcWeightMax = 0;
        timeStabMax = 0;
        archMax = 0;
        weightSavedMax = 0;
        weightSavedLast = 0;
    }

    @Override
    public void onResume() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPrefGetData(sharedPreferences);

        if (debug_archive) {
            //Toast.makeText(getContext(), "отладка вкл", Toast.LENGTH_SHORT).show();
            debugArchiveLayout.setVisibility(View.VISIBLE);
        } else {
            //Toast.makeText(getContext(), "отладка выкл", Toast.LENGTH_SHORT).show();
            debugArchiveLayout.setVisibility(View.GONE);
        }
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_archive_debug, container, false);
        findAllViews(v);
        btnArhive.setOnClickListener(this);
        btnTest.setOnClickListener(this);
//        btnTestHttp.setOnClickListener(this);

        getViewModels();
        weightObserve();

        initArrays();
        getBtnState();
        getLastArchiveItem();
        stabTimerWork();
        archiveOnlyCorr();

        return v;

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

}