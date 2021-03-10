package no.nordicsemi.android.sdr.buttons;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import androidx.annotation.NonNull;

import no.nordicsemi.android.sdr.database.AppDatabase;
import no.nordicsemi.android.sdr.database.CorButton;

import java.util.List;

public class ButtonsViewModel extends AndroidViewModel {

    private final LiveData<List<CorButton>> corButList;
    private final MutableLiveData<CorButton> mCurCorButton = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mSetButton = new MutableLiveData<>();
    private final MutableLiveData<Integer> mCompCorValue = new MutableLiveData<>();
    private LiveData<CorButton> mCorButtonById = new MutableLiveData<>();



    private AppDatabase appDatabase;


    public LiveData<CorButton> getCorButtonById(long id){
        mCorButtonById = appDatabase.buttonsDao().getItemById(id);
        return mCorButtonById;
    }

    public LiveData<Integer> getCompCorValue(){
        return mCompCorValue;
    }

    public void setmCompCorValue(final Integer compValue){
        mCompCorValue.setValue(compValue);
    }

    public LiveData<Boolean> ismSetUpButton(){
        return mSetButton;
    }

    public void setmSetUpButton(final Boolean setButton){
        mSetButton.setValue(setButton);
    }

    public LiveData<CorButton> getmCurCorButton(){
        return mCurCorButton;
    }

    public void setmCurCorButton(final CorButton corButton){
        mCurCorButton.setValue(corButton);
    }



    public ButtonsViewModel(@NonNull Application application) {
        super(application);

        appDatabase = AppDatabase.getDatabase(this.getApplication());
        corButList = appDatabase.buttonsDao().getAllCorButtons();
    }

    public LiveData<List<CorButton>> getCorButList(){
        return corButList;
    }

    public void addCorBut(final CorButton corButton){
        new AddAsyncTask(appDatabase).execute(corButton);
    }

    public void deleteCorBut(CorButton corButton){
        new DeleteAsyncTask(appDatabase).execute(corButton);
    }

    private static class AddAsyncTask extends AsyncTask<CorButton, Void, Void>{
        private AppDatabase db;

        AddAsyncTask(AppDatabase db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(CorButton... corButtons) {
            db.buttonsDao().addButton(corButtons[0]);
            return null;
        }
    }

    private static class DeleteAsyncTask extends AsyncTask<CorButton, Void, Void>{
        private AppDatabase db;

        DeleteAsyncTask(AppDatabase db) {
            this.db = db;
        }


        @Override
        protected Void doInBackground(CorButton... corButtons) {
            db.buttonsDao().deleteButton(corButtons[0]);
            return null;
        }
    }




}
