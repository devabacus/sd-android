package no.nordicsemi.android.sdr.database_archive;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.Date;

@Entity
public class ArchiveData {

    @PrimaryKey(autoGenerate = true)
    public long id;
    @TypeConverters(DateConverter.class)
    private final Date timePoint;
    private final int numOfWeight;
    private final float mainWeight;
    private final float adcWeight;
    private final int adcArchiveValue;
    private final int tareValue;
    private final int typeOfWeight;


    public ArchiveData(Date timePoint, float mainWeight, int numOfWeight, float adcWeight, int adcArchiveValue, int tareValue, int typeOfWeight) {
        this.timePoint = timePoint;
        this.mainWeight = mainWeight;
        this.numOfWeight = numOfWeight;
        this.adcWeight = adcWeight;
        this.adcArchiveValue = adcArchiveValue;
        this.tareValue = tareValue;
        this.typeOfWeight = typeOfWeight;
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

    public int getTypeOfWeight(){return typeOfWeight;}
}
