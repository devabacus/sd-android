package no.nordicsemi.android.sdr.archive;
import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class FtpRoutines {

    public static final String  TAG = "sd_android_ftp";

    FTPClient ftpClient;

    public FtpRoutines(){
        if(ftpClient == null) ftpClient = new FTPClient();
    }

    public void sendFileToServer(String serverAddress, String login, String pass, String path, String remoteFileName){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ftpClient.connect(serverAddress);
                    boolean isAuth = ftpClient.login(login,pass);
                    if(isAuth) Log.d(TAG, "auth: connection is SUCCESS");
                    ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                    ftpClient.enterLocalPassiveMode();
                    uploadFile(path, remoteFileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    void auth(String serverAddress, String login, String pass) throws IOException {
        ftpClient.connect(serverAddress);
        boolean isAuth = ftpClient.login(login,pass);
        if(isAuth) Log.d(TAG, "auth: connection is SUCCESS");
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.enterLocalPassiveMode();
    }

    void uploadFile(String pathToFile, String fileName) throws IOException {
        File file = new File(pathToFile);
        readFileToLog(file);
        FileInputStream in = new FileInputStream(file);
        ftpClient.enterLocalPassiveMode();
        ftpClient.changeWorkingDirectory("/public_html/scale/icons");
        boolean result = ftpClient.storeFile(fileName, in);
        Log.d(TAG, "result upload to server is " + (result ?"SUCCESS":"FAILED"));
        in.close();
        ftpClient.logout();
        ftpClient.disconnect();
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
