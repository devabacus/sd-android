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
    private int numOfWeight;
    private float mainWeight;
    private float adcWeight;
    private int adcArchiveValue;
    private int tareValue;


    public ArchiveData(Date timePoint, float mainWeight, int numOfWeight, float adcWeight, int adcArchiveValue, int tareValue) {
        this.timePoint = timePoint;
        this.mainWeight = mainWeight;
        this.numOfWeight = numOfWeight;
        this.adcWeight = adcWeight;
        this.adcArchiveValue = adcArchiveValue;
        this.tareValue = tareValue;
    }

    public Date getTimePoint() {
        return timePoint;
    }

    public int getNumOfWeight() { return numOfWeight; }

    public float getMainWeight() {
        return mainWeight;
    }

    public float getAdcWeight() { return adcWeight; }



    public int getTareValue() {
        return tareValue;
    }

    public int getAdcArchiveValue() {return adcArchiveValue;}
}
