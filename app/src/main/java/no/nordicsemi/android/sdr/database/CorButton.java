package no.nordicsemi.android.sdr.database;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class CorButton {

    @PrimaryKey
    private long id;

    private String butName;
    private String corDir;
    private int corValue;
    private int compValue;
//    private Boolean initialized;


    public CorButton(long id, String butName, String corDir, int corValue, int compValue) {
        this.id = id;
        this.butName = butName;
        this.corDir = corDir;
        this.corValue = corValue;
        this.compValue = compValue;

    }

    public String getButName() {
        return butName;
    }

    public void setButName(String butName) {
        this.butName = butName;
    }

    public String getCorDir() {
        return corDir;
    }

    public void setCorDir(String corDir) {
        this.corDir = corDir;
    }

    public int getCorValue() {
        return corValue;
    }

    public void setCorValue(int corValue) {
        this.corValue = corValue;
    }

    public int getCompValue() {
        return compValue;
    }

    public void setCompValue(int compValue) {
        this.compValue = compValue;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
