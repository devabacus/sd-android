package no.nordicsemi.android.sdr;

public class Cmd {
    public static final String INIT = "i";
    public static final String COR_RESET = "$r&";
    public static final String COR_MODE_AUTO = "s3/1";
    public static final String COR_MODE_MANUAL = "s3/2";
    public static final String ADC_SHOW_ON_BOTH = "s1/3";
    public static final String ADC_SHOW_ON_SEG = "s1/2";
    public static final String ADC_SHOW_ON_BLE = "s1/1";
    public static final String ADC_SHOW_OFF = "s1/0";
    public static final String CAL_UNLOAD = "s5/1";
    public static final String CAL_WEIGHT = "s5/2";
    public static final String CAL_LOAD = "s5/3";
    public static final String NUM_COR_BUT9_ON = "s6/9";
    public static final String NUM_COR_BUT9_OFF = "s6/3";
    public static final String INCREASE_ARCHIVE_COUNTER = "s13/2";
}
