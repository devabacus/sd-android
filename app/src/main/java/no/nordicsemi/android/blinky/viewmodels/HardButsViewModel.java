package no.nordicsemi.android.blinky.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.widget.Toast;

public class HardButsViewModel extends AndroidViewModel {

    private final MutableLiveData<Integer> mNumber = new MutableLiveData<>();

    public LiveData<Integer> getmNumber() {
        return mNumber;
    }

    public void setmNumber(final Integer number) {
        mNumber.setValue(number);
    }

    public HardButsViewModel(@NonNull Application application) {
        super(application);
    }
}
