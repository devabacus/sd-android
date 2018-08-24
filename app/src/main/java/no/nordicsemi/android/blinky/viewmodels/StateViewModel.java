package no.nordicsemi.android.blinky.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.view.ViewDebug;

public class StateViewModel extends AndroidViewModel {

    private final MutableLiveData<Integer> mAutoCorMode = new MutableLiveData<>();
    private final MutableLiveData<Integer> mADCvalue = new MutableLiveData<>();
    private final MutableLiveData<Float> mWeightValue = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isCorActive = new MutableLiveData<>();



    public LiveData<Integer> getADCvalue() {
        return mADCvalue;
    }

    public LiveData<Float> getWeightValue() { return mWeightValue;}

    public LiveData<Integer> getAutoCorMode() {
        return mAutoCorMode;
    }

    public LiveData<Boolean> getIsCorActive(){return isCorActive;}


    public void setmADCvalue(final Integer adc_value) {
        mADCvalue.setValue(adc_value);
    }
    public void setmWeightValue(final float weight_value) { mWeightValue.setValue(weight_value);}


    public void setmAutoCorMode(final Integer autoCorMode){
        mAutoCorMode.setValue(autoCorMode);
    }

    public void setmIsCorActive (final Boolean corActive) {isCorActive.setValue(corActive);}

    public StateViewModel(@NonNull Application application) {
        super(application);
    }





}
