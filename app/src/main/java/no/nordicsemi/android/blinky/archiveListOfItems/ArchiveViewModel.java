package no.nordicsemi.android.blinky.archiveListOfItems;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import android.support.annotation.NonNull;

import java.util.List;

import no.nordicsemi.android.blinky.database.CorButton;
import no.nordicsemi.android.blinky.database_archive.ArchiveData;
import no.nordicsemi.android.blinky.database_archive.ArchiveDatabase;

public class ArchiveViewModel extends AndroidViewModel {

    private LiveData<List<ArchiveData>> archiveList;

    private ArchiveDatabase archiveDatabase;

    public ArchiveViewModel(@NonNull Application application, LiveData<List<ArchiveData>> archiveList) {
        super(application);

        archiveDatabase = ArchiveDatabase.getArchiveDatabase(this.getApplication());
        archiveList = archiveDatabase.archiveFromDao().getAllArchiveItems();
        //this.archiveList = archiveList;
    }

    public LiveData<List<ArchiveData>> getArchiveList() {
        return archiveList;
    }

    public void addArchiveItem(final ArchiveData archiveData){
        new AddAsyncTask(archiveDatabase).execute(archiveData);
    }

    public void deleteArchiveItem(ArchiveData archiveData) {
        new DeleteAsyncTask(archiveDatabase).execute(archiveData);
    }

    private static class AddAsyncTask extends AsyncTask<ArchiveData, Void, Void>{
        private ArchiveDatabase db;

        AddAsyncTask(ArchiveDatabase db){this.db = db;}

        @Override
        protected Void doInBackground(ArchiveData... archiveData) {
            db.archiveFromDao().addArchiveItem(archiveData[0]);
            return null;
        }
    }

    private static class DeleteAsyncTask extends AsyncTask<ArchiveData, Void, Void>{
        private ArchiveDatabase db;

        DeleteAsyncTask(ArchiveDatabase db){this.db = db;}


        @Override
        protected Void doInBackground(ArchiveData... archiveData) {
            db.archiveFromDao().deleteItem(archiveData[0]);
            return null;
        }
    }

}
