package no.nordicsemi.android.sdr.archive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Locale;

import static no.nordicsemi.android.sdr.archive.Archive.endDate;
import static no.nordicsemi.android.sdr.archive.Archive.listOfArchive;
import static no.nordicsemi.android.sdr.archive.Archive.startDate;

public class AlarmTask extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        Log.d("test", "onReceive: alarm worked");
        MediaPlayer mediaPlayer = MediaPlayer.create(context, Settings.System.DEFAULT_RINGTONE_URI);
        mediaPlayer.start();
        export_archive(context);
        Toast.makeText(context, "exported", Toast.LENGTH_SHORT).show();
    }

    public void export_archive(Context context){
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy", new Locale("ru"));
//        String fileName = sdf.format(startDate) + "-" + sdf.format(endDate);
        String fileName = "17.02.21-17.02.21";
        FileExport fileExport = new FileExport();
//        String pathToFile = fileExport.writeToFile(fileName, "xml", listOfArchive, context);
        String pathToFile = "/storage/emulated/0/Android/data/no.nordicsemi.android.nrfblinky/files/archive_exports/17.02.21-17.02.21.xml";
        Toast.makeText(context, "Экспорт завершен", Toast.LENGTH_SHORT).show();
        FtpRoutines ftpRoutines = new FtpRoutines();
        ftpRoutines.sendFileToServer(context, pathToFile, fileName + ".xml");
    }
}
