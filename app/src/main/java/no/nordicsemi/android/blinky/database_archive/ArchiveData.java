package no.nordicsemi.android.blinky.database_archive;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.Date;

@Entity
public class ArchiveData {

    @PrimaryKey(autoGenerate = true)
    public long id;
    @TypeConverters(DateConverter.class)
    private Date timePoint;
    private int mainWeight;
    private int tareValue;


    public ArchiveData(Date timePoint, int mainWeight, int tareValue) {
        this.timePoint = timePoint;
        this.mainWeight = mainWeight;
        this.tareValue = tareValue;
    }

    public Date getTimePoint() {
        return timePoint;
    }

    public int getMainWeight() {
        return mainWeight;
    }

    public int getTareValue() {
        return tareValue;
    }
}
