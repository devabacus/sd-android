package no.nordicsemi.android.sdr.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.widget.Toast;

public class HardButsViewModel extends AndroidViewModel {

    private final MutableLiveData<Integer> mNumber = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mHardActive = new MutableLiveData<>();




    public LiveData<Integer> getmNumber() {
        return mNumber;
    }

    public LiveData<Boolean> getHardActive() { return mHardActive;}

    public void setmNumber(final Integer number) {
        mNumber.setValue(number);
    }

    public void setmHardActive(final Boolean hardActive) { mHardActive.setValue(hardActive); }

    public HardButsViewModel(@NonNull Application application) {


        super(application);
    }
}
