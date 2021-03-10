package no.nordicsemi.android.sdr.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ParsedDataViewModel extends ViewModel {

    private final MutableLiveData<Float> mWeightValue = new MutableLiveData<>();

    public LiveData<Float> getWeightValue() {return mWeightValue;}

    public void setmWeightValue (final float weightValue) {mWeightValue.setValue(weightValue);}
}
