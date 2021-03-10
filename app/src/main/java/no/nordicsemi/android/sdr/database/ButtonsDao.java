package no.nordicsemi.android.sdr.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ButtonsDao {

    @Insert(onConflict = REPLACE)
    void addButton(CorButton corButton);

    @Query("select * from CorButton")
    LiveData<List<CorButton>> getAllCorButtons();

    @Query("select * from CorButton order by id DESC LIMIT :prefNums ")
    LiveData<List<CorButton>> getButtons(int prefNums);



    @Query("select * from CorButton where id = :id")
    LiveData<CorButton> getItemById(long id);

//    @Query("select initialized from CorButton")
////    Boolean getInitialized();

    @Delete
    void deleteButton(CorButton corButton);





}
