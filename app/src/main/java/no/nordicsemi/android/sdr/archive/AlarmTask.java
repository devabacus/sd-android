package no.nordicsemi.android.sdr.archive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


import static no.nordicsemi.android.sdr.archive.Archive.endDate;
import static no.nordicsemi.android.sdr.archive.Archive.listOfArchive;
import static no.nordicsemi.android.sdr.archive.Archive.startDate;

public class AlarmTask extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        export_archive(context);
    }

    public void export_archive(Context context)  {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy", new Locale("ru"));
        String fileName = sdf.format(new Date());
        final File file = new File(context.getExternalFilesDir("archive_exports"), fileName + ".xml");
//        try {
//            FileWriter writer = new FileWriter(file, true);
//            writer.append("</archive>");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        FtpRoutines ftpRoutines = new FtpRoutines();
        ftpRoutines.sendFileToServer(context, file.getPath(), fileName + ".xml");
        Toast.makeText(context, "Экспорт завершен", Toast.LENGTH_LONG).show();
    }
}
