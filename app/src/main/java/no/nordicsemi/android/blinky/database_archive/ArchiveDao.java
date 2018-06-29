package no.nordicsemi.android.blinky.database_archive;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
@TypeConverters(DateConverter.class)
public interface ArchiveDao {


    @Insert(onConflict = REPLACE)
    void addArchiveItem(ArchiveData archiveData);

    @Query("select * from ArchiveData")
    LiveData<List<ArchiveData>> getAllArchiveItems();

    @Query("select * from ArchiveData where id = :id")
    ArchiveData getItemById(long id);

    @Delete
    void deleteItem(ArchiveData archiveData);


}
