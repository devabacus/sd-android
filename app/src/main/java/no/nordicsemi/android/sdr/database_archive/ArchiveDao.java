package no.nordicsemi.android.sdr.database_archive;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;

import java.util.Date;
import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
@TypeConverters(DateConverter.class)
public interface ArchiveDao {


    @Insert(onConflict = REPLACE)
    void addArchiveItem(ArchiveData archiveData);

    @Query("select * from ArchiveData")
    LiveData<List<ArchiveData>> getAllArchiveItems();

    @Query("select * from ArchiveData order by id DESC LIMIT 1")
    LiveData<List<ArchiveData>> getLastItem();

//    @Query("select * from ArchiveData where numOfWeight = 2")
//    LiveData<List<ArchiveData>> getItemByNumOfWeight();

    @Query("select * from ArchiveData where numOfWeight = :numOfWeight")
    LiveData<List<ArchiveData>> getItemByNumOfWeight(int numOfWeight);

    @Query("select * from ArchiveData where typeOfWeight >= :typeOfWeight")
    LiveData<List<ArchiveData>> getItemByTypeOfWeight(int typeOfWeight);

    @Query("select * from ArchiveData where timePoint BETWEEN :start AND :end")
    LiveData<List<ArchiveData>> getItemsByTheDates(Date start, Date end);

    @Delete
    void deleteItem(ArchiveData archiveData);

    @Query("DELETE FROM ArchiveData")
    void deleteAll();






}
