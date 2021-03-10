package no.nordicsemi.android.sdr.database_archive;


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {ArchiveData.class}, version = 4, exportSchema = false)
public abstract class ArchiveDatabase extends RoomDatabase {
    private static ArchiveDatabase INSTANCE;

    public static ArchiveDatabase getArchiveDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), ArchiveDatabase.class, "archive_db").fallbackToDestructiveMigration().build();
        }
        return INSTANCE;
    }

    public abstract ArchiveDao archiveFromDao();


}
