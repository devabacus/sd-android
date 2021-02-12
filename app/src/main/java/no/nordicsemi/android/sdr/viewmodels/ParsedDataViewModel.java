package no.nordicsemi.android.sdr.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

public class ParsedDataViewModel extends ViewModel {

    private final MutableLiveData<Float> mWeightValue = new MutableLiveData<>();

    public LiveData<Float> getWeightValue() {return mWeightValue;}

    public void setmWeightValue (final float weightValue) {mWeightValue.setValue(weightValue);}
}
