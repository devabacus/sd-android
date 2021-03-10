package no.nordicsemi.android.sdr.archive;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import no.nordicsemi.android.blinky.R;
import no.nordicsemi.android.sdr.Cmd;
import no.nordicsemi.android.sdr.StateFragment;
import no.nordicsemi.android.sdr.WeightPanel;
import no.nordicsemi.android.sdr.buttons.ButtonsViewModel;
import no.nordicsemi.android.sdr.database_archive.ArchiveData;
import no.nordicsemi.android.sdr.preferences.PrefArchive;
import no.nordicsemi.android.sdr.preferences.PrefWeightFrag;
import no.nordicsemi.android.sdr.preferences.SettingsFragment;
import no.nordicsemi.android.sdr.preferences.PrefExport;
import no.nordicsemi.android.sdr.viewmodels.BleViewModel;
import no.nordicsemi.android.sdr.viewmodels.ParsedDataViewModel;
import no.nordicsemi.android.sdr.viewmodels.StateViewModel;

import static java.lang.Math.abs;
import static no.nordicsemi.android.sdr.preferences.PrefArchive.KEY_MAX_WEIGHT_TOLERANCE;

public class ArchiveSaving extends Fragment implements View.OnClickListener, View.OnLongClickListener {
// vars initialize
    BleViewModel bleViewModel;

    StateViewModel stateViewModel;
    ParsedDataViewModel parsedDataViewModel;
    ButtonsViewModel buttonsViewModel;
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

    Boolean butSetUp = false;

    float weightValueFloat = 0;
    float weightSavedLast = 0;
    float weightSavedMax = 0;
    float weightMaxCalculated = 0;

    int archMaxStab = 0;

    int adcWeight = 0;
    public static int tare = 0;
    boolean isPersent = false;
    boolean isPersentMax = false;
    int arch = 0;
    int archLast = 0;
    boolean weightTonn = false;
    boolean isCorActive = false;
    int suspectState = 0;

    float weightMax = 0;
    int adcWeightMax = 0;
    int tareMax = 0;
    int adcValueMax = 0;
    int timeStabMax = 0;

    boolean autoExport = false;
    boolean exportDetail = false;
    float max_tolerance;
    boolean stabWhileUnload;

    FirestoreSave firestoreSave;

    long startStabWeight = 0;
    long endStabWeight = 0;
    Date dateTimeMax;
    Timer mTimer;

    //    File exportFile;
    FileExport fileExport;
    SharedPreferences sp;

    Date[] dateTime;
    ArrayList<Date> dateTimeArrL;
    ArrayList<Float> weightValueArrL;
    ArrayList<Float> weightTrueArrL;
    ArrayList<Integer> tare_arrL;
    ArrayList<Boolean> isPercent_arrL;
    ArrayList<Integer> stab_timeL;
    ArrayList<Integer> adcValue_arrL;
    ArrayList<Integer> adcWeight_arrL;
    ArrayList<Integer> typeOfWeight_arrL;

    void initArrays() {
        dateTime = new Date[20];
        dateTimeMax = new Date();
        dateTimeArrL = new ArrayList<>();
        weightValueArrL = new ArrayList<>();
        weightTrueArrL = new ArrayList<>();
        adcWeight_arrL = new ArrayList<>();
        adcValue_arrL = new ArrayList<>();
        tare_arrL = new ArrayList<>();
        isPercent_arrL = new ArrayList<>();
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

    void sharedPrefGetData() {
        show_weight = sp.getBoolean(SettingsFragment.KEY_WEIGHT_SHOW, false);
        archive = sp.getBoolean(PrefArchive.KEY_ARCHIVE_SAVE, false);
        debug_archive = sp.getBoolean(PrefArchive.KEY_DEBUG, false);
        archiveADC = sp.getBoolean(PrefArchive.KEY_ARCHIVE_SAVE_ADC, false);
        corArchiveSave = sp.getBoolean(PrefArchive.KEY_CORR_ARCHIVE, false);
        weightTonn = sp.getBoolean(PrefWeightFrag.KEY_WEIGHT_TONN, false);
        archiveDriverMax = Integer.parseInt(sp.getString(PrefArchive.KEY_ARCHIVE_DRIVER_WEIGHT_MAX, "0"));
        discrete = Float.parseFloat(sp.getString(PrefWeightFrag.KEY_DISCRETE_VALUE, "0"));
        maxDeviation = Float.parseFloat(sp.getString(PrefArchive.KEY_DISCRETE_MAX, "1"));
        timeStab = Integer.parseInt(sp.getString(PrefArchive.KEY_TIME_STAB, "3"));
        minWeightForSave = Float.parseFloat(sp.getString(PrefArchive.KEY_MIN_WEIGHT, "1"));
        autoExport = sp.getBoolean(PrefExport.KEY_EXPORT_AUTO, false);
        exportDetail = sp.getBoolean(PrefExport.KEY_EXPORT_DETAIL, false);
        stabWhileUnload = sp.getBoolean(PrefArchive.KEY_STAB_WHILE_UNLOAD, false);
        max_tolerance = Float.parseFloat(Objects.requireNonNull(sp.getString(KEY_MAX_WEIGHT_TOLERANCE, "0")));
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


    int parseTare(String butName) {
        String regex = "((-){0,1}[0-9]+)(%)?";
        Pattern r = Pattern.compile(regex);
        Matcher m = r.matcher(butName);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return 0;
    }


    void getBtnState() {

//        buttonsViewModel = ViewModelProviders.of(getActivity()).get(ButtonsViewModel.class);
        buttonsViewModel = new ViewModelProvider(getActivity()).get(ButtonsViewModel.class);
        buttonsViewModel.getmCurCorButton().observe(getActivity(), corButton -> {
            assert corButton != null;
            if (!corButton.getButName().isEmpty()) {
                tare = parseTare(corButton.getButName());
                isPersent = corButton.getButName().contains("%");
                Log.d(TAG, "getBtnState: isPercent = " + isPersent);
            }
        });

        buttonsViewModel.ismSetUpButton().observe(getActivity(), aBoolean -> {
            butSetUp = aBoolean;
        });
    }

    void weightObserve() {
        parsedDataViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(ParsedDataViewModel.class);
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
        return abs(weightValueFloat - weightSavedLast) > maxDeviation;
    }

    public float driveWeightFind() {
        return (weightValueArrL.get(archMaxStab) - weightValueArrL.get(archMaxStab + 1));
//        return (weightValueArrL.get(archMaxStab - 1) - weightValueArrL.get(archMaxStab));
    }

    public void archive_arr_fill(int i, int type) {
        float trueWeight = 0;
        typeOfWeight_arrL.add(i, type);
        if (!isCorActive) {
            tare = 0;
            tareMax = 0;
        }
        if (type == 0) {
            dateTimeArrL.add(i, new Date());
            weightValueArrL.add(i, weightValueFloat);
            adcWeight_arrL.add(i, adcWeight);
            adcValue_arrL.add(i, StateFragment.adcValue);
            tare_arrL.add(i, tare);
            isPercent_arrL.add(i, isPersent);
            weightTrueArrL.add(i, (isPersent && tare != 0) ? weightValueFloat * 100 / abs(tare) : weightValueFloat - tare);
            stab_timeL.add(i, timeCounter);
        } else if (type == 2) {
            dateTimeArrL.add(i, dateTimeMax);
            weightValueArrL.add(i, weightMax);
            adcWeight_arrL.add(i, adcWeightMax);
            adcValue_arrL.add(i, adcValueMax);
            weightTrueArrL.add(i, (float) 0);  // (isPersentMax && tareMax != 0) ? weightMax * 100 / abs(tareMax) : weightMax - tareMax)
            tare_arrL.add(i, tareMax);
            isPercent_arrL.add(i, isPersentMax);
            stab_timeL.add(i, timeStabMax);
        }
    }

    void archive_log_show(int i) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", new Locale("ru"));
        Log.d(TAG, "Array list" + format.format(dateTimeArrL.get(i)) + ", " + arch + ", " +
                weightValueArrL.get(i) + ", " +
                weightTrueArrL.get(i) + ", " +
                adcWeight_arrL.get(i) + ", " +
                adcValue_arrL.get(i) + ", " +
                tare_arrL.get(i) + (isPercent_arrL.get(i) ? "%" : "") + ", " +
                stab_timeL.get(i) + ", " +
                typeOfWeight_arrL.get(i) + "\n");
    }

    public void archive_arr_show(int i) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", new Locale("ru"));
        tvDebugDate.setText(tvDebugDate.getText() + "\n" + format.format(dateTimeArrL.get(i)));
        tvDebugWeight.setText(tvDebugWeight.getText() + "\n" + WeightPanel.fmt(weightValueArrL.get(i)));
        tvDebugTrueWeight.setText(tvDebugTrueWeight.getText() + "\n" + WeightPanel.fmt(weightTrueArrL.get(i)));
        if (adcValue_arrL.get(i) > 0) {
            tvDebugAdc.setText(tvDebugAdc.getText() + "\n" + adcValue_arrL.get(i));
        } else {
            tvDebugAdc.setVisibility(View.GONE);
        }
        if (stab_timeL.size() > i) {
            tvDebugStabTime.setText(tvDebugStabTime.getText() + "\n" + (stab_timeL.get(i) > 0 ? "s" + stab_timeL.get(i) : ""));
        }
        String isPercent = (isPercent_arrL.get(i) && (tare_arrL.get(i) != 0)) ? "%" : "";
        tvDebugTare.setText(tvDebugTare.getText() + "\n" + tare_arrL.get(i) + isPercent);
        tvDebugType.setText(tvDebugType.getText() + "\n" + typeOfWeight_arrL.get(i));
    }

    void observeButActive() {
        stateViewModel.getIsCorActive().observe(getActivity(), corActive -> {
            assert corActive != null;
            isCorActive = corActive;
            Log.d(TAG, "observeButActive: corActive = " + isCorActive);
        });
    }

    void archiveOnlyCorr() {
        stateViewModel.getIsCorActive().observe(getActivity(), corActive -> {
            assert corActive != null;
            if (!butSetUp) {
                if (corArchiveSave) {
                    archive_arr_fill(0, 2);
                    // blinkyViewModel.sendTX("s13/2");
                    archiveViewModel.addArchiveItem(new ArchiveData(numOfWeight, new Date(),
                            weightValueArrL.get(0), weightTrueArrL.get(0), adcWeight_arrL.get(0),
                            adcValue_arrL.get(0), tare, isPercent_arrL.get(0), stab_timeL.get(0), 2, 0));
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
                    weightMaxCalculated = (isPersent && tare != 0) ? weightValueFloat * 100 / abs(tare) : weightValueFloat - tare;
                    archMaxStab = arch;
                } else if (weightValueArrL.get(archMaxStab) - weightValueArrL.get(arch) > archiveDriverMax) {
                    //стабильное при разгрузке

                    if(stabWhileUnload){
                        suspectState |= SuspectMasks.STAB_WHILE_UNLOAD;
                    }

                    Log.d(TAG, "stabTimerIsFired: stab unload suspect = " + suspectState);
                }

                arch++;
//                Log.d(TAG, "stabTimerIsFired: arch = " + arch);
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
        else if (weightValueFloat < minWeightForSave && decWeight) { //&& (weightValueArrL.size() > 0)
            saveArraysIntoDatabase();
        }
//        Log.d(TAG, "weightFloatFromBLEviewmodel: " + weightValueFloat);
        weightValueLast = weightValueFloat;
    }

    void addNewItemInArr() {
        if (minChange()) {
//            Log.d(TAG, "arch = " + arch);
            //значить уже был один стабильный, потому что там он инкрементируется
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
//            tareMax = 0;
            isPersentMax = isPersent;
            adcValueMax = StateFragment.adcValue;
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
//            archive_arr_fill(arch, 2);
        }
        //Log.d(TAG, "onCreateView: вес отличается. Запустили таймер");
        timerCounting = true;
    }

    void saveArraysIntoDatabase() {
        numOfWeight++;
        cleanDebug();
        Log.d(TAG, "saveArraysIntoDatabase: arch = " + arch);
        // проверяем вышел ли водитель. Если да, то берем за основное взвешивание следующее после максимального
        if (weightValueArrL.size() > (archMaxStab + 2)) {
            if (driveWeightFind() <= (archiveDriverMax + maxDeviation)) {
                if ((weightValueArrL.get(archMaxStab + 2) - weightValueArrL.get(archMaxStab + 1)) <= (archiveDriverMax + maxDeviation)) {
                    typeOfWeight_arrL.set(archMaxStab + 1, 1);
//                        suspectState |= SuspectMasks.DRIVER_DETECT;
                    typeOfWeight_arrL.set(archMaxStab, 3); //установили вес водителя
                    Log.d(TAG, "driver weight = " + driveWeightFind() + " suspect = " + suspectState);
                }
            } else {
                typeOfWeight_arrL.set(archMaxStab, 1);
            }
        } else if (weightSavedMax != 0) {
//                        Log.d(TAG, "weightSavedMax != 0");
            typeOfWeight_arrL.set(archMaxStab, 1);
        }

//        if(arch > (archMaxStab + 1) && ((suspectState & SuspectMasks.DRIVER_DETECT) == 1))

        //чтобы запись с максимальным весом и стабильным не дублировались в архиве если разница между ними в пределах погрешности

        if (weightMax - weightMaxCalculated > maxDeviation) {

            if (arch == 0) {
                archive_arr_fill(0, 2);
                suspectState |= SuspectMasks.ONLY_MAX_WEIGHT;
                //это условие поставил вторым потому что если arch ноль то dateTimeArrL.get(0) возвращаем ошибку индекса
            } else if ((dateTimeMax.getTime() < dateTimeArrL.get(0).getTime())) {
                archive_arr_fill(0, 2);

            } else if (arch > 0) {
                int k = 0;
                //перебираем массив, чтобы вставить в нужное место максимальное взвешивание
                for (int i = 0; i < dateTimeArrL.size(); i++) {
                    if ((dateTimeMax.getTime() > dateTimeArrL.get(i).getTime())) {
                        k = i;
                    }
                }
                archive_arr_fill(k + 1, 2);
                suspectState |= SuspectMasks.MAX_WEIGHT;
            }
            if ((weightMax - weightSavedMax > max_tolerance) && (max_tolerance != 0) && (weightSavedMax != 0)){
                suspectState |= SuspectMasks.MAX_WEIGHT;
            }

            Log.d(TAG, "max weight set suspect = " + suspectState);

        } else {
            arch--;
        }

        //write all of the array items into the database
        List<ArchiveData> listOfArchiveData = new ArrayList<>();
        for (int i = 0; i < arch + 1; i++) {
            archive_arr_show(i);

            if (archive) {

                bleViewModel.sendTX(Cmd.INCREASE_ARCHIVE_COUNTER);
                // if archive activated or in demo

                ArchiveData archiveData = new ArchiveData(numOfWeight, dateTimeArrL.get(i),
                        weightValueArrL.get(i), weightTrueArrL.get(i), adcWeight_arrL.get(i),
                        adcValue_arrL.get(i), tare_arrL.get(i), isPercent_arrL.get(i), stab_timeL.get(i), typeOfWeight_arrL.get(i), suspectState);

                if (StateFragment.option_archive != 0) {
                    // сохраняем в базу данных
                    archiveViewModel.addArchiveItem(archiveData);
                    //todo create settings for saving in cloud database
                    firestoreSave.saveToDatabase(archiveData);
                    //Toast.makeText(getContext(), "saved", Toast.LENGTH_SHORT).show();
                }
                listOfArchiveData.add(archiveData);
            }
        }
        if (autoExport) {
            if (!exportDetail) {
                listOfArchiveData = getNotDetailedList(listOfArchiveData);
            }
            fileExport.writeToFile(listOfArchiveData);


        }

        initArrays();
        resetArchiveVars();
    }

    List<ArchiveData> getNotDetailedList(List<ArchiveData> list) {
        List<ArchiveData> _list = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            int suspect = list.get(i).getSuspectState();
            int typeOfWeight = list.get(i).getTypeOfWeight();
            if (typeOfWeight == 1 || suspect == SuspectMasks.ONLY_MAX_WEIGHT) {
                _list.add(list.get(i));
            }
        }
        return _list;
    }

    void resetArchiveVars() {
        arch = 0;
        archLast = 0;
        decWeight = false;
        weightMax = 0;
        weightMax = 0;
        tareMax = 0;
        adcValueMax = 0;
        adcWeightMax = 0;
        timeStabMax = 0;
        archMaxStab = 0;
        weightSavedMax = 0;
        weightSavedLast = 0;
        weightMaxCalculated = 0;
        suspectState = 0;
        isPersent = false;
        isPersentMax = false;
    }

    @Override
    public void onResume() {
        sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPrefGetData();

        if (debug_archive) {
            //Toast.makeText(getContext(), "отладка вкл", Toast.LENGTH_SHORT).show();
            debugArchiveLayout.setVisibility(View.VISIBLE);
        } else {
            //Toast.makeText(getContext(), "отладка выкл", Toast.LENGTH_SHORT).show();
            debugArchiveLayout.setVisibility(View.GONE);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy", new Locale("ru"));
        if (fileExport == null) {
            File exportFile = new File(getActivity().getExternalFilesDir("archive_exports"), sdf.format(new Date()) + ".xml");
            fileExport = new FileExport(exportFile);
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
        firestoreSave = new FirestoreSave();
        getViewModels();
        weightObserve();

        initArrays();
        getBtnState();
        getLastArchiveItem();
        stabTimerWork();
        archiveOnlyCorr();
        observeButActive();
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