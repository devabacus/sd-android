package no.nordicsemi.android.sdr.archive;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import no.nordicsemi.android.sdr.database_archive.ArchiveData;

public class FileExport {

    private final String TAG = "test";

    public String writeToFile(String fileName, String format, List<ArchiveData> listOfData, Context context)
    {
        // Get the directory for the user's public pictures directory.
//        final File path =
//                Environment.getExternalStoragePublicDirectory
//                        (
//                                //Environment.DIRECTORY_PICTURES
//                                Environment.DIRECTORY_DCIM + "/testftp/"
//                        );
        final File path = context.getExternalFilesDir("archive_exports");
        // Make sure the path directory exists.
        assert path != null;
        if(!path.exists())
        {
            Log.d(TAG, "writeToFile: ");
            path.mkdirs();
        }

        final File file = new File(path, fileName + "." + format);
        try
        {
            if(!file.exists()){
                file.createNewFile();
                Log.d(TAG, "writeToFile: create file");
            }


            FileOutputStream fOut = new FileOutputStream(file, true);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            FileGenerate fileGenerate= new FileGenerate();
            Log.d(TAG, "writeToFile: <archive>");
            myOutWriter.append("<archive>");
            for(int i = 0; i < listOfData.size(); i++){
                myOutWriter.append(fileGenerate.itemToXml(listOfData.get(i)));
            }
            myOutWriter.append("</archive>");
            Log.d(TAG, "writeToFile: <archive>");
            Log.d(TAG, "writeToFile: path : " + file.toString());
            myOutWriter.close();

            fOut.flush();
            fOut.close();
        }
        catch (IOException e)
        {
            Log.e("Exception", "File write failed: " + e.toString());
        }
        Log.d(TAG, "writeToFile: file path " + file.getPath());
        return file.getPath();
    }

}
