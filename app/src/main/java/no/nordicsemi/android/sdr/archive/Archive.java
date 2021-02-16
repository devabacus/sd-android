package no.nordicsemi.android.sdr.archive;

import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.LongDef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import no.nordicsemi.android.blinky.R;
import no.nordicsemi.android.sdr.database_archive.ArchiveData;

public class Archive extends AppCompatActivity {

    private static final String TAG = "test";
    Button btnExport, btnStart, btnEnd, btnDeleteAll;

    public static Date startDate;
    public static Date endDate;
    ArchiveViewModel archiveViewModel;
    List<ArchiveData> listOfArchive;


    void findViews() {
        btnStart = findViewById(R.id.btn_date_start);
        btnEnd = findViewById(R.id.btn_date_end);
        btnExport = findViewById(R.id.btn_export);
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
        btnDeleteAll.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                archiveViewModel.deleteAllArchiveItems();
                ArchiveSaving.numOfWeight = 0;
                return false;
            }
        });

        archiveViewModel.getIsDateUpdate().observe(this, isUpdated -> {
            if (isUpdated) {
                archiveViewModel.getArchiveListByDates(Archive.startDate, Archive.endDate).observe(this, archiveListByDate -> {
                    if (archiveListByDate != null) {
//                        archiveAdapter.addItems(archiveListByDate);
                        listOfArchive = archiveListByDate;
                    }
                });
            }
        });

        btnExport.setOnClickListener(v -> {
                Log.d(TAG, "onClick: listOfArchive = " + listOfArchive.toString());

                String fileName = getDateStr(startDate) + "-" + getDateStr(endDate);
                FileExport fileExport = new FileExport();
                String pathToFile = fileExport.writeToFile(fileName, "xml", listOfArchive, this);
                            Toast.makeText(this, "done", Toast.LENGTH_SHORT).show();
                FtpRoutines ftpRoutines = new FtpRoutines();
                ftpRoutines.sendFileToServer("185.12.92.65", "katitka@etalon-ufa.ru", "123QWEasdZXC", pathToFile, fileName + ".xml");
        });
    }


    private void callDatePicker(int typeDate) {
        Calendar cal = Calendar.getInstance();
        int mYear = cal.get(Calendar.YEAR);
        int mMonth = cal.get(Calendar.MONTH);
        int mDay = cal.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    cal.set(year, monthOfYear, dayOfMonth);
                    if (typeDate == 0) {
                        startDate = getStartEndDate(cal.getTime(), false);
                        btnStart.setText(getDateStr(startDate));
                        startDate = cal.getTime();
                    } else if (typeDate == 1) {
                        endDate = getStartEndDate(cal.getTime(), true);
                        btnEnd.setText(getDateStr(endDate));
                    }
                    archiveViewModel.setDateUpdated(true);
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
