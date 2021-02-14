package no.nordicsemi.android.sdr.archive;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import android.support.annotation.NonNull;

import java.util.Date;
import java.util.List;

import no.nordicsemi.android.sdr.database_archive.ArchiveData;
import no.nordicsemi.android.sdr.database_archive.ArchiveDatabase;

public class ArchiveViewModel extends AndroidViewModel {

    private final LiveData<List<ArchiveData>> archiveList;
    private final LiveData<List<ArchiveData>> archiveListLast;
    private LiveData<List<ArchiveData>> archiveListbyNum;
    private LiveData<List<ArchiveData>> archiveListbyType;
    private LiveData<List<ArchiveData>> archiveListByDates;


    private ArchiveDatabase archiveDatabase;

    private final MutableLiveData<Boolean> misDateUpdated = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mOpenDetailArchive = new MutableLiveData<>();
    private final MutableLiveData<Integer> mNumOfWeightPicked = new MutableLiveData<>();

    public ArchiveViewModel(@NonNull Application application) {
        super(application);
        archiveDatabase = ArchiveDatabase.getArchiveDatabase(this.getApplication());
        archiveList = archiveDatabase.archiveFromDao().getAllArchiveItems();
        archiveListLast = archiveDatabase.archiveFromDao().getLastItem();
        //archiveListbyNum = archiveDatabase.archiveFromDao().getItemByNumOfWeight(numOfWeightView);
        //this.archiveList = archiveList;
    }

    public LiveData<Boolean> mIsDetailOpen() {
        return mOpenDetailArchive;
    }

    public LiveData<Boolean> getIsDateUpdate() {
        return misDateUpdated;
    }

    public LiveData<Integer> getNumOfWeightPicked() {
        return mNumOfWeightPicked;
    }

    public LiveData<List<ArchiveData>> getArchiveList() {
        return archiveList;
    }

    public LiveData<List<ArchiveData>> getArchiveListLast() {
        return archiveListLast;
    }

    public LiveData<List<ArchiveData>> getArchiveListbyNum(int numOfWeightPicked) {
        archiveListbyNum = archiveDatabase.archiveFromDao().getItemByNumOfWeight(numOfWeightPicked);
        return archiveListbyNum;
    }

    public LiveData<List<ArchiveData>> getArchiveListbyType(int typeOfWeight) {
        archiveListbyType = archiveDatabase.archiveFromDao().getItemByTypeOfWeight(typeOfWeight);
        return archiveListbyType;
    }

//    public void setArchiveDates(Date start, Date end) {
//        archiveListByDates = archiveDatabase.archiveFromDao().getItemsByTheDates(start, end);
//        return archiveListByDates;
//    }

    public LiveData<List<ArchiveData>> getArchiveListByDates(Date start, Date end) {
        archiveListByDates = archiveDatabase.archiveFromDao().getItemsByTheDates(start, end);
        return archiveListByDates;
    }

    public void setDateUpdated(final Boolean isUpdated) {
        misDateUpdated.setValue(isUpdated);
    }

    public void setmOpenDetailArchive(final Boolean archiveDetailOpen) {
        mOpenDetailArchive.setValue(archiveDetailOpen);
    }

    public void setmNumOfWeightPicked(final Integer numOfWeightPicked) {
        mNumOfWeightPicked.setValue(numOfWeightPicked);
    }

    public void addArchiveItem(final ArchiveData archiveData) {
        new AddAsyncTask(archiveDatabase).execute(archiveData);
    }

    public void deleteArchiveItem(ArchiveData archiveData) {
        new DeleteAsyncTask(archiveDatabase).execute(archiveData);
    }

    public void deleteAllArchiveItems() {
        new DeleteAllAsyncTask(archiveDatabase).execute();
    }


    private static class AddAsyncTask extends AsyncTask<ArchiveData, Void, Void> {
        private ArchiveDatabase db;

        AddAsyncTask(ArchiveDatabase db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(ArchiveData... archiveData) {
            db.archiveFromDao().addArchiveItem(archiveData[0]);
            return null;
        }
    }

    private static class DeleteAsyncTask extends AsyncTask<ArchiveData, Void, Void> {
        private ArchiveDatabase db;

        DeleteAsyncTask(ArchiveDatabase db) {
            this.db = db;
        }


        @Override
        protected Void doInBackground(ArchiveData... archiveData) {
            db.archiveFromDao().deleteItem(archiveData[0]);
            return null;
        }
    }


    private static class DeleteAllAsyncTask extends AsyncTask<Void, Void, Void> {
        private ArchiveDatabase db;

        DeleteAllAsyncTask(ArchiveDatabase db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            db.archiveFromDao().deleteAll();
            return null;
        }
    }


}
