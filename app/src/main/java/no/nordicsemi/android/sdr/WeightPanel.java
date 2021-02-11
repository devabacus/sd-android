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

import java.io.File;
import java.io.IOException;
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
import no.nordicsemi.android.sdr.viewmodels.BlinkyViewModel;
import no.nordicsemi.android.sdr.viewmodels.HardButsViewModel;
import no.nordicsemi.android.sdr.viewmodels.StateViewModel;
import no.nordicsemi.android.sdr.preferences.PrefArchive;
import no.nordicsemi.android.sdr.preferences.PrefWeightFrag;
import no.nordicsemi.android.sdr.preferences.SettingsFragment;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeightPanel extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    String URL = "https://script.google.com/macros/s/AKfycbyodPZIZak1FbdYa0V-gPpI0tCyAt_pBcWfMqj_c9X4AwzJzLst/exec";

    BlinkyViewModel blinkyViewModel;
    ButtonsViewModel buttonsViewModel;
    HardButsViewModel hardButsViewModel;
    StateViewModel stateViewModel;
    ConstraintLayout weightLayout, debugArchiveLayout;
    TextView tvWeight;
    Button btnArhive, btnTest, btnTestHttp;
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
    public static Boolean archive;
    public static Boolean debug_archive;
    Boolean archiveADC;
    Boolean corArchiveSave;
    int archiveDriverMax;
    boolean enoughChange = false;

    float weightValueFloat = 0;
    float weightSaved = 0;
    float weightSavedMax = 0;

    int archMax = 0;

    float adcWeight = 0;
    public static int tare = 0;
    int arch = 0;
    int typeOfWeight = 0;
    boolean weightTonn = false;


    float weightMax = 0;
    float adcWeightMax = 0;
    int tareMax = 0;
    int adcValueMax = 0;
    Date dateTimeMax;

    Date dateTime[];
    ArrayList<Date> dateTimeArrL;
    private OkHttpClient client;


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

//        Log.d(TAG, "Array list" + dateTimeArrL.get(i) + ", " +
//                weightValueArrL.get(i) + ", " +
//                adcWeight_arrL.get(i) + ", " +
//                adcValue_arrL.get(i) + ", " +
//                tare_arrL.get(i) + ", " +
//                typeOfWeight_arrL.get(i) + "\n");
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
        btnTestHttp = v.findViewById(R.id.btn_test1);
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

        client = new OkHttpClient();


        archiveViewModel.getArchiveListLast().observe(getActivity(), archiveDataList -> {
            assert archiveDataList != null;
            if ((archiveDataList.size() > 0) && (!corArchiveSave))
                numOfWeight = archiveDataList.get(0).getNumOfWeight();
        });
        buttonsViewModel.ismSetButton().observe(getActivity(), aBoolean -> {
            butSet = aBoolean;
        });

        hardButsViewModel.getHardActive().observe(getActivity(), aBoolean -> {
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
            assert corButton != null;
            Log.d(TAG, "onCreateView:" + corButton.getButNum());
            if (!corButton.getButNum().isEmpty()) {
//                Log.d(TAG, "onCreateView:" + corButton.getButNum());
                tare = Integer.parseInt(corButton.getButNum());
                //tare = corButton.getButNum();
            }
        });

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

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            blinkyViewModel.sendTX("s13/2");
                        }
                    }, 1000);
                    // Toast.makeText(getContext(), "saved", Toast.LENGTH_SHORT).show();
                }
            } else {
                tare = 0;
            }
            // }
        });

        // Сохранение в архив только корректировок

        btnArhive.setOnClickListener(this);
        btnTest.setOnClickListener(this);
        btnTestHttp.setOnClickListener(this);
        btnTest.setOnLongClickListener(this);

        blinkyViewModel.getUartData().observe(getActivity(), s -> {
            assert s != null;
            if (s.matches("^wt.*")) {
                String weightValueStr = s.substring(s.indexOf('t') + 1);
                weightValueStr = weightValueStr.replaceAll("[^0-9.-]", "");
                weightValueFloat = Float.parseFloat(weightValueStr);
                if (weightTonn) {
                    weightValueFloat *= 1000;
                }
                if ((weightValueFloat != weightValueLast) && (weightValueFloat > minWeightForSave)) {
                    if (minChange()) {
                        timeCounter = 0;
                    }
                    // update weight max value (it's located in first member of array)
                    if (weightValueFloat > weightMax) {
                        dateTimeMax = new Date();
                        weightMax = weightValueFloat;
                        tareMax = tare;
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
//                    Log.d(TAG, "***MAX******MAX******MAX******MAX******MAX******MAX******MAX******MAX***");
                    for (int i = 0; i < arch + 1; i++) {
                        archive_arr_show(i);

                        if (archive) {
                            blinkyViewModel.sendTX("s13/2");
                            if (StateFragment.option_archive != 0) {
                                archiveViewModel.addArchiveItem(new ArchiveData(dateTimeArrL.get(i),
                                        weightValueArrL.get(i), numOfWeight, adcWeight_arrL.get(i),
                                        adcValue_arrL.get(i), tare_arrL.get(i), typeOfWeight_arrL.get(i)));
                                //Toast.makeText(getContext(), "saved", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
//                    Log.d(TAG, "***MAX******MAX******MAX******MAX******MAX******MAX******MAX******MAX***");

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
        Boolean show_weight = sharedPreferences.getBoolean(SettingsFragment.KEY_WEIGHT_SHOW, false);
        archive = sharedPreferences.getBoolean(PrefArchive.KEY_ARCHIVE_SAVE, false);
        debug_archive = sharedPreferences.getBoolean(PrefArchive.KEY_DEBUG, false);
        archiveADC = sharedPreferences.getBoolean(PrefArchive.KEY_ARCHIVE_SAVE_ADC, false);
        corArchiveSave = sharedPreferences.getBoolean(PrefArchive.KEY_CORR_ARCHIVE, false);
        weightTonn = sharedPreferences.getBoolean(PrefArchive.KEY_WEIGHT_TONN, false);
        archiveDriverMax = Integer.parseInt(sharedPreferences.getString(PrefArchive.KEY_ARCHIVE_DRIVER_WEIGHT_MAX, "0"));
        discrete = Float.parseFloat(sharedPreferences.getString(PrefWeightFrag.KEY_DISCRETE_VALUE, "0"));
        maxDeviation = Float.parseFloat(sharedPreferences.getString(PrefArchive.KEY_DISCRETE_MAX, "1"));
        timeStab = Integer.parseInt(sharedPreferences.getString(PrefArchive.KEY_TIME_STAB, "3"));
        minWeightForSave = Float.parseFloat(sharedPreferences.getString(PrefArchive.KEY_MIN_WEIGHT, "1"));

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
            case R.id.btn_test1:
                Toast.makeText(getContext(), "Будем тестировать запрос", Toast.LENGTH_SHORT).show();
//                httpThings();
                FileExport fileExport = new FileExport();
                String pathToFile = fileExport.writeToFile("ivan durak", Objects.requireNonNull(getContext()));
                Toast.makeText(getContext(), pathToFile, Toast.LENGTH_SHORT).show();
                FtpRoutines ftpRoutines = new FtpRoutines();
                ftpRoutines.sendFileToServer("185.12.92.65", "katitka@etalon-ufa.ru", "123QWEasdZXC", pathToFile);
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

    void httpThings() {
        final Request request = new Request.Builder().url(URL).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(getContext(), "Failure!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) {
                Log.d(TAG, "onResponse: " + response.code());
                //Toast.makeText(getContext(), response.body().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void cleanDebug() {
        tvDebugDate.setText("");
        tvDebugWeight.setText("");
        tvDebugTare.setText("");
        tvDebugAdc.setText("");
        tvDebugType.setText("");
    }
}

