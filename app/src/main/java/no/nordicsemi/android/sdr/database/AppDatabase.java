package no.nordicsemi.android.sdr.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;


@Database(entities = {CorButton.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase{

    private static AppDatabase INSTANCE;

    public static AppDatabase getDatabase(Context context){
        if (INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "cor_buttons_db").fallbackToDestructiveMigration().build();
        }
        return INSTANCE;
    }


    public abstract ButtonsDao buttonsDao();

}
