package no.nordicsemi.android.sdr.archive;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import no.nordicsemi.android.blinky.R;

public class ArchiveInterface extends Fragment {
    Button btnDeleteAll, btnExport, btnStart, btnEnd;
    private ArchiveViewModel archiveViewModel;
    public static final String TAG = "test";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_archive_interface, container, false);
        btnDeleteAll = v.findViewById(R.id.btn_delete_all);
        btnExport = v.findViewById(R.id.btn_export);
        btnStart = v.findViewById(R.id.btn_date_start);
        btnEnd = v.findViewById(R.id.btn_date_end);
        btnDeleteAll.setOnLongClickListener(v1 -> {
            archiveViewModel.deleteAllArchiveItems();
            return false;
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                DialogFragment datePicker = new DatePickerFragment();
//                datePicker.show(getFragmentManager().beginTransaction(), "DatePickerFragment");
//                Toast.makeText(getContext(), "Test", Toast.LENGTH_SHORT).show();
//                Log.d(TAG, "onClick: btn start");
            }
        });

        btnDeleteAll.setOnClickListener(v1 -> Log.d("test", "onCreateView: btnDeleteAll.setOnClickListener"));
        btnExport.setOnClickListener(v1 -> Log.d("test", "btnExport.setOnClickListener"));
        return v;
    }

    @Override
    public void onResume() {
//        if (ButtonFrag.curUser.equals("admin") || ButtonFrag.curUser.equals("admin1")) {
//            btnDeleteAll.setVisibility(View.VISIBLE);
//            super.onResume();
//        } else {
//            btnDeleteAll.setVisibility(View.GONE);
//        }
        super.onResume();
    }


    public void showDatePickerDialog(View v) {

    }

//    @Override
//    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//        Calendar c = Calendar.getInstance();
//        c.set(Calendar.YEAR, year);
//        c.set(Calendar.MONTH, month);
//        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//
//        String currenDate = DateFormat.getInstance().format(c.getTime());
//        Toast.makeText(getContext(), currenDate, Toast.LENGTH_LONG).show();
//    }


}