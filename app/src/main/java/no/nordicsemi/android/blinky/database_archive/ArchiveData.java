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
    private float mainWeight;
    private int adcArchiveValue;
    private int tareValue;


    public ArchiveData(Date timePoint, float mainWeight, int adcArchiveValue, int tareValue) {
        this.timePoint = timePoint;
        this.mainWeight = mainWeight;
        this.adcArchiveValue = adcArchiveValue;
        this.tareValue = tareValue;
    }

    public Date getTimePoint() {
        return timePoint;
    }

    public float getMainWeight() {
        return mainWeight;
    }

    public int getTareValue() {
        return tareValue;
    }

    public int getAdcArchiveValue() {return adcArchiveValue;}
}
