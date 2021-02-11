package no.nordicsemi.android.sdr.archive;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class FileExport {

    private final String TAG = "sd-android-fileExport";

    public String writeToFile(String data, Context context)
    {
        // Get the directory for the user's public pictures directory.
//        final File path =
//                Environment.getExternalStoragePublicDirectory
//                        (
//                                //Environment.DIRECTORY_PICTURES
//                                Environment.DIRECTORY_DCIM + "/testftp/"
//                        );
        final File path = context.getExternalFilesDir("new_dir");
        // Make sure the path directory exists.
        assert path != null;
        if(!path.exists())
        {
            // Make it, if it doesn't exit
            Log.d(TAG, "writeToFile: ");
            path.mkdirs();
        }

        final File file = new File(path, "config.txt");

        // Save your stream, don't forget to flush() it before closing it.

        try
        {
            if(!file.exists()){
                file.createNewFile();
                Log.d(TAG, "writeToFile: create file");
            }


            FileOutputStream fOut = new FileOutputStream(file, true);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);
            Log.d(TAG, "writeToFile: path : " + file.toString());
            myOutWriter.close();

            fOut.flush();
            fOut.close();
        }
        catch (IOException e)
        {
            Log.e("Exception", "File write failed: " + e.toString());
        }
        return file.getPath();
    }

}
