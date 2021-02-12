package no.nordicsemi.android.sdr.archive;

import android.content.SharedPreferences;
import android.os.Handler;

import java.util.ArrayList;
import java.util.Date;

import no.nordicsemi.android.sdr.StateFragment;
import no.nordicsemi.android.sdr.database_archive.ArchiveData;
import no.nordicsemi.android.sdr.preferences.PrefArchive;
import no.nordicsemi.android.sdr.preferences.PrefWeightFrag;
import no.nordicsemi.android.sdr.preferences.SettingsFragment;

public class SavingDataToArchive {

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
    ArrayList<Float> weightValueArrL;
    ArrayList<Integer> tare_arrL;
    ArrayList<Integer> adcValue_arrL;
    ArrayList<Float> adcWeight_arrL;
    ArrayList<Integer> typeOfWeight_arrL;
    Boolean butSet = false;
    Boolean show_weight;

    Handler handler = new Handler();

    void timerIsFired(){
        if (timeCounting) {
            timeCounter++;
            if ((timeCounter >= timeStab) && (weightValueFloat > minWeightForSave) && minChange()) {
                if (arch == 0) {
                    //// TODO: 11-Feb-21 call function fo clear tvViews
//                            cleanDebug();
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

    }

    public void timerHandle(){
        int delay = 1000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                timerIsFired();
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    public boolean minChange() {
        return Math.abs(weightValueFloat - weightSaved) > maxDeviation;
    }

    public void archive_arr_show(int i) {
//        Log.d("test", dateTime[i] + ", " +
//                weightValueFloat_arr[i] + ", " +
//                adcWeight_arr[i] + ", " +
//                adcValue_arr[i] + ", " +
//                tare_arr[i] + ", " +
//                typeOfWeight_arr[i] + "\n");
//
//        Log.d(TAG, "archive_arr_show: " + weightValueArrL.get(i) + ", ");
//
//        Log.d(TAG, "Array list" + dateTimeArrL.get(i) + ", " +
//                weightValueArrL.get(i) + ", " +
//                adcWeight_arrL.get(i) + ", " +
//                adcValue_arrL.get(i) + ", " +
//                tare_arrL.get(i) + ", " +
//                typeOfWeight_arrL.get(i) + "\n");
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

    public float driveWeightFind() {
        return (weightValueArrL.get(archMax) - weightValueArrL.get(archMax + 1));
    }

    void sharedPrefGetData(SharedPreferences sp){
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
}
