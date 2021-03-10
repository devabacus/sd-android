package no.nordicsemi.android.sdr.archive;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import androidx.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import no.nordicsemi.android.blinky.R;
import no.nordicsemi.android.sdr.database_archive.ArchiveData;

import static no.nordicsemi.android.sdr.preferences.PrefExport.KEY_EXPORT_DETAIL;
import static no.nordicsemi.android.sdr.preferences.PrefExport.KEY_FTP_LOGIN;
import static no.nordicsemi.android.sdr.preferences.PrefExport.KEY_FTP_PASSWORD;
import static no.nordicsemi.android.sdr.preferences.PrefExport.KEY_FTP_SERVER;

public class Archive extends AppCompatActivity {

    private static final String TAG = "test";
    Button btnExport, btnStart, btnEnd, btnDeleteAll;

    public static Date startDate;
    public static Date endDate;
    ArchiveViewModel archiveViewModel;
    public static List<ArchiveData> listOfArchive;


    void findViews() {
        btnStart = findViewById(R.id.btn_date_start);
        btnEnd = findViewById(R.id.btn_date_end);
        btnDeleteAll = findViewById(R.id.btn_delete_all);
    }

    Date getStartEndDate(Date date, boolean isEndDate){
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        if(isEndDate) {
            c.set(Calendar.HOUR_OF_DAY, 23);
            c.set(Calendar.MINUTE, 59);
            c.set(Calendar.SECOND, 59);
        }
        return c.getTime();
    }

    String getDateStr(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy", new Locale("ru"));
        return sdf.format(date);
    }

    void initDataBtn() {
        startDate = getStartEndDate(new Date(), false);
        endDate = getStartEndDate(new Date(), true);
        btnStart.setText(getDateStr(startDate));
        btnEnd.setText(getDateStr(endDate));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        archiveViewModel = ViewModelProviders.of(this).get(ArchiveViewModel.class);
        archiveViewModel.setDateUpdated(true);
        setContentView(R.layout.activity_archive);
        findViews();
        initDataBtn();
        final Toolbar toolbar2 = (Toolbar) findViewById(R.id.toolbar2);
        toolbar2.setTitle("Архив");
        setSupportActionBar(toolbar2);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        btnStart.setOnClickListener(v -> callDatePicker(0));
        btnEnd.setOnClickListener(v -> callDatePicker(1));

        archiveViewModel.getIsDateUpdate().observe(this, isUpdated -> {
            if (isUpdated) {
                archiveViewModel.getArchiveListByDates(Archive.startDate, Archive.endDate).observe(this, archiveListByDate -> {
                    if (archiveListByDate != null) {
                        listOfArchive = archiveListByDate;
                    }
                });
            }
        });


    }

    //suspect 0 or 8

    List<ArchiveData> getNotDetailedList(List<ArchiveData> list){
        List<ArchiveData> _list = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            int suspect = list.get(i).getSuspectState();
            int typeOfWeight = list.get(i).getTypeOfWeight();
            if( typeOfWeight == 1 || suspect == SuspectMasks.ONLY_MAX_WEIGHT) {
                _list.add(list.get(i));
            }
        }
        return _list;
    }

    public boolean allFieldProvided(SharedPreferences sp){
        String server = sp.getString(KEY_FTP_SERVER, "");
        String login = sp.getString(KEY_FTP_LOGIN, "");
        String pass = sp.getString(KEY_FTP_PASSWORD, "");
        return !server.isEmpty() && !login.isEmpty() && !pass.isEmpty();
    }


    public void export_archive(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if(allFieldProvided(sp)){
            List<ArchiveData> listForExport = listOfArchive;
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy", new Locale("ru"));
            String fileName = sdf.format(startDate) + "-" + sdf.format(endDate);
            File file = new File(getExternalFilesDir("archive_exports"), fileName + ".xml");
            int i = 1;
            while(file.exists()){
                if(fileName.contains("_")){
                    fileName = fileName.split("_")[0];
                }
                fileName += "_" + i;
                file = new File(getExternalFilesDir("archive_exports"), fileName + ".xml");
                i++;
            }
            FileExport fileExport = new FileExport(file);
            if(!sp.getBoolean(KEY_EXPORT_DETAIL, true)){
                listForExport = getNotDetailedList(listForExport);
            }
            String pathToFile = fileExport.writeToFile(listForExport);
            Log.d(TAG, "export_archive: path = " + pathToFile);
//            Toast.makeText(this, "Экспорт завершен", Toast.LENGTH_SHORT).show();
            FtpRoutines ftpRoutines = new FtpRoutines();
            ftpRoutines.sendFileToServer(this, pathToFile, fileName + ".xml");
        } else {
            Toast.makeText(this, "Заполните данные в настройках экспорта", Toast.LENGTH_SHORT).show();
        }

    }

    void alertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Удаление архива")
            .setMessage("Удалить весь архив?")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        archiveViewModel.deleteAllArchiveItems();
                        ArchiveSaving.numOfWeight = 0;
                        Toast.makeText(Archive.this, "Архив удален", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }


    private void callDatePicker(int typeDate) {
        Calendar cal = Calendar.getInstance();
        int mYear = cal.get(Calendar.YEAR);
        int mMonth = cal.get(Calendar.MONTH);
        int mDay = cal.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    cal.set(year, monthOfYear, dayOfMonth);
//                    Log.d(TAG, "callDatePicker: date = " + );
                    if (typeDate == 0) {
                        startDate = getStartEndDate(cal.getTime(), false);
                        btnStart.setText(getDateStr(startDate));
                    } else if (typeDate == 1) {
                        endDate = getStartEndDate(cal.getTime(), true);
                        btnEnd.setText(getDateStr(endDate));
                    }

                    archiveViewModel.setDateUpdated(true);
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_archive, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.export:
                export_archive();
                break;
            case R.id.delete_whole_archive:
                alertDialog();
                break;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
