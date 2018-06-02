package no.nordicsemi.android.blinky;

public class Util {

    public static int butNumConv(int telButNum) {
        int remoteButNum = 0;
        switch (telButNum) {
            case 1: remoteButNum = 1; break;
            case 2: remoteButNum = 4; break;
            case 3: remoteButNum = 7; break;
            case 4: remoteButNum = 2; break;
            case 5: remoteButNum = 5; break;
            case 6: remoteButNum = 8; break;
            case 7: remoteButNum = 3; break;
            case 8: remoteButNum = 6; break;
            case 9: remoteButNum = 9; break;
        }
        return remoteButNum;
    }


}
