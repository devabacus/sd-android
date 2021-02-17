package no.nordicsemi.android.sdr.archive;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import no.nordicsemi.android.sdr.database_archive.ArchiveData;

public class FileExport {
    private final String TAG = "test";
    File file;

    public FileExport(File newFile) {
        if (file == null) {
            file = newFile;
        }
    }

    public String readFromFile(File file) {
        try {
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);
            String line = br.readLine();
            String[] withoutLastTag = line.split("</archive>");
            br.close();
            return withoutLastTag[0];
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String writeToFile(List<ArchiveData> listOfData) {

        try {
            FileGenerate fileGenerate = new FileGenerate();
            StringBuilder sb = new StringBuilder();
            FileWriter writer;
            boolean isFileNew = !file.exists();
            if (isFileNew) {
                sb.append("<?xml version=\"1.0\"?>");
                sb.append("<archive>");
            } else {
                String previousData = readFromFile(file);
                sb.append(previousData);
            }
            writer = new FileWriter(file, isFileNew);
            writer.append(sb);
            for (int i = 0; i < listOfData.size(); i++) {
                writer.append(fileGenerate.itemToXml(listOfData.get(i)));
            }

            writer.append("</archive>");
            writer.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
//        Log.d(TAG, "writeToFile: file path " + file.getPath());
        return file.getPath();
    }
}
