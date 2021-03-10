package no.nordicsemi.android.sdr.database_archive;


import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

@Entity
public class ArchiveData {

    @PrimaryKey(autoGenerate = true)
    public long id;
    @TypeConverters(DateConverter.class)
    private final Date timePoint;
    private final int numOfWeight;
    private final float mainWeight;
    private final float trueWeight;
    private final int adcWeight;
    private final int adcArchiveValue;
    private final int stabTime;
    private final int tareValue;
    private final int typeOfWeight;
    private final int suspectState;
    private final boolean isPercent;



    public ArchiveData(int numOfWeight, Date timePoint, float mainWeight, float trueWeight, int adcWeight, int adcArchiveValue, int tareValue, boolean isPercent, int stabTime,  int typeOfWeight, int suspectState) {
        this.numOfWeight = numOfWeight;
        this.timePoint = timePoint;
        this.mainWeight = mainWeight;
        this.trueWeight = trueWeight;
        this.adcWeight = adcWeight;
        this.adcArchiveValue = adcArchiveValue;
        this.tareValue = tareValue;
        this.isPercent = isPercent;
        this.stabTime = stabTime;
        this.typeOfWeight = typeOfWeight;
        this.suspectState = suspectState;
    }

    public Date getTimePoint() {
        return timePoint;
    }

    public int getNumOfWeight() {
        return numOfWeight;
    }

    public float getMainWeight() {
        return mainWeight;
    }

    public float getTrueWeight() {
        return trueWeight;
    }

    public int getAdcWeight() {
        return adcWeight;
    }

    public int getTareValue() {
        return tareValue;
    }
    public boolean getIsPercent() {
        return isPercent;
    }

    public int getStabTime() {
        return stabTime;
    }

    public int getAdcArchiveValue() {
        return adcArchiveValue;
    }

    public int getTypeOfWeight() {
        return typeOfWeight;
    }
    public int getSuspectState() {
        return suspectState;
    }
}
