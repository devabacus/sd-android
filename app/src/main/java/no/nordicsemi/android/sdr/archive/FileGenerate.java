package no.nordicsemi.android.sdr.archive;

import no.nordicsemi.android.sdr.database_archive.ArchiveData;

public class FileGenerate {
    public static final String TAG = "test";

    public String itemToXml(ArchiveData archiveData) {
        StringBuilder sb = new StringBuilder();
        sb.append("<item>");

//        sb.append("<date>");
//        sb.append(archiveData.getTimePoint());
//        sb.append("</date>");

        sb.append("<unixTime>");
        sb.append(archiveData.getTimePoint().getTime());
        sb.append("</unixTime>");

        sb.append("<weight>");
        sb.append(archiveData.getMainWeight());
        sb.append("</weight>");

        sb.append("<tare>");
        sb.append(archiveData.getTareValue());
        sb.append("</tare>");

        sb.append("</item>");

        return sb.toString();
    }

}
