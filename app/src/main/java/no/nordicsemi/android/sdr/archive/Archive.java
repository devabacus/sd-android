package no.nordicsemi.android.sdr.archive;

import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import no.nordicsemi.android.blinky.R;

public class Archive extends AppCompatActivity {

    private static final String TAG = "test";
    Button btnDeleteAll, btnExport, btnStart, btnEnd;
    public static Date startDate;
    public static Date endDate;
    ArchiveViewModel archiveViewModel;


    void findViews() {
        btnStart = findViewById(R.id.btn_date_start);
        btnEnd = findViewById(R.id.btn_date_end);
        btnExport = findViewById(R.id.btn_export);
        btnDeleteAll = findViewById(R.id.btn_delete_all);
    }

    void initDataBtn() {
        String curDate = new SimpleDateFormat("dd.MM.yy", new Locale("ru")).format(new Date());
        btnStart.setText(curDate);
        btnEnd.setText(curDate);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        archiveViewModel = ViewModelProviders.of(this).get(ArchiveViewModel.class);
        setContentView(R.layout.activity_archive);
        findViews();
        initDataBtn();
        final Toolbar toolbar2 = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar2);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        btnStart.setOnClickListener(v -> callDatePicker(0));
        btnEnd.setOnClickListener(v -> callDatePicker(1));
    }


    private void callDatePicker(int typeDate) {
        Calendar cal = Calendar.getInstance();
        int mYear = cal.get(Calendar.YEAR);
        int mMonth = cal.get(Calendar.MONTH);
        int mDay = cal.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    cal.set(year, monthOfYear, dayOfMonth);
                    String curDate = new SimpleDateFormat("dd.MM.yy", new Locale("ru")).format(cal.getTime());

                    if (typeDate == 0) {
                        btnStart.setText(curDate);
                        Log.d(TAG, "callDatePicker: " + cal.getTime().getTime());
                        startDate = cal.getTime();
                    } else if (typeDate == 1) {
                        endDate = cal.getTime();
                        btnEnd.setText(curDate);
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
