package no.nordicsemi.android.sdr.database_archive;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {ArchiveData.class}, version = 3)
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
