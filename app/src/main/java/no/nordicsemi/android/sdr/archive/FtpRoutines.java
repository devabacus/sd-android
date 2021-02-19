package no.nordicsemi.android.sdr.archive;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import xdroid.toaster.Toaster;

import static no.nordicsemi.android.sdr.preferences.PrefExport.KEY_FTP_LOGIN;
import static no.nordicsemi.android.sdr.preferences.PrefExport.KEY_FTP_PASSWORD;
import static no.nordicsemi.android.sdr.preferences.PrefExport.KEY_FTP_PATH;
import static no.nordicsemi.android.sdr.preferences.PrefExport.KEY_FTP_SERVER;

public class FtpRoutines {

    public static final String  TAG = "sd_android_ftp";

    FTPClient ftpClient;
    Handler mHandler;

    public FtpRoutines(){
        if(ftpClient == null) ftpClient = new FTPClient();
    }

    public void sendFileToServer(Context context, String localPath, String remoteFileName){

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String server = sp.getString(KEY_FTP_SERVER,"");
        String login = sp.getString(KEY_FTP_LOGIN,"");
        String pass = sp.getString(KEY_FTP_PASSWORD,"");
        String remotePath = sp.getString(KEY_FTP_PATH,"");

        new Thread(() -> {
            try {
                ftpClient.connect(server);
                boolean isAuth = ftpClient.login(login,pass);
                if(isAuth) {
                    Log.d(TAG, "auth: connection is SUCCESS");
                    ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                    ftpClient.enterLocalPassiveMode();
                    uploadFile(localPath, remoteFileName, remotePath, context);
                    return;
                } else {
                    Toaster.toast("Ошибка подключения к серверу");
                }

            } catch (IOException e) {
//                e.printStackTrace();
                Log.d(TAG, "sendFileToServer: ошибка подключения");
            }
            Log.d(TAG, "sendFileToServer: ");
        }).start();
    }

    void auth(String serverAddress, String login, String pass) throws IOException {
        ftpClient.connect(serverAddress);
        boolean isAuth = ftpClient.login(login,pass);
        if(isAuth) Log.d(TAG, "auth: connection is SUCCESS");
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.enterLocalPassiveMode();
    }

    void uploadFile(String pathToFile, String fileName, String remotePath, Context context)  {
        try {
            File file = new File(pathToFile);
            readFileToLog(file);
            FileInputStream in = new FileInputStream(file);
            ftpClient.enterLocalPassiveMode();
            ftpClient.changeWorkingDirectory(remotePath);
            boolean result = ftpClient.storeFile(fileName, in);
            if(result) {
                Toaster.toast("Экспорт завершен");
            } else {
                Toaster.toast("Ошибка загрузки файла");
            }
            Log.d(TAG, "result upload to server is " + (result ?"SUCCESS":"FAILED"));
            in.close();
            ftpClient.logout();
            ftpClient.disconnect();

        } catch (IOException e) {
            Toaster.toast("Ошибка загрузки файла"); ;
        }
    }

    void readFileToLog(File file){
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader((new FileReader(file)));
            String line;

            while ((line = br.readLine()) != null){
                text.append(line);
                text.append('\n');
            }
            Log.d(TAG, "readFileToLog: " + text);
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "readFileToLog: error occured : " + e.toString());
        }
    }
}
